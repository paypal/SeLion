/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 eBay Software Foundation                                                                     |
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

package com.paypal.selion.platform.grid;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.AbstractConfigInitializer;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.configuration.Initializer;
import com.paypal.selion.configuration.ListenerInfo;
import com.paypal.selion.configuration.ListenerManager;
import com.paypal.selion.internal.utils.InvokedMethodInformation;
import com.paypal.selion.internal.utils.TestNGUtils;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.reports.reporter.services.ConfigSummaryData;
import com.paypal.selion.reports.reporter.services.ReporterConfigMetadata;
import com.paypal.selion.reports.runtime.SeLionReporter;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Contains the logic that will take care of all the selenium related
 */
public class SeleniumGridListener implements IInvokedMethodListener, ISuiteListener, ITestListener {

    /**
     * This String constant represents the JVM argument that can be used to enable/disable {@link SeleniumGridListener}
     */
    public static final String ENABLE_GRID_LISTENER = "enable.grid.listener";

    private static SimpleLogger logger = SeLionLogger.getLogger();

    public SeleniumGridListener() {
        ListenerManager.registerListener(new ListenerInfo(this.getClass(), ENABLE_GRID_LISTENER));
    }

    /**
     * 
     * Identifies which version and name of browser to start if it specified in &#064;webtest <br>
     * <b>sample</b><br>
     * 
     * &#064;webtest(<b>browser="*firefox"</b>)<br>
     * 
     * @see org.testng.IInvokedMethodListener#beforeInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
     */
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

        logger.entering(new Object[] { method, testResult });
        try {
            if (ListenerManager.executeCurrentMethod(this) == false) {
                logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
                return;
            }

            if (!method.isTestMethod()) {
                return;
            }

            Class<?> cls = method.getTestMethod().getInstance().getClass();
            boolean isWebTestClass = cls.getAnnotation(WebTest.class) != null;
            boolean isMobileTestClass = cls.getAnnotation(MobileTest.class) != null;

            if ((isWebTestClass || isMobileTestClass)) {
                if (isLowPriority(method)) {
                    // For session sharing tests, Need to create new session only for Test (Web or Mobile) with lowest
                    // priority (first test) in the class.
                    testSessionSharingRules(method);
                } else {
                    return;
                }
            }

            AbstractTestSession testSession = TestSessionFactory.newInstance(method);
            Grid.getThreadLocalTestSession().set(testSession);
            InvokedMethodInformation methodInfo = TestNGUtils.getInvokedMethodInformation(method, testResult);
            testSession.initializeTestSession(methodInfo);
            if (!(testSession instanceof BasicTestSession)) {
                // BasicTestSession are non selenium tests. So no need to start the Local hub.
                LocalGridManager.spawnLocalNode(testSession);
            }
        } catch (Exception e) { //NOSONAR
            logger.log(Level.WARNING, "An error occurred while processing beforeInvocation: " + e.getMessage(), e);
        }

        logger.exiting();
    }

    private void testSessionSharingRules(IInvokedMethod method) {

        Test t = method.getTestMethod().getInstance().getClass().getAnnotation(Test.class);

        if (t != null && t.singleThreaded()) {
            if (!isPriorityUnique(method)) {
                throw new IllegalStateException(
                        "All the session sharing test methods within the same class should have a unique priority.");
            } else {
                return;
            }
        }
        throw new IllegalStateException(
                "Session sharing test should have a class level @Test annotation with the property singleThreaded = true defined.");
    }

    private boolean isLowPriority(IInvokedMethod method) {
        int low = method.getTestMethod().getPriority();

        for (ITestNGMethod test : method.getTestMethod().getTestClass().getTestMethods()) {
            if (test.getPriority() < low) {
                return false;
            }
        }
        return true;

    }

    private boolean isHighPriority(IInvokedMethod method) {
        int high = method.getTestMethod().getPriority();

        for (ITestNGMethod test : method.getTestMethod().getTestClass().getTestMethods()) {
            if (test.getPriority() > high) {
                return false;
            }
        }
        return true;

    }

    private boolean isPriorityUnique(IInvokedMethod method) {
        // Logic to check priorities of all test methods are unique. This is used in Session Sharing.

        Set<Integer> check = new HashSet<Integer>();
        int length = method.getTestMethod().getTestClass().getTestMethods().length;
        for (int i = 0; i < length; i++) {
            check.add(method.getTestMethod().getTestClass().getTestMethods()[i].getPriority());
            if (check.size() != i + 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Executes when test case is finished<br>
     * 
     * Identify if webtest wants to have session open, otherwise close session<br>
     * <b>sample</b><br>
     * &#064;webtest(browser="*firefox", <b>keepSessionOpen = true</b>)<br>
     * Analyzes failure if any
     * 
     * @see org.testng.IInvokedMethodListener#afterInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
     * 
     */
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        logger.entering(new Object[] { method, testResult });
        try {
            if (ListenerManager.executeCurrentMethod(this) == false) {
                logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
                return;
            }
            if (!method.isTestMethod()) {
                return;
            }
            Class<?> cls = method.getTestMethod().getInstance().getClass();
            boolean isWebTestClass = cls.getAnnotation(WebTest.class) != null;
            boolean isMobileTestClass = cls.getAnnotation(MobileTest.class) != null;

            if ((isWebTestClass || isMobileTestClass) && !isHighPriority(method)) {
                // For session sharing tests, Need to close session only for Test (Web or Mobile) with highest priority
                // (last test) in the class.
                return;
            }

            AbstractTestSession testSession = Grid.getTestSession();
            testSession.closeSession();
        } catch (Exception e) { //NOSONAR
            logger.log(Level.WARNING, "An error occurred while processing afterInvocation: " + e.getMessage(), e);
        }

        logger.exiting();
    }

    /**
     * Initiate config on suite start
     * 
     * @see org.testng.ISuiteListener#onStart(org.testng.ISuite)
     */
    @Override
    public void onStart(ISuite suite) {
        logger.entering(suite);
        if (ListenerManager.executeCurrentMethod(this) == false) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        // Nothing should query for SeLionConfig values before this point.
        Config.initConfig(suite);
        ConfigSummaryData.initConfigSummary();
        ReporterConfigMetadata.initReporterMetadata();

        // Printing the JVM information.
        // This info will help us when it comes to debugging issues on fusion
        // a typical output will be as below
        // 13:42:04.312 INFO - JDK Information: Java HotSpot(TM) 64-Bit Server VM from Oracle Corporation VM ver.
        // 23.7-b01
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String jdkInfo = runtime.getVmName() + " from " + runtime.getSpecVendor() + " ver. "
                + System.getProperty("java.version");
        logger.info("JDK Information: " + jdkInfo);
        logger.exiting();
    }

    /**
     * Generates and returns output directory string path
     * 
     * @param base
     *            String shows path to the suiteName
     * @param suiteName
     *            String suiteName specified by config file
     * @return String - path to output directory for that particular suite
     */
    public static String filterOutputDirectory(String base, String suiteName) {
        logger.entering(new Object[] { base, suiteName });
        int index = base.lastIndexOf(suiteName);
        String outputFolderWithoutName = base.substring(0, index);
        logger.exiting(outputFolderWithoutName + File.separator);
        return outputFolderWithoutName + File.separator;
    }

    /**
     * Closes selenium session when suite finished to run
     * 
     * @see org.testng.ISuiteListener#onFinish(org.testng.ISuite)
     */
    @Override
    public void onFinish(ISuite suite) {
        logger.entering(suite);
        if (ListenerManager.executeCurrentMethod(this) == false) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }
        LocalGridManager.shutDownLocalNode();
        logger.exiting();
    }

    /**
     * 
     * @see org.testng.ISuiteListener#onFinish(org.testng.ISuite)
     */
    @Override
    public void onFinish(ITestContext context) {
        // Below conditional check needs to be invoked in all TestNG Listener interface implementation.
        // Failing to do so can have un-predictable results.
        if (ListenerManager.executeCurrentMethod(this) == false) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }
        return;
    }

    /**
     * On start each suite initialize config object and report object
     */
    @Override
    public void onStart(ITestContext context) {
        logger.entering(context);
        if (ListenerManager.executeCurrentMethod(this) == false) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        String testName = context.getCurrentXmlTest().getName();
        // initializing the ConfigSummaryData before initializers so that config details can be added.
        ConfigSummaryData.initLocalConfigSummary(testName);

        // We have to ensure that our configuration is the first thing that gets loaded.
        // Our loading mechanism is going to be via listeners. But the problem with TestNG listeners is
        // that the order of loading is never guaranteed. Things become complicated if our downstream consumers
        // want to piggy back on our configuration and want to have their configurations loaded as well.
        // Because of all these issues, we cannot rely on building a config specific listener.
        // So we are relying on the ServiceLoaders in Java to do it for us. The moment we are here, we ensure that
        // not only the SeLion specific configurations are loaded, but all downstream consumer's configurations
        // are loaded as well along with us. So SeLion now becomes the single point of initialization and thus
        // does away with all the inherent setbacks that are associated with TestNG listeners orders.
        invokeInitializersBasedOnPriority(context);

        ConfigManager.printConfiguration(testName);

        ISuite suite = context.getSuite();

        if (!suite.getParallel().equals("false") && logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Parallel suite execution. Updating SeLion local config for Test, "
                    + context.getCurrentXmlTest().getName());
        }

        String base = suite.getOutputDirectory();
        String suiteName = suite.getName();
        String rootFolder = filterOutputDirectory(base, suiteName);
        SeLionReporter.setTestNGOutputFolder(rootFolder);
        SeLionReporter.init();
        logger.exiting();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Below conditional check needs to be invoked in all TestNG Listener interface implementation.
        // Failing to do so can have un-predictable results.
        if (ListenerManager.executeCurrentMethod(this) == false) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }
        return;

    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Below conditional check needs to be invoked in all TestNG Listener interface implementation.
        // Failing to do so can have un-predictable results.
        if (ListenerManager.executeCurrentMethod(this) == false) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }
        return;
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // Below conditional check needs to be invoked in all TestNG Listener interface implementation.
        // Failing to do so can have un-predictable results.
        if (ListenerManager.executeCurrentMethod(this) == false) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }
        return;

    }

    @Override
    public void onTestStart(ITestResult result) {
        // Below conditional check needs to be invoked in all TestNG Listener interface implementation.
        // Failing to do so can have un-predictable results.
        if (ListenerManager.executeCurrentMethod(this) == false) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Below conditional check needs to be invoked in all TestNG Listener interface implementation.
        // Failing to do so can have un-predictable results.
        if (ListenerManager.executeCurrentMethod(this) == false) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
        }
    }

    /**
     * This method facilitates initialization of all configurations from the current project as well as downstream
     * consumers.
     * 
     * @param context
     */
    private void invokeInitializersBasedOnPriority(ITestContext context) {
        ServiceLoader<Initializer> serviceLoader = ServiceLoader.load(Initializer.class);
        List<AbstractConfigInitializer> loader = new ArrayList<AbstractConfigInitializer>();
        for (Initializer l : serviceLoader) {
            loader.add((AbstractConfigInitializer) l);
        }
        Collections.sort(loader);
        for (AbstractConfigInitializer temp : loader) {
            temp.initialize(context);
        }
    }
}
