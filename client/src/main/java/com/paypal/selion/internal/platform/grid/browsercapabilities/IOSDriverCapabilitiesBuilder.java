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

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceVariation;

import com.paypal.selion.internal.platform.grid.MobileTestSession;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

class IOSDriverCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    private static final String MOBILE_NODE_TYPE = "mobileNodeType";

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        IOSCapabilities iOSCapabilities = new IOSCapabilities();
        setMandatoryCapabilitiesFor(iOSCapabilities);
        setOptionalCapabilitiesFor(iOSCapabilities);
        iOSCapabilities.merge(capabilities);
        return iOSCapabilities;
    }

    private void setMandatoryCapabilitiesFor(IOSCapabilities iOSCapabilities) {
        MobileTestSession mobileSession = Grid.getMobileTestSession();
        iOSCapabilities.setCapability(IOSCapabilities.DEVICE, mobileSession.getDevice());
        iOSCapabilities.setCapability(IOSCapabilities.LANGUAGE, mobileSession.getAppLanguage());
        iOSCapabilities.setCapability(IOSCapabilities.LOCALE, mobileSession.getAppLocale());
        iOSCapabilities.setCapability(IOSCapabilities.BUNDLE_NAME, mobileSession.getAppName());
        iOSCapabilities.setCapability(MOBILE_NODE_TYPE, mobileSession.getMobileNodeType().getAsString());
    }

    private void setOptionalCapabilitiesFor(IOSCapabilities iOSCapabilities) {
        MobileTestSession mobileSession = Grid.getMobileTestSession();
        if (StringUtils.isNotBlank(mobileSession.getAppVersion())) {
            iOSCapabilities.setCapability(IOSCapabilities.BUNDLE_VERSION, mobileSession.getAppVersion());
        }
        if (StringUtils.isNotBlank(mobileSession.getDeviceType())) {
            iOSCapabilities.setCapability(IOSCapabilities.VARIATION,
                    DeviceVariation.valueOf(mobileSession.getDeviceType()));
        }
    }

}
