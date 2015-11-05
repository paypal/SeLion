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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;

@WebTest
@Test(singleThreaded = true, groups = "functional")
public class SessionSharingTestWithDataProviderMixedIn {

    private static int flag;
    private String[] sitesToOpen;
    private static SessionId sessionId;

    private SessionId getSessionId() {
        return Grid.driver().getSessionId();
    }

    @BeforeClass
    public void beforeClass() {
        sitesToOpen = new String[] { TestServerUtils.getContainerURL(), TestServerUtils.getTestEditableURL() };
        assertNotNull(Grid.getTestSession());
    }

    @DataProvider(name = "testData")
    public Object[][] provideTestData() {
        return new Object[][] { { "ElementList Unit Test Page" }, { "Sample Unit Test Page" } };
    }

    @Test(priority = 0)
    public void testSessionSharingWithDPStart() {
        Grid.driver().get(TestServerUtils.getDatePickerURL());
        assertTrue(Grid.driver().getTitle().contains("jQuery Datepicker"));
        sessionId = getSessionId();
    }

    @Test(priority = 1, dataProvider = "testData")
    public void testSessionSharingWithDp(String title) {
        assertEquals(getSessionId().toString(), sessionId.toString());
        Grid.driver().get(sitesToOpen[flag]);
        assertTrue(Grid.driver().getTitle().contains(title));
        flag++;
    }

    @Test(priority = 2)
    public void testSessionSharingWithDPFinal() {
        assertEquals(getSessionId().toString(), sessionId.toString());
        Grid.driver().get(TestServerUtils.getDatePickerURL());
        assertTrue(Grid.driver().getTitle().contains("jQuery Datepicker"));
    }

    @AfterClass
    public void afterClass() {
        flag = 0;
    }
}
