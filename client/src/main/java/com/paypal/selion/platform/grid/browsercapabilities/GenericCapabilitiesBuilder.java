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

package com.paypal.selion.platform.grid.browsercapabilities;

import io.selendroid.SelendroidCapabilities;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.uiautomation.ios.IOSCapabilities;

import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.MobileTestSession;
import org.uiautomation.ios.communication.device.DeviceVariation;

class GenericCapabilitiesBuilder extends DefaultCapabilitiesBuilder {
    private String appName = null;
    private String device = null;
    private String deviceType = null;
    private String language = null;
    private String locale = null;
    private String deviceSerial = null;
    private String platformVersion = null;

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        if (Grid.getMobileTestSession() == null) {
            return capabilities;
        }
        initCapabilities();
        DesiredCapabilities caps = null;

        if (this.device.toLowerCase().contains("ipad") || this.device.toLowerCase().contains("iphone")) {
            caps = new IOSCapabilities();

            String appVersion = null;
            if (StringUtils.isNotBlank(appName) && StringUtils.contains(this.appName, ":")) {
                String[] args = StringUtils.split(this.appName,":");
                //check to make sure only we only get 2 params
                if (args == null || args.length != 2) {
                    throw new IllegalArgumentException("The appName should either be provided as 'app:version'. " +
                            "Ex 'Safari:7.0', 'ebay:2.0'.");
                }

                appVersion = args[1];
                appName = args[0];
            }

            caps.setCapability(IOSCapabilities.DEVICE, device);
            caps.setCapability(IOSCapabilities.LANGUAGE, this.language);
            caps.setCapability(IOSCapabilities.LOCALE, this.locale);
            caps.setCapability(IOSCapabilities.BUNDLE_NAME, appName);
            if (StringUtils.isNotBlank(appVersion)) {
                caps.setCapability(IOSCapabilities.BUNDLE_VERSION, appVersion);
            }
            if (StringUtils.isNotBlank(platformVersion)) {
                caps.setCapability(IOSCapabilities.UI_SDK_VERSION, platformVersion);
            }
            if (StringUtils.isNotBlank(deviceType)) {
                caps.setCapability(IOSCapabilities.VARIATION, DeviceVariation.valueOf(deviceType));
            }
        } else if (this.device.toLowerCase().contains("android")) {
            caps = SelendroidCapabilities.android();
            caps.setBrowserName("selendroid");
            caps.setCapability(SelendroidCapabilities.AUT, appName);
            caps.setCapability(SelendroidCapabilities.PLATFORM_VERSION, platformVersion);
            caps.setCapability(SelendroidCapabilities.LOCALE, this.locale);
            if(!this.deviceSerial.isEmpty()) {
                caps.setCapability(SelendroidCapabilities.SERIAL, this.deviceSerial);
            }
        }
        return caps;
    }

    private void initCapabilities() {
        MobileTestSession config = Grid.getMobileTestSession();
        if (config != null) {
            this.appName = config.getAppName();
            this.device = config.getDevice();
            this.language = config.getAppLanguage();
            this.locale = config.getAppLocale();
            this.deviceSerial = config.getdeviceSerial();
            this.deviceType = config.getDeviceType();
            this.platformVersion = config.getPlatformVersion();
        }
    }
}
