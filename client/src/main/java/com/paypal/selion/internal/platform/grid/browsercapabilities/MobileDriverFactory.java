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

import com.paypal.selion.internal.platform.grid.MobileNodeType;
import com.paypal.selion.internal.platform.grid.MobileTestSession;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.EventFiringCommandExecutor;
import com.paypal.selion.platform.grid.EventListener;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.MobileProviderService;
import com.paypal.test.utilities.logging.SimpleLogger;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.List;
import java.util.logging.Level;


/**
 * This factory class is responsible for providing the framework with a {@link RemoteWebDriver} instance based on the
 * Mobile Node type.
 *
 */
public final class MobileDriverFactory {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    /**
     * @return A {@link RemoteWebDriver} instance based on the mobile node type
     */
    public static RemoteWebDriver createInstance() {

        DesiredCapabilities capability = new MobileCapabilitiesBuilder().createCapabilities();
        capability = new UserCapabilitiesBuilder().getCapabilities(capability);
        logger.log(Level.FINE, "Spawning a mobile with the following capabilitiesgit: "
                + DriverFactoryHelper.showCapabilities(capability));

        MobileTestSession mobileSession = Grid.getMobileTestSession();
        MobileNodeType mobileNodeType = mobileSession.getMobileNodeType();
        WebDriverPlatform webDriverPlatform = mobileSession.getPlatform();
        URL url = DriverFactoryHelper.getURL();

        List<EventListener> listeners = DriverFactoryHelper.getSeLionEventListeners();
        RemoteWebDriver driver = null;
        if (listeners.size() > 0) {
            driver = MobileProviderService.getInstance().createDriver(mobileNodeType, webDriverPlatform,
                new EventFiringCommandExecutor(new HttpCommandExecutor(url), listeners), url, capability);
        } else {
            driver = MobileProviderService.getInstance().createDriver(mobileNodeType, webDriverPlatform,
                null, url, capability);
        }

        DriverFactoryHelper.printDebugInfoForUser(driver);
        return driver;
    }

}
