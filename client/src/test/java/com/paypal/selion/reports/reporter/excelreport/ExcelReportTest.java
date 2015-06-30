/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 eBay Software Foundation                                                                     |
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

import static com.paypal.selion.platform.asserts.SeLionAsserts.assertEquals;
import static com.paypal.selion.platform.asserts.SeLionAsserts.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.SuiteRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.Configuration;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.paypal.selion.configuration.ConfigTest;

/**
 * Unit Tests for ExcelReport.
 */
public class ExcelReportTest {

    private String excelReportFileName = "dummy_excel_test.xls";
    private String strReportsDirectory;
    private Path excelFile;

    @BeforeMethod(groups = { "excel-report-test" })
    public void removeExcelFileBeforeTest(ITestContext context) {
        strReportsDirectory = context.getOutputDirectory();
        excelFile = Paths.get(strReportsDirectory, excelReportFileName);
        if (Files.isRegularFile(excelFile)) {
            FileUtils.deleteQuietly(excelFile.toFile());
        }
    }

    @Test(groups = { "excel-report-test" })
    public void testExcelReporter() throws IOException {
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
        SuiteRunner iSuite = new SuiteRunner(config, suite, strReportsDirectory);
        iSuite.getXmlSuite().setParentSuite(suite);

        List<ISuite> iSuites = new ArrayList<>();
        iSuites.add(iSuite);

        ExcelReport excelReport = new ExcelReport();
        excelReport.setExcelFileName(excelReportFileName);
        excelReport.generateReport(xmlSuites, iSuites, strReportsDirectory);

        // Check whether the ExcelReport exists.
        assertTrue(Files.exists(excelFile), "Verify path of Excel Report file.");
        assertTrue(Files.isRegularFile(excelFile), "Verify that Excel Report is a file.");

        FileInputStream fileInputStream = new FileInputStream(excelFile.toFile());
        HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
        assertEquals(ReportSheetNames.values().length, workbook.getNumberOfSheets(),
                "Verify number of worksheets in ExcelReport");
        IOUtils.closeQuietly(fileInputStream);
    }
}