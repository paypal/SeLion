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

package com.paypal.selion.internal.grid.capabilities;

import io.selendroid.grid.SelendroidCapabilityMatcher;

import java.util.Map;

import org.openqa.grid.internal.utils.DefaultCapabilityMatcher;
import org.openqa.selenium.remote.BrowserType;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.grid.IOSCapabilityMatcher;

public class SeLionCapabilitiesMatcher extends DefaultCapabilityMatcher {

    @Override
    public boolean matches(Map<String, Object> nodeCapability, Map<String, Object> requestedCapability) {
        boolean ios = requestedCapability.containsKey(IOSCapabilities.BUNDLE_NAME);
        if (ios) {
            return new IOSCapabilityMatcher().matches(nodeCapability, requestedCapability);
        }
        boolean android = isAndroid(requestedCapability);
        if (android) {
//            return new SelendroidCapabilityMatcher().matches(nodeCapability, requestedCapability);
        }
        return super.matches(nodeCapability, requestedCapability);
    }

    private boolean isAndroid(Map<String, Object> requestedCapability) {
        // first lets check if its a Native or a hybrid app
        boolean nativeApp = requestedCapability.containsKey("aut");
        String browser = (String) requestedCapability.get("browserName");
        boolean mobileWeb = BrowserType.ANDROID.equals(browser) || "selendroid".equals(browser);
        return nativeApp || mobileWeb;
    }
}
