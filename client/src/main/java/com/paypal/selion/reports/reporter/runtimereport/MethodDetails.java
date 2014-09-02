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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.testng.ITestResult;

public class MethodDetails {

    private String suite;
    private String test;
    private String packageInfo;
    private String className;
    private String methodName;
    private String status;
    private String startTime;
    private String endTime;
    private String description;
    private String exception;
    private String[] stacktrace = {};

    private ITestResult result;

    public MethodDetails(String suite, String test, String packages, String classname, ITestResult result) {

        setSuite(suite);
        setTest(test);
        setPackageInfo(packages);
        setClassName(classname);

        setResult(result);

        setMethodName(result.getName());

        if (result.getStatus() == ITestResult.SUCCESS) {
            setStatus("Passed");
        } else if (result.getStatus() == ITestResult.FAILURE) {
            setStatus("Failed");
        } else if (result.getStatus() == ITestResult.SKIP) {
            setStatus("Skipped");
        } else if (result.getStatus() == ITestResult.STARTED) {
            setStatus("Running");
        }

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(result.getStartMillis());
        setStartTime(formatter.format(c.getTime()));

        Calendar endtime = Calendar.getInstance();

        endtime.setTimeInMillis(result.getEndMillis());
        setEndTime(formatter.format(endtime.getTime()));

        if (result.getMethod().getDescription() != null) {
            setDescription(result.getMethod().getDescription());
        } else {
            setDescription("");
        }

        if (result.getThrowable() != null) {
            setException(result.getThrowable().getClass().toString() + ":"
                    + result.getThrowable().getLocalizedMessage());
            setStacktrace(getStackTraceInfo(result.getThrowable()).split("\n"));
        } else {
            setException("");

        }
    }

    public ITestResult getResult() {
        return result;
    }

    public final void setResult(ITestResult result) {
        this.result = result;
    }

    public String escapeQuotes(String data) {
        return data.replace("\"", "&quot;");
    }

    public String removeNewLine(String str) {
        str = str.replace("\n", "<br>");
        str = str.replace("\r", "<br>");
        return str;
    }

    public String escapeScriptTag(String data) {
        data = data.replace("<script>", "<scriptTag>");
        data = data.replace("<SCRIPT>", "<scriptTag>");
        data = data.replace("</script>", "<scriptTag>");
        data = data.replace("</SCRIPT>", "</scriptTag>");
        return data;
    }

    /**
     * Used to return StackTrace of an Exception as String.
     * 
     * @param aThrowable
     * @return StackTrace as String
     */
    public String getStackTraceInfo(Throwable aThrowable) {

        final Writer localWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(localWriter);
        aThrowable.printStackTrace(printWriter);
        return localWriter.toString();
    }

    public String getSuite() {
        return suite;
    }

    public final void setSuite(String suite) {
        this.suite = suite;
    }

    public String getTest() {
        return test;
    }

    public final void setTest(String test) {
        this.test = test;
    }

    public String getPackageInfo() {
        return packageInfo;
    }

    public final void setPackageInfo(String packageInfo) {
        this.packageInfo = packageInfo;
    }

    public String getClassName() {
        return className;
    }

    public final void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public final void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getStatus() {
        return status;
    }

    public final void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public final void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public final void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public String getException() {
        return exception;
    }

    public final void setException(String exception) {
        this.exception = exception;
    }

    public String[] getStacktrace() {
        return Arrays.copyOf(stacktrace, stacktrace.length);
    }

    public final void setStacktrace(String[] stacktrace) {
        this.stacktrace = Arrays.copyOf(stacktrace, stacktrace.length);
    }

}
