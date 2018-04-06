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

package ${package}.logging;

import com.paypal.selion.configuration.LoggerConfig;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;
import com.paypal.test.utilities.logging.SimpleLoggerSettings;

/**
 * An example logger which leverages SeLion's {@link SimpleLogger}. 
 */
public final class AppLogger {
    private static final String LOGGER_NAME = AppLogger.class.getCanonicalName();
    private static final String CLASS_NAME = AppLogger.class.getSimpleName();
    private static SimpleLogger appBaseLogger = null;

    private AppLogger() {
        // defeat all instantiation
    }

    public static synchronized SimpleLogger getLogger() {
        if (appBaseLogger == null) {
            appBaseLogger = SimpleLogger.getLogger(getDefaultLoggerSettings());
        }
        return appBaseLogger;
    }

    private static SimpleLoggerSettings getDefaultLoggerSettings(){
        SimpleLoggerSettings settings = new SimpleLoggerSettings();
        settings.setLoggerName(LOGGER_NAME);
        settings.setLogsDir(LoggerConfig.getConfigProperty(LoggerConfig.LoggerProperties.LOGS_DIR));
        settings.setClassName(CLASS_NAME);
        settings.setUserLogFileName("${artifactId}.log");
        settings.setDeveloperLogFileName("${artifactId}-detailed.log");
        settings.setIdentifier("${version}");
        settings.setDevLevel(SimpleLogger.string2Level(LoggerConfig.getConfigProperty(LoggerConfig.LoggerProperties.LOG_LEVEL_DEV)));
        settings.setUserLevel(SimpleLogger.string2Level(LoggerConfig.getConfigProperty(LoggerConfig.LoggerProperties.LOG_LEVEL_USER)));
        settings.setSimpleLoggerEventsImpl(new SeLionLogger.SeLionLoggerEventsImpl());
        String log2Console = LoggerConfig.getConfigProperty(LoggerConfig.LoggerProperties.LOG_TO_CONSOLE);
        if (log2Console.equalsIgnoreCase("dev")) {
            settings.setLog2Console(SimpleLogger.ConsoleLevel.DEV);
        }
        if (log2Console.equalsIgnoreCase("user")) {
            settings.setLog2Console(SimpleLogger.ConsoleLevel.USER);
        }
        return settings;
    }
}
