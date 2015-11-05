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

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.logging.LogFactory;

/**
 * The SeLion Logger configuration and Logger related properties.
 * 
 * These configuration options can be configured via SELION VM properties / environment variables. For example to
 * specify the LOGS_DIR configuration value define the VM parameter -DSELION_LOGS_DIR=logFiles<br>
 * 
 * When not specified then the default value below will be used.<br>
 */
@ThreadSafe
public final class LoggerConfig {

    private LoggerConfig() {
        // Utility class. So hide the constructor
    }

    /**
     * Enum which contain the SeLion logger properties. To be used with {@link LoggerConfig}.
     */
    public static enum LoggerProperties {
        /**
         * Should we log to the console (dev, user, off/no/false).<br>
         * Default is set to <b>false</b>
         */
        LOG_TO_CONSOLE("log.console", "false"),

        /**
         * The amount of logging done by SeLion that the user wants to see.<br>
         * Default is set to <b>FINE</b>
         */
        LOG_LEVEL_USER("log.userLevel", "FINE"),

        /**
         * The amount of logging done by SeLion that the developer wants to see.<br>
         * Default is set to <b>ALL</b>
         */
        LOG_LEVEL_DEV("log.devLevel", "ALL"),

        /**
         * The logs directory of SeLion
         */
        LOGS_DIR("logsDir", "selionFiles/selionLogs"),

        /**
         * The maximum size in MB for the log file. Default is 0 denoting unlimited
         */
        LOGS_MAX_SIZE("logsMaxSize", "0"),

        /**
         * The maximum limit to the number of files to create for storing logs once the current log file reaches
         * {@link #LOGS_MAX_SIZE}
         */
        LOGS_MAX_FILE_COUNT("logsMaxFileCount", "1");

        private final String propertyName;
        private final String defaultValue;

        private LoggerProperties(String configName, String defaultValue) {
            this.propertyName = configName;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return this.propertyName;
        }

        public String getDefaultValue() {
            return this.defaultValue;
        }
    }

    private static volatile BaseConfiguration config;

    private static BaseConfiguration getConfig() {
        if (config != null) {
            return config;
        }
        initConfig();
        return config;
    }

    private static synchronized void initConfig() {

        final boolean permitClogging = Boolean.valueOf(System.getProperty("SELION_PERMIT_CLOGGING", "false"))
                .booleanValue();

        if (!permitClogging) {
            LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
                    "org.apache.commons.logging.impl.NoOpLog");
        }

        config = new BaseConfiguration();

        // don't auto throw on missing property
        config.setThrowExceptionOnMissing(false);

        /*
         * Setup the defaults
         */
        for (LoggerProperties prop : LoggerProperties.values()) {
            config.setProperty(prop.getName(), prop.getDefaultValue());
        }

        /*
         * Load in environment variables / System Properties (if defined)
         */
        for (LoggerProperties prop : LoggerProperties.values()) {
            String value = System.getenv("SELION_" + prop.name());
            if ((value != null) && (!value.equals(""))) {
                config.setProperty(prop.getName(), value);
            }
            // Now load system properties variables (if defined).
            value = System.getProperty("SELION_" + prop.name());
            if ((value != null) && (!value.equals(""))) {
                config.setProperty(prop.getName(), value);
            }
        }
    }

    /**
     * Returns a logger configuration property <b>String</b> value based off the {@link LoggerProperties}
     * 
     * @param property
     *            String The Property Name
     * @return The configuration property <b>String</b> values
     */
    public static String getConfigProperty(LoggerProperties property) {
        return LoggerConfig.getConfig().getString(property.getName());
    }
}