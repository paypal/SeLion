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

package com.paypal.selion.internal.reports.model;

import org.testng.Reporter;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.reports.runtime.SeLionReporter;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class serves as the base for all logging with respect to UI operations being done either on the browser or on a
 * native app in a simulator/device. Any functionality that intends to provide a reporting capability similar to
 * {@link SeLionReporter} should leverage this class for the basic functionalities and add up only customizations as and
 * where required.
 * 
 * This is an internal class for use in Selion and clients of SeLion should have no need for referencing this public class.
 * 
 */
public class BaseLog {

    protected static SimpleLogger logger = SeLionLogger.getLogger();
    /** custom message for the log */
    protected String msg;
    /** file location of the screenshot associated with this log message */
    protected String screen;
    /** the url of the current page.  This applies only to webtests and mobiletests that are running with safari, etc. */
    protected String location;
    /** file location of the page source associated with this log message */
    protected String href;

    public BaseLog() {
    }

    /**
     * Parses the input string for specific keys=value pairs delimeted by ||. Example would look like ||MSG=My
     * Screenshot 1||SCREEN=screenshots/513ba426-d5a0-4916-bf4b-e783ce943a8a.png
     * 
     * @param s The string to search for specific keys
     */
    public BaseLog(String s) {
        logger.entering(s);

        if (s == null) {
            logger.exiting();
            return;
        }
        String[] parts = s.split("\\|\\|");
        for (int i = 0; i < parts.length; i++) {
            parse(parts[i]);
        }
        logger.exiting();
    }

    /**
     * Add custom parsing of a String that represents a line in the log.
     * 
     * @param part String to look for fields
     */
    protected void parse(String part) {
        logger.entering(part);
        if (part.startsWith("MSG=")) {
            msg = part.replace("MSG=", "");
        } else if (part.startsWith("SCREEN=")) {
            screen = part.replace("SCREEN=", "");
            if ("".equals(screen)) {
                screen = null;
            }
        } else if (part.startsWith("LOCATION=")) {
            location = part.replace("LOCATION=", "");
        } else if (part.startsWith("HREF=")) {
            href = part.replace("HREF=", "");
            if ("".equals(href)) {
                href = null;
            }
        } else {
            msg = part;
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
     * @param msg the new msg
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
     * @param screen the file location
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
     * @param location the new location
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
     * @param href the new href
     */
    public void setHref(String href) {
        this.href = href;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("||MSG=");
        if (msg != null) {
            buff.append(msg);
        }
        buff.append("||SCREEN=");
        if (screen != null && !screen.trim().isEmpty()) {
            buff.append(screen);
        }
        buff.append("||LOCATION=");
        if (location != null) {
            buff.append(location);
        }
        buff.append("||HREF=");
        if (href != null) {
            buff.append(href);
        }
        return buff.toString();
    }
}
