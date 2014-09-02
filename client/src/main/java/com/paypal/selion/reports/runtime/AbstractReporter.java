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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.reports.model.AbstractLog;
import com.paypal.selion.reports.model.PageContents;
import com.paypal.selion.reports.reporter.services.LogAction;
import com.paypal.test.utilities.logging.SimpleLogger;

abstract class AbstractReporter {
    protected static SimpleLogger logger = SeLionLogger.getLogger();
    protected volatile static List<LogAction> actionList = new ArrayList<LogAction>();

    protected static String output;
    protected static DataSaver saver = null;
    protected String baseFileName = UUID.randomUUID().toString();

    protected WebDriver driver;
    private AbstractLog currentLog;

    void setLog(AbstractLog log) {
        this.currentLog = log;
    }

    protected String getBaseFileName() {
        return baseFileName;
    }

    protected AbstractLog getLog() {
        return this.currentLog;
    }

    protected WebDriver getDriver() {
        return this.driver;
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
     * The actual Reporting mechanism should ensure that it provides for a specific implementation for this.
     * 
     * @param takeScreenshot
     *            <b>true/false</b> take or not and save screenshot
     * @param saveSrc
     *            <b>true/false</b> save or not page source
     * @return - An {@link AbstractLog} subclass that represents the actual log that was generated.
     */
    protected abstract AbstractLog createLog(boolean takeScreenshot, boolean saveSrc);

    protected void generateLog(boolean takeScreenshot, boolean saveSrc) {
        logger.entering(new Object[] { takeScreenshot, saveSrc });
        try {
            AbstractLog log = createLog(takeScreenshot, saveSrc);

            String screenshotPath = null;
            log.setScreen(null);

            if (takeScreenshot && driver != null) {
                // screenshot
                PageContents screen = new PageContents(Gatherer.takeScreenshot(driver), baseFileName);
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
     *            {@link WebReporter#log(String, boolean, boolean)} gets called.
     * 
     */
    public static void addLogAction(LogAction action) {
        if (!actionList.contains(action)) {
            actionList.add(action);
        }
    }

}
