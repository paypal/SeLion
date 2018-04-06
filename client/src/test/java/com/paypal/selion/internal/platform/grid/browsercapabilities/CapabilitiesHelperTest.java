/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                          |
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

import static com.paypal.selion.configuration.Config.ConfigProperty.SELENIUM_CUSTOM_CAPABILITIES_PROVIDER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

public class CapabilitiesHelperTest {
    @Test(groups = "unit")
    public void testRetrieveCustomCapsViaServiceLoaders() {
        List<DesiredCapabilities> list = CapabilitiesHelper.retrieveCustomCapsViaServiceLoaders();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        // By default, only the DefaultCapabilitiesBuilder should be present
        assertTrue(list.size() == 1);
        assertTrue(((String) list.get(0).getCapability("name")).contains("testRetrieveCustomCapsViaServiceLoaders"));
    }

    @Test(groups = "unit")
    @WebTest
    public void testRetrieveCustomCapsObjects() {
        // add two capability providers to the local <test> configuration
        StringBuilder providers = new StringBuilder();
        providers.append(TestFrameWorkCapability.class.getName())
                .append(",").append(TestPlatformCapability.class.getName());
        ConfigManager.getConfig(Grid.getWebTestSession().getXmlTestName())
                .setConfigProperty(SELENIUM_CUSTOM_CAPABILITIES_PROVIDER, providers.toString());

        // ensure we get them back through the call into capabilities helper
        List<DesiredCapabilities> list = CapabilitiesHelper.retrieveCustomCapsObjects();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.size() == 2);
        assertEquals(list.get(0).getCapability("framework"), "selion");
        assertEquals(list.get(1).getCapability("platform"), "other");
    }

    @Test(groups = "unit")
    public void testParseIntoCapabilities() {
        String[] capabilities = new String[2];
        capabilities[0] = "capabilityName1:capabilityValue1";
        capabilities[1] = "capabilityName2:capabilityValue2";

        assertEquals(CapabilitiesHelper.retrieveCustomCapabilities(capabilities).getCapability("capabilityName1"),
                "capabilityValue1", "verify the capability is parsed properly");
    }

    public static class TestFrameWorkCapability extends DefaultCapabilitiesBuilder {
        @Override
        public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
            capabilities.setCapability("framework", "selion");
            return capabilities;
        }
    }

    public static class TestPlatformCapability extends DefaultCapabilitiesBuilder {
        @Override
        public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
            capabilities.setCapability("platform", "other");
            return capabilities;
        }
    }

}
