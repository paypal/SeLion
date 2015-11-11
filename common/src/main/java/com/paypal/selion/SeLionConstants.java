/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

package com.paypal.selion;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;

/**
 * Common constants across all SeLion components.
 */
public class SeLionConstants {

    /**
     * Path to the home directory from where the jar archive is launched
     */
    public static final String HOME_PATH = (SystemUtils.USER_HOME == null) ? SystemUtils.USER_DIR
            : SystemUtils.USER_HOME;

    /**
     * The location SeLion-Grid will use to read/write/install required files to
     */
    public static String SELION_HOME_DIR = HOME_PATH + "/.selion/";

    static {
        // allow for the user to override SELION_HOME via a system property
        if (System.getProperty("selionHome") != null) {
            SELION_HOME_DIR = System.getProperty("selionHome");
        }
        // Make sure SELION_HOME ends with a '/' or '\'
        if (!SELION_HOME_DIR.endsWith("/") && !SELION_HOME_DIR.endsWith("\\")) {
            SELION_HOME_DIR += "/";
        }

        SELION_HOME_DIR = FilenameUtils.separatorsToSystem(SELION_HOME_DIR);
    }

    /**
     * Executable name for IEDriver
     */
    public static final String IE_DRIVER = "IEDriverServer.exe";

    /**
     * Platform specific executable name for chromedriver
     */
    public static final String CHROME_DRIVER = SystemUtils.IS_OS_WINDOWS ? "chromedriver.exe" : "chromedriver";

    /**
     * Platform specific executable name for phantomjs
     */
    public static final String PHANTOMJS_DRIVER = SystemUtils.IS_OS_WINDOWS ? "phantomjs.exe" : "phantomjs";

    /**
     * Selenium system property for defining the location of chrome driver
     */
    public static final String WEBDRIVER_CHROME_DRIVER_PROPERTY = "webdriver.chrome.driver";
    
    /**
     * Selenium system property for defining the location of ie driver.
     */
    public static final String WEBDRIVER_IE_DRIVER_PROPERTY = "webdriver.ie.driver";
    
    /**
     * Selenium system property for defining the location of phantomjs
     */
    public static final String WEBDRIVER_PHANTOMJS_DRIVER_PROPERTY = "phantomjs.binary.path";

    /**
     * Selenium system property for defining the location of edge driver.
     */
    public static final String WEBDRIVER_EDGE_DRIVER_PROPERTY = "webdriver.edge.driver";
}
