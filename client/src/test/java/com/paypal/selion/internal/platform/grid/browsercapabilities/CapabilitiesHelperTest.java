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

package com.paypal.selion.internal.platform.grid.browsercapabilities;

import static com.paypal.selion.configuration.Config.ConfigProperty.SELENIUM_CUSTOM_CAPABILITIES_PROVIDER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.internal.platform.grid.browsercapabilities.CapabilitiesHelper;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

public class CapabilitiesHelperTest {
    @BeforeClass(alwaysRun = true)
    public void setup() {
        StringBuilder className = new StringBuilder(CapabilitiesHelperTest.class.getCanonicalName());
        className.append("$").append(MyTestCapabilities.class.getSimpleName());
        Config.setConfigProperty(SELENIUM_CUSTOM_CAPABILITIES_PROVIDER, className.toString());
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        Config.setConfigProperty(SELENIUM_CUSTOM_CAPABILITIES_PROVIDER, "");
    }

    @Test(groups = "functional")
    public void testRetrieveCustomCapsViaServiceLoaders() {
        List<DesiredCapabilities> list = CapabilitiesHelper.retrieveCustomCapsViaServiceLoaders();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.size() == 2);

    }

    @Test(groups = "functional")
    @WebTest(additionalCapabilities = { "is-oss:true" })
    public void testUserProvidedCapabilities() {
        List<DesiredCapabilities> list = CapabilitiesHelper.retrieveCustomCapsViaServiceLoaders();
        boolean found = false;
        for (DesiredCapabilities eachItem : list) {
            if (eachItem.is("is-oss")) {
                found = true;
                assertEquals(eachItem.getCapability("is-oss"), Boolean.TRUE);
                break;
            }
        }
        assertTrue(found);
    }

    @Test(groups = "unit")
    public void testRetrieveCustomCapsObjects() {
        List<DesiredCapabilities> list = CapabilitiesHelper.retrieveCustomCapsObjects();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.size() == 1);
        assertEquals(list.get(0).getCapability("framework"), "selion");
    }

    public static class MyTestCapabilities extends DefaultCapabilitiesBuilder {
        @Override
        public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
            capabilities.setCapability("framework", "selion");
            return capabilities;
        }
    }

}
