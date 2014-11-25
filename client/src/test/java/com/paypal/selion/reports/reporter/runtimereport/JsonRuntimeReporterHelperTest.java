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

package com.paypal.selion.reports.reporter.runtimereport;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonRuntimeReporterHelperTest {

    @Test(groups = "unit")
    public void testJsonRuntimeReporterHelper() {

        assertNotNull(new JsonRuntimeReporterHelper());
    }

    @Test(groups = "unit")
    public void testInsertTestMethodDetail() {

        String suiteName = "sample-suite";
        String testName = "sample-test";
        String packageName = "sample-package";
        String className = "sample-class";

        JsonRuntimeReporterHelper helper = new JsonRuntimeReporterHelper();
        ITestResult result = Reporter.getCurrentTestResult();
        helper.insertTestMethod(suiteName, testName, packageName, className, result);
        result.setStatus(1);
        helper.insertTestMethod(suiteName, testName, packageName, className, result);
        JsonArray jsonArray = helper.getCompletedTestContent();

        assertEquals(jsonArray.size(), 1);
        JsonObject jsonObject = (JsonObject) jsonArray.get(0);
        assertEquals(jsonObject.get("suite").getAsString(), suiteName);
        assertEquals(jsonObject.get("test").getAsString(), testName);
        assertEquals(jsonObject.get("packageInfo").getAsString(), packageName);
        assertEquals(jsonObject.get("className").getAsString(), className);
        assertEquals(jsonObject.get("status").getAsString(), "Passed");

    }

    @Test(groups = "unit")
    public void testInsertConfigMethod() {

        String suiteName = "sample-suite";
        String testName = "sample-test";
        String packageName = "sample-package";
        String className = "sample-class";

        JsonRuntimeReporterHelper helper = new JsonRuntimeReporterHelper();
        ITestResult result = Reporter.getCurrentTestResult();
        helper.insertConfigMethod(suiteName, testName, packageName, className, result);
        result.setStatus(1);
        helper.insertConfigMethod(suiteName, testName, packageName, className, result);
        JsonArray jsonArray = helper.getCompletedConfigContent();

        assertEquals(jsonArray.size(), 1);
        JsonObject jsonObject = (JsonObject) jsonArray.get(0);
        assertEquals(jsonObject.get("suite").getAsString(), suiteName);
        assertEquals(jsonObject.get("test").getAsString(), testName);
        assertEquals(jsonObject.get("packageInfo").getAsString(), packageName);
        assertEquals(jsonObject.get("className").getAsString(), className);
        assertEquals(jsonObject.get("status").getAsString(), "Passed");

    }

}
