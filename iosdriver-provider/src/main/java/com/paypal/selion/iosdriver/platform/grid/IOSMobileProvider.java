/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

package com.paypal.selion.iosdriver.platform.grid;


import com.paypal.selion.internal.platform.grid.MobileNodeType;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.iosdriver.internal.platform.grid.browsercapabilities.IOSDriverCapabilitiesBuilder;
import com.paypal.selion.platform.grid.AbstractMobileProvider;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.IOSCapabilities;

import java.net.URL;

/**
 * The SeLion client mobileDriver provider for IOSDriver.
 */
public class IOSMobileProvider extends AbstractMobileProvider {

    @Override
    public boolean supports(MobileNodeType nodeType) {
        return nodeType.equals(MobileNodeType.IOS_DRIVER);
    }

    @Override
    public RemoteWebDriver createDriver(WebDriverPlatform platform, CommandExecutor commandExecutor, URL url,
                                          Capabilities caps) {
        RemoteWebDriver remoteWebDriver;
        if (platform.equals(WebDriverPlatform.IOS)) {
            if (commandExecutor == null) {
                remoteWebDriver = new SelionRemoteIOSDriver(url, (IOSCapabilities) caps);
            } else {
                remoteWebDriver = new SelionRemoteIOSDriver(commandExecutor, (IOSCapabilities) caps);
            }
        } else {
            throw new UnsupportedOperationException("This MobileDriver provider only supports IOS Platform.");
        }
        return remoteWebDriver;
    }

    @Override
    public DefaultCapabilitiesBuilder capabilityBuilder() {
        return new IOSDriverCapabilitiesBuilder();
    }

}
