/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

import org.openqa.selenium.remote.SessionId;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.reports.runtime.SeLionReporter;

@WebTest(browser = "chrome")
@Test(singleThreaded = true, groups = "functional")
public class SessionSharingTestWithConfigurationMethods {
    private static SessionId sessionId;

    @BeforeSuite
    public void beforeSuite() {
        // not started yet
        assertNull(Grid.getTestSession());
    }

    @BeforeTest
    public void beforeTest() {
        // not started yet
        assertNull(Grid.getTestSession());
    }

    @BeforeGroups
    public void beforeGroups() {
        /*
         * Not predictable. May or may not have an active Grid WD session. Depends on which thread this method is
         * invoked from. SeLion session sharing tests must be on the same thread per @Test class. TestNG does not
         * guarantee which thread @BeforeGroups is invoked from.
         */
    }

    @BeforeClass
    public void beforeClass() {
        // session started
        Grid.open(TestServerUtils.getTestEditableURL());
        SeLionReporter.log("Editable Test Page (" + getSessionId() + ")", true, true);
        sessionId = getSessionId();
    }

    @BeforeMethod
    public void beforeMethod() {
        // session started by now, when beforeClass is present
        assertNotNull(Grid.getTestSession());
        assertEquals(getSessionId().toString(), sessionId.toString());
    }

    @Test(priority = 0)
    public void testSessionSharing_part1() {
        assertEquals(getSessionId().toString(), sessionId.toString());
        SeLionReporter.log("Editable Test Page (" + getSessionId() + ")", true, true);
        assertTrue(Grid.driver().getTitle().contains("Sample Unit Test Page"),
                "should be on Sample Unit Test Page already with this session");
    }

    @Test(priority = 1)
    public void testSessionSharing_part2() throws Exception {
        assertEquals(getSessionId().toString(), sessionId.toString());
        assertTrue(Grid.driver().getCapabilities().getBrowserName().contains("chrome"),
                "Should be using chrome browser.");
    }

    @AfterMethod
    public void afterMethod() {
        // started by now, regardless if beforeClass is present
        assertEquals(getSessionId().toString(), sessionId.toString());
        assertNotNull(Grid.getTestSession());
    }

    @AfterClass
    public void afterClass() {
        // session still available, closed after this method
        assertEquals(getSessionId().toString(), sessionId.toString());
        assertNotNull(Grid.getTestSession());
    }

    @AfterGroups
    public void afterGroups() {
        /*
         * Not predictable. May or may not have an active Grid WD session. Depends on which thread this method is
         * invoked from. SeLion session sharing tests must be on the same thread per @Test class. TestNG does not
         * guarantee which thread @AfterGroups is invoked from.
         */
    }

    @AfterTest
    public void afterTest() {
        // session closed by now
        assertNull(Grid.getTestSession());
    }

    @AfterSuite
    public void afterSuite() {
        // closed by now
        assertNull(Grid.getTestSession());
    }

    private SessionId getSessionId() {
        return Grid.driver().getSessionId();
    }
}
