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

import java.net.URL;
import java.util.List;
import java.util.logging.Level;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.paypal.selion.internal.platform.grid.BrowserFlavors;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.EventFiringCommandExecutor;
import com.paypal.selion.platform.grid.EventListener;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This factory class is responsible for providing the framework with a {@link RemoteWebDriver} instance based on the
 * browser type.
 * 
 */
public final class WebDriverFactory {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    public static RemoteWebDriver createInstance() {
        return createInstance(BrowserFlavors.getBrowser(Grid.getWebTestSession().getBrowser()));
    }

    /**
     * @param browser
     *            - enum that represents the browser flavor for which capabilities are being requested.
     * @return A {@link RemoteWebDriver} instance based on the browser type
     */
    public static RemoteWebDriver createInstance(BrowserFlavors browser) {
        DesiredCapabilities capability = null;

        RemoteWebDriver driver = null;
        switch (browser) {
        case FIREFOX:
            capability = new FireFoxCapabilitiesBuilder().createCapabilities();
            break;
        case CHROME:
            capability = new ChromeCapabilitiesBuilder().createCapabilities();
            break;
        case INTERNET_EXPLORER:
            capability = new IECapabilitiesBuilder().createCapabilities();
            break;
        case MICROSOFT_EDGE:
            capability = new EdgeCapabilitiesBuilder().createCapabilities();
            break;
        case HTMLUNIT:
            capability = new HtmlUnitCapabilitiesBuilder().createCapabilities();
            break;
        case OPERA:
            capability = new OperaCapabilitiesBuilder().createCapabilities();
            break;
        case PHANTOMJS:
            capability = new PhantomJSCapabilitiesBuilder().createCapabilities();
            break;
        case SAFARI:
            capability = new SafariCapabilitiesBuilder().createCapabilities();
            break;
        default:
            break;
        }
        capability = new UserCapabilitiesBuilder().getCapabilities(capability);
        logger.log(Level.FINE, "Spawning a browser with the following capabilities: "
                + DriverFactoryHelper.showCapabilities(capability));
        driver = createDriverInstance(DriverFactoryHelper.getURL(), capability);
        DriverFactoryHelper.printDebugInfoForUser(driver);
        return driver;
    }

    private static RemoteWebDriver createDriverInstance(URL url, DesiredCapabilities capability) {

        List<EventListener> listeners = DriverFactoryHelper.getSeLionEventListeners();
        RemoteWebDriver driver = null;
        if (listeners.size() > 0) {
            driver = new RemoteWebDriver(new EventFiringCommandExecutor(new HttpCommandExecutor(url), listeners),
                    capability);
        } else {
            driver = new RemoteWebDriver(url, capability);
        }
        DriverFactoryHelper.setWindowSize(driver);
        DriverFactoryHelper.registerElementEventListeners();
        return driver;

    }
}
