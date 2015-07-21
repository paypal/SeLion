/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.BrowserFlavors;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This factory class is responsible for providing the framework with a {@link DesiredCapabilities} instance based on
 * the browser type.
 * 
 */
public final class DesiredCapabilitiesFactory {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private DesiredCapabilitiesFactory() {
        // Utility class. So hide the constructor
    }

    /**
     * @param browser
     *            A {@link BrowserFlavors} enum that represents the browser flavor for which capabilities are being
     *            requested. The values that are recognized
     * @return A {@link DesiredCapabilities} object that represents the capabilities the browser to be spawned must
     *         possess.
     */
    public static DesiredCapabilities getCapabilities(BrowserFlavors browser) {
        logger.entering(browser);
        // By default lets create a FF Capability and then overwrite it based on the actual browser.
        // This is to ensure that sonar doesnt trigger a warning saying "Possible null pointer dereference
        DesiredCapabilities capabilities = null;

        switch (browser) {
        case FIREFOX:
            capabilities = new FireFoxCapabilitiesBuilder().createCapabilities();
            break;
        case CHROME:
            capabilities = new ChromeCapabilitiesBuilder().createCapabilities();
            break;
        case INTERNET_EXPLORER:
            capabilities = new IECapabilitiesBuilder().createCapabilities();
            break;
        case HTMLUNIT:
            capabilities = new HtmlUnitCapabilitiesBuilder().createCapabilities();
            break;
        case IPHONE:
            capabilities = new IPhoneCapabilitiesBuilder().createCapabilities();
            break;
        case IPAD:
            capabilities = new IPadCapabilitiesBuilder().createCapabilities();
            break;
        case OPERA:
            capabilities = new OperaCapabilitiesBuilder().createCapabilities();
            break;
        case PHANTOMJS:
            capabilities = new PhantomJSCapabilitiesBuilder().createCapabilities();
            break;
        case SAFARI:
            capabilities = new SafariCapabilitiesBuilder().createCapabilities();
            break;
        case GENERIC:
            capabilities = new GenericCapabilitiesBuilder().createCapabilities();
            break;
        default:
            // There is never a chance that we reach here, because we are being controlled by the
            // enum and it takes care of throwing exceptions whenever a user passes an invalid browser name
            break;
        }
        capabilities = new UserCapabilitiesBuilder().getCapabilities(capabilities);
        logger.exiting(capabilities);
        return capabilities;
    }
}
