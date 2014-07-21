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

package com.paypal.selion.platform.grid;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;

import org.openqa.selenium.WebDriverException;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;

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
import com.paypal.selion.reports.runtime.WebReporter;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Contains the logic that will take care of all the selenium related
 */
public class SeleniumGridListener implements IInvokedMethodListener, ISuiteListener, ITestListener {

    // used to track browser sessions across all threads
    // data structure format <HashMap<String "sessionName", SeLionSession>
    private volatile Map<String, SeLionSession> sessionMap;

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
     * Identifies if test case wants to open new session <br>
     * <b>sample</b><br>
     * &#064;webtest(browser="*firefox", <b>openNewSession = true</b>)
     * 
     * @see org.testng.IInvokedMethodListener#beforeInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
     */
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        logger.entering(new Object[] { method, testResult });
        if (ListenerManager.executeCurrentMethod(this) == false) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        if (!method.isTestMethod()) {
            logger.exiting();
            return;
        }

        if (!noFailedConfigurations(method, testResult)) {
            logger.exiting();
            return;
        }

        Method invokedMethod = method.getTestMethod().getConstructorOrMethod().getMethod();
        AbstractTestSession testSession = TestSessionFactory.newInstance(invokedMethod);
        Grid.getThreadLocalTestSession().set(testSession);
        InvokedMethodInformation methodInfo = TestNGUtils.getInvokedMethodInformation(method, testResult);
        if (WebDriverPlatform.UNDEFINED == testSession.getPlatform()) {
            // We perhaps encountered a regular @Test annotated test method which doesn't warrant anything extra
            // to be done.
            testSession.initializeTestSession(methodInfo);
            logger.exiting();
            return;
        }

        if (testSession.handleSessions()) {
            testSession.initializeTestSession(methodInfo, sessionMap);
        } else {
            testSession.initializeTestSession(methodInfo);
        }

        LocalGridManager.spawnLocalHub(testSession.getPlatform());

        try {
            if (testSession.handleSessions()) {
                testSession.startSession(sessionMap);
            } else {
                testSession.startSesion();
            }

        } catch (WebDriverException e) {
            // We are looking for any additional unchecked exceptions that Grid may have thrown
            String errorMsg = "An error occured while setting up the test environment. \nRoot cause: ";
            Reporter.log(errorMsg + e.getMessage(), true);
            // Tell TestNG the exception occurred in beforeInvocation
            WebSessionException webSessionException = new WebSessionException(errorMsg, e);
            testResult.setThrowable(webSessionException);
            // Time to raise an Exception to let TestNG know that the configuration method failed
            // so that it doesn't start executing the test methods.
            throw webSessionException;
        }

        logger.exiting();
    }

    /**
     * Check whether the configuration methods of current test method passed.
     * 
     * @param currentInvokedMethod
     * @param testResult
     * @return - <code>true</code> if there are no configuration failures detected.
     */
    private boolean noFailedConfigurations(IInvokedMethod currentInvokedMethod, ITestResult testResult) {

        // if configfailurepolicy="continue" is set into suite, testNG will continue to execute the remaining tests
        // in the suite if an @Before* method fails, in this case, browser need to be launched.
        String configFailurePolicyValue = testResult.getTestContext().getSuite().getXmlSuite().getConfigFailurePolicy();
        if ("continue".equals(configFailurePolicyValue)) {
            return true;
        }

        List<ITestResult> allUnSuccessFulConfigs = new ArrayList<ITestResult>();

        IResultMap failedConfigurations = testResult.getTestContext().getFailedConfigurations();
        allUnSuccessFulConfigs.addAll(failedConfigurations.getAllResults());

        IResultMap skippedConfigurations = testResult.getTestContext().getSkippedConfigurations();
        allUnSuccessFulConfigs.addAll(skippedConfigurations.getAllResults());

        String invokedMethodName = currentInvokedMethod.getTestMethod().getConstructorOrMethod().getName();

        List<String> groupsToWhichCurrentInvokedMethodBelongsTo = Arrays.asList(currentInvokedMethod.getTestMethod()
                .getGroups());

        // check @BeforeSuite, @BeforeTest and @BeforeGroups for failures
        for (ITestResult eachUnsuccessFulConfig : allUnSuccessFulConfigs) {

            ITestNGMethod testMethod = eachUnsuccessFulConfig.getMethod();
            String testMethodName = testMethod.getConstructorOrMethod().getName();

            if (testMethod.isBeforeSuiteConfiguration() || testMethod.isBeforeTestConfiguration()) {
                logger.warning(String.format("The @Before(Suite/Test) method [%s] failed. So [%s] will be skipped.",
                        testMethodName, invokedMethodName));
                return false;
            }
            if (testMethod.isBeforeGroupsConfiguration()) {
                for (String confGroup : testMethod.getGroups()) {
                    if (groupsToWhichCurrentInvokedMethodBelongsTo.contains(confGroup)) {
                        logger.warning(String.format("The @BeforeGroups method [%s] failed. So [%s] will be skipped.",
                                testMethodName, invokedMethodName));
                        return false;
                    }
                }
                return true;
            }
        }
        // Check @BeforeClass and @BeforeTest
        List<ITestNGMethod> allConfigMethodsOfTestClass = new ArrayList<ITestNGMethod>();

        ITestClass currentInvokedMethodTestClass = currentInvokedMethod.getTestMethod().getTestClass();

        allConfigMethodsOfTestClass.addAll(Arrays.asList(currentInvokedMethodTestClass.getBeforeClassMethods()));
        allConfigMethodsOfTestClass.addAll(Arrays.asList(currentInvokedMethodTestClass.getBeforeTestMethods()));

        for (ITestNGMethod eachConfigMethodOfTestClass : allConfigMethodsOfTestClass) {
            boolean hasFailedConfig = (!failedConfigurations.getResults(eachConfigMethodOfTestClass).isEmpty());
            boolean hasSkippedConfig = (!skippedConfigurations.getResults(eachConfigMethodOfTestClass).isEmpty());

            String configMethodName = eachConfigMethodOfTestClass.getConstructorOrMethod().getName();

            if (hasFailedConfig || hasSkippedConfig) {
                logger.warning(String.format("The @Before(Class/Test) method [%s] failed. So [%s] will be skipped.",
                        configMethodName, invokedMethodName));
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
        if (ListenerManager.executeCurrentMethod(this) == false) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        if (!method.isTestMethod()) {
            return;
        }
        AbstractTestSession testSession = Grid.getTestSession();

        // TestConfig would be a Null only under the following conditions (of course because of us throwing an
        // exception) [ for @WebTest annotated tests ]
        // a. If the test-case with same session name already exists.
        // b. If we are unable to find an already existing session with name provided
        // c. Session with the session name is already closed.

        if (testSession == null) {
            return;
        }
        if (WebDriverPlatform.UNDEFINED == testSession.getPlatform()) {
            return;
        }
        InvokedMethodInformation methodInfo = TestNGUtils.getInvokedMethodInformation(method, testResult);
        if (testSession.handleSessions()) {
            testSession.closeCurrentSession(sessionMap, methodInfo);
        } else {
            testSession.closeSession();
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
        if (sessionMap != null && !sessionMap.isEmpty()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Thread " + Thread.currentThread().getId() + " finished Suite, Open Sessions = "
                        + sessionMap.toString());
            }
            // This is NOT an optimal solution. But we need to find out some way to clear out all those sessions
            // which we ended up persisting or else the browser instances wont be cleaned up.
            Class<?>[] supportedAnnotations = TestSessionFactory.getSupportedAnnotations();
            for (Class<?> eachAnnotation : supportedAnnotations) {
                AbstractTestSession config = TestSessionFactory.newInstance(eachAnnotation);
                if (config.handleSessions()) {
                    config.closeAllSessions(sessionMap);
                }
            }
        }

        LocalGridManager.shutDownHub();
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

        sessionMap = new HashMap<String, SeLionSession>();
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
        WebReporter.setTestNGOutputFolder(rootFolder);
        WebReporter.init();
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
