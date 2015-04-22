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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IInvokedMethod;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.internal.utils.InvokedMethodInformation;
import com.paypal.selion.reports.runtime.SeLionReporter;

/**
 * A class for loading and representing the {@link WebTest} annotation parameters. Also performs sanity checks.
 */
// TODO: Should this be moved to an "internal" package ?
public class WebTestSession extends AbstractTestSession {

    private String sessionNameToUse = "";
    private String browser = "";
    private Boolean keepSessionOpen = false;
    private Boolean openNewSession = true;
    private int browserHeight = 0;
    private int browserWidth = 0;

    WebTestSession() {
        enableSessionHandling();
    }

    /**
     * Call this to initialize the {@link WebTestSession} object from the TestNG {@link IInvokedMethod}
     * 
     * @param method
     *            - the TestNG {@link IInvokedMethod}
     * @param sessionMap
     *            - the {@link HashMap} used to track sessions in {@link SeleniumGridListener}
     * 
     */
    @Override
    public void initializeTestSession(InvokedMethodInformation method, Map<String, SeLionSession> sessionMap) {
        logger.entering(new Object[] { method, sessionMap });
        initTestSession(method);
        WebTest webTestAnnotation = method.getAnnotation(WebTest.class);
        // Setting the browser value
        this.browser = getLocalConfigProperty(ConfigProperty.BROWSER);
        if (webTestAnnotation != null) {
            if (StringUtils.isNotBlank(webTestAnnotation.browser())) {
                this.browser = webTestAnnotation.browser();
            }
            this.openNewSession = webTestAnnotation.openNewSession();
            this.keepSessionOpen = webTestAnnotation.keepSessionOpen();

            if (webTestAnnotation.browserHeight() > 0 && webTestAnnotation.browserWidth() > 0) {
                this.browserHeight = webTestAnnotation.browserHeight();
                this.browserWidth = webTestAnnotation.browserWidth();
            } else {
                warnUserOfInvalidBrowserDimensions(webTestAnnotation);
            }

            initializeAdditionalCapabilities(webTestAnnotation.additionalCapabilities(), method);
        }

        setSessionName(sessionMap, method);
        logger.fine(this.sessionNameToUse + " will be the session name to be used for " + method.getCurrentMethodName());
    }

    private void warnUserOfInvalidBrowserDimensions(WebTest webTestAnnotation) {
        if (webTestAnnotation.browserHeight() < 0 && webTestAnnotation.browserWidth() < 0) {
            logger.info("The parameters provided in WebTest annotation are less than zero. Ignoring them.");
        }
        if (webTestAnnotation.browserHeight() == 0 && webTestAnnotation.browserWidth() == 0) {
            logger.fine("No parameters for browser dimensions were provided.");
        } else if (webTestAnnotation.browserHeight() == 0) {
            logger.info("The height was not provided ignoring width parameter.");
        } else if (webTestAnnotation.browserWidth() == 0) {
            logger.info("The width was not provided ignoring height parameter.");
        }
    }

    /**
     * Performs checks on and sets the sessionName properly
     * 
     * @param sessionMap
     * @param incomingMethod
     */
    private final void setSessionName(Map<String, SeLionSession> sessionMap, InvokedMethodInformation incomingMethod) {
        logger.entering(new Object[] { sessionMap, incomingMethod });
        WebTest webTest = incomingMethod.getAnnotation(WebTest.class);
        // Lets check if the user provided us a session name via the annotation and use it.
        if (webTest != null) {
            this.sessionNameToUse = webTest.sessionName();
        }
        final String SESSION_PREFIX = this.className + ".";

        // for named sessions we need to append the package and class info to
        // guarantee uniqueness
        if (StringUtils.isNotBlank(this.sessionNameToUse)) {
            this.sessionNameToUse = SESSION_PREFIX + this.sessionNameToUse;
            logger.exiting(this.sessionNameToUse);
            return;
        }

        // for un-named sessions that may want to stay open or connect to an
        // existing session default to this session name in most cases
        this.sessionNameToUse = "unnamed-session-on-thread" + Thread.currentThread().getId();

        // dynamically generate a session name, if the user wants a new
        // session and wants to keep it open. session name will be "pacakge.class.method" name
        if ((this.keepSessionOpen) && (this.openNewSession)) {
            this.sessionNameToUse = SESSION_PREFIX + this.methodName;
            logger.exiting(this.sessionNameToUse);
            return;
        }

        // catch openNewSession=false when there are no dependent methods specified
        if ((!this.openNewSession) && (!hasDependentMethods())) {
            throw new IllegalArgumentException("Can not have an unnamed session without dependent methods and use"
                    + " an existing session. Error in " + SESSION_PREFIX + this.methodName);
        }

        // attempt to map existing session based on the dependsOnMethods
        // specification, when openNewSession = false
        if ((!this.openNewSession) && (hasDependentMethods())) {
            String[] methods = this.dependsOnMethods;

            // go through the dependsOnMethods looking for open sessions
            // which map to the method names
            List<String> foundSessions = new ArrayList<String>();
            List<String> considered = new ArrayList<String>();
            synchronized (WebTestSession.class) {
                for (String searchKey : methods) {
                    if (sessionMap.containsKey(searchKey)) {
                        foundSessions.add(searchKey);
                    }
                    considered.add(searchKey);
                }
            }

            // catch unsupported state errors
            // no matches from the dependOnMethods specified
            if (foundSessions.isEmpty()) {
                throw new IllegalStateException("Unable to find a session that matched selection criteria. "
                        + "Considered " + considered.toString());
            }
            // multiple in-flight sessions and dependent methods specified
            // that matched...
            if (foundSessions.size() > 1) {
                throw new IllegalStateException("Ambiguous match. Found more than one session that "
                        + "matched selection criteria " + foundSessions.toString());
            }

            // set the sessionName to the matched name
            this.sessionNameToUse = foundSessions.get(0);
        }
        logger.exiting(this.sessionNameToUse);
    }

    /**
     * @return the session name for the test method
     */
    public final String getSessionName() {
        return sessionNameToUse;
    }

    /**
     * @return the browser configured for the test method
     */
    public final String getBrowser() {
        logger.entering();
        // By now we would have already set the browser flavor from the local config as part of the
        // initializeTestSession
        // method. So lets check if its still blank and if yes, we default it to the global config value.
        if (StringUtils.isBlank(this.browser)) {
            this.browser = Config.getConfigProperty(ConfigProperty.BROWSER);
        }
        // All of our browser values need to start with the magic char "*"
        if (!StringUtils.startsWith(this.browser, "*")) {
            this.browser = "*".concat(this.browser);
        }
        logger.exiting(this.browser);
        return this.browser;
    }

    /**
     * @return whether the test method requested the session stay open
     */
    public final boolean getKeepSessionOpen() {
        return this.keepSessionOpen;
    }

    /**
     * @return whether the test method requested a new session be opened
     */
    public final boolean getOpenNewSession() {
        return this.openNewSession;
    }

    /**
     * @return the height of the Browser window that will be spawned
     */
    public final int getBrowserHeight() {
        if (this.browserHeight == 0 || this.browserWidth == 0) {
            String height = getLocalConfigProperty(ConfigProperty.BROWSER_HEIGHT);
            if (StringUtils.isNotBlank(height)) {
                this.browserHeight = Integer.parseInt(height);
            }
        }
        return (this.browserHeight);

    }

    /**
     * @return the width of the browser window that will be spawned
     */
    public final int getBrowserWidth() {
        if (this.browserHeight == 0 || this.browserWidth == 0) {
            String width = getLocalConfigProperty(ConfigProperty.BROWSER_WIDTH);
            if (StringUtils.isNotBlank(width)) {
                this.browserWidth = Integer.parseInt(width);
            }
        }
        return (this.browserWidth);
    }

    @Override
    public SeLionSession startSession(Map<String, SeLionSession> sessionMap) {
        logger.entering(sessionMap);
        Long threadId = Thread.currentThread().getId();

        if (openNewSession) {
            // User wants to create a new session.
            SeLionSession newSession = createSession();
            String logMsg = "Thread " + threadId + " created new " + sessionNameToUse + " = " + newSession.toString();
            if (keepSessionOpen) {
                // User wanted a new session but intends to re-use this session.
                synchronized (WebTestSession.class) {
                    if (sessionMap.containsKey(sessionNameToUse)) {
                        StringBuilder msg = new StringBuilder("Found an existing session");
                        msg.append("[").append(sessionNameToUse).append("].");
                        msg.append("Please either change the session name to a unique value (or) ");
                        msg.append("re-use that session.");
                        throw new IllegalStateException(msg.toString());
                    }
                    // Lets add the newly created session to the map to facilitate later usage.
                    sessionMap.put(sessionNameToUse, newSession);
                }
            }
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, logMsg);
            }
            Grid.getThreadLocalWebDriver().set(newSession.getWebDriver());
            logger.exiting(newSession);
            return newSession;
        }
        // if we are here then it means that the user would like to re-use an existing session.
        // try to switch into a session by the same name
        synchronized (WebTestSession.class) {
            if (!sessionMap.containsKey(sessionNameToUse)) {
                throw new IllegalStateException("Unable to find an already existing session with name ["
                        + sessionNameToUse + "].");
            }
            // So the session was a valid one and did exist in our map.
            SeLionSession existingSession = sessionMap.get(sessionNameToUse);
            // If either the session was null or the webdriver instance of the session was null
            // we have a problem.
            if ((existingSession == null) || (existingSession.getWebDriver() == null)) {
                // So lets close the problematic session and tell TestNG the exception occurred in
                // beforeInvocation
                closeCurrentSessionAndRemoveFromMap(sessionMap);
                throw new IllegalStateException("The session " + sessionNameToUse
                        + " is already closed. It probably timed out.");
            }
            // If we are here then it means that the session was a valid one.
            // so lets switch over to it
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Thread " + threadId + " switching into " + sessionNameToUse + " = "
                        + existingSession.toString());
            }
            return switchSession(existingSession);
        }
    }

    /*
     * close a sessions by name. also remove it from the sessionMap
     */
    private void closeCurrentSessionAndRemoveFromMap(Map<String, SeLionSession> sessionMap) {
        logger.entering(sessionMap);

        final String LOG_MSG_PREFIX = "Thread " + Thread.currentThread().getId();
        // Only sessions that are designated with openNewSession=false will be on the session map
        if (!openNewSession) {
            closeSession();
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(LOG_MSG_PREFIX + " removing from session map " + sessionNameToUse);
            }
            synchronized (WebTestSession.class) {
                sessionMap.remove(sessionNameToUse);
            }
            logger.exiting();
            return;
        }
        // If we are here then it means its just a normal session which is not present in the map.
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(LOG_MSG_PREFIX + " closing " + sessionNameToUse + " = "
                    + new SeLionSession(Grid.driver()).toString());
        }
        try {
            closeSession();
        } catch (RuntimeException e) {
            logger.log(Level.FINER, "An exception occurred while closing the web session", e);
        }
        logger.exiting();
    }

    private boolean runLocally() {
        return Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY));
    }

    private SeLionSession createSession() {
        logger.entering();
        BrowserFlavors flavor = BrowserFlavors.getBrowser(getBrowser());
        RemoteWebDriver driver = DriverFactory.createInstance(flavor);

        if (!runLocally()) {
            String hostName = Config.getConfigProperty(ConfigProperty.SELENIUM_HOST);
            int port = Integer.parseInt(Config.getConfigProperty(ConfigProperty.SELENIUM_PORT));
            RemoteNodeInformation node = Grid.getRemoteNodeInfo(hostName, port, driver.getSessionId());
            if (node != null) {
                logger.info(node.toString());
            }
        }
        SeLionSession session = new SeLionSession(driver);
        logger.exiting(session);
        return session;

    }

    @Override
    public void initializeTestSession(InvokedMethodInformation method) {
        throw new UnsupportedOperationException("This test session supports initialization only with sessions");
    }

    @Override
    public SeLionSession startSesion() {
        throw new UnsupportedOperationException("This test session supports starting sessions only with a session map");
    }

    private SeLionSession switchSession(SeLionSession session) {
        logger.entering(session);
        // First save the current WebDriver instance.
        RemoteWebDriver activeWebDriver = Grid.driver();
        // Now push the WebDriver instance from the session object into our ThreadLocal variable.
        Grid.getThreadLocalWebDriver().set(session.getWebDriver());
        Grid.getThreadLocalTestSession().set(this);
        // Now create a new session using the earlier saved WebDriver instance.
        SeLionSession sessionToReturn = new SeLionSession(activeWebDriver);
        logger.exiting(sessionToReturn);
        return sessionToReturn;

    }

    @Override
    public void closeAllSessions(Map<String, SeLionSession> sessionMap) {
        logger.entering(sessionMap);
        sessionMap.clear();
        logger.exiting();
    }

    @Override
    public void closeCurrentSession(Map<String, SeLionSession> sessionMap, InvokedMethodInformation method) {
        logger.entering(new Object[] { sessionMap, method });
        if (keepSessionOpen) {
            // If user wants to keep the session open, then do nothing and just return.
            logger.exiting("Returning without closing " + this.sessionNameToUse);
            return;
        }
        try {
            // let's attempt to capture a screenshot if there was a failure.
            // That way a user can see the how the page looked like
            // when a test failed.
            if (!method.isTestResultSuccess()) {
                warnUserOfTestFailures(method);
            }
        } catch (Exception e) {
            String warning = "An exception occurred after the test method invocation. "
                    + "Gobbling it as the test case itself did not fail";
            logger.log(Level.FINEST, warning, e);
            // do nothing, the failure contains the details that we need displayed to the end-user
        } finally {
            closeCurrentSessionAndRemoveFromMap(sessionMap);
        }
        logger.exiting();

    }

    private void warnUserOfTestFailures(InvokedMethodInformation method) {
        String errMsg = "";
        if (method.getException() != null) {
            errMsg = method.getException().getMessage();
        }
        if (StringUtils.isEmpty(errMsg)) {
            errMsg = "Test Failure screenshot";
        }
        SeLionReporter.log(errMsg, true, true);
        logger.info("Please review the test report for the screenshot at the time of failure.");

    }

    @Override
    public WebDriverPlatform getPlatform() {
        return WebDriverPlatform.WEB;
    }

}
