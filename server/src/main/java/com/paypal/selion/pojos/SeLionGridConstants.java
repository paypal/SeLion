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

/**
 * A Class to house all the constants related to SeLion Grid
 * 
 */
public class SeLionGridConstants {

    private SeLionGridConstants() {
        //Restricting Object creation
    }

    // Path to the home directory from where the archive is launched
    public static final String ARCHIVE_HOME_PATH = System.getProperty("archiveHome");

    // Path to the file which has the details related to downloads
    public static final String DOWNLOAD_FILE_PATH = ARCHIVE_HOME_PATH + "/config/download.properties";

    // Path prefix to pages along with the Grid Api endpoint
    public static String PAGE_RESOURCES = "/grid/resources/pages/";

    // Path to html pages
    public static String GRID_HOME_PAGE = PAGE_RESOURCES + "gridHomePage.html";
    public static String PASSWORD_CHANGE_GET_DETAILS = ARCHIVE_HOME_PATH + "/pages/changePageGetDetails.html";
    public static String GRID_LOGIN_PAGE = PAGE_RESOURCES + "gridLoginPage.html";

    // Path to pages related to sauce grid
    public static String SAUCE_GRID_HOME_PAGE = PAGE_RESOURCES + "sauceGridHomePage.html";
    public static String UPDATE_SAUCE_CONFIG_PAGE = ARCHIVE_HOME_PATH + "/pages/updateSauceConfigPage.html";

    // Constants related to sauce grid
    public static String SAUCE_CONFIG = ARCHIVE_HOME_PATH + "/config/sauceConfig.json";

    // Path to folders in the archive.
    public static String ARTIFACT_FOLDER_PATH = ARCHIVE_HOME_PATH + "/downloads/";
    public static String LOGS_FOLDER_PATH = ARCHIVE_HOME_PATH + "/logs/";
    public static String BIN_FOLDER_PATH = ARCHIVE_HOME_PATH + "/bin/";
    public static String UNIX_EXECUTABLES_PATH = BIN_FOLDER_PATH + "unix/";
    public static String WIN_EXECUTABLES_PATH = BIN_FOLDER_PATH + "windows/";

}
