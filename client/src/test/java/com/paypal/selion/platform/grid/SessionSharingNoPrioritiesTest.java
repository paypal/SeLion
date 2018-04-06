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

import static org.testng.Assert.fail;

import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;

/**
 * Tests that test priorities must be defined for session sharing
 */
@WebTest
@Test(singleThreaded = true, groups = "functional", expectedExceptions = IllegalStateException.class)
public class SessionSharingNoPrioritiesTest {
    public void testSessionSharingStep11() {
        fail("this test should not have been run.");
    }

    public void testSessionSharingStep2() {
        fail("this test should not have been run.");
    }
}
