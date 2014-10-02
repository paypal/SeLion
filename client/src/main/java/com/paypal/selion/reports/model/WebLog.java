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

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Object representing logged web page data. Internally used.
 */
public class WebLog extends AbstractLog {
    private String src;
    private String href;
    private static SimpleLogger logger = SeLionLogger.getLogger();

    public WebLog() {
    }

    /**
     * parse the string and build the object.
     * 
     * @param s
     */
    public WebLog(String s) {
        super(s);
    }

    public String toString() {
        StringBuffer buff = new StringBuffer(super.toString());
        buff.append("||SRC=");
        if (src != null) {
            buff.append(src);
        }
        buff.append("||HREF=");
        if (href != null) {
            buff.append(href);
        }
        return buff.toString();
    }

    @Override
    protected void parse(String part) {
        logger.entering(part);
        if (part.startsWith("SRC=")) {
            src = part.replace("SRC=", "");
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

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getHref() {
        if (href == null) {
            href = "";
        }
        return href.replaceAll("\\\\", "/");
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public boolean hasLogs() {
        return (href != null || (msg != null && !msg.trim().isEmpty()));
    }

}
