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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.testng.ITestResult;
import org.testng.Reporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.internal.reports.model.BaseLog;
import com.paypal.selion.internal.reports.services.ReporterDateFormatter;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Used to hold test method information for reporting purposes.
 */
@SuppressWarnings("unused")
class MethodInfo {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private final String suite;
    private final String test;
    private final String packageInfo;
    private final String className;
    private final String methodName;
    private String status;
    private String startTime;
    private String endTime;
    private String description;
    private String exception;
    private String stacktrace;
    private List<LogInfo> logs;
    // transient because we don't want this field serialized.
    // final because we don't want the object re-assigned
    private final transient ITestResult result;

    /**
     * Constructor.
     * 
     * @param suite
     *            name of the suite
     * @param test
     *            name of the test
     * @param packages
     *            name of the package without class name
     * @param classname
     *            name of the class without package name
     * @param result
     *            ITestResult of the method which need to be reported
     */
    public MethodInfo(String suite, String test, String packages, String classname, ITestResult result) {
        this.suite = suite;
        this.test = test;
        this.packageInfo = packages;
        this.className = classname;
        this.result = result;
        this.methodName = result.getName();
    }

    /**
     * Parse the test results and convert to the MethodInfo fields
     */
    private void parseResults() {
        logger.entering();

        if (result.getStatus() == ITestResult.SUCCESS) {
            this.status = "Passed";
        } else if (result.getStatus() == ITestResult.FAILURE) {
            this.status = "Failed";
        } else if (result.getStatus() == ITestResult.SKIP) {
            this.status = "Skipped";
        } else if (result.getStatus() == ITestResult.STARTED) {
            this.status = "Running";
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(result.getStartMillis());
        this.startTime = ReporterDateFormatter.getISO8601String(c.getTime());
        c.setTimeInMillis(result.getEndMillis());
        this.endTime = ReporterDateFormatter.getISO8601String(c.getTime());

        if (result.getMethod().getDescription() != null) {
            this.description = result.getMethod().getDescription();
        }

        if (result.getThrowable() != null) {
            this.exception = result.getThrowable().getClass().toString() + ":"
                    + result.getThrowable().getLocalizedMessage();
            this.stacktrace = getStackTraceInfo(result.getThrowable());
        }

        loadMethodInfo(result);

        logger.exiting();
    }

    private void loadMethodInfo(ITestResult result) {

        List<LogInfo> tempLogs = new ArrayList<LogInfo>();
        for (String temp : Reporter.getOutput(result)) {

            LogInfo logInfo = new LogInfo();
            BaseLog logLine = new BaseLog(temp);

            if (logLine.getMsg() != null && !logLine.getMsg().isEmpty()) {
                logInfo.setMessage(logLine.getMsg());
            }

            if (logLine.getScreen() != null && !logLine.getScreen().isEmpty()) {
                logInfo.setImage(logLine.getScreen());
            }

            if (logLine.getHref() != null && !logLine.getHref().isEmpty()) {
                logInfo.setSource(logLine.getHref());
            }

            tempLogs.add(logInfo);
        }

        if (!tempLogs.isEmpty()) {
            this.logs = tempLogs;
        }
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

    /**
     * Gets the current test result.
     * 
     * @return An {@link ITestResult}.
     */
    public ITestResult getResult() {
        return result;
    }

    /**
     * This method generate the JSON string for the instance. GSON builder helps to build JSON string and it will
     * exclude the static and transient variable during generation.
     * 
     * @return - JSON string
     */
    public String toJson() {
        logger.entering();

        parseResults();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);
        logger.exiting(json);
        return json;
    }
}
