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

import org.testng.Reporter;
import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;

/**
 * SeLion Hard Assert class which provides the capability to log all the asserts and their status.
 */
public final class SeLionHardAssert extends Assertion {

    @Override
    public void onAssertSuccess(IAssert<?> assertCommand) {
        showAssertInfo(assertCommand, "passed in ");
    }

    @Override
    public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
        showAssertInfo(assertCommand, "failed in ");
    }

    private void showAssertInfo(IAssert<?> assertCommand, String msg) {
        String methodName = Reporter.getCurrentTestResult().getMethod().getMethodName();
        StringBuilder sb = new StringBuilder();
        sb.append("Assert ");
        if (assertCommand.getMessage() != null && !assertCommand.getMessage().trim().isEmpty()) {
            sb.append("[").append(assertCommand.getMessage()).append("] ");
        }
        sb.append(msg).append(methodName).append("()");
        Reporter.log(sb.toString(), true);

    }

}
