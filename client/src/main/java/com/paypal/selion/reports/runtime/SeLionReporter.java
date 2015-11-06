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

package com.paypal.selion.reports.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.internal.reports.model.BaseLog;
import com.paypal.selion.internal.reports.model.PageContents;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.services.LogAction;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A TestNG compatible message logger. Use this class to log messages to the report output and associate them with a
 * {@link Test}, {@link WebTest} and/or {@link MobileTest}
 */
public final class SeLionReporter {
    private static final SimpleLogger logger = SeLionLogger.getLogger();
    private volatile static List<LogAction> actionList = new ArrayList<LogAction>();

    private static String output;
    private static DataSaver saver;
    private final String baseFileName = UUID.randomUUID().toString();

    private BaseLog currentLog;

    private String getBaseFileName() {
        return baseFileName;
    }

    private BaseLog getCurrentLog() {
        return currentLog;
    }

    private void setCurrentLog(BaseLog currentLog) {
        this.currentLog = currentLog;
    }

    /**
     * Sets string path to the output
     * 
     * @param rootFolder
     *            path to the output folder
     */
    public static void setTestNGOutputFolder(String rootFolder) {
        output = rootFolder;
    }

    /**
     * <ol>
     * <li>Provides saver with path to output information.
     * <li>Initializes saver.<br>
     * <li>Creates if missing output directories.<br>
     * </ol>
     */
    public static void init() {
        logger.entering();
        saver = new SaverFileSystem(output);
        saver.init();
        logger.exiting();
    }

    /**
     * Creates an instance of {@link BaseLog}. Calls any {@link LogAction}s which are hooked in.
     * 
     * @param saveSrc
     *            Save the current page source <code>true/false</code>. Requires an active {@link Grid} session.
     * @return A {@link BaseLog} subclass that represents the actual log that was generated.
     */
    protected BaseLog createLog(boolean saveSrc) {
        String href = null;
        /**
         * Changed html file extension to txt
         */
        if (!(saver instanceof SaverFileSystem)) { // NOSONAR
            throw new RuntimeException("Internal error. SeLionReporter expects an instance of SaverFileSystem."); // NOSONAR
        }
        if (saveSrc) {
            PageContents source = new PageContents(Grid.driver().getPageSource(), getBaseFileName());
            saver.saveSources(source);
            href = "sources" + File.separator + getBaseFileName() + ".source.txt";
            getCurrentLog().setHref(href);
        }
        for (LogAction eachAction : actionList) {
            eachAction.perform();
        }

        return getCurrentLog();
    }

    /**
     * Generate a log message and send it to the TestNG {@link Reporter}
     * 
     * @param takeScreenshot
     *            Take a screenshot <code>true/false</code>. Requires an active {@link Grid} session.
     * @param saveSrc
     *            Save the current page source <code>true/false</code>. Requires an active {@link Grid} session.
     */
    protected void generateLog(boolean takeScreenshot, boolean saveSrc) {
        logger.entering(new Object[] { takeScreenshot, saveSrc });
        try {
            BaseLog log = createLog(saveSrc);

            String screenshotPath = null;
            log.setScreen(null);

            if (takeScreenshot) {
                // screenshot
                PageContents screen = new PageContents(Gatherer.takeScreenshot(Grid.driver()), getBaseFileName());
                screenshotPath = saver.saveScreenshot(screen);
                log.setScreen(screenshotPath);
            }
            // creating a string from all the info for the report to deserialize
            Reporter.log(log.toString());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "error in the logging feature of SeLion " + e.getMessage(), e);
        }
        logger.exiting();
    }

    /**
     * @param action
     *            A {@link LogAction} object that represents the custom log action to be invoked when
     *            {@link SeLionReporter#log(String, boolean, boolean)} gets called.
     * 
     */
    public static void addLogAction(LogAction action) {
        if (!actionList.contains(action)) {
            actionList.add(action);
        }
    }

    /**
     * Generates log entry with message provided
     * 
     * @param message
     *            Entry description
     * @param takeScreenshot
     *            Take a screenshot <code>true/false</code>. Requires an active {@link Grid} session.
     */
    public static void log(String message, boolean takeScreenshot) {
        log(message, takeScreenshot, false);
    }

    /**
     * Generates log entry with message provided
     * 
     * @param message
     *            Entry description
     * @param takeScreenshot
     *            Take a screenshot <code>true/false</code>. Requires an active {@link Grid} session.
     * @param saveSrc
     *            Save the current page source <code>true/false</code>. Requires an active {@link Grid} session.
     */
    public static void log(String message, boolean takeScreenshot, boolean saveSrc) {
        SeLionReporter reporter = new SeLionReporter();
        BaseLog currentLog = new BaseLog();
        currentLog.setMsg(message);
        currentLog.setLocation(Gatherer.saveGetLocation(Grid.driver()));
        reporter.setCurrentLog(currentLog);
        reporter.generateLog(takeScreenshot, saveSrc);
        logger.exiting();
    }
}
