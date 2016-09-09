/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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
 * This enum contains the list of processes that are of interest for the SeLion Grid.
 */
public enum ProcessNames {
    INTERNET_EXPLORER("iexplore.exe", "NOTAPPLICABLE"),
    FIREFOX("firefox.exe", "firefox"),
    CHROME("chrome.exe", "chrome"),
    CHROMEDRIVER("chromedriver.exe", "chromedriver"),
    IEDRIVER("iedriverserver.exe", "NOTAPPLICABLE"),
    PHANTOMJS("phantomjs.exe", "phantomjs"),
    WERFAULT("werfault.exe", "NOTAPPLICABLE"),
    MICROSOFTEDGE("MicrosoftEdge.exe", "NOTAPPLICABLE"),
    EDGEDRIVER("MicrosoftWebDriver.exe", "NOTAPPLICABLE"),
    GECKODRIVER("geckodriver.exe", "geckodriver"),
    ;

    private ProcessNames(String windowsImageName, String unixImageName) {
        this.windowsImageName = windowsImageName;
        this.unixImageName = unixImageName;
    }

    private String windowsImageName;
    private String unixImageName;

    public String getWindowsImageName() {
        return windowsImageName;
    }

    public String getUnixImageName() {
        return unixImageName;
    }
}
