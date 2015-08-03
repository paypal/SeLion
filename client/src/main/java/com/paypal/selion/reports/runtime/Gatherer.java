/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

import java.util.logging.Level;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

class Gatherer {

    private Gatherer() {
        // Utility class. So hide the constructor
    }

    private static SimpleLogger logger = SeLionLogger.getLogger();

    // this is applicable for both web and mobile. In mobile, you can use safari, etc, to test mobile webpages and
    // getCurrentUrl will get the webpage URL. If testing a mobile application instead of mobile webbrowser then
    // getCurrentURL will throw Exception  
    static String saveGetLocation(WebDriver driver) {
        logger.entering(driver);
        String location = "n/a";
        try {
            if (driver != null) {
                location = driver.getCurrentUrl();
            }
        } catch (Exception exception) {
            logger.log(Level.FINER, "Current location couldn't be retrieved by getCurrentUrl(). This can be SAFELY "
                    + "IGNORED if testing a non-web mobile application. Reason: ", exception);
        }
        logger.exiting(location);
        return location;
    }

    static byte[] takeScreenshot(WebDriver driver) {
        logger.entering(driver);
        try {
            byte[] decodeBuffer = null;

            if (driver instanceof TakesScreenshot) {
                TakesScreenshot screenshot = ((TakesScreenshot) driver);
                String ss = screenshot.getScreenshotAs(OutputType.BASE64);
                decodeBuffer = Base64.decodeBase64(ss.getBytes());
            }
            logger.exiting(decodeBuffer);
            return decodeBuffer;
        } catch (Exception exception) {
            logger.log(Level.WARNING, "Screenshot couldn't be retrieved by getScreenshotAs().", exception);
            return null;
        }
    }

}
