/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;
import org.testng.collections.Maps;

/**
 * SeLion Soft Asserts which provides the capability to log the asserts and their status. But the test continue to run
 * till the end of the test method.
 */
class SeLionSoftAssert extends Assertion {

    static final String SOFT_ASSERT_ATTRIBUTE_NAME = SeLionSoftAssert.class.getCanonicalName();
    private Map<AssertionError, IAssert> allErrors = Maps.newLinkedHashMap();

    @Override
    protected void doAssert(IAssert assertCommand) {
        onBeforeAssert(assertCommand);
        try {
            executeAssert(assertCommand);
        } finally {
            onAfterAssert(assertCommand);
        }
    }

    @Override
    public void onAssertSuccess(IAssert assertCommand) {
        showAssertInfo(assertCommand, null, false);
    }

    @Override
    public void onAssertFailure(IAssert assertCommand, AssertionError ex) {
        showAssertInfo(assertCommand, ex, true);
    }

    private void showAssertInfo(IAssert assertCommand, AssertionError ex, boolean failedTest) {
        String methodName = Reporter.getCurrentTestResult().getMethod().getMethodName();
        StringBuilder sb = new StringBuilder();
        sb.append("Soft assert ");
        if (assertCommand.getMessage() != null && !assertCommand.getMessage().trim().isEmpty()) {
            sb.append("[").append(assertCommand.getMessage()).append("] ");
        }
        if (failedTest) {
            sb.append("failed in ");
        } else {
            sb.append("passed in ");
        }
        sb.append(methodName).append("()");
        if (failedTest) {
            Reporter.getCurrentTestResult().setThrowable(ex);
        }
        Reporter.log(sb.toString(), true);

    }

    @Override
    public void executeAssert(IAssert a) {
        try {
            a.doAssert();
            onAssertSuccess(a);
        } catch (AssertionError ex) {
            onAssertFailure(a, ex);
            allErrors.put(ex, a);
        }
    }

    /**
     * This method should be called in order for all the soft asserts to be evaluated.
     */
    public void assertAll() {
        if (allErrors.isEmpty()) {
            return;
        }
        ITestResult result = Reporter.getCurrentTestResult();
        String msg = String.format("Following soft asserts failed in %s.%s() :", result.getTestClass().getName(),
                result.getMethod().getMethodName());
        Reporter.log(msg, true);
        for (Entry<AssertionError, IAssert> eachEntry : allErrors.entrySet()) {
            IAssert eachAssert = eachEntry.getValue();
            AssertionError eachError = eachEntry.getKey();
            StringBuilder sb = new StringBuilder();
            sb.append("The validation ");
            if (StringUtils.isNotBlank(eachAssert.getMessage())) {
                sb.append(" [").append(eachAssert.getMessage()).append("] ");
            }
            sb.append("failed because the expected value of [").append(eachAssert.getExpected()).append("] ");
            sb.append("was different from the actual value [").append(eachAssert.getActual()).append("]");
            
            sb.append("\n").append("StackTrace as below");
            
            StringWriter sw = new StringWriter();
            eachError.printStackTrace(new PrintWriter(sw));
            
            sb.append("\n").append(sw.toString());
            Reporter.log(sb.toString(), true);
        }
        result.setStatus(ITestResult.FAILURE);
    }

}
