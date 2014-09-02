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

package com.paypal.selion.reports.model;

import org.testng.Reporter;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.reports.runtime.WebReporter;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class serves as the base for all logging with respect to UI operations being done either on the browser or on a
 * native app in a simulator/device. Any functionality that intends to provide a reporting capability similar to
 * {@link WebReporter} should leverage this class for the basic functionalities and add up only customizations as and
 * where required.
 * 
 */
public abstract class AbstractLog {

    protected static SimpleLogger logger = SeLionLogger.getLogger();

    protected String type;
    protected String msg;
    protected String screen;
    protected String location;

    /**
     * Add custom parsing of a String that represents a line in the log.
     * 
     * @param part
     */
    abstract protected void parse(String part);

    /**
     * @return - <code>true</code> if there are logs that need to be dumped into the TestNG reports via
     *         {@link Reporter#log(String)}.
     */
    abstract public boolean hasLogs();

    protected AbstractLog() {
    }

    protected AbstractLog(String s) {
        logger.entering(s);

        if (s == null) {
            logger.exiting();
            return;
        }
        String[] parts = s.split("\\|\\|");
        for (int i = 0; i < parts.length; i++) {
            baseParse(parts[i]);
        }
        logger.exiting();
    }

    protected void baseParse(String part) {
        if (part.startsWith("TYPE=")) {
            type = part.replace("TYPE=", "");
            if ("".equals(type)) {
                type = null;
            }
        } else if (part.startsWith("MSG=")) {
            msg = part.replace("MSG=", "");
        } else if (part.startsWith("SCREEN=")) {
            screen = part.replace("SCREEN=", "");
            if ("".equals(screen)) {
                screen = null;
            }
        } else {
            parse(part);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getScreen() {
        return screen;
    }

    public String getLocation() {
        return location;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("TYPE=");
        if (type != null) {
            buff.append(type);
        }
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
        return buff.toString();

    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getScreenURL() {
        logger.entering();
        String returnValue = "no screen";
        if (screen != null) {
            returnValue = screen.replaceAll("\\\\", "/");
        }
        logger.exiting(returnValue);
        return returnValue;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }
}
