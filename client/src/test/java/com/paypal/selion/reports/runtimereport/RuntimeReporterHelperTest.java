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

package com.paypal.selion.reports.runtimereport;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.testng.Assert.*;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import com.paypal.selion.reports.reporter.runtimereport.RuntimeReporterHelper;
import com.paypal.selion.reports.reporter.services.ConfigSummaryData;

public class RuntimeReporterHelperTest {
    DocumentBuilder docBuilder;

    @BeforeClass(groups = "unit")
    public void beforeClass() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
        }

    }

    @Test(groups = "unit")
    public void testInsertDocument() {

        Document doc = docBuilder.newDocument();

        RuntimeReporterHelper helper = new RuntimeReporterHelper(doc);
        String output = "<li class='folder' id='1'  title='Suite:Suite'>Suite:Suite<ul><li class='folder' id='2'  title='Test:Test'>Test:Test<ul><li class='folder' id='3'  title='Package:Package'>Package:Package<ul><li  id='4'  title='Class:Class'>Class:Class</li></ul></li></ul></li></ul></li>";

        helper.insertDocument("Suite", "Test", "Package", "Class");
        assertEquals(helper.generateTreeView(), output);

    }

    @Test(groups = "unit")
    public void testInsertTestMethodDetail() {
        String[] output = new String[] { "\"<img src='resources/details_open.png'/>\"", "\"Suite\"", "\"Test\"",
                "\"Package\"", "\"Class\"", "\"testInsertTestMethodDetail\"", "", "\"Passed\"", "", "", "", "", "",
                "\"<table width='100%'></table>\"" };
        Document doc = docBuilder.newDocument();
        RuntimeReporterHelper helper = new RuntimeReporterHelper(doc);
        ITestResult result = Reporter.getCurrentTestResult();
        helper.insertTestMethod("Suite", "Test", "Package", "Class", result);
        result.setStatus(1);
        helper.insertTestMethod("Suite", "Test", "Package", "Class", result);
        String result1 = helper.getCompletedTestContent();
        result1 = result1.substring(result1.indexOf("[") + 1, result1.indexOf("]"));
        String[] actual = result1.split(",");

        for (int i = 0; i < actual.length; i++) {
            if (!output[i].isEmpty()) {
                assertTrue(actual[i].matches(output[i]), "Index :" + i);
            }
        }

    }

    @Test(groups = "unit")
    public void testRunningTestMethodDetail() {
        String[] output = new String[] { "\"<img src='resources/details_open.png'/>\"", "\"Suite\"", "\"Test\"",
                "\"Package\"", "\"Class\"", "\"testRunningTestMethodDetail\"", "", "\"Running\"", "", "", "", "", "",
                "\"<table width='100%'></table>\"" };
        Document doc = docBuilder.newDocument();
        RuntimeReporterHelper helper = new RuntimeReporterHelper(doc);
        ITestResult result = Reporter.getCurrentTestResult();
        helper.insertTestMethod("Suite", "Test", "Package", "Class", result);
        String result1 = helper.getRunningTestMethodDetails().toString();
        result1 = result1.substring(result1.indexOf("[") + 1, result1.indexOf("]"));
        String[] actual = result1.split(",");

        for (int i = 0; i < actual.length; i++) {
            if (!output[i].isEmpty()) {
                assertTrue(actual[i].matches(output[i]), "Index :" + i);
            }
        }
    }
    
    @Test(groups = "unit")
    public void testGenerateLocalTestConfigValues() {
        assertNotNull(ConfigSummaryData.getLocalConfigSummary("Unit").get("Current Date"));
    }
}
