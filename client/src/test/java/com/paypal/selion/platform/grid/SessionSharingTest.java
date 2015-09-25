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

import java.io.IOException;

import org.openqa.selenium.remote.SessionId;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.reports.runtime.SeLionReporter;

@WebTest(browser = "chrome")
@Test(singleThreaded = true, groups = "functional")
public class SessionSharingTest {
    private static SessionId sessionId;

    @Test(priority = 1)
    public void testSessionSharing_part1() {
        Grid.open(TestServerUtils.getTestEditableURL());
        SeLionReporter.log("Editable Test Page (" + getSessionId() + ")", true, true);
        sessionId = getSessionId();
    }

    private SessionId getSessionId() {
        return Grid.driver().getSessionId();
    }

    @Test(priority = 2)
    public void testSessionSharing_part2() throws IOException {
        // should already be on test Page
        assertEquals(getSessionId().toString(), sessionId.toString());
        SeLionReporter.log("Editable Test Page (" + getSessionId() + ")", true, true);
        assertTrue(Grid.driver().getTitle().contains("Sample Unit Test Page"),
                "should be on Sample Unit Test Page already with this session");
    }

    @Test(priority = 3)
    public void testSessionSharing_part3() throws Exception {
        assertEquals(getSessionId().toString(), sessionId.toString());
        assertTrue(Grid.driver().getCapabilities().getBrowserName().contains("chrome"),
                "Should be using chrome browser.");
    }

}
