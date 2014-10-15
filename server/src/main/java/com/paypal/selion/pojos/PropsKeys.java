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
 * This enum is essentially a container for housing all of the keys that would be part of the download
 * properties file.
 *
 */
public enum PropsKeys {
    // NOTE: When new keys are added then make sure the keys are also added to values(platform) method as per platform.
    SELENIUM_URL("selenium_url", "Download Selenium Jar from"),
    SELENIUM_CHECKSUM("selenium_checksum", "Selenium Jar's checksum"),
    IE_URL("ie_url", "Download IEDriverServer binary from"),
    IE_CHECKSUM("ie_checksum","IEDriverServer binary's checksum"),
    CHROME_URL("chrome_url","Download ChromeDriver binary from"),
    CHROME_CHECKSUM("chrome_checksum","ChromeDriver binary's checksum"),
    PHANTOMJS_URL("phantomjs_url","Download PhantomJS binary from"),
    PHANTOMJS_CHECKSUM("phantomjs_checksum","PhantomJS binary's checksum"), 
    CHROME_LINUX_URL("chrome_linux_url", "Download ChromeDriver linux binary from"),
    CHROME_LINUX_CHECKSUM("chrome_linux_checksum", "ChromeDriver binary's linux checksum"), 
    CHROME_MAC_URL("chrome_mac_url", "Download the ChromeDriver mac binary from"),
    CHROME_MAC_CHECKSUM("chrome_mac_checksum", "Chromedriver binary's mac checksum"),
    PHANTOMJS_LINUX_URL("phantomjs_linux_url", "Download PhantomJS linux binary from"), 
    PHANTOMJS_LINUX_CHECKSUM("phantomjs_linux_checksum", "PhantomJS binary's linux checksum"),
    PHANTOMJS_MAC_URL("phantomjs_mac_url", "Download PhantomJS mac binary from"), 
    PHANTOMJS_MAC_CHECKSUM("phantomjs_mac_checksum", "PhantomJS binary's mac checksum");

    private PropsKeys(String key, String labelText) {
        this.key = key;
        this.labelText = labelText;
    }

    private String key, labelText;

    public String toString() {
        return this.key + "," + this.labelText;
    }
    public String getLabelText() {
        return labelText;
    }
    
    public String getKey() {
        return key;
    }

    /**
     * Method to return the {@link PropsKeys} as per the {@link Platform}
     * 
     * @param platform
     *            - The OS Platform referred from {@link Platform}
     * @return Array of {@link PropsKeys} as per the platform
     */
    public static PropsKeys[] getValuesForCurrentPlatform() {
        
        List<PropsKeys> platformKeyList = new ArrayList<PropsKeys>();
        // The selenium jar is not tied to any platform
        Collections.addAll(platformKeyList, SELENIUM_URL, SELENIUM_CHECKSUM);
        switch (Platform.getCurrent()) {
        case UNIX:
        case LINUX: {
            Collections.addAll(platformKeyList, CHROME_LINUX_URL, CHROME_LINUX_CHECKSUM, PHANTOMJS_LINUX_URL,
                    PHANTOMJS_LINUX_CHECKSUM);
            break;
        }
        case MAC: {
            Collections.addAll(platformKeyList, CHROME_MAC_URL, CHROME_MAC_CHECKSUM, PHANTOMJS_MAC_URL,
                    PHANTOMJS_MAC_CHECKSUM);
            break;
        }
        default: {
            Collections.addAll(platformKeyList, IE_URL, IE_CHECKSUM, CHROME_URL, CHROME_CHECKSUM, PHANTOMJS_URL,
                    PHANTOMJS_CHECKSUM);
            break;
        }
        }
        PropsKeys[] propArray = new PropsKeys[platformKeyList.size()];
        platformKeyList.toArray(propArray);
        return propArray;
    }
}
