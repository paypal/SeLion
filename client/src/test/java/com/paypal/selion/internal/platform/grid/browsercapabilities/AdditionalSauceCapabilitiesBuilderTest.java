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

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.internal.platform.grid.browsercapabilities.AdditionalSauceCapabilitiesBuilder;

public class AdditionalSauceCapabilitiesBuilderTest {
    private static String SAUCE_CONFIG_FILE = "sauceConfig.json";

    @Test
    public void test() {
        Config.setConfigProperty(ConfigProperty.SELENIUM_SAUCELAB_GRID_CONFIG_FILE, SAUCE_CONFIG_FILE);
        Config.setConfigProperty(ConfigProperty.SELENIUM_USE_SAUCELAB_GRID, "true");
        AdditionalSauceCapabilitiesBuilder builder = new AdditionalSauceCapabilitiesBuilder();
        DesiredCapabilities capabilities = builder.getCapabilities(new DesiredCapabilities());

        assertTrue(capabilities.getCapability("tunnel-identifier").equals(""));
        assertTrue(capabilities.getCapability("tags") instanceof List<?>);
        assertTrue(capabilities.getCapability("selenium-version") != null);
        assertTrue(capabilities.getCapability("username") != null);
        assertTrue(capabilities.getCapability("accessKey") != null);
        assertTrue(capabilities.getCapability("parent-tunnel") != null);
    }
}
