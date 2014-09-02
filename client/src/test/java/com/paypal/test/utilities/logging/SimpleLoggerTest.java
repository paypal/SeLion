/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.test.utilities.logging;

import java.io.File;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.test.utilities.logging.SimpleLogger;
import com.paypal.test.utilities.logging.SimpleLoggerEvents;
import com.paypal.test.utilities.logging.SimpleLoggerSettings;
import com.paypal.test.utilities.logging.SimpleLogger.ConsoleLevel;

public class SimpleLoggerTest {

    public File getWorkDir() {
        return new File(Config.getConfigProperty(ConfigProperty.WORK_DIR));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalStateException.class })
    public void testLoggerWithNullsettings() {
        SimpleLoggerSettings settings = null;
        SimpleLogger.getLogger(settings);
    }

    @Test(groups = "unit")
    public void testLogLevels() {
        SimpleLoggerSettings settings = new SimpleLoggerSettings();
        settings.setLogsDir(getWorkDir().getAbsolutePath());

        settings.setUserLevel(Level.OFF);
        Logger logger = SimpleLogger.getLogger(settings);

        assertEquals(logger.getLevel(), settings.getDevLevel(),
                "Log levels should have defaulted to value of dev log level");
    }

    @Test(groups = "unit")
    public void testHooksForLoggerConfiguration() {
        SimpleLoggerSettings settings = new SimpleLoggerSettings();
        settings.setLoggerName("testHooksForLoggerConfiguration");
        settings.setLogsDir(getWorkDir().getAbsolutePath());

        SimpleLoggerEvents events = new SimpleLoggerEvents() {

            public void onPreInitialization(SimpleLogger logger) {
                Filter anonymousFilter = new Filter() {
                    public boolean isLoggable(LogRecord record) {
                        return false;
                    }
                };
                logger.setFilter(anonymousFilter);
            }

            public void onPostInitialization(SimpleLogger logger) {
                for (Handler handler : logger.getHandlers()) {
                    handler.close();
                    logger.removeHandler(handler);
                }
            }

            public void onLog(LogRecord record) {
            }
        };

        settings.setSimpleLoggerEventsImpl(events);
        Logger logger = SimpleLogger.getLogger(settings);
        assertEquals(logger.getHandlers().length, 0,
                "Post hook invocation Failed. All handlers should have been removed.");

        Filter f = logger.getFilter();
        LogRecord record = new LogRecord(Level.INFO, "dummy msg");
        assertFalse(f.isLoggable(record), "Pre-hook filter setting was not reflected");
    }

    @Test(groups = "functional")
    public void testFileCreationBySimpleLogger() {
        SimpleLoggerSettings settings = new SimpleLoggerSettings();
        settings.setLogsDir(getWorkDir().getAbsolutePath());
        settings.setLog2Console(ConsoleLevel.USER);
        settings.setDeveloperLogFileName("test-detailed.log");
        settings.setUserLogFileName("test.log");
        settings.setLoggerName("Tester");

        Logger logger = SimpleLogger.getLogger(settings);
        logger.info("test message");

        File userLogsFile = new File(settings.getLogsDir() + File.separator + settings.getUserLogFileName());
        assertTrue(userLogsFile.exists(), "User logs was not created");
        File devLogsFile = new File(settings.getLogsDir() + File.separator + settings.getDeveloperLogFileName());

        assertTrue(devLogsFile.exists(), "Dev logs was not created");
        assertEquals(logger.getName(), "Tester", "Logger was not created with provided name");
    }
}
