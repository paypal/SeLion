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

import org.testng.Reporter;

import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.model.AbstractLog;
import com.paypal.selion.reports.model.PageContents;
import com.paypal.selion.reports.model.WebLog;
import com.paypal.selion.reports.reporter.services.LogAction;

/**
 * Static log method allow you to create a more meaningful piece of log for a web test. It will log a message, but also
 * take a screenshot , get the URL of the page and capture the source of the page.
 * 
 * @see Reporter
 */
public class WebReporter extends AbstractReporter {

    @Override
    protected AbstractLog createLog(boolean takeScreenshot, boolean saveSrc) {
        String href = null;
        /**
         * Changed html file extension to txt
         */
        if (!(saver instanceof SaverFileSystem)) {
            throw new RuntimeException("Internal error. WebReporter expects an instance of SaverFileSystem.");
        }
        if (saveSrc) {
            if (this.driver != null) {
                PageContents source = new PageContents(driver.getPageSource(), getBaseFileName());
                saver.saveSources(source);
            }
            href = "sources" + File.separator + getBaseFileName() + ".source.txt";
        }
        WebLog log = (WebLog) getLog();
        log.setHref(href);
        for (LogAction eachAction : actionList) {
            eachAction.perform();
        }

        return log;
    }

    public static void log(String message, boolean takeScreenshot, boolean saveSrc) {
        WebReporter reporter = new WebReporter();
        reporter.driver = Grid.wrappedDriver();
        WebLog currentLog = new WebLog();
        currentLog.setMsg(message);
        currentLog.setType("WEB");
        currentLog.setLocation(Gatherer.saveGetLocation(Grid.wrappedDriver()));
        reporter.setLog(currentLog);
        reporter.generateLog(takeScreenshot, saveSrc);
        logger.exiting();
    }
}
