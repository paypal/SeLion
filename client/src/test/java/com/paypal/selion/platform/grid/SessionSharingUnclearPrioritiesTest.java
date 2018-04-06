/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;

/**
 * Tests that exceptions is thrown when all test methods do not define a priority
 */
@WebTest
@Test(singleThreaded = true, groups = "functional", expectedExceptions = IllegalStateException.class)
public class SessionSharingUnclearPrioritiesTest {
    @BeforeClass
    public void beforeClass() {
        assertNotNull(Grid.getTestSession());
        Grid.driver();
    }

    @Test(priority = 1, expectedExceptions = IllegalStateException.class)
    public void testSessionSharingStep1() {
        fail("this test should not have been run.");
    }

    public void testSessionSharingStep2() {
        fail("this test should not have been run.");
    }

    @AfterClass
    public void afterClass() {
        // This should close the browser!!!
        assertNotNull(Grid.getTestSession());
    }
}
