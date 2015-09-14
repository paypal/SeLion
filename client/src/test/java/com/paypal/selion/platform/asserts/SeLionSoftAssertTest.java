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

package com.paypal.selion.platform.asserts;

import org.testng.annotations.Test;

public class SeLionSoftAssertTest {

    @Test(groups = { "unit" })
    public void softAssertTest1() {

        SeLionSoftAssert softAssert = new SeLionSoftAssert();
        softAssert.assertEquals("OK", "OK", "soft Assert 1");
        softAssert.assertTrue(true, "soft Assert 2");
        softAssert.assertAll();

    }

    @Test(groups = { "unit" })
    public void softAssertTest2() {

        SeLionSoftAssert softAssert = new SeLionSoftAssert();
        softAssert.assertNotEquals("OK", "NOTOK", "soft Assert 3");
        softAssert.assertFalse(false, "soft Assert 4");
        softAssert.assertAll();

    }

    @Test(groups = { "unit" }, expectedExceptions = { AssertionError.class })
    public void softAssertFailTest() {

        SeLionSoftAssert softAssert = new SeLionSoftAssert();
        softAssert.assertNotEquals("OK", "NOTOK", "soft Assert 3");
        softAssert.assertFalse(true, "soft Assert 4");
        softAssert.assertEquals(true, true, "soft Assert 5");
        softAssert.assertAll();

    }

}
