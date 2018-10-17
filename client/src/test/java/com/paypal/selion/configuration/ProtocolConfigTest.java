/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2018 PayPal                                                                                          |
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

// Test requires execution with SeLionProtocolConfig-Suite.xml
public class ProtocolConfigTest {
	
    @Test(groups = { "protocol" }, priority = 1)
    public void testGetProtocalProperty() {
        Assert.assertNotNull(Config.getConfigProperty(ConfigProperty.SELENIUM_PROTOCOL), "Get config property should not be null");
    }

    @Test(groups = { "protocol" }, priority = 2)
    public void testInitProtocalConfig(ITestContext context) {
        Config.initConfig(context);
        Assert.assertNotNull(Config.getConfigProperty(ConfigProperty.SELENIUM_PROTOCOL), "Config should not be null");
    }

    @Test(groups = { "protocol" }, priority = 3)
    public void testInitProtocalConfigNegative() {
    	Assert.assertFalse(Config.getConfigProperty(ConfigProperty.SELENIUM_PROTOCOL).contentEquals("http"),
    			"Negative-TC: PROTOCOL should match the suite-file. (https) is set in suite file -> not (http)");
    	
    }

    @Test(groups = { "protocol" }, priority = 4)
    public void testInitProtocalConfigChangeOption() {
    	Assert.assertEquals(Config.getConfigProperty(ConfigProperty.SELENIUM_PROTOCOL), "https",
                "PROTOCOL should be (https)");
        Map<ConfigProperty, String> initValues = new HashMap<ConfigProperty, String>();
        initValues.put(ConfigProperty.SELENIUM_PROTOCOL, "http");
        Config.initConfig(initValues);
        Assert.assertEquals(Config.getConfigProperty(ConfigProperty.SELENIUM_PROTOCOL), "http",
                "PROTOCOL should be (http)");
    }
    
    
}
