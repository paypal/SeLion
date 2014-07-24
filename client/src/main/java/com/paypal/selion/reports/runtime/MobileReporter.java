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

import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.model.AbstractLog;
import com.paypal.selion.reports.model.AppLog;

/**
 * Static log method allow you to create a more meaningful piece of log for a device test. It will log a message and
 * also take a screenshot.
 */
public class MobileReporter extends AbstractReporter {

    /**
     * Generates log entry with message provided
     * 
     * @param message
     *            Entry description
     * @param takeScreenshot
     *            <b>true/false</b> take or not and save screenshot
     */
    public static void log(String message, boolean takeScreenshot) {
        MobileReporter reporter = new MobileReporter();
        try {
            reporter.driver = Grid.genericDriver();
        } catch (Exception e) {
            e.printStackTrace(); // TODO Handle exception better
        }
        AppLog currentLog = new AppLog();
        currentLog.setMsg(message);
        // TODO: Point to ponder.
        // Does this matter if I set the type as Web or App ?
        // What should the report be tweaked for type as WEB ?
        currentLog.setType("WEB");
        reporter.setLog(currentLog);
        // reporter.generateLog(takeScreenshot, false);
        reporter.generateLog(takeScreenshot, true); // enabling screenshots during reporting
    }

    @Override
    protected AbstractLog createLog(boolean takeScreenshot, boolean saveSrc) {
        return getLog();
    }
}
