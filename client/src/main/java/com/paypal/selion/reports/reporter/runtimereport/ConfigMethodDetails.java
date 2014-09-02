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

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.reports.model.WebLog;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class is used to convert all the details of the Configuration Method in the order used by Runtime Reporter.
 * 
 */
public class ConfigMethodDetails extends MethodDetails {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private String type;

    /**
     * Instantiate ConfigMethodDetails object with suite, test, packages, class name and result details
     * 
     * @param suite
     *            - name of the suite
     * @param test
     *            - name of the test
     * @param packages
     *            - name of the package without class name
     * @param classname
     *            - name of the class without package name
     * @param type
     *            - The type of the method.
     * @param result
     *            - ITestResult of the config method which need to be reported
     */
    public ConfigMethodDetails(String suite, String test, String packages, String classname, String type,
            ITestResult result) {
        super(suite, test, packages, classname, result);
        this.type = type;

    }

    /**
     * Convert all the configuration information as javascript array
     * 
     * @return javascript array
     */
    public StringBuffer toJavaScriptArray() {
        logger.entering();

        StringBuffer output = new StringBuffer("[\"<img src='resources/details_open.png'/>\",");

        output.append("\"").append(escapeQuotes(getSuite())).append("\",");
        output.append("\"").append(escapeQuotes(getTest())).append("\",");
        output.append("\"").append(escapeQuotes(getPackageInfo())).append("\",");
        output.append("\"").append(escapeQuotes(getClassName())).append("\",");
        output.append("\"").append(escapeQuotes(getMethodName())).append("\",");
        output.append("\"").append(escapeQuotes(type)).append("\",");
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

        for (String temp : Reporter.getOutput(getResult())) {
            WebLog logLine = new WebLog(temp);
            logOutput.append("<tr>");
            if (logLine.getMsg() != null && !logLine.getMsg().isEmpty()) {
                logOutput.append("<td>").append(removeNewLine(escapeQuotes(escapeScriptTag(logLine.getMsg()))))
                        .append("</td>");
            } else {
                logOutput.append("<td></td>");
            }

            if (logLine.getScreen() != null && !logLine.getScreen().isEmpty()) {
                logOutput.append("<td style='width: 100px'> <a ");
                logOutput.append("href=").append(oneDirUp).append(logLine.getScreen()).append(">");
                logOutput.append("Screen Shot</a></td>");

            }
            if (logLine.getHref() != null && !logLine.getHref().isEmpty()) {
                logOutput.append("<td style='width: 100px'> <a ");
                logOutput.append("href=").append(oneDirUp).append(logLine.getHref()).append(">");
                logOutput.append("Source</a></td>");
            }
            logOutput.append("</tr>");

        }
        logOutput.append("</table>");

        output.append("\"").append(logOutput).append("\"");

        output.append("]");

        logger.exiting(output);
        return output;

    }

}
