/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2017 PayPal                                                                                          |
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

package com.paypal.selion.appium.platform.grid;

import com.paypal.selion.internal.platform.grid.MobileNodeType;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.AbstractMobileProvider;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import com.paypal.selion.appium.internal.platform.grid.browsercapabilities.AppiumCapabilitiesBuilder;
import com.paypal.test.utilities.logging.SimpleLogger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.logging.Level;

/**
 * The Selion client mobile provider implementation for Appium.
 */
public class SeLionAppiumProvider extends AbstractMobileProvider {

    private final static SimpleLogger logger = SeLionLogger.getLogger();

    @Override
    public boolean supports(MobileNodeType nodeType) {
        return nodeType.equals(MobileNodeType.APPIUM);
    }

    /**
     * create an instance of SeLionAppiumIOSDriver or SeLionAppiumAndroidDriver
     */
    @Override
    public RemoteWebDriver createDriver(WebDriverPlatform platform, CommandExecutor commandExecutor,
                                          URL url, Capabilities caps) {

        if (platform.equals(WebDriverPlatform.ANDROID)) {
            if (commandExecutor == null) {
                return new SeLionAppiumAndroidDriver(url, caps);
            } else {
                return new SeLionAppiumAndroidDriver(commandExecutor, caps, url);
            }
        } else if (platform.equals(WebDriverPlatform.IOS)) {
            if (commandExecutor == null) {
                return new SeLionAppiumIOSDriver(url, caps);
            } else {
                return new SeLionAppiumIOSDriver(commandExecutor, caps, url);
            }
        }
        logger.log(Level.SEVERE, "Error creating instance of Appium RemoteWebDriver for " + platform);
        return null;
    }

    /**
     * create an instance of The Appium capabilities builder.
     */
    @Override
    public DefaultCapabilitiesBuilder capabilityBuilder() {
        return new AppiumCapabilitiesBuilder();
    }

}
