/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2017 PayPal                                                                                          |
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

import static org.testng.Assert.*;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.configuration.LocalConfig;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;


public class HeadlessChromeCapabilitiesBuilderTest {
    @Test
    public void testIsHeadless(ITestContext ctx) {
        //first set a local config to not run this @test headless
        LocalConfig lc = new LocalConfig();
        lc.setConfigProperty(Config.ConfigProperty.BROWSER_RUN_HEADLESS, "false");
        ConfigManager.addConfig(ctx.getCurrentXmlTest().getName(), lc);

        DesiredCapabilities capabilities =
            new HeadlessChromeCapabilitiesBuilder().getCapabilities(new DesiredCapabilities());

        //assert the capabilities returned includes the '--headless' argument.
        assertNotNull(capabilities.getCapability(ChromeOptions.CAPABILITY));

        Map<String, List<String>> chromeOptionsMap =
            (Map<String, List<String>>) capabilities.getCapability(ChromeOptions.CAPABILITY);
        assertTrue(chromeOptionsMap.containsKey("args"));
        assertTrue(chromeOptionsMap.get("args").contains("--headless"));
    }
}
