/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.paypal.test.utilities.logging.SimpleLogger.ConsoleLevel;

/**
 * This class represents the collections of settings which are consumed by {@link SimpleLogger}. Use this class to
 * configure your customized logger.<br>
 * <br>
 * With this class you can configure {@link SimpleLogger} to create User and/or Developer log files and at {@link Level}
 * s which best fit your needs. In addition, you can attach a {@link ConsoleHandler} to either the User or Developer
 * log. You can enable rolling logs by specifying a max size you want your log file to grow to and the number
 * of files to save (roll).<br>
 * <br>
 * Default settings are as follows;<br>
 * 
 * <pre>
 *      - Logger name = return value of SimpleLogger.class.getCanonicalName();
 *      - Logger class name = return value of SimpleLogger.class.getSimpleName();
 *      - Logger directory = return value of {@link System#getProperty(String)} with 'user.dir' passed
 *      - User log filename = user.log
 *      - Developer log filename = developer.log
 *      - User log level = {@link Level#FINE}
 *      - Developer log level = {@link Level#ALL}
 *      - Application identifier = ''
 *      - Console log level = {@link ConsoleLevel#OFF}
 *      - SimpleLogger events = none
 *      - Max File Size = 0 (unlimited)
 *      - Max File Count = 1
 * </pre>
 * 
 */
public class SimpleLoggerSettings {
    private String loggerName;
    private String className;
    private String logsDir;
    private String userLogFileName;
    private String developerLogFileName;
    private Level userLevel;
    private Level devLevel;
    private String identifier;
    private ConsoleLevel consoleLogLevel;
    private SimpleLoggerEvents loggerEventsImpl;
    private int maxFileSize;
    private int maxFileCount;
    
    /**
     * Create new {@link SimpleLoggerSettings} with default settings
     */
    public SimpleLoggerSettings() {
        this.loggerName = SimpleLogger.class.getCanonicalName();
        this.className = SimpleLogger.class.getSimpleName();
        this.logsDir = System.getProperty("user.dir");
        this.userLogFileName = "user.log";
        this.developerLogFileName = "developer.log";
        this.userLevel = Level.FINE;
        this.devLevel = Level.ALL;
        this.identifier = null;
        this.consoleLogLevel = ConsoleLevel.OFF;
        this.maxFileSize = 0;
        this.maxFileCount = 1;        
        this.loggerEventsImpl = new SimpleLoggerEvents() {
            public void onPreInitialization(SimpleLogger logger) {
                return;
            }

            public void onPostInitialization(SimpleLogger logger) {
                return;
            }

            public void onLog(LogRecord record) {
                return;
            }
        };
    }

    /**
     * @return the configured logging class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the configured developer log file name for developer level events
     */
    public String getDeveloperLogFileName() {
        return developerLogFileName;
    }

    /**
     * @return the configured log {@link Level} for developer level events
     */
    public Level getDevLevel() {
        return devLevel;
    }

    /**
     * @return the configured logger name
     */
    public String getLoggerName() {
        return loggerName;
    }

    /**
     * @return the configured user log file name for user level events
     */
    public String getUserLogFileName() {
        return userLogFileName;
    }

    /**
     * @return the configured log {@link Level} for user level events
     */
    public Level getUserLevel() {
        return userLevel;
    }

    /**
     * @return the configured application identifier for logging events
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * set the logging class name
     * 
     * @param className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * set the filename for developer log activity
     * 
     * @param filename
     */
    public void setDeveloperLogFileName(String filename) {
        this.developerLogFileName = filename;
    }

    /**
     * set the {@link Level} for developer log activity
     * 
     * @param level
     */
    public void setDevLevel(Level level) {
        this.devLevel = level;
    }

    /**
     * set the logger name
     * 
     * @param name
     */
    public void setLoggerName(String name) {
        this.loggerName = name;
    }

    /**
     * set the filename for user log activity
     * 
     * @param filename
     */
    public void setUserLogFileName(String filename) {
        this.userLogFileName = filename;
    }

    /**
     * set the {@link Level} for user log activity
     * 
     * @param level
     */
    public void setUserLevel(Level level) {
        this.userLevel = level;
    }

    /**
     * set the application identifier for logging activity.
     * 
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the configured directory for storing log files
     */
    public String getLogsDir() {
        return logsDir;
    }

    /**
     * set the directory for storing log files
     * 
     * @param dir
     */
    public void setLogsDir(String dir) {
        this.logsDir = dir;
    }

    /**
     * @return the configured {@link ConsoleLevel} for echoing log activity to stderr via a {@link ConsoleHandler}
     */
    public ConsoleLevel getLog2Console() {
        return consoleLogLevel;
    }

    /**
     * set the {@link ConsoleLevel} that represents the log activity you want to see in stderr via a
     * {@link ConsoleHandler}
     * 
     * @param level
     */
    public void setLog2Console(ConsoleLevel level) {
        this.consoleLogLevel = level;
    }

    /**
     * register your own {@link SimpleLoggerEvents} implementation for receiving {@link SimpleLogger} events
     * 
     * @param impl
     */
    public void setSimpleLoggerEventsImpl(SimpleLoggerEvents impl) {
        this.loggerEventsImpl = impl;
    }

    /**
     * @return the configured {@link SimpleLoggerEvents} implementation to receive {@link SimpleLogger} events
     */
    public SimpleLoggerEvents getSimpleLoggerEventsImpl() {
        return this.loggerEventsImpl;
    }

    /**
     * @return the maxFileSize in MB.  If 0 then there is no max file size limit.
     */
    public int getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * @param maxFileSize
     *      the maxFileSize in MB to set. Use 0 to set no max file size limit.
     */
    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    /**
     * @return the maximum number of files that will be created from rolling logs.
     */
    public int getMaxFileCount() {
        return maxFileCount;
    }

    /**
     * @param maxFileCount
     *      the maximum number of files that will be created from rolling logs.
     */
    public void setMaxFileCount(int maxFileCount) {
        this.maxFileCount = maxFileCount;
    }

}
