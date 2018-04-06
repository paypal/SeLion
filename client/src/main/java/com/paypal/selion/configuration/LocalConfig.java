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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.lang.StringUtils;

import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;

/**
 * A local configuration object that contains SeLion configuration property values (i.e. "property bag") of
 * {@link ConfigProperty}. Local configuration is really composed of the global SeLionConfig values as well as property
 * value "updates" made for each local configuration instance. Local configuration property values over-ride the
 * corresponding global SeLionConfig property value.
 * 
 * A local configuration supports configuration via parameters that are in the testng "test" tag scope over-riding
 * parameters values in suite scope.
 * 
 * <pre>
 * Map&lt;ConfigProperty, String&gt; localValues = new HashMap&lt;ConfigProperty, String&gt;();
 * LocalConfig localConfig = new LocalConfig(localValues);
 * </pre>
 * 
 * See {@link Config.ConfigProperty} for the configuration properties that are supported and their initial default
 * values. Some configuration properties are global only in scope and cannot be specified in a local configuration.
 * These properties must be specified globally using {@link Config}.
 */
@ThreadSafe
public class LocalConfig {
    // config intentionally kept basic. We support no loading of configuration from files etc. To support the global
    // configuration "over-rides" and possible future passing as a parameter into methods.
    private final BaseConfiguration baseConfig;

    /**
     * Constructs a new instance of this class.
     */
    public LocalConfig() {
        baseConfig = new BaseConfiguration();
    }

    /**
     * Constructs a new instance of this class with the specified initial value.
     * 
     * @param configProperty
     *            {@link Config.ConfigProperty} The ConfigProperty to set in the local configuration.
     * @param value
     *            String The configuration property value to set.
     */
    public LocalConfig(ConfigProperty configProperty, String value) {
        this();
        checkArgument(configProperty != null, "Config property cannot be null");
        checkArgument(value != null, "Config property value cannot be null");
        checkArgument(checkNotInGlobalScope(configProperty),
                String.format("The configuration property (%s) is not supported in local config.", configProperty)); // NOSONAR

        baseConfig.setProperty(configProperty.getName(), value);
    }

    /**
     * Constructs a new instance of this class from the specified initial values.
     * 
     * @param initialValues
     *            Map The initial MAP of ConfigProperty values used to create the local configuration.
     */
    public LocalConfig(Map<ConfigProperty, String> initialValues) {
        this();
        if (initialValues != null && !initialValues.isEmpty()) {
            for (Map.Entry<ConfigProperty, String> entry : initialValues.entrySet()) {
                if (entry.getKey().isGlobalScopeOnly()) {
                    String message = String.format("The configuration property (%s) is not supported in local config.",
                            entry.getKey());
                    throw new IllegalArgumentException(message);
                }
                baseConfig.setProperty(entry.getKey().getName(), entry.getValue());
            }
        }
    }

    /**
     * Get the configuration property value for configProperty.
     * 
     * @param configProperty
     *            The configuration property value to get
     * 
     * @return The configuration property value or null if property does not exit.
     */
    public synchronized String getConfigProperty(Config.ConfigProperty configProperty) {
        SeLionLogger.getLogger().entering(configProperty);
        checkArgument(configProperty != null, "Config property cannot be null");

        // Search locally then query SeLionConfig if not found
        String propValue = null;
        if (baseConfig.containsKey(configProperty.getName())) {
            propValue = baseConfig.getString(configProperty.getName());
        }

        if (StringUtils.isBlank(propValue)) {
            propValue = Config.getConfigProperty(configProperty);
        }
        SeLionLogger.getLogger().exiting(propValue);
        return propValue;
    }

    public synchronized List<Object> getListConfigProperty(Config.ConfigProperty configProperty) {
        SeLionLogger.getLogger().entering(configProperty);
        checkArgument(configProperty != null, "Config property cannot be null");

        // Search locally then query SeLionConfig if not found
        List<Object> propValue = null;
        if (baseConfig.containsKey(configProperty.getName())) {
            propValue = baseConfig.getList(configProperty.getName());
        }

        if (propValue == null || propValue.isEmpty()) {
            propValue = Config.getListConfigProperty(configProperty);
        }
        SeLionLogger.getLogger().exiting(propValue);
        return propValue;
    }

    public synchronized int getIntConfigProperty(Config.ConfigProperty configProperty) {
        SeLionLogger.getLogger().entering(configProperty);
        checkArgument(configProperty != null, "Config property cannot be null");

        // start with the global value, then update from the local value, if it exists
        int propValue = Config.getIntConfigProperty(configProperty);
        if (baseConfig.containsKey(configProperty.getName())) {
            propValue = baseConfig.getInt(configProperty.getName());
        }
        SeLionLogger.getLogger().exiting(propValue);
        return propValue;
    }

    public synchronized boolean getBooleanConfigProperty(Config.ConfigProperty configProperty) {
        SeLionLogger.getLogger().entering(configProperty);
        checkArgument(configProperty != null, "Config property cannot be null");

        // start with the global value, then update from the local value, if it exists
        boolean propValue = Config.getBoolConfigProperty(configProperty);
        if (baseConfig.containsKey(configProperty.getName())) {
            propValue = baseConfig.getBoolean(configProperty.getName());
        }
        SeLionLogger.getLogger().exiting(propValue);
        return propValue;
    }

    /**
     * Sets the SeLion configuration property value.
     * 
     * @param configProperty
     *            The configuration property to set.
     * @param configPropertyValue
     *            The configuration property value to set.
     */
    public synchronized void setConfigProperty(Config.ConfigProperty configProperty, Object configPropertyValue) {
        checkArgument(configProperty != null, "Config property cannot be null");
        checkArgument(checkNotInGlobalScope(configProperty),
                String.format("The configuration property (%s) is not supported in local config.", configProperty)); // NOSONAR
        checkArgument(configPropertyValue != null, "Config property value cannot be null");

        baseConfig.setProperty(configProperty.getName(), configPropertyValue);
    }

    /**
     * Prints the configuration values associated with the LocalConfig. Used for logging/debug.
     * 
     * @param testName
     *            The &lt;test&gt; to which this configuration pertains to.
     */
    public synchronized void printConfigValues(String testName) {
        if (baseConfig.isEmpty()) {
            return;
        }

        StringBuilder builder = new StringBuilder(String.format("Configuration for <%s>: {", testName));
        boolean isFirst = true;

        for (ConfigProperty configProperty : ConfigProperty.values()) {
            if (!isFirst) {
                builder.append(", ");
            }
            String value = getConfigProperty(configProperty);
            builder.append(String.format("(%s: %s)", configProperty, value));
            isFirst = false;
        }
        builder.append("}\n");
        SeLionLogger.getLogger().info(builder.toString());
    }

    /**
     * Returns only the local configuration values associated with the Local Config. Used for logging/reporting.
     * 
     * @return The local configuration property name/values as map.
     */
    public synchronized Map<String, String> getLocalConfigValues() {
        Map<String, String> result = new HashMap<String, String>();
        Iterator<String> iter = baseConfig.getKeys();
        while (iter.hasNext()) {
            String key = iter.next();
            result.put(key, baseConfig.getString(key));
        }
        return result;
    }

    /**
     * Answer if local configuration contains a value for specified property.
     * 
     * @return True if local configuration has value for configProperty.
     */
    public synchronized boolean isLocalValuePresent(ConfigProperty configProperty) {
        checkArgument(configProperty != null, "Config property cannot be null");
        String value = baseConfig.getString(configProperty.getName());
        return value != null;
    }

    private boolean checkNotInGlobalScope(ConfigProperty configProperty) {
        return !configProperty.isGlobalScopeOnly();
    }
}
