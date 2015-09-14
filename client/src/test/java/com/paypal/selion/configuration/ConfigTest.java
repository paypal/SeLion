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

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.paypal.selion.configuration.Config.ConfigProperty;

// Test requires execution with SeLionConfig-Suite.xml
public class ConfigTest {

    @Test(groups = { "unit" })
    public void testGetConfigProperty() {
        Assert.assertNotNull(Config.getConfigProperty(ConfigProperty.BROWSER), "Get config property should not be null");
    }

    @Test(groups = { "unit" })
    public void testInitConfig(ITestContext context) {
        Config.initConfig(context);
        Assert.assertNotNull(Config.getConfigProperty(ConfigProperty.BROWSER), "Config should not be null");
    }

    @Test(groups = { "unit" })
    public void testInitConfig_ChangeOption() {
        String value = Config.getConfigProperty(ConfigProperty.BROWSER);
        Map<ConfigProperty, String> initValues = new HashMap<ConfigProperty, String>();
        initValues.put(ConfigProperty.BROWSER, "new" + value);
        Config.initConfig(initValues);
        Assert.assertEquals(Config.getConfigProperty((ConfigProperty.BROWSER)), "new" + value,
                "BROWSER should be changed");
    }

}
