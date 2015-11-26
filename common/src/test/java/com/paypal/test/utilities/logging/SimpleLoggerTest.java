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

package com.paypal.test.utilities.logging;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.paypal.test.utilities.logging.SimpleLogger.ConsoleLevel;

public class SimpleLoggerTest {

    public File getWorkDir() {
        File f = new File(System.getProperty("user.dir") + "/target/test-output/");
        f.mkdirs();
        return f;
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
        settings.setLogsDir(getWorkDir().getAbsolutePath());

        SimpleLoggerEvents events = new SimpleLoggerEvents() {

            @Override
            public void onPreInitialization(SimpleLogger logger) {
                Filter anonymousFilter = new Filter() {
                    @Override
                    public boolean isLoggable(LogRecord record) {
                        return false;
                    }
                };
                logger.setFilter(anonymousFilter);
            }

            @Override
            public void onPostInitialization(SimpleLogger logger) {
                for (Handler handler : logger.getHandlers()) {
                    handler.close();
                    logger.removeHandler(handler);
                }
            }

            @Override
            public void onLog(LogRecord record) {
                // Nothing to Log
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

    @Test(groups = "functional")
    public void testRollingLog() throws InterruptedException {
        SimpleLoggerSettings settings = new SimpleLoggerSettings();
        settings.setLogsDir(getWorkDir().getAbsolutePath());
        settings.setLog2Console(ConsoleLevel.OFF);
        settings.setDeveloperLogFileName("rolling-detailed.log");
        settings.setUserLogFileName("rolling.log");
        settings.setLoggerName("Rolling");
        settings.setMaxFileSize(2);
        settings.setMaxFileCount(3);
        Logger logger = SimpleLogger.getLogger(settings);

        for (int i = 0; i < 75000; i++) {
            logger.log(Level.FINE, " My Test Message. " + i);// 87 bytes approximately
        }

        // Verify that 3rd log file exist and their file size is no larger than 2 MB
        File thirdLogFile = new File(getWorkDir().getAbsolutePath() + File.separator + "rolling-detailed.log.2");
        assertTrue(thirdLogFile.exists(), "Rolling log file does not exist.");
        assertTrue(thirdLogFile.length() > 0, "Rolling log file is empty.");
        // allow for some tolerance since the last log write will make it exceed exactly 2000000 bytes
        assertTrue(thirdLogFile.length() < 2001000,
                "The log file length must be less than 2MB.  Actual log length is: " + thirdLogFile.length());

        // Better not be a 4th log file since max file count was 3
        File fourthLogFile = new File(getWorkDir().getAbsolutePath() + File.separator + "rolling-detailed.log.3");
        assertFalse(fourthLogFile.exists(),
                "4th rolling log file 'rolling-detailed.log.3' should not exist but has been detected.");
    }
}
