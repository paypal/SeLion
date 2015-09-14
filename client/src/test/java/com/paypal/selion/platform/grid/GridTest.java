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

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.configuration.LocalConfig;
import com.paypal.selion.internal.platform.grid.MobileTestSession;
import com.paypal.selion.internal.platform.grid.WebTestSession;
import com.paypal.selion.platform.grid.Grid;

import static com.paypal.selion.platform.asserts.SeLionAsserts.assertEquals;
import static com.paypal.selion.platform.asserts.SeLionAsserts.assertNotNull;

public class GridTest {

    /**
     */
    @WebTest
    @Test(groups = "functional")
    public void testGetNewTimeOut(ITestContext ctx) {
        LocalConfig lc = ConfigManager.getConfig(ctx.getCurrentXmlTest().getName());
        lc.setConfigProperty(ConfigProperty.EXECUTION_TIMEOUT, "20000");
        assertEquals(Grid.getExecutionTimeoutValue(), 20000l, "Verify the timeout value is correctly retrieved");
    }

    @WebTest
    @Test(groups = "functional")
    public void testGetDriver() {
        assertNotNull(Grid.driver(), "verify that the driver instance returned is not null");
    }

    @Test(expectedExceptions = { IllegalStateException.class })
    public void testGridDriverWithOutWebTest() {
        Grid.driver().get("http://www.paypal.com");
    }

    @WebTest
    @Test(groups = "functional")
    public void testGetTestSession() {
        assertNotNull(Grid.getTestSession(), "verify that the test session returned is not null");
    }

    /**
     * TODO enable and check this test method once we have the mobile simulators setup
     */
    @MobileTest
    @Test(enabled = false, groups = "functional")
    public void testGetMobileTestSession() {
        assertNotNull(Grid.getMobileTestSession(), "verify that the mobiletestSession returned is not null");
        assertEquals(Grid.getMobileTestSession().getClass(), MobileTestSession.class,
                "verify the returned test session is an instance MobileTestSession");
    }

    @WebTest
    @Test(groups = "functional")
    public void testGetWebTestSession() {
        assertNotNull(Grid.getWebTestSession(), "verify that the mobiletestSession returned is not null");
        assertEquals(Grid.getWebTestSession().getClass(), WebTestSession.class,
                "verify the returned test session is an instance WebTestSession");
    }

}
