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

package com.paypal.selion.internal.platform.grid.browsercapabilities;

import io.appium.java_client.remote.MobileCapabilityType;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.internal.platform.grid.MobileTestSession;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

class AppiumCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    private static final String APPIUM_ANDROID_PLATFORM_TYPE = "ANDROID";
    private static final String APPIUM_IOS_PLATFORM_TYPE = "iOS";
    private static final String MOBILE_NODE_TYPE = "mobileNodeType";
    private static final String APPIUM_LOCALE_TYPE = "locale";
    private static final String APPIUM_LANGUAGE_TYPE = "language";

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {

        MobileTestSession mobileSession = Grid.getMobileTestSession();

        capabilities.setCapability(MobileCapabilityType.APP, mobileSession.getAppName());
        if (StringUtils.isBlank(mobileSession.getAppName())) {
            capabilities.setCapability(MobileCapabilityType.APP, mobileSession.getAppPath());
        }
        capabilities.setCapability(MOBILE_NODE_TYPE, mobileSession.getMobileNodeType().getAsString());
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, mobileSession.getDeviceType());
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, mobileSession.getPlatformVersion());
        capabilities.setCapability(APPIUM_LOCALE_TYPE, mobileSession.getAppLocale());
        capabilities.setCapability(APPIUM_LANGUAGE_TYPE, mobileSession.getAppLanguage());
        if (mobileSession.getPlatform() == WebDriverPlatform.ANDROID) {
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, APPIUM_ANDROID_PLATFORM_TYPE);
        }
        if (mobileSession.getPlatform() == WebDriverPlatform.IOS) {
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, APPIUM_IOS_PLATFORM_TYPE);
        }

        return capabilities;
    }
}
