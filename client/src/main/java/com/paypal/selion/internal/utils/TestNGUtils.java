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

import java.util.HashMap;
import java.util.Map;

import org.testng.IInvokedMethod;
import org.testng.ITestResult;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A utility class that helps in housing some of the TestNG specific functionalities.
 * 
 */
public final class TestNGUtils {
    private static SimpleLogger logger = SeLionLogger.getLogger();

    private TestNGUtils() {
        // Utility class. So hide constructor
    }

    /**
     * This method helps in creating a Test runner neutral object that represents a method.
     * 
     * @param method
     *            - An {@link IInvokedMethod} that represents the current invoked method.
     * @param result
     *            - An {@link ITestResult} object that represents a test result.
     * @return - A {@link InvokedMethodInformation} that represents the current invoked method.
     */
    public static InvokedMethodInformation getInvokedMethodInformation(IInvokedMethod method, ITestResult result) {
        logger.entering(new Object[] { method, result });
        InvokedMethodInformation methodInfo = new InvokedMethodInformation();
        methodInfo.setMethodParameters(result.getParameters());
        methodInfo.setActualMethod(method.getTestMethod().getConstructorOrMethod().getMethod());
        methodInfo.setTestMethodAttributes(extractAttributes(result));
        methodInfo.setCurrentMethodName(method.getTestMethod().getMethodName());
        methodInfo.setCurrentTestName(result.getTestContext().getCurrentXmlTest().getName());
        methodInfo.setTestResultSuccess(result.isSuccess());
        methodInfo.setException(result.getThrowable());
        methodInfo.setMethodsDependedUpon(method.getTestMethod().getMethodsDependedUpon());
        logger.exiting(methodInfo);
        return methodInfo;
    }

    private static Map<String, Object> extractAttributes(ITestResult result) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        for (String eachAttribute : result.getAttributeNames()) {
            attributes.put(eachAttribute, result.getAttribute(eachAttribute));
        }
        return attributes;
    }

}
