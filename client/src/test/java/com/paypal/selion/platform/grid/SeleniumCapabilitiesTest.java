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

package com.paypal.selion.platform.grid;

import static org.testng.Assert.*;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.platform.grid.Grid;

@Test(groups = "SeleniumCapabilitiesTest")
public class SeleniumCapabilitiesTest {
    @Test(groups = { "functional", "firefox-only" })
    @WebTest(browser = "*firefox")
    public void testDefaultBrowser() {
        String userAgent = (String) Grid.driver().executeScript("return navigator.userAgent", "");
        assertTrue(userAgent.toLowerCase().contains("firefox"));
    }

    @Test(groups = "functional")
    @WebTest()
    public void testDefaultBrowser1(ITestContext ctx) {
        String userAgent = (String) Grid.driver().executeScript("return navigator.userAgent", "");
        String browser = ConfigManager.getConfig(ctx.getCurrentXmlTest().getName())
                .getConfigProperty(ConfigProperty.BROWSER).replace("*", "");
        assertTrue(userAgent.toLowerCase().contains(browser));
    }

    @Test(groups = "functional")
    @WebTest(browser = "")
    public void testDefaultBrowser2(ITestContext ctx) {
        String userAgent = (String) Grid.driver().executeScript("return navigator.userAgent", "");
        String browser = ConfigManager.getConfig(ctx.getCurrentXmlTest().getName())
                .getConfigProperty(ConfigProperty.BROWSER).replace("*", "");
        assertTrue(userAgent.toLowerCase().contains(browser));
    }

    @Test(groups = { "functional", "ie-only" })
    @WebTest(browser = "*iexplore")
    public void testIexploreBrowser() {
        String userAgent = (String) Grid.driver().executeScript("return navigator.userAgent", "");
        assertTrue(userAgent.toLowerCase().contains("msie"));
    }

    @Test(groups = "functional")
    @WebTest(browser = "*chrome")
    public void testChromeBrowser() {
        String userAgent = (String) Grid.driver().executeScript("return navigator.userAgent", "");
        assertTrue(userAgent.toLowerCase().contains("chrome"));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    @WebTest(browser = "*mybrowser")
    public void testWrongBrowser() {
        Grid.driver().executeScript("return navigator.userAgent", "");
        fail("No such browser and hence and IllegalArgumentException should have been thrown.");
    }

}
