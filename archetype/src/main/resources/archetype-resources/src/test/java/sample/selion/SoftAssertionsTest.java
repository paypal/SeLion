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

package ${package}.sample.selion;

import com.paypal.selion.platform.asserts.SeLionAsserts;

import org.testng.annotations.Test;

/**
 * This sample demonstrates the Soft assertion capabilities that SeLion provides for.
 * Soft assertions are basically those assertions which represent a validation in order to determine the pass/fail state for a
 * test, but they are special as in, the validation failure doesn't abort the test execution right there itself but lets the test
 * run through to completion before reporting validation failures.
 *
 */
public class SoftAssertionsTest {

    @Test
    /*
     * Note: This test will fail with a hard assert once all the SoftAsserts fail.
     */
    public void simpleTestMethod () {
        SeLionAsserts.verifyFalse(true, "Ensuring that falsy values are always falsy");
        SeLionAsserts.verifyEquals(null, "Krishnan", "Ensuring that the author of this test was Krishnan");
        SeLionAsserts.verifyNull("Rambo", "Ensuring that Null values stay that way");
    }

}
