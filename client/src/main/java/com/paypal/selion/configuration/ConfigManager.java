/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
|                                                                                                                     |
|  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     |
|  with the License.                                                                                                  |
|                                                                                                                     |
|  You may obtain a copy of the License at                                                                            |
|                                                                                                                     |
|       http://www.apache.org/licenses/LICENSE-2.0                                                                    |
|                                                                                                                     |
|  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   |
|  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  |
|  the specific language governing permissions and limitations under the License.                                     |
\*-------------------------------------------------------------------------------------------------------------------*/

package com.paypal.selion.configuration;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.lang.StringUtils;

import com.paypal.selion.logger.SeLionLogger;

/**
 * This class supports configuration operations on SeLion local configurations {@link LocalConfig} being used in TestNG
 * suites.
 * 
 * This supports configurations when running with parallel suites (i.e. TestNG parallel = {tests, methods, classes,
 * instances}) by supporting distinct local configurations for each TestNG xmltest. When reading config values without
 * parallel execution (parallel=false) values are effectively same as the value provided by {@link Config}
 * 
 * To get access to the SeLion local configuration values for the currently executing &lt;test&gt;.
 * 
 * <pre>
 * &#064;Test
 * public void f(ITestContext ctx) {
 *     String name = ctx.getCurrentXmlTest().getName();
 *     LocalConfig config = ConfigManager.getConfig(name);
 *     String Value = config.getConfigProperty(ConfigProperty.HOSTNAME);
 * }
 * </pre>
 * 
 * If the current &lt;test&gt; name can not be determined, then defaults to the global configuration that is available
 * via {@link Config#getConfigProperty(com.paypal.selion.configuration.Config.ConfigProperty)}/
 * {@link Config#setConfigProperty(com.paypal.selion.configuration.Config.ConfigProperty, String)}.
 * 
 */
@ThreadSafe
public final class ConfigManager {

    // Map for each TestNG test name -> local SeLion config associated with <test>
    private static Map<Thread, LocalConfig> configsMap = new ConcurrentHashMap<Thread, LocalConfig>();

    private ConfigManager() {
        // Utility class. So hide the constructor
    }

    /**
     * Adds the local configuration {@link LocalConfig} associated with thread. Over-rides any config with the same thread.
     * 
     * @param config
     *            The LocalConfig.
     */
    public static synchronized void addConfig(LocalConfig config) {
        SeLionLogger.getLogger().entering(new Object[] { config });
        checkArgument(config != null, "A configuration object cannot be null.");
        if (configsMap.containsKey(Thread.currentThread())) {
            String message = "Overwriting an already existing configuration";
            SeLionLogger.getLogger().warning(message);
        }
        configsMap.put(Thread.currentThread(), config);
        SeLionLogger.getLogger().exiting();
    }

    /**
     * Returns the local configuration {@link LocalConfig} associated the provided thread. If no config with that name has
     * been added an {@link IllegalArgumentException} is thrown.
     * 
     * <br>
     * 
     * <pre>
     * 
     * <b><i>Use this method for reading any "test-specific" configuration at any listener invocation.</b></i>
     * 
     * </pre>
     *
     * @return - A {@link LocalConfig} object that either represent's the configuration object that maps to the name
     *         given (or) a default Configuration object that apes the global configuration.
     */
    public static synchronized LocalConfig getConfig() {
        SeLionLogger.getLogger().entering();

        // if no local config added? I.e reading from a TestNG listener (before AddConfig) or listeners disabled?
        LocalConfig localConfiguration = configsMap.get(Thread.currentThread());

        //If LocalConfig is null create a new instance and set with Global Config.
        if (localConfiguration == null) {
            localConfiguration = new LocalConfig(Config.getConfig());
            ConfigManager.addConfig(localConfiguration);
        }
        SeLionLogger.getLogger().exiting(localConfiguration);
        return localConfiguration;
    }

    /**
     * Remove the local configuration {@link LocalConfig} associated supplied thread.
     *
     * @return - <code>true</code> if the configuration was successfully removed.
     */
    public static synchronized boolean removeConfig() {
        return configsMap.remove(Thread.currentThread()) != null ? true : false;
    }

    /**
     * A utility method that can dump the configuration for a given &lt;test&gt; identified with its thread.
     *
     */
    public static synchronized void printConfiguration() {
        LocalConfig currentConfig = getConfig();
        currentConfig.printConfigValues(Thread.currentThread().getName());
    }

    public static synchronized boolean isTestConfigPresent() {
        return configsMap.containsKey(Thread.currentThread());
    }
}
