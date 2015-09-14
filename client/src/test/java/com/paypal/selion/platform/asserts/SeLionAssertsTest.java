/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.paypal.selion.platform.asserts.SeLionAsserts;

/**
 * Unit Tests for SeLionAssert.
 */

public class SeLionAssertsTest {
    @Test(groups = { "unit" })
    public void testSoftAssertCapabilities() {
        SeLionAsserts.verifyTrue(true, "SeLion Soft assert1");
        SeLionAsserts.verifyEquals(true, true, "SeLion Soft assert2");
        SeLionAsserts.verifyEquals("OK", "OK", "SeLion Soft assert3");
        SeLionAsserts.verifyFalse(false, "SeLion Soft assert4");
        SeLionAsserts.verifyNotEquals("OK", "NOTOK", "SeLion Soft assert5");
        SeLionAsserts.verifyNotNull("SomeValue", "SeLion Soft assert 6");
        SeLionAsserts.verifyNull(null, "SeLion Soft assert 7");
    }

    @Test(groups = { "unit" })
    public void testHardAssertCapabilities() {
        SeLionAsserts.assertEquals(true, true, "SeLion Hard assert1");
        SeLionAsserts.assertFalse(false, "SeLion Hard assert2");
        SeLionAsserts.assertEquals("OK", "OK", "SeLion Hard assert3");
        SeLionAsserts.assertTrue(true, "SeLion Hard assert4");
        SeLionAsserts.assertNotEquals("OK", "NOTOK", "SeLion Hard assert5");
        SeLionAsserts.assertNotNull("SomeValue", "SeLion Hard assert6");
        SeLionAsserts.assertNull(null, "SeLion Hard assert7");
    }

    @Test(groups = { "unit" })
    public void testHardAndSoftAssertCapabilities() {
        SeLionAsserts.verifyTrue(true, "My assert1");
        SeLionAsserts.verifyEquals(true, true, "My assert2");
        SeLionAsserts.verifyEquals("OK", "OK", "My assert3");
        SeLionAsserts.verifyFalse(false, "My assert4");
        SeLionAsserts.assertEquals("OK", "OK", "My assert5");
        SeLionAsserts.assertFalse(false, "My assert6");
    }

    @Test(groups = { "unit" }, expectedExceptions = { AssertionError.class })
    public void testSoftAssertFailTest() {
        SeLionAsserts.verifyTrue(true, "My assert1");
        SeLionAsserts.verifyEquals(true, false, "My failure assert2");
        SeLionAsserts.verifyEquals("OK", "OK", "My assert3");
        SeLionAsserts.verifyFalse(false, "My assert4");
        SeLionAsserts.verifyNotEquals(1, 1, "My failure assert5");
        SeLionSoftAssert sa = (SeLionSoftAssert) Reporter.getCurrentTestResult().getAttribute(
                SeLionSoftAssert.SOFT_ASSERT_ATTRIBUTE_NAME);
        sa.assertAll();
        Reporter.getCurrentTestResult().removeAttribute(SeLionSoftAssert.SOFT_ASSERT_ATTRIBUTE_NAME);
    }

}
