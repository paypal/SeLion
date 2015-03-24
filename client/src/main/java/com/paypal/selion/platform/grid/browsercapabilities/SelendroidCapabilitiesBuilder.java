/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

package com.paypal.selion.platform.grid.browsercapabilities;

import java.io.File;

import io.selendroid.common.SelendroidCapabilities;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.MobileTestSession;

class SelendroidCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    private String MOBILE_NODE_TYPE = "mobileNodeType";
    private String SELENDROID = "selendroid";
    private String ANDROID = "android";

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {

        MobileTestSession mobileSession = Grid.getMobileTestSession();
        capabilities = SelendroidCapabilities.android();

        // check if apk exists for native app to set BrowserName to 'selendroid'
        // else set it to 'android'
        if ((new File(mobileSession.getAppLocation()).exists())
                && ((new File(mobileSession.getAppLocation() + File.separator + mobileSession.getAppName())).exists())) {
            capabilities.setBrowserName(SELENDROID);
        } else {
            capabilities.setBrowserName(ANDROID);
        }

        capabilities.setCapability(MOBILE_NODE_TYPE, SELENDROID);
        capabilities.setCapability(SelendroidCapabilities.AUT, mobileSession.getAppName());
        capabilities.setCapability(SelendroidCapabilities.LOCALE, mobileSession.getAppLocale());
        if (StringUtils.isNotBlank(mobileSession.getDeviceType())) {
            capabilities.setCapability(SelendroidCapabilities.MODEL, mobileSession.getDeviceType());
        }
        if (StringUtils.isNotBlank(mobileSession.getPlatformVersion())) {
            capabilities.setCapability(SelendroidCapabilities.PLATFORM_VERSION, mobileSession.getPlatformVersion());
        }
        if (StringUtils.isNotBlank(mobileSession.getdeviceSerial())) {
            capabilities.setCapability(SelendroidCapabilities.SERIAL, mobileSession.getdeviceSerial());
        }
        return capabilities;
    }
}
