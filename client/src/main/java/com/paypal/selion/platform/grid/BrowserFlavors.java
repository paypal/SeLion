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

package com.paypal.selion.platform.grid;

import java.util.Arrays;
import java.util.List;

import com.paypal.selion.annotations.MobileTest;

/**
 * An enum class that represents the browser flavors supported by SeLion
 */
public enum BrowserFlavors {
    GENERIC("*generic"), 
    FIREFOX("*firefox"), 
    INTERNET_EXPLORER("*iexplore"), 
    HTMLUNIT("*htmlunit"), 
    CHROME("*chrome"),
    /**
     * @deprecated SeLion is moving away from IPhoneDriver and now starting to use IOS-Driver. In order to be able to
     *             run tests on an iPad simulator (or) device using the Safari browser, please use
     *             {@link MobileTest#device()} equal to "iphone" and {@link MobileTest#appName()} equal to "Safari"
     */
    IPHONE("*iphone"),
    /**
     * @deprecated SeLion is moving away from IPhoneDriver and now starting to use IOS-Driver. In order to be able to
     *             run tests on an iPad simulator (or) device using the Safari browser, please use
     *             {@link MobileTest#device()} equal to "ipad" and {@link MobileTest#appName()} equal to "Safari"
     */
    IPAD("*ipad"), 
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
        errorMsg.append("Current Supported Browser flavors are : [").append(BrowserFlavors.getSupportedBrowsersAsCSV())
                .append("].");

        IllegalArgumentException e = new IllegalArgumentException(errorMsg.toString());
        throw e;
    }

    /**
     * @return - {@link BrowserFlavors#IPAD} and {@link BrowserFlavors#IPHONE} as a {@link BrowserFlavors} array. This
     *         method can be invoked to query the set of browser flavors in SeLion that are aimed at referring to the
     *         IOS Mobile platform.
     * @deprecated IPhoneDriver is deprecated in Selenium. Use IOSDriver via {@link MobileTest} instead.
     */
    public static BrowserFlavors[] getIOSDeviceFlavors() {
        return new BrowserFlavors[] { IPAD, IPHONE };
    }

    /**
     * @return - An array of {@link BrowserFlavors} which represents the set of browser flavors on which alerts are
     *         <b>NOT</b> supported.
     */
    public static BrowserFlavors[] getBrowsersWithoutAlertSupport() {
        return new BrowserFlavors[] { IPAD, IPHONE, PHANTOMJS };
    }
    
    /**
     * @param flavor - A {@link BrowserFlavors} object that represents a browser.
     * @return <code>true</code> if the given browser is a headless browser.
     */
    public static boolean isHeadLessBrowser(BrowserFlavors flavor) {
        List<BrowserFlavors> headless = Arrays.asList(new BrowserFlavors[] {PHANTOMJS, HTMLUNIT});
        return headless.contains(flavor);
    }
}
