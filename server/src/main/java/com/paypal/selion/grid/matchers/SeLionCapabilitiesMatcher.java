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

package com.paypal.selion.grid.matchers;

import io.selendroid.common.SelendroidCapabilities;
import io.selendroid.grid.SelendroidCapabilityMatcher;

import java.util.HashMap;
import java.util.Map;

import org.openqa.grid.internal.utils.DefaultCapabilityMatcher;
import org.openqa.selenium.remote.BrowserType;
import org.uiautomation.ios.IOSCapabilities;

/**
 * {@link DefaultCapabilityMatcher} which includes matching rules for ios-driver and selendroid nodes.
 * @deprecated in SeLion 1.2.0
 */
@Deprecated
public class SeLionCapabilitiesMatcher extends DefaultCapabilityMatcher {

    @Override
    public boolean matches(Map<String, Object> nodeCapability, Map<String, Object> requestedCapability) {
        boolean ios = requestedCapability.containsKey(IOSCapabilities.BUNDLE_NAME);
        if (ios) {
            return new MinimalIOSCapabilityMatcher().matches(nodeCapability, requestedCapability);
        }
        boolean android = isAndroid(requestedCapability);
        if (android) {
            //TODO Hack -- As of Selendroid 0.10.0, the AUT capabilities are not added, so we are removing it from the
            // requested capabilities before sending to the matcher. 
            // See io.selendroid.server.grid.SelfRegisteringRemote#getNodeConfig() for more on this problem
            Map<String, Object> augmentedRequestedCapabilities = new HashMap<>();
            augmentedRequestedCapabilities.putAll(requestedCapability);
            augmentedRequestedCapabilities.remove(SelendroidCapabilities.AUT);
            return new SelendroidCapabilityMatcher().matches(nodeCapability, augmentedRequestedCapabilities);
        }
        return super.matches(nodeCapability, requestedCapability);
    }

    private boolean isAndroid(Map<String, Object> requestedCapability) {
        // return true if the requestedCapabilies include an android app or an android browser type
        boolean nativeApp = requestedCapability.containsKey("aut");
        String browser = (String) requestedCapability.get("browserName");
        boolean mobileWeb = BrowserType.ANDROID.equals(browser) || "selendroid".equals(browser);
        return nativeApp || mobileWeb;
    }
}
