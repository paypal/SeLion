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

import org.testng.ITestResult;

/**
 * This class transforms a Configuration Method information into a JSON format so that it can be used by RuntimeReporter
 * 
 */
@SuppressWarnings("unused")
class ConfigMethodInfo extends MethodInfo {

    private final String type;

    /**
     * Instantiate ConfigMethodInfo object with suite, test, packages, class name and result details
     * 
     * @param suite
     *            - name of the suite
     * @param test
     *            - name of the test
     * @param packages
     *            - name of the package without class name
     * @param classname
     *            - name of the class without package name
     * @param type
     *            - The type of the method.
     * @param result
     *            - ITestResult of the config method which need to be reported
     */
    public ConfigMethodInfo(String suite, String test, String packages, String classname, String type,
            ITestResult result) {
        super(suite, test, packages, classname, result);
        this.type = type;
    }

}
