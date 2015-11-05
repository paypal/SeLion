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

package com.paypal.selion.internal.reports.html;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A Utility class which has method to create and associate a unique random number for each TestNG test method. A html
 * file will be created for each TestNG test method with this random number, which contains details about that specific
 * test method.
 * 
 */
final class ReportDataGenerator {

    private static boolean isReportInitialized;
    private static SimpleLogger logger = SeLionLogger.getLogger();

    private ReportDataGenerator() {
        // Utility class. So hide the constructor
    }

    /**
     * init the uniques id for the methods , needed to create the navigation.
     * 
     * @param suites
     */
    public static void initReportData(List<ISuite> suites) {
        logger.entering(suites);
        if (!isReportInitialized) {
            for (ISuite suite : suites) {
                Map<String, ISuiteResult> r = suite.getResults();
                for (ISuiteResult r2 : r.values()) {
                    ITestContext tc = r2.getTestContext();
                    ITestNGMethod[] methods = tc.getAllTestMethods();
                    for (ITestNGMethod method : methods) {
                        method.setId(UUID.randomUUID().toString());
                    }
                }
            }
            isReportInitialized = true;
        }
        logger.exiting();
    }
}
