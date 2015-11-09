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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A generic, reusable, and configurable logger for Java applications.<br>
 * <br>
 * This logger allows applications to have independently configured user and developer event level logging. In addition,
 * it can be configured to echo either developer or user level logs to stderr.<br>
 * <br>
 * To use this logger you would typically define your own {@link SimpleLoggerSettings} and optionally your own
 * {@link SimpleLoggerEvents}. To initialize the logger call the static method
 * {@link SimpleLogger#getLogger(SimpleLoggerSettings)} (see code snippet 1 below).<br>
 * <br>
 * This logger will creates a {@link FileHandler} with a {@link SimpleFormatter} for all developer level logging, a
 * {@link FileHandler} with a {@link SingleLineFormatter} for all user level logging, and a {@link ConsoleHandler} for
 * either user or developer log activity when {@link SimpleLoggerSettings#getLog2Console()} != {@link ConsoleLevel#OFF}.
 * To override this behavior you can close, remove, and/or add new handlers in your implementation of
 * {@link SimpleLoggerEvents#onPostInitialization(SimpleLogger)} via the methods {@link SimpleLogger#getHandlers()},
 * {@link Handler#close()}, {@link SimpleLogger#removeHandler(Handler)}, and {@link SimpleLogger#addHandler(Handler)}
 * (see code snippet 2 below).<br>
 * <br>
 * If you use the default {@link FileHandler}, you can enable rolling logs by specifying a max size you want your log
 * file to grow to and the number of files to save(roll) in {@link SimpleLoggerSettings}<br>
 * <br>
 *
 * <b>For Example:</b><br>
 * <i>Snippet 1</i>
 * 
 * <pre>
 *        public class SingletonAppLogger {
 *            private static SimpleLogger logger = null;
 *            private static final String LOGGER_NAME = SingletonAppLogger.class.getCanonicalName();
 *            private static final String CLASS_NAME = SingletonAppLogger.class.getSimpleName();
 * 
 *            public static SimpleLogger getLogger() {
 *                if (logger == null) {
 *                    logger = SimpleLogger.getLogger(new SingletonAppLoggerSettings());
 *                }
 *                return logger;
 *            }
 * 
 *            private static class SingletonAppLoggerEvents implements SimpleLoggerEvents {
 *                public void onPreInitialization(SimpleLogger logger) {
 *                    //TODO :: any pre initialization customization you want to define goes here
 *                }
 * 
 *                public void onPostInitialization(SimpleLogger logger) {
 *                    //TODO :: any post initialization customization you want to define goes here
 *                }
 * 
 *                public void onLog(LogRecord record) {
 *                    //TODO :: Use this method if you want to peek at the log record and take
 *                    // some sort of action before the log event is forwarded to all registered log handlers
 *                }
 *            }
 * 
 *            private static class SingletonAppLoggerSettings extends SimpleLoggerSettings {
 *                //Create a logger for this application that directs user level output to app.log and 
 *                //developer level output to app-detailed.log. Also, register our SingletonAppLoggerEvents with 
 *                //this logger. Utilize the defaults in SimpleLoggerSettings for all other settings.
 *                public SingletonAppLoggerSettings() {
 *                        super();
 *                        this.setLoggerName(LOGGER_NAME);
 *                        this.setClassName(CLASS_NAME);
 *                        this.setUserLogFileName("app.log");
 *                        this.setDeveloperLogFileName("app-detailed.log");
 *                        this.setSimpleLoggerEventsImpl(new SingletonAppLoggerEvents());
 *                    }
 *            }
 *
 * </pre>
 *
 * <i>Snippet 2</i>
 *
 * <pre>
 *      ......
 *                public void onPostInitialization(SimpleLogger logger) {
 *                    //Swap out the SimpleFormatter on the developer log FileHandler with an XMLFormatter
 *                    Handler[] handlers = logger.getHandlers();
 *                    if (handlers != null) {
 *                        for (Handler element : handlers) {
 *                            if ((element.getFormatter().getClass().getSimpleName().matches("SimpleFormatter")) && 
 *                                (element.getClass().getSimpleName().matches("FileHandler"))  && 
 *                                (element.getLevel() == logger.getLoggerSettings().getDevLevel())) {
 *                                try {
 *                                    element.setFormatter(new XMLFormatter());
 *                                } catch (SecurityException e) {
 *                                    e.printStackTrace();
 *                                }
 *                            }
 *                        }
 *                    }
 *                }
 *     ........
 * </pre>
 *
 */
public final class SimpleLogger extends Logger implements Closeable {

    private static final String LOGGER_NAME = SimpleLogger.class.getCanonicalName();
    private static final String CLASS_NAME = SimpleLogger.class.getSimpleName();

    private SimpleLoggerSettings loggerSettings;
    private static volatile Map<String, SimpleLogger> simpleLoggerMap;
    static {
        simpleLoggerMap = new ConcurrentHashMap<String, SimpleLogger>();
    }

    /**
     * Creates a new logger with default {@link SimpleLoggerSettings}.
     */
    private SimpleLogger() {
        super(LOGGER_NAME, null);
        this.loggerSettings = new SimpleLoggerSettings();
        // We need to ensure we close everything properly. So lets add a shut down hook
        // of doing this. Doing this as part of finalize() seems to be triggering sonar
        // warnings.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public synchronized void start() {
                close();
            }
        });
    }

    /**
     * Creates a new logger with your {@link SimpleLoggerSettings}
     *
     * @param settings
     *            - the configured {@link SimpleLoggerSettings} for your logger
     */
    private SimpleLogger(SimpleLoggerSettings settings) {
        super(settings.getLoggerName(), null);
        this.loggerSettings = settings;
    }

    /**
     * Get the configured {@link SimpleLogger} for this instance.
     *
     * @return the {@link SimpleLogger} configured
     */
    private synchronized SimpleLogger getLogger() {
        if (this.loggerSettings == null) {
            return getLogger(new SimpleLoggerSettings());
        }
        return getLogger(this.loggerSettings);
    }

    /**
     * Find or create a new logger of type {@link SimpleLogger} utilizing the settings specified via
     * {@link SimpleLoggerSettings}.<br/>
     * <br/>
     * If a new logger is created, log levels will be configured based on the {@link SimpleLoggerSettings} configuration
     * and it will also be configured to send logging output to parent handlers. Lastly, it will be registered in the
     * {@link LogManager} global namespace.
     *
     * @param logSettings
     *            - the {@link SimpleLoggerSettings} to apply
     * @return the {@link SimpleLogger} instance
     */
    public static synchronized SimpleLogger getLogger(SimpleLoggerSettings logSettings) {
        if (logSettings == null) {
            throw new IllegalStateException("Logger settings cannot be null.");
        }

        // first look for the logger on our internal ConcurrentHashMap
        SimpleLogger simpleLogger = simpleLoggerMap.get(logSettings.getLoggerName());
        if (simpleLogger == null) {
            simpleLogger = new SimpleLogger(logSettings);
            LogManager.getLogManager().addLogger(simpleLogger);
            // add it to our simpleLoggerMap
            simpleLoggerMap.put(logSettings.getLoggerName(), simpleLogger);
        } else {
            return simpleLogger;
        }

        setupLogger(logSettings, simpleLogger);

        return simpleLogger;
    }

    private static void setupLogs(SimpleLoggerSettings logSettings, SimpleLogger logger) throws IOException {
        File logsDir = new File(logSettings.getLogsDir());
        File userLog = new File(logsDir.getAbsoluteFile() + File.separator + logSettings.getUserLogFileName());
        File devLog = new File(logsDir.getAbsoluteFile() + File.separator + logSettings.getDeveloperLogFileName());
        Level userLevel = logSettings.getUserLevel();
        Level devLevel = logSettings.getDevLevel();
        // ensure log directory and log files exist when level is not OFF
        if ((devLevel != Level.OFF) || (userLevel != Level.OFF)) {
            logsDir.mkdirs();
        }

        // add corresponding file handlers
        if (userLevel != Level.OFF) {
            logger.addFileHandler(userLog, userLevel, logger.new SingleLineFormatter(logSettings.getIdentifier()),
                    logSettings.getMaxFileSize(), logSettings.getMaxFileCount());
        }
        if (devLevel != Level.OFF) {
            logger.addFileHandler(devLog, devLevel, new SimpleFormatter(), logSettings.getMaxFileSize(),
                    logSettings.getMaxFileCount());
        }
    }

    private static void setupConsoleHandler(SimpleLoggerSettings logSettings, SimpleLogger logger) {
        Level userLevel = logSettings.getUserLevel();
        Level devLevel = logSettings.getDevLevel();
        if (logSettings.getLog2Console() == ConsoleLevel.DEV) {
            // setup a "dev" level console handler
            logger.addConsoleHandler(devLevel, new SimpleFormatter());
        } else if (logSettings.getLog2Console() == ConsoleLevel.USER) {
            // setup a "user" level console handler
            logger.addConsoleHandler(userLevel, logger.new SingleLineFormatter(logSettings.getIdentifier()));
        }

    }

    /**
     * Called to setup the {@link SimpleLogger} based on specified {@link SimpleLoggerSettings}
     *
     * @param logSettings
     * @param logger
     */
    private static void setupLogger(SimpleLoggerSettings logSettings, SimpleLogger logger) {
        logger.loggerSettings = logSettings;

        Level userLevel = logSettings.getUserLevel();
        Level devLevel = logSettings.getDevLevel();

        try {
            // call any pre initialization hooks that may be defined
            logSettings.getSimpleLoggerEventsImpl().onPreInitialization(logger);

            setupLogs(logSettings, logger);
            setupConsoleHandler(logSettings, logger);

            // set the overall logger level
            Level overallLevel = logger.calculateMax(userLevel, devLevel);
            logger.setLevel(overallLevel);
            // set the parent handlers notification default
            logger.setUseParentHandlers(true);
        } catch (IOException e) {
            System.err.println("Failed to create SimpleLogger for " + logSettings.getLoggerName());
            e.printStackTrace();
        } catch (SecurityException e) {
            System.err.println("An error occured while creating SimpleLogger for " + logSettings.getLoggerName());
            e.printStackTrace();
        }

        // call any post initialization hook that may be defined
        logSettings.getSimpleLoggerEventsImpl().onPostInitialization(logger);
    }

    /**
     * Turns level string into {@link Level}
     *
     * @return The log level
     */
    public static Level string2Level(String logLevelString) {
        Level level = Level.ALL;
        if (logLevelString.equalsIgnoreCase("ALL")) {
            level = Level.ALL;
        } else if (logLevelString.equalsIgnoreCase("CONFIG")) {
            level = Level.CONFIG;
        } else if (logLevelString.equalsIgnoreCase("INFO")) {
            level = Level.INFO;
        } else if (logLevelString.equalsIgnoreCase("OFF")) {
            level = Level.OFF;
        } else if (logLevelString.equalsIgnoreCase("FINE")) {
            level = Level.FINE;
        } else if (logLevelString.equalsIgnoreCase("FINER")) {
            level = Level.FINER;
        } else if (logLevelString.equalsIgnoreCase("FINEST")) {
            level = Level.FINEST;
        } else if (logLevelString.equalsIgnoreCase("SEVERE")) {
            level = Level.SEVERE;
        } else if (logLevelString.equalsIgnoreCase("WARNING")) {
            level = Level.WARNING;
        }
        return level;
    }

    /**
     * @return the configured {@link SimpleLoggerSettings} for this logger
     */
    public SimpleLoggerSettings getLoggerSettings() {
        return this.loggerSettings;
    }

    /**
     * Function entry log convenience method.
     */
    public void entering() {
        if (this.isLoggable(Level.FINER)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().entering(fi.className, fi.methodName);
        }
    }

    /**
     * Function entry log convenience method with additional parm.
     *
     * @param param
     *            additional param
     */
    public void entering(Object param) {
        if (this.isLoggable(Level.FINER)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().entering(fi.className, fi.methodName, param);
        }
    }

    /**
     * Function entry log convenience method (varargs-style).
     *
     * @param params
     *            varargs
     */
    public void entering(Object... params) {
        if (this.isLoggable(Level.FINER)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().entering(fi.className, fi.methodName, params);
        }
    }

    /**
     * Function exit log convenience method.
     */
    public void exiting() {
        if (this.isLoggable(Level.FINER)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().exiting(fi.className, fi.methodName);
        }
    }

    /**
     * Function exit log convenience method.
     *
     * @param param
     *            return value
     */
    public void exiting(Object param) {
        if (this.isLoggable(Level.FINER)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().exiting(fi.className, fi.methodName, param);
        }
    }

    /**
     * Function exit log convenience method (varargs-style).
     *
     * @param params
     *            return values
     */
    public void exiting(Object... params) {
        if (this.isLoggable(Level.FINER)) {
            FrameInfo fi = getLoggingFrame();
            String msg = "RETURN";
            if (null != params) {
                StringBuilder msgBuffer = new StringBuilder("RETURN");
                for (int i = 0; i < params.length; i++) {
                    msgBuffer.append(" {" + i + "}");
                }
                msg = msgBuffer.toString();
            }
            LogRecord record = new LogRecord(Level.FINER, msg);
            record.setLoggerName(this.getName());
            record.setSourceClassName(fi.className);
            record.setSourceMethodName(fi.methodName);
            record.setParameters(params);
            log(record);
        }
    }

    @Override
    public void log(LogRecord record) {
        if (this.isLoggable(record.getLevel())) {
            // notify and custom logger event handlers defined
            this.loggerSettings.getSimpleLoggerEventsImpl().onLog(record);
            // deal with this record normally
            super.log(record);
        }
    }

    @Override
    public void log(Level level, String msg) {
        if (this.isLoggable(level)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().logp(level, fi.className, fi.methodName, msg);
        }
    }

    @Override
    public void log(Level level, String msg, Object param) {
        if (this.isLoggable(level)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().logp(level, fi.className, fi.methodName, msg, param);
        }
    }

    @Override
    public void log(Level level, String msg, Object[] params) {
        if (this.isLoggable(level)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().logp(level, fi.className, fi.methodName, msg, params);
        }
    }

    @Override
    public void log(Level level, String msg, Throwable thrown) {
        if (this.isLoggable(level)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().logp(level, fi.className, fi.methodName, msg, thrown);
        }
    }

    @Override
    public void fine(String msg) {
        if (this.isLoggable(Level.FINE)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().logp(Level.FINE, fi.className, fi.methodName, msg);
        }
    }

    @Override
    public void finer(String msg) {
        if (this.isLoggable(Level.FINER)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().logp(Level.FINER, fi.className, fi.methodName, msg);
        }
    }

    @Override
    public void finest(String msg) {
        if (this.isLoggable(Level.FINEST)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().logp(Level.FINEST, fi.className, fi.methodName, msg);
        }
    }

    @Override
    public void config(String msg) {
        if (this.isLoggable(Level.CONFIG)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().logp(Level.CONFIG, fi.className, fi.methodName, msg);
        }
    }

    @Override
    public void info(String msg) {
        if (this.isLoggable(Level.INFO)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().logp(Level.INFO, fi.className, fi.methodName, msg);
        }
    }

    @Override
    public void severe(String msg) {
        if (this.isLoggable(Level.SEVERE)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().logp(Level.SEVERE, fi.className, fi.methodName, msg);
        }
    }

    @Override
    public void warning(String msg) {
        if (this.isLoggable(Level.WARNING)) {
            FrameInfo fi = getLoggingFrame();
            getLogger().logp(Level.WARNING, fi.className, fi.methodName, msg);
        }
    }

    /**
     * Add a console handler with the appropriate log level and formatter
     */
    private void addConsoleHandler(Level logLevel, Formatter formatter) {
        Handler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        handler.setLevel(logLevel);
        getLogger().addHandler(handler);
    }

    /**
     * Add a file handler with the appropriate log level and formatter and filename
     */
    private void addFileHandler(File logFile, Level logLevel, Formatter formatter, int maxFileSizeMB, int maxFileCount)
            throws IOException {
        Handler handler = new FileHandler(logFile.getAbsolutePath(), maxFileSizeMB * 1000000, maxFileCount, true);
        handler.setFormatter(formatter);
        handler.setLevel(logLevel);
        getLogger().addHandler(handler);
    }

    /**
     * Figure out highest log level which is actually the lowest in {@link Level}'s backwards logic.
     */
    private Level calculateMax(Level userLevel, Level devLevel) {
        return Level.parse(Integer.toString(Math.min(userLevel.intValue(), devLevel.intValue())));
    }

    /**
     * Calculate the logging frame's class name and method name.
     *
     * @return FrameInfo with className and methodName.
     */
    private FrameInfo getLoggingFrame() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement loggingFrame = stackTrace[0];
        String loggingFrameClassName;
        /*
         * We need to dig through all the frames until we get to a frame that contains this class, then dig through all
         * frames for this class, to finally come to a point where we have the frame for the calling method.
         */
        // Skip stackTrace[0], which is getStackTrace() on Win32 JDK 1.6.
        for (int ix = 1; ix < stackTrace.length; ix++) {
            loggingFrame = stackTrace[ix];
            loggingFrameClassName = loggingFrame.getClassName();
            if (loggingFrameClassName.substring(loggingFrameClassName.lastIndexOf('.') + 1).equals(CLASS_NAME)) {
                for (int iy = ix; iy < stackTrace.length; iy++) {
                    loggingFrame = stackTrace[iy];
                    loggingFrameClassName = loggingFrame.getClassName();
                    if (!loggingFrameClassName.substring(loggingFrameClassName.lastIndexOf('.') + 1).equals(CLASS_NAME)) {

                        // TODO :: extract method refactoring and possibly
                        // recursion would be useful here.
                        // we need to keep digging
                        if (loggingFrameClassName.substring(loggingFrameClassName.lastIndexOf('.') + 1).equals(
                                this.loggerSettings.getClassName())) {
                            for (int iz = iy; iz < stackTrace.length; iz++) {
                                loggingFrame = stackTrace[iz];
                                if (!loggingFrameClassName.substring(loggingFrameClassName.lastIndexOf('.') + 1)
                                        .equals(this.loggerSettings.getClassName())) {
                                    break;
                                }
                            }
                        }
                        // good enough, identify this frame as the calling
                        // method
                        break;
                    }
                }
                break;
            }
        }
        return new FrameInfo(loggingFrame.getClassName(), loggingFrame.getMethodName());
    }

    /**
     * Closes all open log handlers. Internal exceptions ignored, never thrown.
     */
    public synchronized void close() {
        this.setLevel(Level.INFO);
        Handler[] handlers = this.getHandlers();
        if (handlers != null) {
            for (Handler element : handlers) {
                // close all handlers.
                // When unknown exceptions happen, ignore them and go on
                try {
                    element.close();
                } catch (Exception e) {
                    // ignored
                }
            }
        }
        simpleLoggerMap.remove(this.loggerSettings.getLoggerName());
    }

    /**
     * Used to encapsulate class and method info from the stack trace
     */
    private final class FrameInfo {
        private final String className;
        private final String methodName;

        private FrameInfo(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }

        @Override
        public String toString() {
            return this.className + "." + this.methodName;
        }
    }

    /**
     * This enum class represents the three options for console logging.
     */
    public enum ConsoleLevel {
        /**
         * developer level logs displayed in console
         */
        DEV("dev"),
        /**
         * user and developer logs not displayed in console
         */
        OFF("false"),
        /**
         * user level logs displayed in console
         */
        USER("user");
        private String setting;

        private ConsoleLevel(String setting) {
            this.setting = setting;
        }

        public String toString() {
            return this.setting;
        }
    }

    /**
     * Simple formatter class to produce terse, single line output
     */
    public final class SingleLineFormatter extends Formatter {
        private final String LINE_SEPARATOR = System.getProperty("line.separator");
        private final Format df = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss.SSS");
        private final String identifier;

        public SingleLineFormatter(String identifier) {
            super();
            this.identifier = identifier;
        }

        /**
         * Synchronized to protect the Format instance from concurrent access...
         *
         * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
         */
        @Override
        public synchronized String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();

            sb.append(df.format(new Date(record.getMillis())));
            sb.append(",[");
            sb.append(record.getThreadID());
            sb.append("] ");

            if (this.identifier != null) {
                sb.append(this.identifier);
            } else {
                sb.append(record.getLoggerName());
            }

            sb.append(" ");
            sb.append(record.getLevel().getLocalizedName());
            sb.append(" ");
            sb.append(formatMessage(record));
            sb.append(LINE_SEPARATOR);

            if (record.getThrown() != null) {
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    pw.close();
                    sb.append(sw.toString());
                } catch (Exception ignore) {
                }
            }

            return sb.toString();
        }
    }
}
