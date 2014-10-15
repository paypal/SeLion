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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.reports.reporter.html.ReporterException;
import com.paypal.selion.reports.reporter.services.ConfigSummaryData;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * RuntimeReportHelper will provide methods to create html file which contains information about list of test methods
 * and configuration methods executed and there corresponding status.
 *
 */
public class RuntimeReporterHelper {

    private volatile Document doc;
    private List<TestMethodDetails> runningTest;
    private List<ConfigMethodDetails> runningConfig;

    private File completedTest;
    private File completedConfig;
    private StringBuffer configSummary = new StringBuffer("");
    private Map<String,String> testLocalConfigSummary = new HashMap<String,String>();
    private long previousTime = 0;
    private int id = 0;

    private static SimpleLogger logger = SeLionLogger.getLogger();

    public RuntimeReporterHelper(Document doc) {
        this.doc = doc;
        doc.appendChild(doc.createElement("RuntimeReporter"));
        runningTest = new ArrayList<TestMethodDetails>();
        runningConfig = new ArrayList<ConfigMethodDetails>();
        try {
            File workingDir = new File(Config.getConfigProperty(ConfigProperty.WORK_DIR));
            workingDir.mkdirs();
            completedTest = File.createTempFile("selion", "RuntimeReporterCompletedTest", workingDir);
            completedTest.deleteOnExit();
            completedConfig = File.createTempFile("selion", "RuntimeReporterCompletedConfig", workingDir);
            completedConfig.deleteOnExit();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * This method returns the running test method details as javascript array
     *
     * @return testmethod details as javascript array
     */
    public StringBuilder getRunningTestMethodDetails() {

        logger.entering();

        StringBuilder runningTestMethodDetails = new StringBuilder("");

        for (TestMethodDetails temp : runningTest) {
            runningTestMethodDetails.append(temp.toJavaScriptArray() + ",\n");
        }
        if (runningTestMethodDetails.length() > 0) {
            runningTestMethodDetails.setLength(runningTestMethodDetails.length() - 2);
        }
        logger.exiting(runningTestMethodDetails);
        return runningTestMethodDetails;
    }

    /**
     * This method returns the running config method details as javascript array
     *
     * @return configmethod details as javascript array
     */
    public StringBuilder getRunningConfigMethodDetails() {
        logger.entering();
        StringBuilder runningConfigMethodDetails = new StringBuilder("");
        for (ConfigMethodDetails temp : runningConfig) {
            runningConfigMethodDetails.append(temp.toJavaScriptArray() + ",\n");
        }
        if (runningConfigMethodDetails.length() > 0) {
            runningConfigMethodDetails.setLength(runningConfigMethodDetails.length() - 2);
        }
        logger.exiting(runningConfigMethodDetails);
        return runningConfigMethodDetails;
    }

    /**
     * This method will generate html tags to create tree view from the list of Suites, Tests, Packages and Class
     * executed
     *
     * @return html tags used to create tree view.
     */
    public String generateTreeView() {
        logger.entering();
        StringBuilder result = new StringBuilder();
        id = 0;
        NodeList list = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i) instanceof Element) {
                result.append(generateTreeView((Element) list.item(i)));
            }
        }

        logger.exiting(result);
        return result.toString();

    }

    private String generateTreeView(Element e) {
        logger.entering(e);
        StringBuilder result = new StringBuilder();
        id = id + 1;
        if (e.getNodeName().equals(TagType.SUITE.getTagType()) || e.getNodeName().equals(TagType.TEST.getTagType())
                || e.getNodeName().equals(TagType.PACKAGE.getTagType())) {
            result.append("<li class='folder'");
            result.append(" id='").append(String.valueOf(id)).append("' ");
            result.append(" title='").append(e.getNodeName()).append(":").append(e.getAttribute("name")).append("'>");
            result.append(e.getNodeName()).append(":").append(e.getAttribute("name"));

            NodeList list = e.getChildNodes();
            result.append("<ul>");
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i) instanceof Element) {
                    result.append(generateTreeView((Element) list.item(i)));
                }
            }
            result.append("</ul>");
        } else {
            result.append("<li ");
            result.append(" id='").append(String.valueOf(id)).append("' ");
            result.append(" title='").append(e.getNodeName()).append(":").append(e.getAttribute("name")).append("'>");
            result.append(e.getNodeName()).append(":").append(e.getAttribute("name"));

        }
        result.append("</li>");
        logger.exiting(result);
        return result.toString();
    }

    public String getCompletedTestContent() {
        return getFileContent(completedTest);
    }

    public String getCompletedConfigContent() {
        return getFileContent(completedConfig);
    }

    private String getFileContent(File file) {
        BufferedReader br = null;
        StringBuilder output = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(file));

            String temp = null;

            while ((temp = br.readLine()) != null) {
                output.append(temp);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
        return output.toString();

    }

    /**
     * This method will generate Configuration summary by fetching the details from ReportDataGenerator
     */
    public void generateConfigSummary() {
        logger.entering();

        for (Entry<String, String> temp : ConfigSummaryData.getConfigSummary().entrySet()) {
            configSummary.append("<tr><td>" + temp.getKey() + "</td>");
            configSummary.append("<td>" + temp.getValue() + "</td></tr>");
        }
        logger.exiting();
    }

    /**
     * This method will generate test local configuration summary in html format to render as table
     *
     * @param testName
     */
    public void generateLocalConfigSummary(String testName) {
        logger.entering(testName);
        Map<String, String> testLocalConfigValues = ConfigSummaryData.getLocalConfigSummary(testName);
        StringBuilder localConfigSummary = new StringBuilder();
        if (testLocalConfigValues == null) {
            localConfigSummary.append("<tr><td>Current Date</td>");
            localConfigSummary.append("<td>")
                    .append(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date()))
                    .append("</td></tr>");
        } else {
            for (Entry<String, String> temp : testLocalConfigValues.entrySet()) {
                localConfigSummary.append("<tr><td>").append(temp.getKey()).append("</td>");
                localConfigSummary.append("<td>").append(temp.getValue()).append("</td></tr>");
            }
        }

        this.testLocalConfigSummary.put(testName, localConfigSummary.toString());
        logger.exiting();
    }

    /**
     * This method is used to insert test method details based on the methods suite, test, groups and class name.
     *
     * @param suite
     *            - suite name of the test method.
     * @param test
     *            - test name of the test method.
     * @param packages
     *            - group name of the test method. If the test method doesn't belong to any group then we should pass
     *            null.
     * @param classname
     *            - class name of the test method.
     * @param result
     *            - ITestResult instance of the test method.
     */

    public synchronized void insertTestMethod(String suite, String test, String packages, String classname,
            ITestResult result) {
        logger.entering(new Object[] { suite, test, packages, classname, result });

        insertDocument(suite, test, packages, classname);

        TestMethodDetails test1 = new TestMethodDetails(suite, test, packages, classname, result);

        if (result.getStatus() == ITestResult.STARTED) {
            runningTest.add(test1);
        } else {
            TestMethodDetails testToRemove = null;
            for (TestMethodDetails temp : runningTest) {
                if (temp.getResult().equals(result)) {
                    testToRemove = temp;
                    break;
                }
            }

            if (testToRemove != null) {
                runningTest.remove(testToRemove);
                appendFile(completedTest, test1.toJavaScriptArray().append(",\n"));

            } else if (result.getStatus() == ITestResult.SKIP) {
                appendFile(completedTest, test1.toJavaScriptArray().append(",\n"));
            }
        }
        logger.exiting();
    }

    public void appendFile(File file, StringBuffer data) {
        logger.entering(new Object[] { file, data });
        FileWriter fileWritter;
        try {
            fileWritter = new FileWriter(file, true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(data.toString());
            bufferWritter.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        logger.exiting();
    }

    /**
     * This method is used to insert configuration method details based on the suite, test, groups and class name.
     *
     * @param suite
     *            - suite name of the configuration method.
     * @param test
     *            - test name of the configuration method.
     * @param packages
     *            - group name of the configuration method. If the configuration method doesn't belong to any group then
     *            we should pass null.
     * @param classname
     *            - class name of the configuration method.
     * @param result
     *            - ITestResult instance of the configuration method.
     */

    public synchronized void insertConfigMethod(String suite, String test, String packages, String classname,
            ITestResult result) {

        logger.entering(new Object[] { suite, test, packages, classname, result });
        String type = null;
        if (result.getMethod().isBeforeSuiteConfiguration()) {
            type = "BeforeSuite";
        } else if (result.getMethod().isBeforeTestConfiguration()) {
            type = "BeforeTest";
        } else if (result.getMethod().isBeforeGroupsConfiguration()) {
            type = "BeforeGroup";
        } else if (result.getMethod().isBeforeClassConfiguration()) {
            type = "BeforeClass";
        } else if (result.getMethod().isBeforeMethodConfiguration()) {
            type = "BeforeMethod";
        } else if (result.getMethod().isAfterSuiteConfiguration()) {
            type = "AfterSuite";
        } else if (result.getMethod().isAfterTestConfiguration()) {
            type = "AfterTest";
        } else if (result.getMethod().isAfterGroupsConfiguration()) {
            type = "AfterGroup";
        } else if (result.getMethod().isAfterClassConfiguration()) {
            type = "AfterClass";
        } else if (result.getMethod().isAfterMethodConfiguration()) {
            type = "AfterMethod";
        }

        insertDocument(suite, test, packages, classname);

        ConfigMethodDetails config1 = new ConfigMethodDetails(suite, test, packages, classname, type, result);

        if (result.getStatus() == ITestResult.STARTED) {
            runningConfig.add(config1);
        } else {
            ConfigMethodDetails configToRemove = null;
            for (ConfigMethodDetails temp : runningConfig) {
                if (temp.getResult().equals(result)) {
                    configToRemove = temp;
                }
            }

            if (configToRemove != null) {
                runningConfig.remove(configToRemove);
                appendFile(completedConfig, config1.toJavaScriptArray().append(",\n"));

            } else {
                appendFile(completedConfig, config1.toJavaScriptArray().append(",\n"));
            }

        }
        logger.exiting();
    }

    public void insertDocument(String suite, String test, String packages, String classname) {
        logger.entering(new Object[] { suite, test, packages, classname });
        Element suiteElement = getSuiteElement(doc.getDocumentElement(), suite);
        if (suiteElement != null) {
            Element testElement = getTestElement(suiteElement, test);
            if (testElement != null) {
                Element packageElement = getPackageElement(testElement, packages);
                if (packageElement != null) {
                    Element classElement = getClassElement(packageElement, classname);
                    if (classElement == null) {
                        addClass(packageElement, classname);
                    }
                } else {
                    addClass(addPackage(testElement, packages), classname);
                }
            } else {
                addClass(addPackage(addTest(suiteElement, test), packages), classname);
            }
        } else {
            addClass(addPackage(addTest(addSuite(doc.getDocumentElement(), suite), test), packages), classname);
        }
        logger.exiting();

    }

    private Element addSuite(Element parent, String suiteName) {

        return addElement(parent, TagType.SUITE, suiteName);
    }

    private Element addTest(Element parent, String testName) {
        return addElement(parent, TagType.TEST, testName);
    }

    private Element addClass(Element parent, String className) {

        return addElement(parent, TagType.CLASS, className);
    }

    private Element addPackage(Element parent, String groupName) {

        return addElement(parent, TagType.PACKAGE, groupName);
    }

    private Element getSuiteElement(Element parent, String suiteName) {

        return getElement(parent, TagType.SUITE, suiteName);
    }

    private Element getTestElement(Element parent, String testName) {

        return getElement(parent, TagType.TEST, testName);
    }

    private Element getPackageElement(Element parent, String groupName) {

        return getElement(parent, TagType.PACKAGE, groupName);
    }

    private Element getClassElement(Element parent, String className) {

        return getElement(parent, TagType.CLASS, className);
    }

    /**
     * Used to get element of tag "type" from the parent element.
     *
     * @param parent
     *            - parent element whose descendants are searched.
     * @param type
     *            - tag name of the element to be searched (Suite, Test, Group or Class).
     * @param name
     *            - value for name attribute of the element
     * @return the element with tag name type or null
     */
    private Element getElement(Element parent, TagType type, String name) {
        logger.entering(new Object[] { parent, type, name });

        NodeList list = parent.getElementsByTagName(type.getTagType());
        for (int i = 0; i < list.getLength(); i++) {
            Element temp = (Element) list.item(i);
            if (temp.getAttribute("name").equals(name)) {
                logger.exiting(temp);
                return temp;
            }
        }
        logger.exiting(null);
        return null;
    }

    /**
     * Creates an element and add it as child to a parent element with tag as type
     *
     * @param parent
     *            - parent Element.
     * @param type
     *            - tag name of the element to be created
     * @param name
     *            - value of name attribute of the element created.
     * @return the newly created element.
     */
    public Element addElement(Element parent, TagType type, String name) {
        logger.entering(new Object[] { parent, type, name });
        Element e = doc.createElement(type.getTagType());
        e.setAttribute("name", name);
        parent.appendChild(e);
        logger.exiting(e);
        return e;
    }

    public synchronized void writeXML(String outputDirectory, boolean bForceWrite) {
        logger.entering(new Object[] { outputDirectory, bForceWrite });

        long currentTime = System.currentTimeMillis();
        if (!bForceWrite) {
            if (currentTime - previousTime < (1000 * 60)) {
                return;
            }
        }
        previousTime = currentTime;
        ClassLoader localClassLoader = this.getClass().getClassLoader();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputDirectory + File.separator + "index.html"));
                BufferedReader templateReader = new BufferedReader(new InputStreamReader(
                        localClassLoader.getResourceAsStream("templates/RuntimeReporter/index.html")));) {

            String temp = null;
            while ((temp = templateReader.readLine()) != null) {

                if (temp.trim().equals("${configSummary}")) {
                    writer.write(configSummary.toString());
                    writer.newLine();
                } else if (temp.trim().equals("${tree}")) {
                    writer.write(generateTreeView());
                    writer.newLine();
                } else if (temp.trim().equals("${testMethod}")) {
                    StringBuilder runningTestDetails = getRunningTestMethodDetails();
                    if (runningTestDetails.length() > 0) {
                        writeReader(writer, completedTest, false);
                        writer.write(runningTestDetails.toString());
                        writer.newLine();
                    } else {
                        writeReader(writer, completedTest, true);
                        writer.newLine();
                    }

                } else if (temp.trim().equals("${configMethod}")) {
                    StringBuilder runningConfigDetails = getRunningConfigMethodDetails();
                    if (runningConfigDetails.length() > 0) {
                        writeReader(writer, completedConfig, false);
                        writer.write(runningConfigDetails.toString());
                        writer.newLine();
                    } else {
                        writeReader(writer, completedConfig, true);
                        writer.newLine();
                    }
                } else if (temp.trim().equals("var testLocalConfigSummaryMap = {};")) {
                    StringBuilder mapString = new StringBuilder();
                    mapString.append("var testLocalConfigSummaryMap = ");
                    mapString.append(this.getTestLocalConfigAsJsonString());
                    mapString.append(";");
                    if (!this.testLocalConfigSummary.isEmpty()) {
                        writer.write(mapString.toString());
                        writer.newLine();
                    }
                } else {
                    writer.write(temp);
                    writer.newLine();
                }
            }
        } catch (IOException | JSONException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ReporterException(e);
        }
        logger.exiting();
    }

    private void writeReader(BufferedWriter writer, File file, boolean bTruncateComma) throws IOException {

        BufferedReader tempReader = new BufferedReader(new FileReader(file));
        String temp = null;
        String temp1 = null;

        while ((temp1 = tempReader.readLine()) != null) {
            if (temp != null) {
                writer.write(temp);
                writer.newLine();
            }
            temp = temp1;

        }
        if (temp != null) {
            if (bTruncateComma) {
                writer.write(temp.substring(0, temp.lastIndexOf(',')));
            } else {
                writer.write(temp);
            }
            writer.newLine();
        }
        tempReader.close();

    }

    private String getTestLocalConfigAsJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        for (Entry<String, String> temp : testLocalConfigSummary.entrySet()) {
            json.put(temp.getKey(), temp.getValue());
        }
        json.put("GlobalConfig", configSummary.toString());
        return json.toString();
    }
}
