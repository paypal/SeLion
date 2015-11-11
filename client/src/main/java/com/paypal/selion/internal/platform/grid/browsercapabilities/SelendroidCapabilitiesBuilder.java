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

import java.io.File;

import io.selendroid.common.SelendroidCapabilities;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.internal.platform.grid.MobileTestSession;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

class SelendroidCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    private static final String MOBILE_NODE_TYPE = "mobileNodeType";
    private static final String SELENDROID = "selendroid";
    private static final String ANDROID = "android";

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {

        MobileTestSession mobileSession = Grid.getMobileTestSession();
        DesiredCapabilities tempCapabilities = SelendroidCapabilities.android();

        // check if apk exists for native app to set BrowserName to 'selendroid'
        // else set it to 'android'
        if ((new File(mobileSession.getAppLocation()).exists())
                && ((new File(mobileSession.getAppLocation() + File.separator + mobileSession.getAppName())).exists())) {
            tempCapabilities.setBrowserName(SELENDROID);
        } else {
            tempCapabilities.setBrowserName(ANDROID);
        }

        tempCapabilities.setCapability(MOBILE_NODE_TYPE, mobileSession.getMobileNodeType().getAsString());
        tempCapabilities.setCapability(SelendroidCapabilities.AUT, mobileSession.getAppName());
        tempCapabilities.setCapability(SelendroidCapabilities.LOCALE, mobileSession.getAppLocale());
        if (StringUtils.isNotBlank(mobileSession.getDeviceType())) {
            tempCapabilities.setCapability(SelendroidCapabilities.MODEL, mobileSession.getDeviceType());
        }
        if (StringUtils.isNotBlank(mobileSession.getPlatformVersion())) {
            tempCapabilities.setCapability(SelendroidCapabilities.PLATFORM_VERSION, mobileSession.getPlatformVersion());
        }
        if (StringUtils.isNotBlank(mobileSession.getdeviceSerial())) {
            tempCapabilities.setCapability(SelendroidCapabilities.SERIAL, mobileSession.getdeviceSerial());
        }
        return tempCapabilities;
    }
}
