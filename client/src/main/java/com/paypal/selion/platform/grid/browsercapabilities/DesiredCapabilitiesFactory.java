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

import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.configuration.ExtendedConfig;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.BrowserFlavors;
import com.paypal.selion.platform.grid.Grid;
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
        DefaultCapabilitiesBuilder capabilitiesBuilder = new FireFoxCapabilitiesBuilder();

        switch (browser) {
        case FIREFOX:
            break;
        case CHROME:
            capabilitiesBuilder = new ChromeCapabilitiesBuilder();
            break;
        case INTERNET_EXPLORER:
            capabilitiesBuilder = new IECapabilitiesBuilder();
            break;
        case HTMLUNIT:
            capabilitiesBuilder = new HtmlUnitCapabilitiesBuilder();
            break;
        case IPHONE:
            capabilitiesBuilder = new IPhoneCapabilitiesBuilder();
            break;
        case IPAD:
            capabilitiesBuilder = new IPadCapabilitiesBuilder();
            break;
        case OPERA:
            capabilitiesBuilder = new OperaCapabilitiesBuilder();
            break;
        case PHANTOMJS:
            capabilitiesBuilder = new PhantomJSCapabilitiesBuilder();
            break;
        case SAFARI:
            capabilitiesBuilder = new SafariCapabilitiesBuilder();
            break;
        case GENERIC:
            capabilitiesBuilder = new GenericCapabilitiesBuilder();
            break;
        default:
            // There is never a chance that we reach here, because we are being controlled by the
            // enum and it takes care of throwing exceptions whenever a user passes an invalid browser name
            break;
        }

        DesiredCapabilities capability = capabilitiesBuilder.createCapabilities();

        capability.setCapability(ExtendedConfig.TEST_NAME.getConfig(), Grid.getTestSession().getTestName());

        // Lets check if the user provided more capabilities via the Configuration parameter and add them
        for (DesiredCapabilities eachCaps : CapabilitiesHelper.retrieveCustomCapsObjects()) {
            capability.merge(eachCaps);
        }

        // Lets check if the user provided more capabilities via ServiceLoaders and add them
        for (DesiredCapabilities eachCaps : CapabilitiesHelper.retrieveCustomCapsViaServiceLoaders()) {
            capability.merge(eachCaps);
        }

        logger.exiting(capability);
        return capability;
    }
}
