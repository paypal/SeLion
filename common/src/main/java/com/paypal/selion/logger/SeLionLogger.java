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

package com.paypal.selion.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import com.paypal.selion.SeLionBuildInfo;
import com.paypal.selion.SeLionBuildInfo.SeLionBuildProperty;
import com.paypal.selion.configuration.LoggerConfig;
import com.paypal.selion.configuration.LoggerConfig.LoggerProperties;
import com.paypal.test.utilities.logging.SimpleLogger;
import com.paypal.test.utilities.logging.SimpleLoggerEvents;
import com.paypal.test.utilities.logging.SimpleLoggerSettings;
import com.paypal.test.utilities.logging.SimpleLogger.ConsoleLevel;

/**
 * The {@link SimpleLogger} for SeLion.
 */
public final class SeLionLogger {
    private static final String SELION_LOGGER_NAME = "com.paypal.selion";
    private static final String CLASS_NAME = SeLionLogger.class.getSimpleName();

    private SeLionLogger() {
        // defeat all instantiation
    }

    private static SimpleLogger baseLogger;

    /**
     * Establish the {@link SimpleLoggerSettings} for {@link SeLionLogger}
     */
    public static class SeLionLoggerSettings extends SimpleLoggerSettings {
        public SeLionLoggerSettings() {
            super();

            this.setLoggerName(SELION_LOGGER_NAME);
            this.setLogsDir(LoggerConfig.getConfigProperty(LoggerConfig.LoggerProperties.LOGS_DIR));
            this.setClassName(CLASS_NAME);
            this.setUserLogFileName("selion.log");
            this.setDeveloperLogFileName("selion-detailed.log");
            this.setDevLevel(SimpleLogger.string2Level(LoggerConfig
                    .getConfigProperty(LoggerConfig.LoggerProperties.LOG_LEVEL_DEV)));
            this.setUserLevel(SimpleLogger.string2Level(LoggerConfig
                    .getConfigProperty(LoggerConfig.LoggerProperties.LOG_LEVEL_USER)));
            this.setSimpleLoggerEventsImpl(new SeLionLoggerEventsImpl());
            this.setIdentifier(SeLionBuildInfo.getBuildValue(SeLionBuildProperty.SELION_VERSION));
            this.setMaxFileSize(Integer.parseInt(LoggerConfig.getConfigProperty(LoggerProperties.LOGS_MAX_SIZE)));
            this.setMaxFileCount(Integer.parseInt(LoggerConfig.getConfigProperty(LoggerProperties.LOGS_MAX_FILE_COUNT)));
            String log2Console = LoggerConfig.getConfigProperty(LoggerConfig.LoggerProperties.LOG_TO_CONSOLE);
            if (log2Console.equalsIgnoreCase("dev")) {
                this.setLog2Console(ConsoleLevel.DEV);
            }
            if (log2Console.equalsIgnoreCase("user")) {
                this.setLog2Console(ConsoleLevel.USER);
            }
        }
    }

    /**
     * Establish the {@link SimpleLoggerEvents} for {@link SeLionLogger}
     */
    public static class SeLionLoggerEventsImpl implements SimpleLoggerEvents {
        @Override
        public void onPostInitialization(SimpleLogger logger) {
            // install our own SingleLineFormatter for the RootLogger's
            // ConsoleHandler
            Handler[] handlers = SimpleLogger.getLogger("").getHandlers();
            for (Handler handler : handlers) {
                // proceed only if the RootLogger has a ConsoleHandler with a
                // SimpleFormatter
                if ((handler instanceof ConsoleHandler) && (handler.getFormatter() instanceof SimpleFormatter)) {
                    handler.setFormatter(logger.new SingleLineFormatter(null));
                }
            }
        }

        @Override
        public void onPreInitialization(SimpleLogger logger) {
            // nothing to do here
        }

        @Override
        public void onLog(LogRecord record) {
            // nothing to do here
        }
    }

    /**
     * @return the {@link SimpleLogger} configured for SeLion.
     */
    public static synchronized SimpleLogger getLogger() {
        if (baseLogger == null) {
            baseLogger = SimpleLogger.getLogger(new SeLionLoggerSettings());
        }
        return baseLogger;
    }
}
