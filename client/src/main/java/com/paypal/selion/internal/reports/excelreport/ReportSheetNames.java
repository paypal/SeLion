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

package com.paypal.selion.internal.reports.excelreport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Enum defining the major reports. Contains list of sub reports
 */
public enum ReportSheetNames {

    TESTSUMMARYREPORT("Test Summary", Arrays.asList(new String[] { "Full Suite Summary", "Test Summary", 
            "Classwise Summary", "Groupwise Summary" })), 
    GROUPSUMMARYREPORT("Detailed Groupwise Summary", new ArrayList<String>()),
    TESTCASEREPORT("TestCasewise Report", Arrays.asList(new String[] { "Failed TC List", "Passed TC List", 
            "Skipped TC List" })), 
    DEFECTREPORT("Failure List", Arrays.asList(new String[] { "Defect Summary" })),
    TESTOUTPUTDETAILSREPORT("Test Output", Arrays.asList("Test Output"));

    private String sRepName;
    private List<String> lsSubReportNames;

    ReportSheetNames(String sReportName, List<String> lsSubReports) {
        this.sRepName = sReportName;
        this.lsSubReportNames = lsSubReports;
    }

    public String getName() {
        return this.sRepName;
    }

    public List<String> getSubReportNames() {
        return this.lsSubReportNames;
    }

    public void setReport(List<String> lsReports) {
        this.lsSubReportNames = lsReports;

    }

    public void addReport(String sSubReportName) {
        this.lsSubReportNames.add(sSubReportName);
    }
}
