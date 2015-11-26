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

package com.paypal.selion.internal.utils;

import org.testng.IInvokedMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.testng.internal.InvokedMethod;

import com.paypal.selion.internal.utils.InvokedMethodInformation;
import com.paypal.selion.internal.utils.TestNGUtils;
import com.paypal.selion.platform.asserts.SeLionAsserts;

public class TestNGUtilsTest {
    @Test(groups = "unit")
    public void dummyTestMethod() {
        // dummy test method
    }

    @Test(groups = "unit", dependsOnMethods = { "dummyTestMethod" })
    public void testGetInvokedMethodInformation() {
        ITestResult result = Reporter.getCurrentTestResult();
        IInvokedMethod method = new InvokedMethod(this, result.getMethod(), null, System.currentTimeMillis(), result);
        result.setAttribute("foo", "bar");
        InvokedMethodInformation response = TestNGUtils.getInvokedMethodInformation(method, result);
        SeLionAsserts.assertEquals(response.getCurrentTestName(), result.getTestContext().getCurrentXmlTest()
                .getName(), "Verify current Test name");
        SeLionAsserts.assertEquals(response.getActualMethod().getName(), "testGetInvokedMethodInformation",
                "Verify actual method");
        SeLionAsserts.assertEquals(response.getMethodParameters().length, 0, "Verify parameters");
        SeLionAsserts.assertEquals(response.getTestAttribute("foo"), "bar", "Verify attributes");
        SeLionAsserts.assertEquals(response.isTestResultSuccess(), false, "Verify initial test status");
        SeLionAsserts.assertEquals(response.getMethodsDependedUpon().length, 1, "Verify dependency count");
    }

}
