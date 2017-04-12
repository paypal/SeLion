/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2017 PayPal                                                                                          |
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

package com.paypal.selion.plugins;

import com.paypal.test.utilities.logging.SimpleLogger;
import com.paypal.test.utilities.logging.SimpleLoggerEvents;
import com.paypal.test.utilities.logging.SimpleLoggerSettings;
import com.paypal.test.utilities.logging.SimpleLogger.ConsoleLevel;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class CodeGeneratorSimpleLogger implements CodeGeneratorLogger {

    private static Logger logger = SimpleLogger.getLogger(new CodeGeneratorSimpleLoggerSettings());

    private static class CodeGeneratorSimpleLoggerSettings extends SimpleLoggerSettings {
        public CodeGeneratorSimpleLoggerSettings() {
            super();
            setLoggerName(CodeGenerator.class.getCanonicalName());
            setClassName(CodeGenerator.class.getSimpleName());
            setUserLogFileName("codegenerator.log");
            setUserLevel(Level.ALL);
            setDevLevel(Level.OFF);
            setIdentifier(CodeGenerator.class.getSimpleName());
            setLog2Console(ConsoleLevel.USER);
            setSimpleLoggerEventsImpl(new SimpleLoggerEvents() {
                @Override
                public void onPreInitialization(SimpleLogger logger) {
                    // nothing to do
                }

                @Override
                public void onPostInitialization(SimpleLogger logger) {
                    logger.setUseParentHandlers(false);
                }

                @Override
                public void onLog(LogRecord record) {
                    // nothing to do
                }
            });
        }
    }

    @Override
    public void debug(String msg) {
        logger.fine(msg);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void debug(Throwable e) {
        logger.log(Level.FINE, e.getMessage(), e);
    }

}
