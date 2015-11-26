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

package com.paypal.selion.internal.reports.model;

import java.lang.reflect.Modifier;

import org.testng.Reporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.reports.runtime.SeLionReporter;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class serves as the base for all logging with respect to UI operations being done either on the browser or on a
 * mobile simulator/device. Any functionality that intends to provide a reporting capability similar to
 * {@link SeLionReporter} should leverage this class for the basic functionalities and add up only customizations as and
 * where required.
 * 
 * This is an internal class for use in Selion and clients of SeLion should have no need for referencing this public
 * class.
 */
public class BaseLog {

    private static SimpleLogger logger = SeLionLogger.getLogger();
    // custom message for the log
    private String msg;
    // file location of the screenshot associated with this log message
    private String screen;
    // the url of the current page. This applies only to webtests and mobiletests that are running with safari, etc.
    private String location;
    // file location of the page source associated with this log message
    private String href;

    public BaseLog() {
        // default constructor
    }

    /**
     * Parses the JSON string and load it to BaseLog instance<br>
     * <br>
     * For example;
     * <pre>
     * {
     *   "msg": "Google Page with SeLion",
     *   "screen": "screenshots/bd0bac20-9ad0-41b2-bc82-f2a856054129.png",
     *   "location": "https://www.google.com/?gws_rd\u003dssl",
     *   "href": "sources\\bd0bac20-9ad0-41b2-bc82-f2a856054129.source.txt"
     * }
     * </pre>
     * 
     * @param json
     *            The JSON string
     */
    public BaseLog(String json) {
        logger.entering(json);

        if (json == null) {
            logger.exiting();
            return;
        }
        parse(json);
        logger.exiting();
    }

    /**
     * Parsing the JSON string using Gson library.
     * 
     * @param json
     *            JSON String
     */
    protected void parse(String json) {
        logger.entering(json);

        try {
            Gson gson = new Gson();
            BaseLog baseLog = gson.fromJson(json, this.getClass());
            this.msg = baseLog.msg;
            this.screen = baseLog.screen;
            this.location = baseLog.location;
            this.href = baseLog.href;
        } catch (JsonSyntaxException e) {
            // If not JSON string then treat this as an message
            this.msg = json;
        }

        logger.exiting();
    }

    /**
     * @return - <code>true</code> if there are logs that need to be dumped into the TestNG reports via
     *         {@link Reporter#log(String)}.
     */
    public boolean hasLogs() {
        return (href != null || (msg != null && !msg.trim().isEmpty()));
    }

    /**
     * Gets the msg.
     *
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Sets the msg.
     *
     * @param msg
     *            the new msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Gets the file location of the screenshot.
     *
     * @return the screen
     */
    public String getScreen() {
        return screen;
    }

    /**
     * Replaces any occurances of \\ in the screen shot file location with /
     *
     * @return the screen url
     */
    public String getScreenURL() {
        logger.entering();
        String returnValue = "no screen";
        if (screen != null) {
            returnValue = screen.replaceAll("\\\\", "/");
        }
        logger.exiting(returnValue);
        return returnValue;
    }

    /**
     * Sets the file location of the screenshot.
     *
     * @param screen
     *            the file location
     */
    public void setScreen(String screen) {
        this.screen = screen;
    }

    /**
     * Gets the url of the current page.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the url of the current page.
     *
     * @param location
     *            the new location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the file location of the page source. All occurrences of \\ will be replaced with /
     *
     * @return the href
     */
    public String getHref() {
        if (href == null) {
            href = "";
        }
        return href.replaceAll("\\\\", "/");
    }

    /**
     * Sets the file location of the page source.
     *
     * @param href
     *            the new href
     */
    public void setHref(String href) {
        this.href = href;
    }

    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT).create();
        return gson.toJson(this);
    }
}
