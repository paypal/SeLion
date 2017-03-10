/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2017 PayPal                                                                                     |
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

package com.paypal.selion.appium.internal.platform.grid.browsercapabilities;

import io.appium.java_client.remote.MobileCapabilityType;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.internal.platform.grid.MobileTestSession;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

public class AppiumCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    private static final String APPIUM_ANDROID_PLATFORM_TYPE = "ANDROID";
    private static final String APPIUM_IOS_PLATFORM_TYPE = "iOS";
    private static final String MOBILE_NODE_TYPE = "mobileNodeType";
    private static final String ANDROID_APP_MAIN_ACTIVITY = "appActivity";
    private static final String ANDROID_APP_WAIT_ACTIVITY = "appWaitActivity";
    private static final String ANDROID_APP_PACKAGE = "appPackage";
    private static final String ANDROID_APP_WAIT_PACKAGE = "appWaitPackage";

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {

        MobileTestSession mobileSession = Grid.getMobileTestSession();

        capabilities.setCapability(MobileCapabilityType.APP, mobileSession.getAppName());
        if (StringUtils.isBlank(mobileSession.getAppName())) {
            capabilities.setCapability(MobileCapabilityType.APP, mobileSession.getAppPath());
        }
        if (StringUtils.isNotBlank(mobileSession.getAndroidAppMainActivity())) {
            capabilities.setCapability(ANDROID_APP_MAIN_ACTIVITY, mobileSession.getAndroidAppMainActivity());
            capabilities.setCapability(ANDROID_APP_WAIT_ACTIVITY, mobileSession.getAndroidAppMainActivity());
        }
        if (StringUtils.isNotBlank(mobileSession.getAndroidAppPackage())) {
            capabilities.setCapability(ANDROID_APP_PACKAGE, mobileSession.getAndroidAppPackage());
            capabilities.setCapability(ANDROID_APP_WAIT_PACKAGE, mobileSession.getAndroidAppPackage());
        }

        capabilities.setCapability(MOBILE_NODE_TYPE, mobileSession.getMobileNodeType().getAsString());
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, mobileSession.getDeviceType());
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, mobileSession.getPlatformVersion());
        capabilities.setCapability(MobileCapabilityType.LOCALE, mobileSession.getAppLocale());
        capabilities.setCapability(MobileCapabilityType.LANGUAGE, mobileSession.getAppLanguage());
        if (mobileSession.getPlatform() == WebDriverPlatform.ANDROID) {
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, APPIUM_ANDROID_PLATFORM_TYPE);
        }
        if (mobileSession.getPlatform() == WebDriverPlatform.IOS) {
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, APPIUM_IOS_PLATFORM_TYPE);
        }

        return capabilities;
    }
}
