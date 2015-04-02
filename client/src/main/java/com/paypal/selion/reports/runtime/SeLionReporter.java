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

package com.paypal.selion.reports.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.testng.Reporter;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.internal.reports.model.BaseLog;
import com.paypal.selion.internal.reports.model.PageContents;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.reporter.services.LogAction;
import com.paypal.test.utilities.logging.SimpleLogger;

public class SeLionReporter {
    protected static SimpleLogger logger = SeLionLogger.getLogger();
    protected volatile static List<LogAction> actionList = new ArrayList<LogAction>();

    protected static String output;
    protected static DataSaver saver = null;
    protected String baseFileName = UUID.randomUUID().toString();

    private BaseLog currentLog;

    void setLog(BaseLog log) {
        this.currentLog = log;
    }

    protected String getBaseFileName() {
        return baseFileName;
    }

    protected BaseLog getLog() {
        return this.currentLog;
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
     * Creates the log
     * 
     * @param takeScreenshot
     *            <b>true/false</b> take or not and save screenshot
     * @param saveSrc
     *            <b>true/false</b> save or not page source
     * @return - An {@link BaseLog} subclass that represents the actual log that was generated.
     */
    protected BaseLog createLog(boolean takeScreenshot, boolean saveSrc) {
        String href = null;
        /**
         * Changed html file extension to txt
         */
        if (!(saver instanceof SaverFileSystem)) {
            throw new RuntimeException("Internal error. SeLionReporter expects an instance of SaverFileSystem.");
        }
        if (saveSrc) {
            if (Grid.driver() != null) {
                PageContents source = new PageContents(Grid.driver().getPageSource(), getBaseFileName());
                saver.saveSources(source);
            }
            href = "sources" + File.separator + getBaseFileName() + ".source.txt";
        }
        BaseLog log = (BaseLog) getLog();
        log.setHref(href);
        for (LogAction eachAction : actionList) {
            eachAction.perform();
        }

        return log;
    }

    protected void generateLog(boolean takeScreenshot, boolean saveSrc) {
        logger.entering(new Object[] { takeScreenshot, saveSrc });
        try {
            BaseLog log = createLog(takeScreenshot, saveSrc);

            String screenshotPath = null;
            log.setScreen(null);

            if (takeScreenshot && Grid.driver() != null) {
                // screenshot
                PageContents screen = new PageContents(Gatherer.takeScreenshot(Grid.driver()), baseFileName);
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
     *            - A {@link LogAction} object that represents the custom log action to be invoked when
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
     *            <b>true/false</b> take a screenshot
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
     *            <b>true/false</b> take a screenshot
     * @param saveSrc
     *            <b>true/false</b> save the current page source
     */
    public static void log(String message, boolean takeScreenshot, boolean saveSrc) {
        SeLionReporter reporter = new SeLionReporter();
        BaseLog currentLog = new BaseLog();
        currentLog.setMsg(message);
        currentLog.setLocation(Gatherer.saveGetLocation(Grid.driver()));
        reporter.setLog(currentLog);
        reporter.generateLog(takeScreenshot, saveSrc);
        logger.exiting();
    }
}
