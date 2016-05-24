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
package com.paypal.selion.internal.platform.grid.browsercapabilities;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * This class represents the capabilities that are specific to the Gecko/marionette (PKA wires) driver.
 */
class GeckoCapabilitiesBuilder extends FireFoxCapabilitiesBuilder {

    public static final String MARIONETTE = "marionette";

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        DesiredCapabilities caps = super.getCapabilities(capabilities);
        caps.setCapability(MARIONETTE, true);

        String geckoDriverPath = getBinaryPath();
        if (isLocalRun() && StringUtils.isNotBlank(geckoDriverPath)) {
            System.setProperty(SeLionConstants.WEBDRIVER_GECKO_DRIVER_PROPERTY, geckoDriverPath);
        }
        return caps;
    }

    /*
     * Returns the location of Gecko/marionette driver or empty string if it cannot be determined.
     */
    private String getBinaryPath() {
        String location = System.getProperty(SeLionConstants.WEBDRIVER_GECKO_DRIVER_PROPERTY,
                Config.getConfigProperty(ConfigProperty.SELENIUM_GECKODRIVER_PATH));
        return location;
    }
}
