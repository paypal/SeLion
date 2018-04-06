/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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
 * {@link Config#setConfigProperty(com.paypal.selion.configuration.Config.ConfigProperty, Object)}.
 * 
 */
@ThreadSafe
public final class ConfigManager {

    // Map for each TestNG test name -> local SeLion config associated with <test>
    private static Map<String, LocalConfig> configsMap = new ConcurrentHashMap<String, LocalConfig>();

    private ConfigManager() {
        // Utility class. So hide the constructor
    }

    /**
     * Adds the local configuration {@link LocalConfig} associated with name. Over-rides any config with the same name.
     * 
     * @param config
     *            The LocalConfig.
     * @param name
     *            The name to associate with local config.
     */
    public static synchronized void addConfig(String name, LocalConfig config) {
        SeLionLogger.getLogger().entering(new Object[] { name, config });
        checkArgument(StringUtils.isNotBlank(name),
                "A testname for which configuration is being added cannot be null (or) empty.");
        checkArgument(config != null, "A configuration object cannot be null.");
        if (configsMap.containsKey(name)) {
            String message = "Overwriting an already existing configuration";
            SeLionLogger.getLogger().warning(message);
        }
        configsMap.put(name, config);
        SeLionLogger.getLogger().exiting();
    }

    /**
     * Returns the local configuration {@link LocalConfig} associated the provided name. If no config with that name has
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
     * @param name
     *            The name to search for.
     * @return - A {@link LocalConfig} object that either represent's the configuration object that maps to the name
     *         given (or) a default Configuration object that apes the global configuration.
     */
    public static synchronized LocalConfig getConfig(String name) {
        SeLionLogger.getLogger().entering(name);
        checkArgument(StringUtils.isNotBlank(name),
                "The test name for which configuration is being retrieved cannot be null (or) empty.");

        // if no local config added? I.e reading from a TestNG listener (before AddConfig) or listeners disabled?
        LocalConfig localConfiguration = configsMap.get(name);
        if (localConfiguration == null) {
            throw new IllegalArgumentException(
                    "A local configuration with specified name was not found. Please double check the <test> name and retry.");
        }
        SeLionLogger.getLogger().exiting(localConfiguration);
        return localConfiguration;
    }

    /**
     * Remove the local configuration {@link LocalConfig} associated supplied name.
     * 
     * @param name
     *            The name to remove
     * @return - <code>true</code> if the configuration was successfully removed.
     */
    public static synchronized boolean removeConfig(String name) {
        checkArgument(StringUtils.isNotBlank(name),
                "The test name for which configuration is being retrieved cannot be null (or) empty.");
        return configsMap.remove(name) != null ? true : false;
    }

    /**
     * A utility method that can dump the configuration for a given &lt;test&gt; identified with its name.
     * 
     * @param testName
     *            - The name of the test as given in the suite xml file.
     */
    public static synchronized void printConfiguration(String testName) {
        LocalConfig currentConfig = getConfig(testName);
        currentConfig.printConfigValues(testName);
    }

    public static synchronized boolean isTestConfigPresent(String testName) {
        return configsMap.containsKey(testName);
    }
}
