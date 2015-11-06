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

package com.paypal.selion.internal.reports.runtimereport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.testng.ITestResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.internal.reports.html.ReporterException;
import com.paypal.selion.internal.reports.services.ReporterDateFormatter;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.reports.services.ConfigSummaryData;
import com.paypal.selion.reports.services.ReporterConfigMetadata;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * JsonRuntimeReporterHelper will provide methods to create JSON file which contains information about list of test
 * methods and configuration methods executed with their corresponding status.
 * 
 */
public class JsonRuntimeReporterHelper {

    public static final String IS_COMPLETED = "isCompleted";
    private static final int ONE_MINUTE = 60000;
    private static final String AFTER_METHOD = "AfterMethod";
    private static final String AFTER_CLASS = "AfterClass";
    private static final String AFTER_GROUP = "AfterGroup";
    private static final String AFTER_TEST = "AfterTest";
    private static final String AFTER_SUITE = "AfterSuite";
    private static final String BEFORE_METHOD = "BeforeMethod";
    private static final String BEFORE_CLASS = "BeforeClass";
    private static final String BEFORE_GROUP = "BeforeGroup";
    private static final String BEFORE_TEST = "BeforeTest";
    private static final String BEFORE_SUITE = "BeforeSuite";
    private final List<TestMethodInfo> runningTest = new ArrayList<TestMethodInfo>();;
    private final List<ConfigMethodInfo> runningConfig = new ArrayList<ConfigMethodInfo>();
    private List<TestMethodInfo> completedTest = new ArrayList<TestMethodInfo>();

    private File jsonCompletedTest;
    private File jsonCompletedConfig;
    private final JsonArray testJsonLocalConfigSummary = new JsonArray();
    private JsonObject jsonConfigSummary;
    private long previousTime;

    private static SimpleLogger logger = SeLionLogger.getLogger();

    public JsonRuntimeReporterHelper() {
        try {
            File workingDir = new File(Config.getConfigProperty(ConfigProperty.WORK_DIR));
            boolean isCreated = workingDir.mkdirs();
            logger.log(Level.FINER, "Working directory created successfully: " + isCreated);
            jsonCompletedTest = File.createTempFile("selion-rrct", null, workingDir);
            jsonCompletedTest.deleteOnExit();
            jsonCompletedConfig = File.createTempFile("selion-rrcf", null, workingDir);
            jsonCompletedConfig.deleteOnExit();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ReporterException(e);
        }
    }

    /**
     * This method will generate Configuration summary by fetching the details from ReportDataGenerator
     */
    private JsonObject generateConfigSummary() throws JsonParseException {

        logger.entering();

        if (jsonConfigSummary == null) {
            jsonConfigSummary = new JsonObject();
            for (Entry<String, String> temp : ConfigSummaryData.getConfigSummary().entrySet()) {
                jsonConfigSummary.addProperty(temp.getKey(), temp.getValue());
            }
        }
        logger.exiting(jsonConfigSummary);

        return jsonConfigSummary;
    }

    /**
     * This method will generate local Configuration summary by fetching the details from ReportDataGenerator
     * 
     * @param suiteName
     *            suite name of the test method.
     * @param testName
     *            test name of the test method.
     */
    public void generateLocalConfigSummary(String suiteName, String testName) {

        logger.entering(new Object[] { suiteName, testName });

        try {
            Map<String, String> testLocalConfigValues = ConfigSummaryData.getLocalConfigSummary(testName);
            JsonObject json = new JsonObject();
            if (testLocalConfigValues == null) {
                json.addProperty(ReporterDateFormatter.CURRENTDATE, ReporterDateFormatter.getISO8601String(new Date()));
            } else {
                for (Entry<String, String> temp : testLocalConfigValues.entrySet()) {
                    json.addProperty(temp.getKey(), temp.getValue());
                }
            }

            json.addProperty("suite", suiteName);
            json.addProperty("test", testName);
            // Sometimes json objects getting null value when data provider parallelism enabled
            // To solve this added synchronized block
            synchronized (this) {
                this.testJsonLocalConfigSummary.add(json);
            }
        } catch (JsonParseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ReporterException(e);
        }

        logger.exiting();
    }

    /**
     * This method is used to insert test method details based on the methods suite, test, groups and class name.
     * 
     * @param suite
     *            suite name of the test method.
     * @param test
     *            test name of the test method.
     * @param packages
     *            group name of the test method. If the test method doesn't belong to any group then we should pass
     *            null.
     * @param classname
     *            class name of the test method.
     * @param result
     *            ITestResult instance of the test method.
     */
    public synchronized void insertTestMethod(String suite, String test, String packages, String classname,
            ITestResult result) {
        logger.entering(new Object[] { suite, test, packages, classname, result });

        TestMethodInfo test1 = new TestMethodInfo(suite, test, packages, classname, result);

        if (result.getStatus() == ITestResult.STARTED) {
            runningTest.add(test1);
            return;
        }

        for (TestMethodInfo temp : runningTest) {
            if (temp.getResult().getMethod().equals(result.getMethod())) {
                runningTest.remove(temp);
                break;
            }
        }
        completedTest.add(test1);
        logger.exiting();
    }

    private void appendFile(File file, String data) {

        logger.entering(new Object[] { file, data });

        try {
            FileUtils.writeStringToFile(file, data, true);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ReporterException(e);
        }

        logger.exiting();
    }

    /**
     * This method is used to insert configuration method details based on the suite, test, groups and class name.
     * 
     * @param suite
     *            suite name of the configuration method.
     * @param test
     *            test name of the configuration method.
     * @param packages
     *            group name of the configuration method. If the configuration method doesn't belong to any group then
     *            we should pass null.
     * @param classname
     *            class name of the configuration method.
     * @param result
     *            ITestResult instance of the configuration method.
     */

    public synchronized void insertConfigMethod(String suite, String test, String packages, String classname,
            ITestResult result) {

        logger.entering(new Object[] { suite, test, packages, classname, result });
        String type = null;
        if (result.getMethod().isBeforeSuiteConfiguration()) {
            type = BEFORE_SUITE;
        } else if (result.getMethod().isBeforeTestConfiguration()) {
            type = BEFORE_TEST;
        } else if (result.getMethod().isBeforeGroupsConfiguration()) {
            type = BEFORE_GROUP;
        } else if (result.getMethod().isBeforeClassConfiguration()) {
            type = BEFORE_CLASS;
        } else if (result.getMethod().isBeforeMethodConfiguration()) {
            type = BEFORE_METHOD;
        } else if (result.getMethod().isAfterSuiteConfiguration()) {
            type = AFTER_SUITE;
        } else if (result.getMethod().isAfterTestConfiguration()) {
            type = AFTER_TEST;
        } else if (result.getMethod().isAfterGroupsConfiguration()) {
            type = AFTER_GROUP;
        } else if (result.getMethod().isAfterClassConfiguration()) {
            type = AFTER_CLASS;
        } else if (result.getMethod().isAfterMethodConfiguration()) {
            type = AFTER_METHOD;
        }

        ConfigMethodInfo config1 = new ConfigMethodInfo(suite, test, packages, classname, type, result);

        if (result.getStatus() == ITestResult.STARTED) {
            runningConfig.add(config1);
            return;
        }

        for (ConfigMethodInfo temp : runningConfig) {
            if (temp.getResult().getMethod().equals(result.getMethod())) {
                runningConfig.remove(temp);
                break;
            }
        }

        appendFile(jsonCompletedConfig, config1.toJson().concat(",\n"));

        logger.exiting();
    }

    /**
     * Generate the final report.json from the completed test and completed configuration temporary files.
     * 
     * @param outputDirectory
     *            output directory
     * @param bForceWrite
     *            setting true will forcibly generate the report.json
     */
    public synchronized void writeJSON(String outputDirectory, boolean bForceWrite) {
        logger.entering(new Object[] { outputDirectory, bForceWrite });

        long currentTime = System.currentTimeMillis();
        if (!bForceWrite) {
            if (currentTime - previousTime < ONE_MINUTE) {
                return;
            }
        }
        previousTime = currentTime;

        parseCompletedTest();

        generateReports(outputDirectory);

        logger.exiting();
    }

    /**
     * Parse the list of completed test and write to the completed temp file
     */
    private void parseCompletedTest() {
        logger.entering();

        List<TestMethodInfo> tempCompletedTest = new ArrayList<TestMethodInfo>();
        for (TestMethodInfo temp : completedTest) {
            Object isCompleted = temp.getResult().getAttribute(IS_COMPLETED);
            if (isCompleted != null && (boolean) isCompleted) {
                appendFile(jsonCompletedTest, temp.toJson().concat(",\n"));
            } else {
                tempCompletedTest.add(temp);
            }
        }
        completedTest = tempCompletedTest;
        logger.exiting();
    }

    /**
     * Generate JSON report and HTML report
     * 
     * @param outputDirectory
     */
    private void generateReports(String outputDirectory) {
        logger.entering(outputDirectory);

        ClassLoader localClassLoader = this.getClass().getClassLoader();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputDirectory + File.separator + "index.html"));
                BufferedWriter jsonWriter = new BufferedWriter(new FileWriter(outputDirectory + File.separator
                        + "report.json"));
                BufferedReader templateReader = new BufferedReader(new InputStreamReader(
                        localClassLoader.getResourceAsStream("templates/RuntimeReporter/index.html")));) {

            JsonObject reporter = buildJSONReport();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonReport = gson.toJson(reporter);
            jsonWriter.write(jsonReport);
            jsonWriter.newLine();

            generateHTMLReport(writer, templateReader, jsonReport);

        } catch (IOException | JsonParseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ReporterException(e);
        }

        logger.exiting();
    }

    /**
     * Writing JSON content to HTML file
     *
     * @param writer
     * @param templateReader
     * @param jsonReport
     * @throws IOException
     */
    private void generateHTMLReport(BufferedWriter writer, BufferedReader templateReader, String jsonReport)
            throws IOException {

        logger.entering(new Object[] { writer, templateReader, jsonReport });

        String readLine = null;
        while ((readLine = templateReader.readLine()) != null) {
            if (readLine.trim().equals("${reports}")) {
                writer.write(jsonReport);
                writer.newLine();
            } else {
                writer.write(readLine);
                writer.newLine();
            }
        }
        logger.exiting();
    }

    /**
     * Construct the JSON report for report generation
     * 
     * @return A {@link JsonObject} which represents the report.
     */
    private JsonObject buildJSONReport() {
        logger.entering();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonArray testObjects = loadJSONArray(jsonCompletedTest);

        for (TestMethodInfo temp : completedTest) {
            testObjects.add(gson.fromJson(temp.toJson(), JsonElement.class));
        }

        for (TestMethodInfo temp : runningTest) {
            testObjects.add(gson.fromJson(temp.toJson(), JsonElement.class));
        }

        JsonArray configObjects = loadJSONArray(jsonCompletedConfig);
        for (ConfigMethodInfo temp : runningConfig) {
            configObjects.add(gson.fromJson(temp.toJson(), JsonElement.class));
        }

        JsonObject summary = new JsonObject();
        summary.add("testMethodsSummary", getReportSummaryCounts(testObjects));
        summary.add("configurationMethodsSummary", getReportSummaryCounts(configObjects));

        JsonElement reportMetadata = gson.fromJson(ReporterConfigMetadata.toJsonAsString(), JsonElement.class);

        JsonObject reporter = new JsonObject();
        reporter.add("reportSummary", summary);
        reporter.add("testMethods", testObjects);
        reporter.add("configurationMethods", configObjects);
        reporter.add("configSummary", generateConfigSummary());
        reporter.add("localConfigSummary", testJsonLocalConfigSummary);
        reporter.add("reporterMetadata", reportMetadata);

        logger.exiting(reporter);

        return reporter;
    }

    /**
     * Provides a JSON object representing the counts of tests passed, failed, skipped and running.
     * 
     * @param testObjects
     *            Array of the current tests as a {@link JsonArray}.
     * @return A {@link JsonObject} with counts for various test results.
     */
    private JsonObject getReportSummaryCounts(JsonArray testObjects) {
        logger.entering(testObjects);

        int runningCount = 0;
        int skippedCount = 0;
        int passedCount = 0;
        int failedCount = 0;
        String result;

        for (JsonElement test : testObjects) {
            result = test.getAsJsonObject().get("status").getAsString();
            switch (result) {
            case "Running":
                runningCount += 1;
                break;
            case "Passed":
                passedCount += 1;
                break;
            case "Failed":
                failedCount += 1;
                break;
            case "Skipped":
                skippedCount += 1;
                break;
            default:
                logger.warning("Found invalid status of the test being run. Status: " + result);
            }
        }

        JsonObject testSummary = new JsonObject();
        if (0 < runningCount) {
            testSummary.addProperty("running", runningCount);
        }
        testSummary.addProperty("passed", passedCount);
        testSummary.addProperty("failed", failedCount);
        testSummary.addProperty("skipped", skippedCount);

        logger.exiting(testSummary);
        return testSummary;
    }

    /**
     * Load the json array for the given file
     * 
     * @param jsonFile
     *            json file location
     * @return JSONArray
     * @throws JSONException
     */
    private JsonArray loadJSONArray(File jsonFile) throws JsonParseException {

        logger.entering(jsonFile);

        String jsonTxt;

        try {
            jsonTxt = FileUtils.readFileToString(jsonFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ReporterException(e);
        }

        StringBuilder completeJSONTxt = new StringBuilder("[");
        completeJSONTxt.append(StringUtils.removeEnd(jsonTxt, ",\n"));
        completeJSONTxt.append("]");
        JsonArray testObjects = (new JsonParser()).parse(completeJSONTxt.toString()).getAsJsonArray();

        logger.exiting(testObjects);

        return testObjects;
    }

    /**
     * Get list of test methods.
     * 
     * @return A list of {@link TestMethodInfo}.
     */
    public List<TestMethodInfo> getCompletedTestContent() {
        return completedTest;
    }

    /**
     * Get list of configuration methods as a {@link JsonArray}.
     * 
     * @return A {@link JsonArray}.
     */
    public JsonArray getCompletedConfigContent() {
        return loadJSONArray(jsonCompletedConfig);
    }
}