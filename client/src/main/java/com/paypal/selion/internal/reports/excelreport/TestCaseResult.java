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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.paypal.selion.internal.reports.model.BaseLog;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Contains all Result data related to each testcase
 */
public class TestCaseResult implements Comparable<TestCaseResult> {

    private static SimpleLogger logger = SeLionLogger.getLogger();
    private ITestResult iTestResult;
    private String className;
    private String methodName;
    private List<String> lstDefectMsg;
    private List<String> lstError;
    private String testDescription;
    private List<String> sGroup;
    private long durationTaken;
    private List<String> sSSMsg;
    private int iStatus;

    public static final String NA = "NA";
    public static final String NEWLINE = "\n";
    private static String sOutputDir;

    public static void setOutputDirectory(String outputDirectory) {
        sOutputDir = outputDirectory;
    }

    public TestCaseResult() {
        this.iTestResult = null;
        this.className = null;
        this.methodName = null;
        this.lstDefectMsg = new ArrayList<String>();
        this.lstError = new ArrayList<String>();
        this.testDescription = null;
        this.sGroup = new ArrayList<String>();
        this.sSSMsg = new ArrayList<String>();

    }

    public void setITestResultobj(ITestResult iTestResult) {
        logger.entering(iTestResult);
        this.iTestResult = iTestResult;
        this.durationTaken = iTestResult.getEndMillis() - iTestResult.getStartMillis();
        this.iStatus = iTestResult.getStatus();

        ITestNGMethod singleMethod = iTestResult.getMethod();
        // Generating method name for cases based on DataProvider
        // These would appear as 'metName [param1] [param2] ...

        StringBuilder sMethodName = new StringBuilder(this.iTestResult.getName());
        Object[] metParamArray = this.iTestResult.getParameters();
        if (metParamArray.length > 0) {
            for (int i = 0; i < metParamArray.length; i++) {
                sMethodName.append(" [").append(metParamArray[i]).append("]");
            }
        }
        this.setMethodName(sMethodName.toString());
        this.setClassName(singleMethod.getTestClass().getName());
        this.setTestDesc(singleMethod.getDescription());
        if (singleMethod.getGroups().length == 0) {
            this.getGroup().add(NA);
            this.setGroup(this.getGroup());
        } else {
            this.setGroup(Arrays.asList(singleMethod.getGroups()));
        }

        this.sSSMsg = makeContentForLinksColumn(iTestResult);

        // if failed, then add error details
        if (iStatus == ITestResult.FAILURE) {
            String stacktrace = StringUtils.substringBetween(ExceptionUtils.getStackTrace(iTestResult.getThrowable()),
                    NEWLINE, "\tat sun.reflect");
            stacktrace = StringUtils
                    .defaultString(stacktrace, ExceptionUtils.getStackTrace(iTestResult.getThrowable()));
            lstError.add(iTestResult.getThrowable().toString() + NEWLINE + stacktrace.replace("\t", "\t\t"));
            String defectMsg = TestCaseErrors.getInstance().debugError(iTestResult.getThrowable());
            lstDefectMsg.add(defectMsg == null ? "Assert Failed or Script error" : defectMsg);
        }
        logger.exiting();
    }

    /*
     * Generates a list of textual content of the test output that includes test parameters, screenshot file, page
     * source file and other custom message logged in TestNG output for the given test result.
     * 
     * @return Test Output in List of string.
     */
    private List<String> makeContentForLinksColumn(ITestResult result) {
        List<String> fileLinks = new LinkedList<String>();
        Object[] parameters = result.getParameters();
        boolean hasParameters = parameters != null && parameters.length > 0;
        List<String> msgs = Reporter.getOutput(result);
        boolean hasReporterOutput = msgs.size() > 0;
        Throwable exception = result.getThrowable();
        boolean hasThrowable = exception != null;

        if (hasReporterOutput || hasThrowable) {
            if (hasParameters) {
                fileLinks.add("parameters:");
                for (int i = 0; i < parameters.length; i++) {
                    Object p = parameters[i];
                    String paramAsString = "null";
                    if (p != null) {
                        paramAsString = p.toString() + "(" + p.getClass().getSimpleName() + ")";
                    }
                    fileLinks.add(paramAsString);
                }
            }

            for (String line : msgs) {
                BaseLog logLine = new BaseLog(line);
                if (logLine.getScreen() != null) {
                    fileLinks.add("screen capture: file:" + File.separator + File.separator + sOutputDir
                            + File.separator + logLine.getScreenURL());
                }

                if ((logLine.getHref() != null) && (logLine.getHref().length() > 1)) {
                    fileLinks.add("page source: file:" + File.separator + File.separator + sOutputDir + File.separator
                            + logLine.getHref());
                    fileLinks.add("page URL: " + logLine.getLocation());
                }
                if ((logLine.getMsg() != null) && !logLine.getMsg().isEmpty()) {
                    fileLinks.add("message: " + logLine.getMsg());
                }
            }

        }

        return fileLinks;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setTestDesc(String testDescription) {
        this.testDescription = testDescription;
    }

    public void setError(List<String> sError) {
        this.lstError = sError;
    }

    public void setDefect(List<String> sDefect) {
        this.lstDefectMsg = sDefect;
    }

    public void setGroup(List<String> sGroup) {
        this.sGroup = sGroup;
    }

    public ITestResult getITestResultobj() {
        return this.iTestResult;
    }

    public String getClassName() {
        return this.className;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public List<String> getDefect() {
        return this.lstDefectMsg;
    }

    public List<String> getError() {
        return this.lstError;
    }

    public String getTestDesc() {
        return this.testDescription;
    }

    public List<String> getGroup() {
        return this.sGroup;
    }

    public long getDurationTaken() {
        return this.durationTaken;
    }

    public List<String> getssmsg() {
        return this.sSSMsg;
    }

    public int getStatus() {
        return this.iStatus;
    }

    @Override
    public int compareTo(TestCaseResult o) {
        if (this.getClassName().compareTo(o.getClassName()) == 0) {
            return this.getMethodName().compareTo(o.getMethodName());
        } else {
            return this.getClassName().compareTo(o.getClassName());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TestCaseResult)) {
            return false;
        }
        TestCaseResult other = (TestCaseResult) obj;
        if (className == null) {
            if (other.className != null) {
                return false;
            }
        } else if (!className.equals(other.className)) {
            return false;
        }
        if (methodName == null) {
            if (other.methodName != null) {
                return false;
            }
        } else if (!methodName.equals(other.methodName)) {
            return false;
        }
        return true;
    }
}