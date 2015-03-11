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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.configuration.ExtendedConfig;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.internal.grid.SauceLabsHelper;
import com.paypal.selion.internal.utils.InvokedMethodInformation;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.html.support.events.ElementEventListener;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A class for loading and representing the {@link WebTest}/{@link MobileTest} annotation basic parameters. Also
 * performs sanity checks. Concrete instances of this class are created via the {@link TestSessionFactory} factory.
 * 
 */
public abstract class AbstractTestSession {
    protected String methodName = "";
    protected String className = "";
    protected DesiredCapabilities additionalCapabilities = new DesiredCapabilities();
    protected String parameters;
    protected String[] dependsOnMethods = new String[] {};
    protected static SimpleLogger logger = SeLionLogger.getLogger();
    private boolean handlesSessions = false;
    protected String xmlTestName = "";
    protected List<ElementEventListener> listeners = new ArrayList<ElementEventListener>();


    public final boolean handleSessions() {
        return this.handlesSessions;
    }

    public final void enableSessionHandling() {
        this.handlesSessions = true;
    }

    public final DesiredCapabilities getAdditionalCapabilities() {
        return this.additionalCapabilities;
    }

    protected final String getParamsInfo(InvokedMethodInformation method) {
        logger.entering(method);
        StringBuffer parameters = null;
        for (Object eachParameter : method.getMethodParameters()) {
            String eachParamAsString = (eachParameter == null ? "null" : eachParameter.toString());
            if (parameters == null) {
                parameters = new StringBuffer();
                parameters.append(eachParamAsString);
            } else {
                parameters.append(",");
                parameters.append(eachParamAsString);
            }
        }
        logger.exiting(parameters);
        if (parameters == null) {
            return null;
        } else {
            return parameters.toString();
        }
    }

    protected final void initTestSession(InvokedMethodInformation method) {
        logger.entering(method);

        this.dependsOnMethods = method.getMethodsDependedUpon();

        this.className = method.getCurrentClassName();
        this.methodName = method.getCurrentMethodName();
        this.parameters = getParamsInfo(method);
        this.xmlTestName = method.getCurrentTestName();

    }

    protected void initializeAdditionalCapabilities(String[] additionalCapabilities, InvokedMethodInformation method) {
        Object additionalCaps = method.getTestAttribute(ExtendedConfig.CAPABILITIES.getConfig());
        if (additionalCaps instanceof DesiredCapabilities) {
            this.additionalCapabilities.merge((DesiredCapabilities) additionalCaps);
        }
        if (additionalCapabilities.length != 0) {
            Map<String, Object> capabilityMap = parseIntoCapabilities(additionalCapabilities);
            // We found some capabilities. Lets merge them.
            this.additionalCapabilities.merge(new DesiredCapabilities(capabilityMap));
        }
    }

    protected final Map<String, Object> parseIntoCapabilities(String[] capabilities) {
        Map<String, Object> capabilityMap = new HashMap<String, Object>();
        for (String eachCapability : capabilities) {
            //split into key/value at the ':' character
            String[] keyValuePair = eachCapability.split(":", 2);
            if (keyValuePair.length == 2) {
                String value = keyValuePair[1];
                Object desiredCapability = value;
                //treat true/false values surrounded with ' marks as strings
                if (value.startsWith("'") && value.endsWith("'")) {
                    String trimmedValue = StringUtils.mid(value, 1, value.length() - 2);
                    if (trimmedValue.equalsIgnoreCase("true")) {
                        desiredCapability = "true";
                    } else if (trimmedValue.equalsIgnoreCase("false")) {
                        desiredCapability = "false";
                    }
                } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    desiredCapability = Boolean.parseBoolean(value);
                }
                capabilityMap.put(keyValuePair[0], desiredCapability);
            } else {
                StringBuffer errMsg = new StringBuffer();
                errMsg.append("Capabilities are to be provided as name value pair separated by colons. ");
                errMsg.append("For e.g., capabilityName:capabilityValue");
                throw new IllegalArgumentException(errMsg.toString());
            }
        }
        return capabilityMap;
    }

    /**
     * @return the declaring class name for the test in the form <b>package.classname</b>
     */
    public final String getDeclaringClassName() {
        return this.className;
    }

    /**
     * @return the method name for the test
     */
    public final String getMethodName() {
        return this.methodName;
    }

    /**
     * @return array of dependent methods specified by the test
     */
    public synchronized final String[] getDependsOnMethods() {
        return Arrays.copyOf(this.dependsOnMethods, this.dependsOnMethods.length);
    }

    /**
     * @return - The test name for the current method which is formed by concatenating the Class name, Method name and
     *         Method parameters if any.
     */
    public final String getTestName() {
        String testName = getDeclaringClassName() + ":" + getMethodName() + "()";
        if (parameters != null) {
            parameters = "[" + parameters + "]";
            testName = testName + ":" + parameters;
        }
        return testName;

    }

    /**
     * A Method to start a new session taking into account a map of already existing sessions.
     * 
     * @param sessions
     *            - A {@link Map} of already created {@link SeLionSession} sessions.
     * @return - A new {@link SeLionSession} object that represents the newly created session.
     */
    public abstract SeLionSession startSession(Map<String, SeLionSession> sessions);

    /**
     * A Method to start a new session.
     * 
     * @return - A new {@link SeLionSession} object that represents the newly created session.
     */
    public abstract SeLionSession startSesion();

    /**
     * A initializer that initializes the sub-class of {@link AbstractTestSession} based on the annotation.
     * 
     * @param method
     *            - An {@link InvokedMethodInformation} object that represents the currently invoked method.
     * @param sessionMap
     *            - A {@link Map} of {@link SeLionSession} to be considered. This method is typically invoked when the
     *            annotation is capable of session management.
     */
    public abstract void initializeTestSession(InvokedMethodInformation method, Map<String, SeLionSession> sessionMap);

    /**
     * A initializer that initializes the sub-class of {@link AbstractTestSession} based on the annotation.
     * 
     * @param method
     *            - An {@link InvokedMethodInformation} object that represents the currently invoked method. This method
     *            is typically invoked when the annotation doesn't deal with session management.
     */
    public abstract void initializeTestSession(InvokedMethodInformation method);

    /**
     * A method that helps in closing off the current session in conjunction with a {@link Map} of
     * {@link SeLionSession}
     * 
     * @param sessionMap
     *            - A {@link Map} of {@link SeLionSession}s.
     * @param result
     *            - The {@link InvokedMethodInformation} object that represents the currently invoked method.
     */
    public abstract void closeCurrentSession(Map<String, SeLionSession> sessionMap, InvokedMethodInformation result);

    /**
     * A method that helps in closing off all the sessions in the given {@link Map} of {@link SeLionSession}s.
     * 
     * @param sessionMap
     *            - A {@link Map} of {@link SeLionSession}s.
     */
    public abstract void closeAllSessions(Map<String, SeLionSession> sessionMap);

    /**
     * @return - A {@link WebDriverPlatform} object that represents the current platform.
     */
    public abstract WebDriverPlatform getPlatform();

    /**
     * A method that helps in closing off the current session. This method is typically used in cases wherein the
     * annotation doesnt support session management.
     */
    public final void closeSession() {
        logger.entering();
        new SauceLabsHelper().embedSauceLabsJobUrlToTestReport();
        ScreenShotRemoteWebDriver driver = Grid.driver();
        if (driver != null) {
            Grid.getThreadLocalWebDriver().set(null);
            Grid.getThreadLocalTestSession().set(null);
            driver.quit();
        }
        logger.exiting();

    }

    public final boolean hasDependentMethods() {
        return (this.dependsOnMethods.length > 0);
    }

    final String getLocalConfigProperty(ConfigProperty property) {
        return ConfigManager.getConfig(this.xmlTestName).getConfigProperty(property);
    }

    /**
     * @return the TestNG suite xml test name.
     */
    public final String getXmlTestName() {
        return this.xmlTestName;
    }
    
    /**
     * @return a {@link List} of {@link ElementEventListener}s attached to the test session.
     */
    public final List<ElementEventListener> getElementEventListeners() {
        return this.listeners;
    }

}
