/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016-2017 PayPal                                                                                     |
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

package com.paypal.selion.selendroid.platform.grid;


import com.paypal.selion.internal.platform.grid.MobileNodeType;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.AbstractMobileProvider;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import com.paypal.selion.selendroid.internal.platform.grid.browsercapabilities.SelendroidCapabilitiesBuilder;
import com.paypal.test.utilities.logging.SimpleLogger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.logging.Level;

/**
 * The Selion client mobileDriver provider for Selendroid.
 */
public class SelendroidMobileProvider extends AbstractMobileProvider {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    @Override
    public boolean supports(MobileNodeType nodeType) {
        return nodeType.equals(MobileNodeType.SELENDROID);
    }

    @Override
    public RemoteWebDriver createDriver(WebDriverPlatform platform, CommandExecutor commandExecutor, URL url,
                                          Capabilities caps) {

        RemoteWebDriver remoteWebDriver = null;
        if (platform.equals(WebDriverPlatform.ANDROID)) {
            try {
                if (commandExecutor == null) {
                    remoteWebDriver = new SeLionSelendroidDriver(url, caps);
                } else {
                    remoteWebDriver = new SeLionSelendroidDriver(commandExecutor, caps);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error creating instance of Selendroid RemoteWebDriver", e);
            }

        } else {
            throw new UnsupportedOperationException("This MobileDriver provider only supports Android Platform.");
        }
        return remoteWebDriver;
    }

    @Override
    public DefaultCapabilitiesBuilder capabilityBuilder() {
        return new SelendroidCapabilitiesBuilder();
    }

}
