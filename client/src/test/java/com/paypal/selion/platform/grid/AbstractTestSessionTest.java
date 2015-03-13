/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

import static com.paypal.selion.platform.asserts.SeLionAsserts.*;

import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.internal.utils.InvokedMethodInformation;
import com.paypal.selion.platform.grid.AbstractTestSession;
import com.paypal.selion.platform.grid.Grid;

/**
 * This class is used to test the methods implemented within the abstract class AbstractTestSession
 * 
 * The methods implemented elsewhere are tested in their corresponding places
 * 
 */
public class AbstractTestSessionTest {

    @WebTest
    @Test(groups = "functional")
    public void testHandleSessions() {
        Grid.open("about:blank");
        AbstractTestSession session = Grid.getTestSession();
        assertTrue(session.handleSessions(), "verify that the sesion handling is true by default for WebTestSession");

    }

    @WebTest(additionalCapabilities = { "key1:value1", "key2:value2" })
    @Test(groups = "functional")
    public void testGetAdditionalCapabilities() {
        Grid.open("about:blank");
        AbstractTestSession session = Grid.getTestSession();

        assertNotNull(session.getAdditionalCapabilities(), "verify that the additional capabilities are not null");
        assertEquals(session.getAdditionalCapabilities().getCapability("key1"), "value1",
                "verify the capability is read correctly");
        assertEquals(session.getAdditionalCapabilities().getCapability("key2"), "value2",
                "verify the capability is read correctly");
    }

    @WebTest
    @Test(groups = "functional")
    public void testCloseSession() {
        Grid.getTestSession().closeSession();
        assertNull(Grid.driver(), "verify that the driver has been shut down");
    }

    @Test(groups = "functional")
    public void testGetParamsInfo() {
        String[] parameters = new String[2];
        parameters[0] = "parameter1";
        parameters[1] = "parameter2";
        InvokedMethodInformation info = new InvokedMethodInformation();
        info.setMethodParameters(parameters);

        assertTrue(Grid.getTestSession().getParamsInfo(info).equals("parameter1,parameter2"),
                "verify the test parameters are properly parsed");
    }

    @Test(groups = "functional")
    public void testParseIntoCapabilities() {
        String[] capabilities = new String[2];
        capabilities[0] = "capabilityName1:capabilityValue1";
        capabilities[1] = "capabilityName2:capabilityValue2";

        assertEquals(Grid.getTestSession().parseIntoCapabilities(capabilities).get("capabilityName1"),
                "capabilityValue1", "verify the capability is parsed properly");
    }

    @Test(groups = "functional")
    public void testGetDeclaringNames() {
        assertEquals(Grid.getTestSession().getDeclaringClassName(), this.getClass().getCanonicalName(),
                "verify the class is retireved correctly");
        assertEquals(Grid.getTestSession().getMethodName(), "testGetDeclaringNames",
                "verify the method name was correctly retrieved");
    }

    @Test(groups = "functional")
    public void testGetTestName() {
        assertTrue(Grid.getTestSession().getTestName().contains("testGetTestName()"),
                "verify the test name is properly formed");
    }

}
