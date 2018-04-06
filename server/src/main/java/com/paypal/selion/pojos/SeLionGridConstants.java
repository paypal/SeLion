/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;

import static com.paypal.selion.SeLionConstants.*;

/**
 * A Class which contains String constants used throughout SeLion Grid.
 * 
 * Patterns & conventions used in naming the constants for this class.
 * </br> 
 * </br> - Anything that is a *DIR* ends with a trailing separator. File system dirs are platform specific  ('\' Windows, 
 * '/' Unix). CLASSPATH resource dirs will always be '/'. 
 * </br> - Anything that is a *FILE* is a file 
 * </br> - Anything that is a *URL* is relative to http://{hostname}:{port} 
 * </br> - Anything that is a *RESOURCE* is a jar resource that can be loaded via the CLASSPATH 
 * </br> - Anything that is a *PAGE* is a HTML page 
 * </br> - Anything that is a *ARG* is a program argument 
 * </br> - Any single constant may utilize more than one of these patterns/conventions 
 * </br>
 */
public class SeLionGridConstants {

    private static final String PLATFORM_RESOURCE_DIR = SystemUtils.IS_OS_WINDOWS ? "windows/" : "unix/";

    /**
     * Path prefix to pages along with the Grid API endpoint
     */
    private static final String GRID_PAGES_URL_PATH_PREFIX = "/grid/resources/com/paypal/selion/html/";

    /**
     * Relative directory for config files
     */
    public static final String CONFIG_DIR = "config/";

    /**
     * Installed path to the downloads directory
     */
    public static final String DOWNLOADS_DIR = FilenameUtils.separatorsToSystem(SELION_HOME_DIR + "downloads/");

    /**
     * Installed path to the SeLion-Grid log files
     */
    public static final String LOGS_DIR = FilenameUtils.separatorsToSystem(SELION_HOME_DIR + "logs/");

    /**
     * Resource path to the download.json file
     */
    public static final String DOWNLOAD_JSON_FILE_RESOURCE = "/" + CONFIG_DIR + "download.json";

    /**
     * Installed/Extracted path to the download.json file
     */
    public static final String DOWNLOAD_JSON_FILE = FilenameUtils.separatorsToSystem(SELION_HOME_DIR + CONFIG_DIR
            + "download.json");

    /**
     * URL to the grid home page
     */
    public static final String GRID_HOME_PAGE_URL = GRID_PAGES_URL_PATH_PREFIX + "gridHomePage.html";

    /**
     * URL to the SeLion sauce grid home page
     */
    public static final String SAUCE_GRID_HOMEPAGE_URL = GRID_PAGES_URL_PATH_PREFIX + "sauceGridHomePage.html";

    /**
     * Resource path to the default SeLionConfig.json file
     */
    public static final String SELION_CONFIG_FILE_RESOURCE = "/" + CONFIG_DIR + PLATFORM_RESOURCE_DIR
            + "SeLionConfig.json";

    /**
     * Installed/Extracted path to the SeLionConfig.json file
     */
    public static final String SELION_CONFIG_FILE = FilenameUtils.separatorsToSystem(SELION_HOME_DIR + CONFIG_DIR
            + "SeLionConfig.json");

    /**
     * Resource path to the default hubConfig.json file
     */
    public static final String HUB_CONFIG_FILE_RESOURCE = "/" + CONFIG_DIR + "hubConfig.json";

    /**
     * Resource path to the default hubSauceConfig.json file
     */
    public static final String HUB_SAUCE_CONFIG_FILE_RESOURCE = "/" + CONFIG_DIR + "hubSauceConfig.json";

    /**
     * Resource path to the default nodeSauceConfig.json file
     */
    public static final String NODE_SAUCE_CONFIG_FILE_RESOURCE = "/" + CONFIG_DIR + "nodeSauceConfig.json";

    /**
     * Installed/Extracted path to the hubConfig.json file
     */
    public static final String HUB_CONFIG_FILE = FilenameUtils.separatorsToSystem(SELION_HOME_DIR + CONFIG_DIR
            + "hubConfig.json");

    /**
     * Installed/Extracted path to the hubSauceConfig.json file
     */
    public static final String HUB_SAUCE_CONFIG_FILE = FilenameUtils.separatorsToSystem(SELION_HOME_DIR + CONFIG_DIR
            + "hubSauceConfig.json");

    /**
     * Installed/Extracted path to the nodeSauceConfig.json file
     */
    public static final String NODE_SAUCE_CONFIG_FILE = FilenameUtils.separatorsToSystem(SELION_HOME_DIR + CONFIG_DIR
            + "nodeSauceConfig.json");

    /**
     * Resource path to the default sauceConfig.json file
     */
    public static final String SAUCE_CONFIG_FILE_RESOURCE = "/" + CONFIG_DIR + "sauceConfig.json";

    /**
     * Installed/Extracted path to the sauceConfig.json file
     */
    public static final String SAUCE_CONFIG_FILE = FilenameUtils.separatorsToSystem(SELION_HOME_DIR + CONFIG_DIR
            + "sauceConfig.json");

    /**
     * Resource path to the default logging.properties file
     */
    public static final String LOGGING_PROPERTIES_FILE_RESOURCE = "/" + CONFIG_DIR + "logging.properties";

    /**
     * Installed/Extracted path to the logging.properties file
     */
    public static final String LOGGING_PROPERTIES_FILE = FilenameUtils.separatorsToSystem(SELION_HOME_DIR + CONFIG_DIR
            + "logging.properties");

    /**
     * Resource path to the default nodeConfig.json file
     */
    public static final String NODE_CONFIG_FILE_RESOURCE = "/" + CONFIG_DIR + PLATFORM_RESOURCE_DIR + "nodeConfig.json";

    /**
     * Installed/Extracted path to the nodeConfig.json file
     */
    public static final String NODE_CONFIG_FILE = FilenameUtils.separatorsToSystem(SELION_HOME_DIR + CONFIG_DIR
            + "nodeConfig.json");

    private SeLionGridConstants() {
        // Restricting Object creation
    }
}
