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

package com.paypal.selion.internal.reports.runtimereport;

import java.util.ArrayList;
import java.util.List;

import org.testng.ITestResult;

/**
 * This class is used to convert all the details of the TestNG Test Method to JSON array in the order used by Runtime
 * Reporter
 * 
 */
@SuppressWarnings("unused")
class TestMethodInfo extends MethodInfo {

    private List<String> parameters;

    /**
     * Instantiate TestMethodInfo object with suite, test, packages, classname and result details
     * 
     * @param suite
     *            - name of the suite
     * @param test
     *            - name of the test
     * @param packages
     *            - name of the package without class name
     * @param classname
     *            - name of the class without package name
     * @param result
     *            - ITestResult of the test method which need to be reported
     */
    public TestMethodInfo(String suite, String test, String packages, String classname, ITestResult result) {
        super(suite, test, packages, classname, result);

        List<String> param = new ArrayList<>();
        for (Object temp : result.getParameters()) {
            if (temp != null) {
                param.add(temp.toString());
            }
        }

        if (!param.isEmpty()) {
            this.parameters = param;
        }
    }

}