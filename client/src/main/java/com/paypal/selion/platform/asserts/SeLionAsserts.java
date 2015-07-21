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

/**
 * 
 * SeLion static asserts class, which provides methods for both hard and soft assertion.
 * 
 */
public final class SeLionAsserts {

    private static SeLionHardAssert hardAssert = new SeLionHardAssert();
    private static SeLionSoftAssert softAssert = new SeLionSoftAssert();

    private SeLionAsserts() {
        // Utility class. So hide the constructor
    }

    /**
     * assertTrue method is used to assert the condition based on boolean input and provide the Pass result for a TRUE
     * value. assertTrue will Fail for a FALSE value and abort the test case. * <br>
     * Sample Usage<br>
     * <code>
     * SeLionAsserts.assertTrue(true);
     * </code>
     * 
     * @param condition
     *            - A test condition to be validated for pass/fail
     */
    public static void assertTrue(boolean condition) {
        hardAssert.assertTrue(condition);
    }

    /**
     * assertFalse method is used to assert the condition based on boolean input and provide the Pass result for a FALSE
     * value.assertFalse will fail for a TRUE value and abort the test case
     * 
     * @param condition
     *            - A test condition to be validated for pass/fail * <br>
     *            Sample Usage<br>
     *            <code>
     * SeLionAsserts.assertFalse(false);
     * </code>
     */
    public static void assertFalse(boolean condition) {
        hardAssert.assertFalse(condition);
    }

    /**
     * assertEquals method is used to assert based on actual and expected values and provide a Pass result for a same
     * match.assertEquals will yield a Fail result for a mismatch and abort the test case.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param expected
     *            - Expected value for the test to pass. <br>
     *            Sample Usage<br>
     *            <code>
     * SeLionAsserts.assertEquals("OK","OK");
     * </code>
     */
    public static void assertEquals(Object actual, Object expected) {
        hardAssert.assertEquals(actual, expected);
    }

    /**
     * assertNotEquals method is used to assert based on actual and expected values and provide a Pass result for a
     * mismatch.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param expected
     *            - Expected value for the test to pass. <br>
     *            Sample Usage<br>
     *            <code>
     * SeLionAsserts.assertNotEquals("OK","NOTOK");
     * </code>
     */
    public static void assertNotEquals(Object actual, Object expected) {
        hardAssert.assertNotEquals(actual, expected);
    }

    /**
     * assertNotEquals method is used to assert based on actual and expected values and provide a Pass result for a
     * mismatch.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param expected
     *            - Expected value for the test to pass. <br>
     * @param msg
     *            - A descriptive text narrating a validation being done Sample Usage<br>
     *            <code>
     * SeLionAsserts.assertNotEquals("OK","NOTOK", "My Assert message");
     * </code>
     */
    public static void assertNotEquals(Object actual, Object expected, String msg) {
        hardAssert.assertNotEquals(actual, expected, msg);
    }

    /**
     * assertNull method is used to assert based on actual value and provide a Pass result if the object or actual value
     * is null.
     * 
     * @param actual
     *            - Actual value obtained from executing a test <br>
     *            <code>
     * SeLionAsserts.assertNull(null);
     * </code>
     */
    public static void assertNull(Object actual) {
        hardAssert.assertNull(actual);
    }

    /**
     * assertNull method is used to assert based on actual value and provide a Pass result if the object or actual value
     * is null.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param msg
     *            - A descriptive text narrating a validation being done Sample Usage<br>
     *            <code>
     * SeLionAsserts.assertNull(null, "My Assert message");
     * </code>
     */
    public static void assertNull(Object actual, String msg) {
        hardAssert.assertNull(actual, msg);
    }

    /**
     * assertNotNull method is used to assert based on actual value and provide a Pass result if the object or actual
     * value is NOT null.
     * 
     * @param actual
     *            - Actual value obtained from executing a test <br>
     *            <code>
     * SeLionAsserts.assertNotNull(null);
     * </code>
     */
    public static void assertNotNull(Object actual) {
        hardAssert.assertNotNull(actual);
    }

    /**
     * assertNotNull method is used to assert based on actual value and provide a Pass result if the object or actual
     * value is NOT null.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param msg
     *            - A descriptive text narrating a validation being done Sample Usage<br>
     *            <code>
     * SeLionAsserts.assertNotNull(null, "My Assert message");
     * </code>
     */
    public static void assertNotNull(Object actual, String msg) {
        hardAssert.assertNotNull(actual, msg);
    }

    /**
     * verifyTrue method is used to assert the condition based on boolean input and provide the Pass result for a TRUE
     * value.verifyTrue will Fail for a FALSE value and continue to run the test case. <br>
     * Sample Usage<br>
     * <code>
     * SeLionAsserts.verifyTrue(true,"Some Message");
     * </code>
     * 
     * @param condition
     *            - A test condition to be validated for pass/fail
     * @param msg
     *            - A descriptive text narrating a validation being done
     * 
     */
    public static void verifyTrue(boolean condition, String msg) {
        getSoftAssertInContext().assertTrue(condition, msg);
    }

    /**
     * verifyTrue method is used to assert the condition based on boolean input and provide the Pass result for a TRUE
     * value.verifyTrue will Fail for a FALSE value and continue to run the test case. <br>
     * Sample Usage<br>
     * <code>
     * SeLionAsserts.verifyTrue(true);
     * </code>
     * 
     * @param condition
     *            - A test condition to be validated for pass/fail
     */
    public static void verifyTrue(boolean condition) {
        getSoftAssertInContext().assertTrue(condition);
    }

    /**
     * verifyFalse method is used to assert the condition based on boolean input and provide the Pass result for a FALSE
     * value.verifyFalse will Fail for a TRUE value and continue to run the test case. <br>
     * Sample Usage<br>
     * <code>
     * SeLionAsserts.verifyFalse(false,"Some Message");
     * </code>
     * 
     * @param condition
     *            - A test condition to be validated for pass/fail
     * @param msg
     *            - A descriptive text narrating a validation being done
     * 
     */
    public static void verifyFalse(boolean condition, String msg) {
        getSoftAssertInContext().assertFalse(condition, msg);
    }

    /**
     * verifyFalse method is used to assert the condition based on boolean input and provide the Pass result for a FALSE
     * value.verifyFalse will Fail for a TRUE value and continue to run the test case. <br>
     * Sample Usage<br>
     * <code>
     * SeLionAsserts.verifyFalse(false);
     * </code>
     * 
     * @param condition
     *            - A test condition to be validated for pass/fail
     */
    public static void verifyFalse(boolean condition) {
        getSoftAssertInContext().assertFalse(condition);
    }

    /**
     * verifyEquals method is used to assert based on actual and expected values and provide a Pass result for a same
     * match.verifyEquals will yield a Fail result for a mismatch and continue to run the test case.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param expected
     *            - Expected value for the test to pass.
     * @param msg
     *            - A descriptive text narrating a validation being done. <br>
     *            Sample Usage<br>
     *            <code>
     * SeLionAsserts.verifyEquals("OK","OK" ,"Some Message");
     * </code>
     */
    public static void verifyEquals(Object actual, Object expected, String msg) {
        getSoftAssertInContext().assertEquals(actual, expected, msg);
    }

    /**
     * verifyEquals method is used to assert based on actual and expected values and provide a Pass result for a same
     * match.verifyEquals will yield a Fail result for a mismatch and continue to run the test case.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param expected
     *            - Expected value for the test to pass. <br>
     *            Sample Usage<br>
     *            <code>
     * SeLionAsserts.verifyEquals("OK","OK");
     * </code>
     */
    public static void verifyEquals(Object actual, Object expected) {
        getSoftAssertInContext().assertEquals(actual, expected);
    }

    /**
     * verifyNotEquals method is used to assert based on actual and expected values and provide a Pass result for a
     * mismatch and continue to run the test case.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param expected
     *            - Expected value for the test to pass. <br>
     *            <code>
     * SeLionAsserts.verifyNotEquals("OK","NOTOK");
     * </code>
     */
    public static void verifyNotEquals(Object actual, Object expected) {
        getSoftAssertInContext().assertNotEquals(actual, expected);
    }

    /**
     * verifyNotEquals method is used to assert based on actual and expected values and provide a Pass result for a
     * mismatch and continue to run the test.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param expected
     *            - Expected value for the test to pass. <br>
     * @param msg
     *            - A descriptive text narrating a validation being done Sample Usage<br>
     *            <code>
     * SeLionAsserts.verifyNotEquals("OK","NOTOK", "My Assert message");
     * </code>
     */
    public static void verifyNotEquals(Object actual, Object expected, String msg) {
        getSoftAssertInContext().assertNotEquals(actual, expected, msg);
    }

    /**
     * verifyNull method is used to assert based on actual value and provide a Pass result if the actual value is null
     * and continue to run the test.
     * 
     * @param actual
     *            - Actual value obtained from executing a test <code>
     * SeLionAsserts.verifyNull("OK");
     * </code>
     */
    public static void verifyNull(Object actual) {
        getSoftAssertInContext().assertNull(actual);
    }

    /**
     * verifyNull method is used to assert based on actual value and provide a Pass result if the actual value is null
     * and continue to run the test.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param msg
     *            - A descriptive text narrating a validation being done Sample Usage<br>
     *            <code>
     * SeLionAsserts.verifyNull("OK","My Assert message");
     * </code>
     */
    public static void verifyNull(Object actual, String msg) {
        getSoftAssertInContext().assertNull(actual, msg);
    }

    /**
     * verifyNotNull method is used to assert based on actual value and provide a Pass result if the actual value is NOT
     * null and continue to run the test.
     * 
     * @param actual
     *            - Actual value obtained from executing a test <code>
     * SeLionAsserts.verifyNotNull("OK");
     * </code>
     */
    public static void verifyNotNull(Object actual) {
        getSoftAssertInContext().assertNotNull(actual);
    }

    /**
     * verifyNotNull method is used to assert based on actual value and provide a Pass result if the actual value is NOT
     * null and continue to run the test.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param msg
     *            - A descriptive text narrating a validation being done Sample Usage<br>
     *            <code>
     * SeLionAsserts.verifyNotNull("OK","My Assert message");
     * </code>
     */
    public static void verifyNotNull(Object actual, String msg) {
        getSoftAssertInContext().assertNotNull(actual, msg);
    }

    /**
     * assertTrue method is used to assert the condition based on boolean input and provide the Pass result for a TRUE
     * value. assertTrue will Fail for a FALSE value and abort the test case. * <br>
     * Sample Usage<br>
     * <code>
     * SeLionAsserts.assertTrue(true, "Some Message");
     * </code>
     * 
     * @param condition
     *            - A test condition to be validated for pass/fail
     * @param message
     *            - A descriptive text narrating a validation being done.
     */
    public static void assertTrue(boolean condition, String message) {
        hardAssert.assertTrue(condition, message);
    }

    /**
     * assertFalse method is used to assert the condition based on boolean input and provide the Pass result for a FALSE
     * value.assertFalse will fail for a TRUE value and abort the test case
     * 
     * @param condition
     *            - A test condition to be validated for pass/fail
     * @param message
     *            - A descriptive text narrating a validation being done. <br>
     *            Sample Usage<br>
     *            <code>
     * SeLionAsserts.assertFalse(false,"Some Message");
     * </code>
     */
    public static void assertFalse(boolean condition, String message) {
        hardAssert.assertFalse(condition, message);
    }

    /**
     * assertEquals method is used to assert based on actual and expected values and provide a Pass result for a same
     * boolean.assertEquals will yield a Fail result for a mismatch and abort the test case.
     * 
     * @param actual
     *            - Actual boolean value obtained from executing a test
     * @param expected
     *            - Expected boolean value for the test to pass. <br>
     *            Sample Usage<br>
     *            <code>
     * SeLionAsserts.assertEquals(true,true);
     * </code>
     * 
     */
    public static void assertEquals(boolean actual, boolean expected) {
        hardAssert.assertEquals(actual, expected);
    }

    /**
     * assertEquals method is used to assert based on actual and expected values and provide a Pass result for a same
     * match.assertEquals will yield a Fail result for a mismatch and abort the test case.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param expected
     *            - Expected value for the test to pass. <br>
     *            Sample Usage<br>
     *            <code>
     * SeLionAsserts.assertEquals("OK","OK");
     * </code>
     */
    public static void assertEquals(Object[] actual, Object[] expected) {
        hardAssert.assertEquals(actual, expected);
    }

    /**
     * assertEquals method is used to assert based on actual and expected values and provide a Pass result for a same
     * match.assertEquals will yield a Fail result for a mismatch and abort the test case.
     * 
     * @param actual
     *            - Actual value obtained from executing a test
     * @param expected
     *            - Expected value for the test to pass.
     * @param message
     *            - A descriptive text narrating a validation being done. <br>
     *            Sample Usage<br>
     *            <code>
     * SeLionAsserts.assertEquals("OK","OK", "Some Message");
     * </code>
     * 
     */
    public static void assertEquals(Object actual, Object expected, String message) {
        hardAssert.assertEquals(actual, expected, message);
    }

    /**
     * Fail method fails a flow. * <br>
     * Sample Usage<br>
     * <code>
     * SeLionAsserts.fail("Some Message");
     * </code>
     * 
     * @param message
     *            -- A descriptive text narrating a validation being done.
     */
    public static void fail(String message) {
        hardAssert.fail(message);
    }

    public static void fail(Throwable e, String message) {
        hardAssert.fail(message, e);
    }
    
    /**
     * Gets the instance of {@link SeLionSoftAssert} depending on whether the soft assert methods
     * (verify methods) are being called in TestNG context or as a Java application.
     * @return A {@link SeLionSoftAssert} instance.
     */
    private static SeLionSoftAssert getSoftAssertInContext() {
        SeLionSoftAssert sa;
        if (null == Reporter.getCurrentTestResult()) {
            // Assume Java Application, and return the static instance of SeLionSoftAssert.
            sa = softAssert;
        }
        else {
            sa = (SeLionSoftAssert) Reporter.getCurrentTestResult().getAttribute(
                SeLionSoftAssert.SOFT_ASSERT_ATTRIBUTE_NAME);
        }
        return sa;
    }

}
