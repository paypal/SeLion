/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

package com.paypal.selion.internal.platform.grid;

import java.util.Arrays;
import java.util.List;

/**
 * An enum class that represents the browser flavors supported by SeLion
 */
public enum BrowserFlavors {
    FIREFOX("*firefox"),
    INTERNET_EXPLORER("*iexplore"),
    MICROSOFT_EDGE("*microsoftedge"),
    HTMLUNIT("*htmlunit"),
    CHROME("*chrome"),
    SAFARI("*safari"),
    OPERA("*opera"),
    PHANTOMJS("*phantomjs");

    private String browser;

    private BrowserFlavors(String browser) {
        this.browser = browser;
    }

    /**
     * Returns the browser flavor as a string
     * 
     * @return - A string that represents the browser flavor in question
     */
    public String getBrowser() {
        return this.browser;
    }

    /**
     * This method returns all the browser flavors that are supported by the SeLion framework as a String with each
     * value delimited by a comma.
     * 
     * @return - A comma separated string that represents all supported browser flavors.
     */
    public static String getSupportedBrowsersAsCSV() {
        StringBuilder buffer = new StringBuilder();
        String delimiter = ",";
        for (BrowserFlavors flavor : BrowserFlavors.values()) {
            buffer.append(flavor.getBrowser()).append(delimiter);
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    /**
     * @param browser
     *            - The raw browser string for which the enum format is sought.
     * @return - A {@link BrowserFlavors} enum that represents a SeLion compliant browser.
     */
    public static BrowserFlavors getBrowser(String browser) {
        for (BrowserFlavors flavor : BrowserFlavors.values()) {
            if (flavor.getBrowser().equalsIgnoreCase(browser)) {
                return flavor;
            }
        }
        // No corresponding browser was found. Throwing an exception
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Browser name \'");
        errorMsg.append(browser).append("\' did not match any browser flavors supported by SeLion.\n");
        errorMsg.append("Supported Browser flavors are : [").append(BrowserFlavors.getSupportedBrowsersAsCSV())
                .append("].");

        IllegalArgumentException e = new IllegalArgumentException(errorMsg.toString());
        throw e;
    }

    /**
     * @return - An array of {@link BrowserFlavors} which represents the set of browser flavors on which alerts are
     *         <b>NOT</b> supported.
     */
    public static BrowserFlavors[] getBrowsersWithoutAlertSupport() {
        return new BrowserFlavors[] { PHANTOMJS };
    }
    
    /**
     * @param flavor - A object that represents a browser.
     * @return <code>true</code> if the given browser is a headless browser.
     */
    public static boolean isHeadLessBrowser(String flavor) {
        List<String> headless = Arrays.asList(new String[] {PHANTOMJS.browser, HTMLUNIT.browser});
        return headless.contains(flavor.toLowerCase());
    }
}
