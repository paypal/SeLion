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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import com.google.common.base.Preconditions;
import com.paypal.selion.configuration.ListenerInfo;
import com.paypal.selion.configuration.ListenerManager;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.reports.services.ConfigSummaryData;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Add this class as listener to generate an ExcelReport for a suite run after the SoftAssertCapabilities file. </br>
 * Implements the IReporter interface to fetch data from TestNG.
 *
 */
@SuppressWarnings("unchecked")
public class ExcelReport implements IReporter {
    private static SimpleLogger logger = SeLionLogger.getLogger();

    /**
     * This String constant represents the JVM argument that can be enabled/disabled to enable/disable
     * {@link ExcelReport}
     */
    public static final String ENABLE_EXCEL_REPORTER_LISTENER = "enable.excel.reporter.listener";

    private HSSFWorkbook wb;
    private String reportFileName = "Excel_Report.xls";

    private final List<SummarizedData> lSuites = new ArrayList<SummarizedData>();
    private final List<SummarizedData> lTests = new ArrayList<SummarizedData>();
    private final List<SummarizedData> lGroups = new ArrayList<SummarizedData>();
    private final List<SummarizedData> lClasses = new ArrayList<SummarizedData>();
    private final List<TestCaseResult> allTestsResults = new ArrayList<TestCaseResult>();

    private final List<List<String>> tcFailedData = new ArrayList<List<String>>();
    private final List<List<String>> tcPassedData = new ArrayList<List<String>>();
    private final List<List<String>> tcSkippedData = new ArrayList<List<String>>();
    private final List<List<String>> tcDefectData = new ArrayList<List<String>>();
    private final List<List<String>> tcOutputData = new ArrayList<List<String>>();

    private final Map<String, SummarizedData> mpGroupClassData = new HashMap<String, SummarizedData>();

    private static List<ReportMap<?>> fullReportMap = new ArrayList<ReportMap<?>>();

    public ExcelReport() {
        // Register this listener with the ListenerManager; disabled by default when not defined in VM argument.
        ListenerManager.registerListener(new ListenerInfo(this.getClass(), ENABLE_EXCEL_REPORTER_LISTENER, false));
    }

    /**
     * The first method that gets called when generating the report. Generates data in way the Excel should appear.
     * Creates the Excel Report and writes it to a file.
     */
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String sOpDirectory) {
        logger.entering(new Object[] { xmlSuites, suites, sOpDirectory });
        if (ListenerManager.isCurrentMethodSkipped(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "Generating ExcelReport");
        }

        TestCaseResult.setOutputDirectory(sOpDirectory);

        // Generate data to suit excel report.
        this.generateSummaryData(suites);
        this.generateTCBasedData(allTestsResults);

        // Create the Excel Report
        this.createExcelReport();

        // Render the report
        Path p = Paths.get(sOpDirectory, reportFileName);

        try {
            Path opDirectory = Paths.get(sOpDirectory);
            if (!Files.exists(opDirectory)) {
                Files.createDirectories(Paths.get(sOpDirectory));
            }

            FileOutputStream fOut = new FileOutputStream(p.toFile());
            wb.write(fOut);
            fOut.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "Excel File Created @ " + p.toAbsolutePath().toString());
        }
    }

    /**
     * Sets the output file name for the Excel Report. The file should have 'xls' extension.
     * 
     * @param fileName
     *            The file name without any path.
     */
    public void setExcelFileName(String fileName) {
        Preconditions.checkArgument(StringUtils.endsWith(fileName, ".xls"), "Excel file name must end with '.xls'.");
        reportFileName = fileName;
    }

    /**
     * Initialized styles used in the workbook. Generates the report related info. Creates the structure of the Excel
     * Reports
     */
    @SuppressWarnings("rawtypes")
    private void createExcelReport() {
        logger.entering();

        wb = new HSSFWorkbook();
        Styles.initStyles(wb);

        // Report Details
        this.createReportInfo();

        // Map of sheet names - individual reports and corresponding data
        this.createReportMap();

        // Render reports in the Workbook
        for (ReportMap rm : fullReportMap) {
            List<BaseReport<?>> allReports = rm.getGeneratedReport();
            allReports.iterator().next().generateRep(this.wb, rm.getName(), rm.getGeneratedReport());
        }

        logger.exiting();
    }

    /**
     * Create Run details like owner of run, time and stage used.
     */
    private void createReportInfo() {
        logger.entering();

        HSSFSheet summarySheet = wb.createSheet(ReportSheetNames.TESTSUMMARYREPORT.getName());

        Map<String, String> reportInfo = new LinkedHashMap<String, String>();

        for (Entry<String, String> temp : ConfigSummaryData.getConfigSummary().entrySet()) {
            reportInfo.put(temp.getKey(), temp.getValue());
        }

        int rowNum = 0;
        HSSFCell col;
        HSSFRow row;
        for (Entry<String, String> eachReportInfo : reportInfo.entrySet()) {
            int colNum = 2;
            row = summarySheet.createRow(rowNum++);

            col = row.createCell(colNum);
            col.setCellStyle(Styles.getSubHeading2Style());
            col.setCellValue(eachReportInfo.getKey());

            // Next column holds the values
            col = row.createCell(++colNum);
            col.setCellStyle(Styles.getThinBorderStyle());
            col.setCellValue(eachReportInfo.getValue());
        }

        logger.exiting();
    }

    /**
     * Creates all the report details like which sheet should contain which report and the data associated with the
     * report
     */
    private void createReportMap() {
        logger.entering();

        // Summary Report
        Map<String, List<SummarizedData>> subReportMap = new LinkedHashMap<String, List<SummarizedData>>();
        subReportMap.put("Full Suite Summary", lSuites);
        subReportMap.put("Test Summary", lTests);
        subReportMap.put("Classwise Summary", lClasses);
        subReportMap.put("Groupwise Summary", lGroups);

        ReportMap<SummarizedData> testSummaryReport = new ReportMap<SummarizedData>(
                ReportSheetNames.TESTSUMMARYREPORT.getName(), subReportMap, 0);
        fullReportMap.add(testSummaryReport);

        // Group Detailed Report
        List<SummarizedData> groupsClone = new ArrayList<SummarizedData>(lGroups);
        List<SummarizedData> classData;

        SummarizedData naGroupData = new SummarizedData();
        naGroupData.setsName(TestCaseResult.NA);
        groupsClone.add(naGroupData);
        subReportMap = new LinkedHashMap<String, List<SummarizedData>>();
        for (SummarizedData group : groupsClone) {

            String sGroupName = group.getsName();
            classData = new ArrayList<SummarizedData>();
            for (String sGroupClassName : mpGroupClassData.keySet()) {
                if (sGroupClassName.substring(0, sGroupName.length()).equals(sGroupName)) {
                    classData.add(mpGroupClassData.get(sGroupClassName));
                }
            }
            subReportMap.put(sGroupName, classData);
        }

        ReportMap<SummarizedData> secondReport = new ReportMap<SummarizedData>(
                ReportSheetNames.GROUPSUMMARYREPORT.getName(), subReportMap, 0);
        fullReportMap.add(secondReport);

        // TestCase Status Report
        Map<String, List<List<String>>> subDetailReportMap = new LinkedHashMap<String, List<List<String>>>();
        subDetailReportMap.put("Passed TC List", tcPassedData);
        subDetailReportMap.put("Failed TC List", tcFailedData);
        subDetailReportMap.put("Skipped TC List", tcSkippedData);

        ReportMap<List<String>> thirdReport = new ReportMap<List<String>>(ReportSheetNames.TESTCASEREPORT.getName(),
                subDetailReportMap, 1);
        fullReportMap.add(thirdReport);

        // Defect Report
        Map<String, List<List<String>>> lstDefectReports = new LinkedHashMap<String, List<List<String>>>();
        lstDefectReports.put("Defect Summary", tcDefectData);
        ReportMap<List<String>> fourthReport = new ReportMap<List<String>>(ReportSheetNames.DEFECTREPORT.getName(),
                lstDefectReports, 1);
        fullReportMap.add(fourthReport);

        // Changing the titles of the Defect Report
        BaseReport<List<String>> bR = (BaseReport<List<String>>) fullReportMap.get(fullReportMap.size() - 1)
                .getGeneratedReport().iterator().next();
        List<String> lsTitles = Arrays.asList("Class Name", "Method/Testcase id", "Test Description",
                "Group[s]", "Time taken", "Output Logs", "Error Message", "Error Details");
        bR.setColTitles(lsTitles);

        // TestCase Output Details Report
        Map<String, List<List<String>>> fifthTestOutputSubReportMap = new LinkedHashMap<String, List<List<String>>>();
        fifthTestOutputSubReportMap.put("Test Output", tcOutputData);

        ReportMap<List<String>> fifthReportSheet = new ReportMap<List<String>>(
                ReportSheetNames.TESTOUTPUTDETAILSREPORT.getName(),
                fifthTestOutputSubReportMap, 2);
        fullReportMap.add(fifthReportSheet);
        logger.exiting();
    }

    /**
     * Generates all summarized counts for various reports
     * 
     * @param suites
     *            the {@link List} of {@link ISuite}
     */
    private void generateSummaryData(List<ISuite> suites) {
        logger.entering(suites);

        SummarizedData tempSuite;
        SummarizedData tempTest;
        SummarizedData tempGroups;
        this.generateTestCaseResultData(suites);

        // Generating Group Summary data
        for (ISuite suite : suites) {

            tempSuite = new SummarizedData();
            tempSuite.setsName(suite.getName());
            Map<String, ISuiteResult> allResults = suite.getResults();
            for (Entry<String, Collection<ITestNGMethod>> sGroupName : suite.getMethodsByGroups().entrySet()) {
                tempGroups = new SummarizedData();
                tempGroups.setsName(sGroupName.getKey());
                tempGroups.incrementiTotal(sGroupName.getValue().size());

                for (TestCaseResult tr : allTestsResults) {
                    if (tr.getGroup().contains(sGroupName.getKey())) {
                        tempGroups.incrementCount(tr.getStatus());
                        tempGroups.incrementDuration(tr.getDurationTaken());
                    }
                }
                tempGroups.setiTotal(tempGroups.getiPassedCount() + tempGroups.getiFailedCount()
                        + tempGroups.getiSkippedCount());
                lGroups.add(tempGroups);

            }

            // Generating Test summary data
            for (ISuiteResult testResult : allResults.values()) {
                ITestContext testContext = testResult.getTestContext();
                tempTest = new SummarizedData();
                tempTest.setsName(testContext.getName());
                tempTest.setiFailedCount(testContext.getFailedTests().size());
                tempTest.setiPassedCount(testContext.getPassedTests().size());
                tempTest.setiSkippedCount(testContext.getSkippedTests().size());
                tempTest.setiTotal(tempTest.getiPassedCount() + tempTest.getiFailedCount()
                        + tempTest.getiSkippedCount());
                tempTest.setlRuntime(testContext.getEndDate().getTime() - testContext.getStartDate().getTime());

                lTests.add(tempTest);
            }

            // Generating Suite Summary data
            for (SummarizedData test : lTests) {

                tempSuite.setiPassedCount(test.getiPassedCount() + tempSuite.getiPassedCount());
                tempSuite.setiFailedCount(test.getiFailedCount() + tempSuite.getiFailedCount());
                tempSuite.setiSkippedCount(tempSuite.getiSkippedCount() + test.getiSkippedCount());
                tempSuite.setiTotal(tempSuite.getiPassedCount() + tempSuite.getiFailedCount()
                        + tempSuite.getiSkippedCount());
                tempSuite.setlRuntime(test.getlRuntime() + tempSuite.getlRuntime());
            }
            lSuites.add(tempSuite);

        }

        Collections.sort(lGroups);
        Collections.sort(lTests);
        logger.exiting();
    }

    /**
     * Method to generate array of all results of all testcases that were run in a suite Output : Populates the
     * allTestsResults arraylist with results and info for all test methods.
     */
    private void generateTestCaseResultData(List<ISuite> suites) {
        logger.entering();
        for (ISuite suite : suites) {

            Map<String, ISuiteResult> allResults = suite.getResults();

            for (ISuiteResult testResult : allResults.values()) {

                ITestContext testContext = testResult.getTestContext();

                IResultMap passedResultMap = testContext.getPassedTests();
                IResultMap failedResultMap = testContext.getFailedTests();
                IResultMap skippedResultMap = testContext.getSkippedTests();

                this.allTestsResults.addAll(this.createResultFromMap(passedResultMap));
                this.allTestsResults.addAll(this.createResultFromMap(failedResultMap));
                this.allTestsResults.addAll(this.createResultFromMap(skippedResultMap));
            }
        }
        logger.exiting();
    }

    /**
     * Generates individual TestCase Results based on map of passed, failed and skipped methods Returns the list of
     * TestCaseResult objects generated.
     */
    private List<TestCaseResult> createResultFromMap(IResultMap resultMap) {
        logger.entering(resultMap);
        List<TestCaseResult> statusWiseResults = new ArrayList<TestCaseResult>();

        for (ITestResult singleMethodResult : resultMap.getAllResults()) {
            TestCaseResult tcresult1 = new TestCaseResult();
            tcresult1.setITestResultobj(singleMethodResult);
            statusWiseResults.add(tcresult1);

        }
        Collections.sort(statusWiseResults);
        logger.exiting(statusWiseResults);
        return statusWiseResults;
    }

    /**
     * Generates class based summary and the basis for Detailed group-wise summary report
     */
    private void generateTCBasedData(List<TestCaseResult> allTestsList) {
        logger.entering(allTestsList);
        SummarizedData tempClass;
        SummarizedData tempGroupClass;
        Map<String, SummarizedData> mpClassData = new HashMap<String, SummarizedData>();
        int outputSheetRowCounter = 3;

        for (TestCaseResult tcResult : allTestsList) {

            // Segregating for class data
            String sTempClassName = tcResult.getClassName();

            // If class not already added to Class data, then create new ClassObject exists
            if (!mpClassData.containsKey(sTempClassName)) {
                tempClass = new SummarizedData();
                tempClass.setsName(sTempClassName);

            } else {
                tempClass = mpClassData.get(sTempClassName);
            }

            // Adding test to total count
            tempClass.incrementiTotal();

            // Adding all groups to map
            for (String sGroup : tcResult.getGroup()) {

                // Forming a key for the GroupClass map which is <GroupName><ClassName>
                String sGroupClassName = sGroup + sTempClassName;
                if (!mpGroupClassData.containsKey(sGroupClassName)) {
                    tempGroupClass = new SummarizedData();
                    tempGroupClass.setsName(sTempClassName);
                } else {
                    tempGroupClass = mpGroupClassData.get(sGroupClassName);
                }

                tempGroupClass.incrementiTotal();
                tempGroupClass.incrementCount(tcResult.getStatus());
                tempGroupClass.incrementDuration(tcResult.getDurationTaken());
                mpGroupClassData.put(sGroupClassName, tempGroupClass);
            }

            // Segregating for detailed Testcase Status wise data
            List<String> str = new ArrayList<String>();
            str.add(tcResult.getClassName());
            str.add(tcResult.getMethodName());
            str.add(tcResult.getTestDesc());
            str.add(tcResult.getGroup().toString());
            str.add(String.valueOf(tcResult.getDurationTaken()));
            str.add("'" + ReportSheetNames.TESTOUTPUTDETAILSREPORT.getName() + "'!B"
                    + Integer.toString(outputSheetRowCounter));

            List<String> outputStr = new ArrayList<String>();
            outputStr.add("Class Name:" + tcResult.getClassName());
            outputStr.add("Method/Testcase id:" + tcResult.getMethodName());
            outputStr.addAll(tcResult.getssmsg());

            // Based on status, incrementing class count and adding str to correct
            // list for TC detailed report
            switch (tcResult.getStatus()) {
                case ITestResult.FAILURE: {
                    tcFailedData.add(str);
                    // For failed cases adding data for defect description sheet
                    for (int iErrorCount = 0; iErrorCount < tcResult.getError().size(); iErrorCount++) {
                        List<String> tmpList = new ArrayList<String>();
                        tmpList.addAll(0, str);
                        tmpList.add(tcResult.getDefect().get(iErrorCount));
                        tmpList.add(tcResult.getError().get(iErrorCount));
                        outputStr.add("Stacktrace:" + tcResult.getError().get(iErrorCount));
                        tcDefectData.add(tmpList);
                    }
                    break;
                }
                case ITestResult.SUCCESS: {
                    tcPassedData.add(str);
                    break;
                }
                case ITestResult.SKIP: {
                    tcSkippedData.add(str);
                    break;
                }
                default: {
                    break;
                }
            }
            tcOutputData.add(outputStr);
            outputSheetRowCounter = outputSheetRowCounter + 1 + outputStr.size();

            tempClass.incrementCount(tcResult.getStatus());
            // Add to the total runtime of the class
            tempClass.setlRuntime(tempClass.getlRuntime() + tcResult.getDurationTaken());
            mpClassData.put(sTempClassName, tempClass);
        }
        lClasses.addAll(mpClassData.values());
        Collections.sort(lClasses);
        logger.exiting();
    }

}
