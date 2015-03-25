/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

package com.paypal.selion.pojos;

import org.apache.commons.lang.SystemUtils;

/**
 * A Class to house all the constants related to SeLion Grid
 * 
 */
public class SeLionGridConstants {

    private SeLionGridConstants() {
        // Restricting Object creation
    }

    private static final String PLATFORM = SystemUtils.IS_OS_WINDOWS ? "windows/" : "unix/";

    // Path to the home directory from where the archive is launched
    private static String CONFIG = "/config/";
    private static String HOME_PATH = (SystemUtils.USER_HOME == null) ? SystemUtils.USER_DIR : SystemUtils.USER_HOME;
    public static String SELION_HOME = System.getProperty("selionHome");

    static {
        if (SELION_HOME == null) {
            SELION_HOME = HOME_PATH + "/.selion/";
        } else {
            SELION_HOME = SELION_HOME + "/";
        }
    }

    // Path to the file which has the details related to downloads
    public static final String JAR_DOWNLOAD_FILE_PATH = CONFIG + "download.properties";
    public static final String DOWNLOAD_FILE_PATH = SELION_HOME + "download.properties";
    public static final String DOWNLOAD_DIR_PATH = SELION_HOME + "downloads/";
    public static final String IE_DRIVER = "IEDriverServer.exe";
    public static final String CHROME_DRIVER = SystemUtils.IS_OS_WINDOWS ? "chromedriver.exe" : "chromedriver";
    public static final String PHANTOMJS_DRIVER = SystemUtils.IS_OS_WINDOWS ? "phantomjs.exe" : "phantomjs";

    // Path prefix to pages along with the Grid Api endpoint
    public static String PAGE_RESOURCES = "/grid/resources/pages/";

    // Path to html pages
    public static String GRID_HOME_PAGE = PAGE_RESOURCES + "gridHomePage.html";
    public static String PASSWORD_CHANGE_GET_DETAILS = "/pages/changePageGetDetails.html";
    public static String GRID_LOGIN_PAGE = PAGE_RESOURCES + "gridLoginPage.html";

    // Path to pages related to sauce grid
    public static String SAUCE_GRID_HOME_PAGE = PAGE_RESOURCES + "sauceGridHomePage.html";
    public static String UPDATE_SAUCE_CONFIG_PAGE = "/pages/updateSauceConfigPage.html";

    // Constants related to grid
    public static String SELION_CONFIG = CONFIG + PLATFORM + "SeLionConfig.json";
    public static String JAR_HUB_CONFIG = CONFIG + "hubConfig.json";
    public static String JAR_HUB_SAUCE_CONFIG = CONFIG + "hubSauceConfig.json";
    public static String HUB_CONFIG = SELION_HOME + "hubConfig.json";
    public static String HUB_SAUCE_CONFIG = SELION_HOME + "hubSauceConfig.json";

    // Constants related to sauce grid
    public static String JAR_SAUCE_CONFIG = CONFIG + "sauceConfig.json";
    public static String SAUCE_CONFIG = SELION_HOME + "sauceConfig.json";

    // Constants related to logger
    public static String JAR_LOGGER = CONFIG + "logging.properties";
    public static String LOGGER = SELION_HOME + "logging.properties";
    public static String LOGS_FOLDER_PATH = SELION_HOME + "logs/";

    // Constants related to node
    public static String JAR_NODE_CONFIG = CONFIG + PLATFORM + "nodeConfig.json";
    public static String NODE_CONFIG = SELION_HOME + "nodeConfig.json";

    // Constants related to SeLion arguments
    public static final String CONFIG_NAME = "-config";
}
