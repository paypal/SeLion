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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.platform.grid.Grid;

public class LocalConfigTest {

    @Test(groups = "unit")
    public void testLocalInitValues() {
        Map<ConfigProperty, String> initLocalValues = new HashMap<ConfigProperty, String>();
        String localUserAgentValue = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML; like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";
        initLocalValues.put(ConfigProperty.SELENIUM_USERAGENT, localUserAgentValue);
        LocalConfig localConfig = new LocalConfig(initLocalValues);
        String testName = "test";
        ConfigManager.addConfig(testName, localConfig);
        assertEquals(localConfig.getConfigProperty(ConfigProperty.SELENIUM_USERAGENT), localUserAgentValue,
                "Hostname value not as expected");
        String userAgent = Config.getConfigProperty(ConfigProperty.SELENIUM_USERAGENT);
        assertNotNull(userAgent);
        assertTrue(!userAgent.equals(localUserAgentValue),
                "hostname values in localConfig and selionConfig should not match");
        // cleanup
        ConfigManager.removeConfig(testName);
    }

    @Test(groups = "unit")
    public void testLocalGetConfigProperty() {
        LocalConfig localConfig = new LocalConfig();
        String portNumber = localConfig.getConfigProperty(ConfigProperty.SELENIUM_PORT);
        assertTrue(!portNumber.isEmpty(), "selenium port returned by getConfigProperty cannot be empty.");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testLocalGetConfigNullValue() throws Exception {
        LocalConfig localConfig = new LocalConfig();
        localConfig.getConfigProperty(null);
    }

    @Test(groups = { "parallelBrowserTest1" })
    public void testGetLocalConfigValues(ITestContext ctx) {
        LocalConfig lc = ConfigManager.getConfig(ctx.getCurrentXmlTest().getName());
        Map<String, String> values = lc.getLocalConfigValues();
        assertTrue(values.get("browser").equals("*chrome"));
    }

    @Test(groups = { "parallelBrowserTest1" })
    public void testgetLocalConfigValues(ITestContext ctx) throws Exception {
        LocalConfig lc = ConfigManager.getConfig(ctx.getCurrentXmlTest().getName());
        Map<String, String> localValues = lc.getLocalConfigValues();
        assertTrue(!localValues.isEmpty());
    }

    // Parallel=tests with different browsers. (See SeLionConfigTest-Parallel-Tests-Suite.xml)
    @Test(groups = "parallelBrowserTest1")
    public void testParallelLocalConfigChrome(ITestContext ctx) {
        String name = ctx.getCurrentXmlTest().getName();
        String default_browser = ConfigManager.getConfig(name).getConfigProperty(ConfigProperty.BROWSER);
        assertEquals(default_browser, "*chrome", "Browser config value for this test is not chrome.");
    }

    @Test(groups = "parallelBrowserTest2")
    public void testParallelLocalConfigIE(ITestContext ctx) {
        String name = ctx.getCurrentXmlTest().getName();
        String default_browser = ConfigManager.getConfig(name).getConfigProperty(ConfigProperty.BROWSER);
        assertEquals(default_browser, "*iexplore", "Browser config value for this test is not iexplore.");
    }

    @Test(groups = "parallelBrowserTest3")
    public void testParallelLocalConfigFireFox(ITestContext ctx) {
        String name = ctx.getCurrentXmlTest().getName();
        String default_browser = ConfigManager.getConfig(name).getConfigProperty(ConfigProperty.BROWSER);
        assertEquals(default_browser, "*firefox", "Browser config value for this test is not firefox.");
    }

    @Test(groups = { "parallelBrowserTest1", "parallelBrowserTest2", "parallelBrowserTest3" })
    @WebTest
    public void testCorrectBrowserLaunched(ITestContext ctx) {
        Grid.driver().get("http://www.google.com");
        String userAgent = (String) Grid.driver().executeScript("return navigator.userAgent", "");
        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
        ReadableUserAgent agent = parser.parse(userAgent);
        String actualBrowser = agent.getName().toLowerCase();
        // Read suite param directly.
        String browserParam = ctx.getCurrentXmlTest().getParameter("browser");
        // Ensure you've configured this test to run with browser param in suite file.
        assertTrue(!browserParam.isEmpty());
        // Drop the "* in browser and first char"
        if ("*iexplore".equals(browserParam)) {
            //Only for IE the user agent parsing library returns it as IE.
            assertTrue(actualBrowser.equalsIgnoreCase("ie"));
        }else {
            assertTrue(actualBrowser.contains(browserParam.substring(1).toLowerCase()));
        }
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testExceptionWhenAddingGlobalScopePropToLocalConfigThroughConstructor() {
        Map<ConfigProperty, String> dummyMap = new HashMap<ConfigProperty, String>();
        dummyMap.put(getPropertiesWithGlobalScope().get(0), "foo");
        new LocalConfig(dummyMap);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testExceptionWhenAddingGlobalScopePropToLocalConfigThroughConstructor1() {
        new LocalConfig(getPropertiesWithGlobalScope().get(0), "foo");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testExceptionWhenAddingGlobalScopePropToLocalConfigThroughSetter() {
        LocalConfig cfg = new LocalConfig();
        cfg.setConfigProperty(getPropertiesWithGlobalScope().get(0), "foo");
    }

    public static List<ConfigProperty> getPropertiesWithGlobalScope() {
        List<ConfigProperty> props = new ArrayList<ConfigProperty>();
        for (ConfigProperty eachProp : ConfigProperty.values()) {
            if (eachProp.isGlobalScopeOnly()) {
                props.add(eachProp);
            }
        }
        return props;
    }

}
