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

/**
 * This enum is essentially a container for housing all of the keys that would be part of the download
 * properties file.
 *
 */
public enum PropsKeys {
    SELENIUM_URL("selenium_url", "Download Selenium Jar from"),
    SELENIUM_CHECKSUM("selenium_checksum", "Selenium Jar's checksum"),
    IE_URL("ie_url", "Download IEDriverServer binary from"),
    IE_CHECKSUM("ie_checksum","IEDriverServer binary's checksum"),
    CHROME_URL("chrome_url","Download ChromeDriver binary from"),
    CHROME_CHECKSUM("chrome_checksum","ChromeDriver binary's checksum"),
    PHANTOMJS_URL("phantomjs_url","Download PhantomJS binary from"),
    PHANTOMJS_CHECKSUM("phantomjs_checksum","PhantomJS binary's checksum");

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
}
