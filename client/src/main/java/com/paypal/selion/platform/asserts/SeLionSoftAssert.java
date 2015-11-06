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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;
import org.testng.collections.Maps;

import com.paypal.selion.internal.platform.asserts.SeLionAssertsListener;

/**
 * SeLion Soft Asserts which provides the capability to log the asserts and their status. But the test continue to run
 * till the end of the test method.
 */
public final class SeLionSoftAssert extends Assertion {

    public static final String SOFT_ASSERT_ATTRIBUTE_NAME = SeLionSoftAssert.class.getCanonicalName();
    
    private final Map<AssertionError, IAssert<?>> allErrors = Maps.newLinkedHashMap();
    
    @Override
    protected void doAssert(IAssert<?> assertCommand) {
        onBeforeAssert(assertCommand);
        try {
            executeAssert(assertCommand);
        } finally {
            onAfterAssert(assertCommand);
        }
    }

    @Override
    public void onAssertSuccess(IAssert<?> assertCommand) {
        showAssertInfo(assertCommand, null, false);
    }

    @Override
    public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
        showAssertInfo(assertCommand, ex, true);
    }

    /**
     * Shows a message in Reporter based on the assert result and also includes the stacktrace for failed assert.
     * 
     * @param assertCommand
     *            The assert conditions for current test.
     * @param ex
     *            An {@link AssertionError} in case of failed assert, else null.
     * @param failedTest
     *            A boolean {@code true} when the assert has failed.
     */
    private void showAssertInfo(IAssert<?> assertCommand, AssertionError ex, boolean failedTest) {
        ITestResult testResult = Reporter.getCurrentTestResult();

        // Checks whether the soft assert was called in a TestNG test run or else within a Java application.
        String methodName = "main";
        if (testResult != null) {
            methodName = testResult.getMethod().getMethodName();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Soft Assert ");
        if (assertCommand.getMessage() != null && !assertCommand.getMessage().trim().isEmpty()) {
            sb.append("[").append(assertCommand.getMessage()).append("] ");
        }
        if (failedTest) {
            sb.append("failed in ");
        } else {
            sb.append("passed in ");
        }
        sb.append(methodName).append("()\n");
        if (failedTest) {
            sb.append(ExceptionUtils.getStackTrace(ex));
        }
        Reporter.log(sb.toString(), true);
    }

    @Override
    public void executeAssert(IAssert<?> a) {
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
     * This is also called by {@link SeLionAssertsListener#afterInvocation(org.testng.IInvokedMethod, ITestResult)}.
     */
    public void assertAll() {
        if (allErrors.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        if (1 == allErrors.size()) {
            sb.append("A soft assertion failure occurred [\n");
        } else {
            sb.append("Multiple (").append(allErrors.size()).append(") soft assertion failures occurred [\n");
        }

        int counter = 0;
        AssertionError eachError;
        for (Entry<AssertionError, IAssert<?>> eachEntry : allErrors.entrySet()) {
            eachError = eachEntry.getKey();
            sb.append("\t")
                    .append(counter += 1)
                    .append(". ")
                    .append(ExceptionUtils.getRootCauseMessage(eachError))
                    .append("\n");
            
            if (Reporter.getCurrentTestResult() != null) {
                    // Get stacktrace up to 'at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)'
                    sb.append(StringUtils.substringBetween(ExceptionUtils.getStackTrace(eachError), "\n",
                            "\tat sun.reflect").replace("\t", "\t\t"));
            }
            else {
                sb.append(StringUtils.substringAfter(ExceptionUtils.getStackTrace(eachError),"\n").replace("\t", "\t\t"));
            }
        }
        sb.append("\t]");
        if (Reporter.getCurrentTestResult() != null) {
            Reporter.getCurrentTestResult().setThrowable(new AssertionError(sb.toString()));
            Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
        } else {
            throw new AssertionError(sb.toString());
        }
    }

}
