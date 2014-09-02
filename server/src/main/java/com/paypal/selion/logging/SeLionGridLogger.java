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

package com.paypal.selion.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.paypal.selion.utils.JarSpawner;

/**
 * A Simple utils logger that will be used by {@link JarSpawner}
 * 
 */
public class SeLionGridLogger {
    private static final String CLASS_NAME = SeLionGridLogger.class.getSimpleName();
    public static final String CONSOLE_LOGGER_NAME = SeLionGridLogger.class.getPackage().toString();
    private static Logger logger = null;

    public static synchronized Logger getLogger() {
        if (logger != null) {
            return logger;
        }
        logger = Logger.getLogger(CONSOLE_LOGGER_NAME);
        Handler handler = null;
        try {
            handler = new FileHandler("jar-spawner.log", true);
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.FINE);
            logger.addHandler(handler);
            return logger;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function entry log convenience method.
     */
    public static void entering() {
        if (!getLogger().isLoggable(Level.FINER)) {
            return;
        }
        FrameInfo fi = getLoggingFrame();
        getLogger().entering(fi.className, fi.methodName);
    }

    /**
     * Function entry log convenience method with additional parm.
     * 
     * @param object
     *            additional parm
     */
    public static void entering(Object object) {
        if (!getLogger().isLoggable(Level.FINER)) {
            return;
        }
        FrameInfo fi = getLoggingFrame();
        getLogger().entering(fi.className, fi.methodName, object);
    }

    /**
     * Function exit log convenience method.
     */
    public static void exiting() {
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
    public static void exiting(Object object) {
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
        private String className;
        private String methodName;

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
