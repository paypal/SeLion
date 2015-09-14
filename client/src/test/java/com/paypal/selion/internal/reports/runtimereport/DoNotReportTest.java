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

import org.testng.annotations.Test;

import com.paypal.selion.annotations.DoNotReport;

public class DoNotReportTest {

    @Test(groups = "donot-report-functional")
    @DoNotReport
    public void testBaseMethodPassed() {
    }

    @Test(groups = "donot-report-functional", dependsOnMethods = "testBaseMethodPassed")
    public void testPassed() {
    }

    @DoNotReport
    @Test(groups = "donot-report-functional")
    public void testBaseMethodFailed() {
        throw new RuntimeException();
    }

    @Test(groups = "donot-report-functional", dependsOnMethods = "testBaseMethodFailed")
    public void testFailed() {
    }

    @DoNotReport
    @Test(groups = "donot-report-functional")
    public void testBaseMethodSkipped() {
        throw new RuntimeException();
    }

    @DoNotReport
    @Test(groups = "donot-report-functional", dependsOnMethods = "testBaseMethodSkipped")
    public void testBaseMethodSkipped2() {

    }

    @Test(groups = "donot-report-functional", dependsOnMethods = "testBaseMethodSkipped2")
    public void testSkipped() {
    }
}
