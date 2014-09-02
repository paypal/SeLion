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

package com.paypal.selion.reports.reporter.excelreport;

import com.paypal.selion.configuration.ConfigTest;
import com.paypal.selion.reports.reporter.excelreport.ExcelReport;

import org.testng.ISuite;
import org.testng.SuiteRunner;
import org.testng.annotations.Test;
import org.testng.internal.Configuration;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit Tests for ExcelReport.
 */
public class ExcelReportTest {

    private static final String SUREFIRE_DEFAULT_REPORTS_DIR = "target/surefire-reports";

    @Test(groups = { "excel-report-test" })
    public void testExcelReporter() {
        XmlSuite suite = new XmlSuite();
        suite.setName("ExcelReporterSuite");

        XmlTest test = new XmlTest(suite);
        test.setName("ExcelReporterTest");

        List<XmlClass> classes = new ArrayList<>();
        classes.add(new XmlClass(ConfigTest.class));
        test.setXmlClasses(classes);

        List<XmlSuite> xmlSuites = new ArrayList<>();
        xmlSuites.add(suite);

        Configuration config = new Configuration();
        SuiteRunner iSuite = new SuiteRunner(config, suite, SUREFIRE_DEFAULT_REPORTS_DIR);
        iSuite.getXmlSuite().setParentSuite(suite);

        List<ISuite> iSuites = new ArrayList<>();
        iSuites.add(iSuite);

        ExcelReport excelReport = new ExcelReport();
        excelReport.generateReport(xmlSuites, iSuites, SUREFIRE_DEFAULT_REPORTS_DIR);
    }
}
