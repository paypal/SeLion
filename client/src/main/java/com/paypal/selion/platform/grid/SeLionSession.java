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

package com.paypal.selion.platform.grid;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A utility class to represent SeLion web sessions
 */
final class SeLionSession {
    private RemoteWebDriver driver;
    private static SimpleLogger logger = SeLionLogger.getLogger();

    protected SeLionSession(RemoteWebDriver webDriver) {
        this.driver = webDriver;
    }

    protected RemoteWebDriver getWebDriver() {
        return this.driver;
    }

    public String toString() {
        logger.entering();
        String returnValue = null;
        if (this.driver == null) {
            returnValue = "SeLionSession {null}";
        } else {
            try {
                
                returnValue = "SeLionSession {sessionId=" + driver.getSessionId() + ", title="
                        + getPageTitle() + "}";
            } catch (WebDriverException e) {
                if (e.getLocalizedMessage().contains("Session not available")) {
                    // session was probably killed by the HUB
                    this.driver = null;
                    returnValue = "SeLionSession {null}";
                } else {
                    returnValue = super.toString();
                }
            }
        }

        logger.exiting(returnValue);
        return returnValue;
    }

    /**
     * This method attempts at fetching the page title but does it ONLY when there is No alert present.
     * 
     * @return - The title of the page if and only if there were no alerts present.
     */
    private String getPageTitle() {
        try {
            this.driver.switchTo().alert();
            return "{Cannot fetch Title because an alert is present}";
        } catch (NoAlertPresentException exception) {
            return this.driver.getTitle();
        }
    }
}
