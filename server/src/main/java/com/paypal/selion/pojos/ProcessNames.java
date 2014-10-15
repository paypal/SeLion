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

package com.paypal.selion.pojos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.Platform;


/**
 * This enum contains the list of processes that are of interest for the SeLion Grid.
 *
 */
public enum ProcessNames {
    JAVA_UPDATE_SCHEDULER("jusched.exe", "NOTAPPLICABLE"),
    JAVA_UPDATE_CHECK("jucheck.exe", "NOTAPPLICABLE"),
    INTERNET_EXPLORER("iexplore.exe", "iexplore"),
    FIREFOX("firefox.exe", "firefox"),
    CHROME("chrome.exe", "chrome"),
    CHROMEDRIVER("chromedriver.exe", "chromedriver"),
    IEDRIVER("iedriverserver.exe", "NOTAPPLICABLE"),
    PHANTOMJS("phantomjs.exe", "phantomjs");

    private ProcessNames(String windowsImageName, String nonWindowsImageName){
        this.windowsImageName = windowsImageName;
        this.nonWindowsImageName = nonWindowsImageName;
    }
    private String windowsImageName;
    private String nonWindowsImageName;
    
    public String getWindowsImageName(){
        return windowsImageName;
    }
    public String getNonWindowsImageName() {
        return nonWindowsImageName;
    }

    /**
     * Utility method to return the executable names for the specified platform.
     * 
     * @param platform
     *            - The {@link Platform}
     * @return {@link List} of {@link String} containing the executable file names.
     */
    public static List<String> getExecutableNames() {
        List<String> executableName = new ArrayList<String>();
        switch (Platform.getCurrent()) {
        case MAC:
        case UNIX:
        case LINUX: {
            Collections.addAll(executableName, ProcessNames.PHANTOMJS.getNonWindowsImageName(),
                    ProcessNames.CHROMEDRIVER.getNonWindowsImageName());
            break;
        }
        default: {
            Collections.addAll(executableName, ProcessNames.PHANTOMJS.getWindowsImageName(),
                    ProcessNames.CHROMEDRIVER.getWindowsImageName(), ProcessNames.IEDRIVER.getWindowsImageName());
            break;
        }
        }
        return executableName;
    }

}
