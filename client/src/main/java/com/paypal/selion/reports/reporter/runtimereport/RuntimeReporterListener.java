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

import java.io.File;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener2;

import com.paypal.selion.annotations.DoNotReport;
import com.paypal.selion.configuration.ListenerInfo;
import com.paypal.selion.configuration.ListenerManager;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * RuntimeReporter is a listener which gets invoked when a test method or configuration method starts or completed.
 * 
 */
public class RuntimeReporterListener implements IResultListener2, ISuiteListener {

    private String outputDirectory;
    private JsonRuntimeReporterHelper jsonHelper;

    /**
     * This String constant represents the JVM argument that can be enabled/disabled to enable/disable
     * {@link RuntimeReporterListener}
     */
    public static final String ENABLE_RUNTIME_REPORTER_LISTENER = "enable.runtime.reporter.listener";

    private static volatile boolean bInitConfig = false;

    static void markConfigAsInitialized() {
        bInitConfig = true;
    }

    private static SimpleLogger logger = SeLionLogger.getLogger();

    public RuntimeReporterListener() {

        // Lets register this listener with the ListenerManager
        ListenerManager.registerListener(new ListenerInfo(this.getClass(), ENABLE_RUNTIME_REPORTER_LISTENER));

        jsonHelper = new JsonRuntimeReporterHelper();
    }

    public void onTestStart(ITestResult result) {

        if (!ListenerManager.executeCurrentMethod(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        logger.entering(new Object[] { result });

        if (result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(DoNotReport.class) != null) {
            return;
        }

        updateTestDetails(result);

        logger.exiting();

    }

    @Override
    public void onTestSuccess(ITestResult result) {

        if (!ListenerManager.executeCurrentMethod(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        logger.entering(new Object[] { result });

        if (result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(DoNotReport.class) != null) {
            return;
        }

        updateTestDetails(result);

        logger.exiting();

    }

    @Override
    public void onTestFailure(ITestResult result) {

        if (!ListenerManager.executeCurrentMethod(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        logger.entering(new Object[] { result });

        updateTestDetails(result);

        logger.exiting();

    }

    @Override
    public void onTestSkipped(ITestResult result) {

        if (!ListenerManager.executeCurrentMethod(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        logger.entering(new Object[] { result });

        if (result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(DoNotReport.class) != null) {
            return;
        }

        updateTestDetails(result);

        logger.exiting();
    }

    public void updateTestDetails(ITestResult result) {

        logger.entering(new Object[] { result });

        String fullClassName = result.getTestClass().getName();

        String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);

        String packageName = fullClassName.substring(0, fullClassName.lastIndexOf('.'));

        jsonHelper.insertTestMethod(result.getTestContext().getSuite().getName(), result.getTestContext()
                .getCurrentXmlTest().getName(), packageName, className, result);

        jsonHelper.writeJSON(outputDirectory, false);

        logger.exiting();

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {
        if (!ListenerManager.executeCurrentMethod(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        logger.entering(new Object[] { context });
        jsonHelper.generateLocalConfigSummary(context.getSuite().getName(), context.getCurrentXmlTest().getName());
        logger.exiting();

    }

    @Override
    public void onFinish(ITestContext context) {

    }

    @Override
    public void onConfigurationSuccess(ITestResult result) {

        if (!ListenerManager.executeCurrentMethod(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        logger.entering(new Object[] { result });

        updateConfigDetails(result);

        logger.exiting();

    }

    @Override
    public void onConfigurationFailure(ITestResult result) {

        if (!ListenerManager.executeCurrentMethod(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        logger.entering(new Object[] { result });

        updateConfigDetails(result);

        logger.exiting();

    }

    @Override
    public void onConfigurationSkip(ITestResult result) {

        if (!ListenerManager.executeCurrentMethod(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        logger.entering(new Object[] { result });
        updateConfigDetails(result);
        logger.exiting();

    }

    @Override
    public void beforeConfiguration(ITestResult result) {

        if (!ListenerManager.executeCurrentMethod(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

    }

    public void updateConfigDetails(ITestResult result) {

        logger.entering(new Object[] { result });

        String fullClassName = result.getTestClass().getName();

        String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);

        String packageName = fullClassName.substring(0, fullClassName.lastIndexOf('.'));

        jsonHelper.insertConfigMethod(result.getTestContext().getSuite().getName(), result.getTestContext()
                .getCurrentXmlTest().getName(), packageName, className, result);

        jsonHelper.writeJSON(outputDirectory, false);

        logger.exiting();

    }

    @Override
    public void onStart(ISuite suite) {
        logger.entering(new Object[] { suite });

        if (!ListenerManager.executeCurrentMethod(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        if (!bInitConfig) {
            markConfigAsInitialized();
            File outFile = new File(suite.getOutputDirectory());
            outputDirectory = outFile.getParent() + File.separator + "RuntimeReporter";
            logger.info("Runtime Report : " + outputDirectory + File.separator + "index.html");
            RuntimeReportResourceManager resourceMgr = new RuntimeReportResourceManager();
            resourceMgr.copyResources(outFile.getParent());
        }
        logger.exiting();
    }

    @Override
    public void onFinish(ISuite suite) {
        logger.entering(new Object[] { suite });

        if (!ListenerManager.executeCurrentMethod(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        jsonHelper.writeJSON(outputDirectory, true);

        logger.exiting();

    }

}
