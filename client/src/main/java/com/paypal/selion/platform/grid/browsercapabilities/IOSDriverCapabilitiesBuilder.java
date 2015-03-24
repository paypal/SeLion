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

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceVariation;

import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.MobileTestSession;

class IOSDriverCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    private String MOBILE_NODE_TYPE = "mobileNodeType";
    private String IOS_DRIVER = "ios-driver";
    
    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {

        MobileTestSession mobileSession = Grid.getMobileTestSession();
        capabilities.setCapability(IOSCapabilities.DEVICE, mobileSession.getDevice());
        capabilities.setCapability(IOSCapabilities.LANGUAGE, mobileSession.getAppLanguage());
        capabilities.setCapability(IOSCapabilities.LOCALE, mobileSession.getAppLocale());
        capabilities.setCapability(IOSCapabilities.BUNDLE_NAME, mobileSession.getAppName());
        capabilities.setCapability(MOBILE_NODE_TYPE, IOS_DRIVER);
        if (StringUtils.isNotBlank(mobileSession.getAppVersion())) {
            capabilities.setCapability(IOSCapabilities.BUNDLE_VERSION, mobileSession.getAppVersion());
        }
        if (StringUtils.isNotBlank(mobileSession.getPlatformVersion())) {
            capabilities.setCapability(IOSCapabilities.UI_SDK_VERSION, mobileSession.getPlatformVersion());
        }
        if (StringUtils.isNotBlank(mobileSession.getDeviceType())) {
            capabilities.setCapability(IOSCapabilities.VARIATION,
                    DeviceVariation.valueOf(mobileSession.getDeviceType()));
        }
        return capabilities;
    }
}
