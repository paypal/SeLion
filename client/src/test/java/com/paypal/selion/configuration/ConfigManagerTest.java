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

package com.paypal.selion.configuration;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.internal.platform.grid.BrowserFlavors;

public class ConfigManagerTest {
    private static final String TEST_CONFIG_NAME = "Test1";

    LocalConfig localConfig = new LocalConfig();
    String browserValue = BrowserFlavors.OPERA.getBrowser();

    @Test(groups = { "unit" })
    public void testAddConfig() {
        assertNotNull(localConfig, "could not get the SeLion local config");
        // Set new values in local config
        localConfig.setConfigProperty(ConfigProperty.BROWSER, BrowserFlavors.OPERA.getBrowser());
        ConfigManager.addConfig(TEST_CONFIG_NAME, localConfig);
    }

    @Test(groups = { "unit" }, dependsOnMethods = { "testAddConfig" })
    public void testGetConfig() {
        assertNotNull(localConfig, "Could not get the SeLion local config");
        LocalConfig testConfig = ConfigManager.getConfig(TEST_CONFIG_NAME);
        assertNotNull(testConfig);
        String newBrowserValue = testConfig.getConfigProperty(ConfigProperty.BROWSER);
        assertTrue(newBrowserValue.equals(browserValue), "value from local config is not equal to the value set");

    }

    @Test(groups = { "unit" }, dependsOnMethods = { "testGetConfig", "testAddConfig" })
    public void testRemoveConfig() {
        assertTrue(ConfigManager.removeConfig(TEST_CONFIG_NAME), "Remove config failed");
    }

    @Test(groups = { "unit" }, dependsOnMethods = { "testAddConfig" })
    public void testGetConfigProperty(ITestContext ctx) {
        String name = ctx.getCurrentXmlTest().getName();
        String browser = ConfigManager.getConfig(name).getConfigProperty(ConfigProperty.BROWSER);
        assertNotNull(browser);
    }
}
