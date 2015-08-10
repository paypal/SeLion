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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;

@WebTest
@Test(singleThreaded = true)
public class SessionSharingTestWithDataProvider {

    private static int flag = 0;
    private String[] sitesToOpen = null;

    @BeforeClass(groups = "functional")
    public void initURL() {
        sitesToOpen = new String[] { TestServerUtils.getContainerURL(), TestServerUtils.getTestEditableURL() };
    }

    @DataProvider(name = "testData")
    public Object[][] provideTestData() {
        return new Object[][] { { "ElementList Unit Test Page" }, { "Sample Unit Test Page" } };
    }

    @Test(priority = 0, dataProvider = "testData", groups = "functional")
    public void testSessionSharingWithDpAlone(String title) {
        Grid.driver().get(sitesToOpen[flag]);
        Assert.assertTrue(Grid.driver().getTitle().contains(title));
        flag++;
    }

    @AfterClass
    public void tearDown() {
        flag = 0;
    }
}
