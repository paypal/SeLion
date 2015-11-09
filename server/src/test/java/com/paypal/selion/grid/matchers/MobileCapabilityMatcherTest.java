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

package com.paypal.selion.grid.matchers;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import io.selendroid.common.SelendroidCapabilities;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.remote.CapabilityType;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;

public class MobileCapabilityMatcherTest {

    @Test
    public void matchAppiumNode() {
        MobileCapabilityMatcher matcher = new MobileCapabilityMatcher();

        Map<String, Object> nodeCapability = new HashMap<String, Object>();
        nodeCapability.put("platformName", "Android");
        nodeCapability.put("platformVersion", "4.4.2");
        nodeCapability.put("mobileNodeType", "appium");

        //Success scenario1
        Map<String, Object> requestedCapability = new HashMap<String, Object>();
        requestedCapability.put("mobileNodeType", "appium");
        requestedCapability.put("platformName", "Android");
        requestedCapability.put("platformVersion", "4.4.2");
        requestedCapability.put("deviceName", "Android Emulator");
        assertTrue(matcher.matches(nodeCapability, requestedCapability));

        //Success scenario2
        requestedCapability.clear();
        requestedCapability.put("mobileNodeType", "appium");
        requestedCapability.put("platformName", "Android");
        assertTrue(matcher.matches(nodeCapability, requestedCapability));

        //Failure scenario
        requestedCapability.clear();
        requestedCapability.put("mobileNodeType", "appium");
        requestedCapability.put("platformName", "Android");
        requestedCapability.put("platformVersion", "4.2.2");
        requestedCapability.put("deviceName", "Android Emulator");
        assertFalse(matcher.matches(nodeCapability, requestedCapability));
    }

    @Test
    public void matchSelendroid() {
        MobileCapabilityMatcher matcher = new MobileCapabilityMatcher();

        Map<String, Object> nodeCapability = new HashMap<String, Object>();
        nodeCapability.put(CapabilityType.BROWSER_NAME, "selendroid");
        nodeCapability.put("mobileNodeType", "selendroid");

        //Success scenario
        Map<String, Object> requestedCapability = new HashMap<String, Object>();
        requestedCapability.put("mobileNodeType", "selendroid");
        requestedCapability.put(CapabilityType.BROWSER_NAME, "selendroid");
        requestedCapability.put(SelendroidCapabilities.AUT, "appname");
        requestedCapability.put(SelendroidCapabilities.LOCALE, "en_us");
        assertTrue(matcher.matches(nodeCapability, requestedCapability));

        //Failure scenario
        requestedCapability.clear();
        requestedCapability.put("mobileNodeType", "selendroid");
        requestedCapability.put(CapabilityType.BROWSER_NAME, "firefox");
        assertFalse(matcher.matches(nodeCapability, requestedCapability));
    }

    @Test
    public void matchIOSNode() {
        MobileCapabilityMatcher matcher = new MobileCapabilityMatcher();

        //Failure scenario
        Map<String, Object> requestedCapability = new HashMap<String, Object>();
        requestedCapability.put("mobileNodeType", "ios-driver");
        requestedCapability.put(IOSCapabilities.DEVICE, "iphone");
        requestedCapability.put(IOSCapabilities.LANGUAGE, "english");
        requestedCapability.put(IOSCapabilities.LOCALE, "en_us");
        requestedCapability.put(IOSCapabilities.BUNDLE_NAME, "appname");
        assertFalse(matcher.matches(new HashMap<String, Object>(), requestedCapability));
    }

    @Test
    public void matchBrowser() {
        MobileCapabilityMatcher matcher = new MobileCapabilityMatcher();

        Map<String, Object> nodeCapability = new HashMap<String, Object>();
        nodeCapability.put(CapabilityType.BROWSER_NAME, "chrome");

        //Success scenario
        Map<String, Object> requestedCapability = new HashMap<String, Object>();
        requestedCapability.put(CapabilityType.BROWSER_NAME, "chrome");

        assertTrue(matcher.matches(nodeCapability, requestedCapability));
    }

    @Test
    public void matchBrowserWithNonMatchingMobileNodeType() {
        MobileCapabilityMatcher matcher = new MobileCapabilityMatcher();

        Map<String, Object> nodeCapability = new HashMap<String, Object>();
        nodeCapability.put(CapabilityType.BROWSER_NAME, "ff");

        //Success scenario
        Map<String, Object> requestedCapability = new HashMap<String, Object>();
        requestedCapability.put("mobileNodeType", "");
        requestedCapability.put(CapabilityType.BROWSER_NAME, "ff");

        assertTrue(matcher.matches(nodeCapability, requestedCapability));
    }
}
