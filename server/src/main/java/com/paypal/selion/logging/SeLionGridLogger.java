/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 PayPal                                                                                     |
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

package com.paypal.selion.logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * A wrapper around JUL {@link Logger} which adds additional entering / exiting methods
 */
public class SeLionGridLogger extends Logger {
    private static final String CLASS_NAME = SeLionGridLogger.class.getSimpleName();

    private String loggerName = SeLionGridLogger.class.getName();
    private static Map<String, SeLionGridLogger> loggerMap;
    static {
        loggerMap = new ConcurrentHashMap<String, SeLionGridLogger>();
    }

    private SeLionGridLogger(String name) {
        super(name, null);
        this.loggerName = name;
    }

    public static SeLionGridLogger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    private SeLionGridLogger getLogger() {
        return getLogger(this.loggerName);
    }

    public static synchronized SeLionGridLogger getLogger(String name) {
        // first look for the logger on our internal ConcurrentHashMap
        SeLionGridLogger gridLogger = loggerMap.get(name);
        if (gridLogger == null) {
            gridLogger = new SeLionGridLogger(name);
            LogManager.getLogManager().addLogger(gridLogger);
            // add it to our loggerMap
            loggerMap.put(name, gridLogger);
        } else {
            return gridLogger;
        }

        return gridLogger;
    }

    /**
     * Function entry log convenience method.
     */
    public void entering() {
        if (!getLogger().isLoggable(Level.FINER)) {
            return;
        }
        FrameInfo fi = getLoggingFrame();
        getLogger().entering(fi.className, fi.methodName);
    }

    /**
     * Function entry log convenience method with additional param.
     * 
     * @param object
     *            additional parm
     */
    public void entering(Object object) {
        if (!getLogger().isLoggable(Level.FINER)) {
            return;
        }
        FrameInfo fi = getLoggingFrame();
        getLogger().entering(fi.className, fi.methodName, object);
    }

    /**
     * Function entry log convenience method with additional param.
     * 
     * @param object
     *            additional parms
     */
    public void entering(Object ... object) {
        if (!getLogger().isLoggable(Level.FINER)) {
            return;
        }
        FrameInfo fi = getLoggingFrame();
        getLogger().entering(fi.className, fi.methodName, object);
    }

    /**
     * Function exit log convenience method.
     */
    public void exiting() {
        if (!getLogger().isLoggable(Level.FINER)) {
            return;
        }
        FrameInfo fi = getLoggingFrame();
        getLogger().exiting(fi.className, fi.methodName);
    }

    /**
     * Function exit log convenience method.
     * 
     * @param object
     *            return value
     */
    public void exiting(Object object) {
        if (!getLogger().isLoggable(Level.FINER)) {
            return;
        }
        FrameInfo fi = getLoggingFrame();
        getLogger().exiting(fi.className, fi.methodName, object);
    }

    /**
     * Calculate the logging frame's class name and method name.
     * 
     * @return FrameInfo with className and methodName.
     */
    private static FrameInfo getLoggingFrame() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement loggingFrame = null;
        /*
         * We need to dig through all the frames until we get to a frame that contains this class, then dig through all
         * frames for this class, to finally come to a point where we have the frame for the calling method.
         */
        // Skip stackTrace[0], which is getStackTrace() on Win32 JDK 1.6.
        for (int ix = 1; ix < stackTrace.length; ix++) {
            loggingFrame = stackTrace[ix];
            if (loggingFrame.getClassName().contains(CLASS_NAME)) {
                for (int iy = ix; iy < stackTrace.length; iy++) {
                    loggingFrame = stackTrace[iy];
                    if (!loggingFrame.getClassName().contains(CLASS_NAME)) {
                        break;
                    }
                }
                break;
            }
        }
        return new FrameInfo(loggingFrame.getClassName(), loggingFrame.getMethodName());
    }

    private static class FrameInfo {
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
}
