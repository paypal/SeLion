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

import org.testng.ITestResult;
import org.testng.Reporter;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.reports.model.AbstractLog;
import com.paypal.selion.reports.model.AppLog;
import com.paypal.selion.reports.model.WebLog;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class is used to convert all the details of the TestNG Test Method to JSON array in the order used by Runtime
 * Reporter
 * 
 */
public class TestMethodDetails extends MethodDetails {

    private static SimpleLogger logger = SeLionLogger.getLogger();
    private String parameters;

    /**
     * Instantiate TestMethodDetails object with suite, test, packages, classname and result details
     * 
     * @param suite
     *            - name of the suite
     * @param test
     *            - name of the test
     * @param packages
     *            - name of the package without class name
     * @param classname
     *            - name of the class without package name
     * @param result
     *            - ITestResult of the test method which need to be reported
     */
    public TestMethodDetails(String suite, String test, String packages, String classname, ITestResult result) {
        super(suite, test, packages, classname, result);

        StringBuilder param = new StringBuilder();
        for (Object temp : result.getParameters()) {
            if (temp == null) {
                param.append("null").append(" ");
            } else {
                param.append(escapeScriptTag(temp.toString())).append(" ");
            }
        }

        parameters = param.toString();
    }

    public StringBuffer toJavaScriptArray() {

        logger.entering();

        StringBuffer output = new StringBuffer("[\"<img src='resources/details_open.png'/>\",");
        output.append("\"").append(escapeQuotes(getSuite())).append("\",");
        output.append("\"").append(escapeQuotes(getTest())).append("\",");
        output.append("\"").append(escapeQuotes(getPackageInfo())).append("\",");
        output.append("\"").append(escapeQuotes(getClassName())).append("\",");
        output.append("\"").append(escapeQuotes(getMethodName())).append("\",");
        output.append("\"").append(escapeQuotes(parameters)).append("\",");
        output.append("\"").append(escapeQuotes(getStatus())).append("\",");
        output.append("\"").append(escapeQuotes(getStartTime())).append("\",");
        output.append("\"").append(escapeQuotes(getEndTime())).append("\",");
        output.append("\"").append(escapeQuotes(getDescription())).append("\",");
        output.append("\"").append(removeNewLine(escapeQuotes(getException()))).append("\",");

        StringBuffer stackTraceOutput = new StringBuffer("");
        for (String temp : getStacktrace()) {
            stackTraceOutput.append(removeNewLine(escapeQuotes(temp))).append("<br>");
        }

        output.append("\"").append(stackTraceOutput).append("\",");

        StringBuffer logOutput = new StringBuffer("<table width='100%'>");
        String oneDirUp = "../";

        boolean isDeviceTest = getResult().getMethod().getConstructorOrMethod().getMethod()
                .getAnnotation(MobileTest.class) != null;
        boolean isWebTest = getResult().getMethod().getConstructorOrMethod().getMethod().getAnnotation(WebTest.class) != null;

        for (String temp : Reporter.getOutput(getResult())) {
            AbstractLog logLine = new WebLog(temp);
            if (isDeviceTest) {
                logLine = new AppLog(temp);
            }

            logOutput.append("<tr>");
            if (logLine.getMsg() != null && !logLine.getMsg().isEmpty()) {
                logOutput.append("<td>").append(removeNewLine(escapeQuotes(escapeScriptTag(logLine.getMsg()))))
                        .append("</td>");
            } else {
                logOutput.append("<td></td>");
            }

            if (logLine.getScreen() != null && !logLine.getScreen().isEmpty()) {
                logOutput.append("<td style='width: 100px'> <a class='fancybox' data-fancybox-group='gallery'");
                logOutput.append("href=").append(oneDirUp).append(logLine.getScreen()).append(">");
                logOutput.append("Screen Shot</a></td>");

            }
            if (isWebTest) {
                WebLog thisLine = (WebLog) logLine;
                if (thisLine.getHref() != null && !thisLine.getHref().isEmpty()) {
                    logOutput.append("<td style='width: 100px'> <a ");
                    logOutput.append("href=").append(oneDirUp).append(thisLine.getHref()).append(">");
                    logOutput.append("Source</a></td>");
                }
                logOutput.append("</tr>");
            }
            logOutput.append("</tr>");

        }

        logOutput.append("</table>");

        output.append("\"" + logOutput + "\"");
        output.append("]");

        logger.exiting();

        return output;
    }
}
