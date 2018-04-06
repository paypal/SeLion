/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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

package com.paypal.selion.internal.platform.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.internal.platform.grid.browsercapabilities.CapabilitiesHelper;
import com.paypal.selion.internal.utils.InvokedMethodInformation;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.support.events.ElementEventListener;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A class for loading and representing the {@link WebTest}/{@link MobileTest} annotation basic parameters. Also
 * performs sanity checks. Concrete instances of this class are created via the {@link TestSessionFactory} factory.
 *
 */
public abstract class AbstractTestSession {

    /**
     * Shared session flag. This flag is populated during initTestSession method.
     */
    protected boolean isSharedSession;

    private boolean isSessionStarted;

    private String methodName = "";

    private String className = "";

    private final DesiredCapabilities additionalCapabilities = new DesiredCapabilities();

    private String parameters;

    private String[] dependsOnMethods = new String[] {};

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    private String xmlTestName = "";

    private final List<ElementEventListener> listeners = new ArrayList<>();

    /**
     * @return whether the session is started <code>true</code> or <code>false</code>
     */
    public boolean isStarted() {
        return isSessionStarted;
    }

    /**
     * Set the session to started.
     *
     * @param started
     *            <code>true</code> or <code>false</code>
     */
    protected final void setStarted(boolean started) {
        this.isSessionStarted = started;
    }

    public final DesiredCapabilities getAdditionalCapabilities() {
        return this.additionalCapabilities;
    }

    protected final String getParamsInfo(InvokedMethodInformation method) {
        logger.entering(method);
        StringBuilder parameters = null;
        for (Object eachParameter : method.getMethodParameters()) {
            String eachParamAsString = (eachParameter == null ? "null" : eachParameter.toString());
            if (parameters == null) {
                parameters = new StringBuilder();
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
        isSharedSession = isSessionShared(method);
        this.dependsOnMethods = method.getMethodsDependedUpon();
        this.className = method.getCurrentClassName();
        this.methodName = method.getCurrentMethodName();
        this.parameters = getParamsInfo(method);
        this.xmlTestName = method.getCurrentTestName();
        logger.exiting();
    }

    /*
     * Returns true if SessionSharing is enforced by the client test class
     */
    private boolean isSessionShared(InvokedMethodInformation invokedMethodInformation) {

        /*
         * SessionSharing is identified positive if the Class is annotated by @Test annotation with 'singleThreaded'
         * attribute as true and if the Class bears a @WebTest or @MobileTest annotation.
         */
        Class<?> declaringClass = invokedMethodInformation.getActualMethod().getDeclaringClass();
        boolean isSingleThreaded = declaringClass.getAnnotation(Test.class) != null
                && declaringClass.getAnnotation(Test.class).singleThreaded();
        boolean isWebTestClass = declaringClass.getAnnotation(WebTest.class) != null;
        boolean isMobileTestClass = declaringClass.getAnnotation(MobileTest.class) != null;
        return isSingleThreaded && (isWebTestClass || isMobileTestClass);
    }

    protected void initializeAdditionalCapabilities(String[] additionalCapabilities) {
        this.additionalCapabilities.merge(CapabilitiesHelper.retrieveCustomCapabilities(additionalCapabilities));
    }

    @Deprecated
    protected void initializeAdditionalCapabilities(InvokedMethodInformation methodInfo) {
        this.additionalCapabilities.merge(CapabilitiesHelper.retrieveCustomCapabilities(methodInfo));
    }

    protected void initializeAdditionalCapabilities(Class<? extends DefaultCapabilitiesBuilder>[] builders) {
        if (builders == null || builders.length == 0) {
            return;
        }
        for (Class<? extends DefaultCapabilitiesBuilder> builder : builders) {
            this.additionalCapabilities.merge(CapabilitiesHelper.retrieveCustomCapabilities(builder));
        }
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
     * Returns a test name for the current method. This method returns the the Class name, Method name, and Method
     * parameters if any, for a test case running on a Non-Session-Sharing context. For a test case running under
     * Session-Sharing context this method returns the Class name, Method name, and Method parameters if any.
     *
     * @return - test name.
     */
    public final String getTestName() {
        StringBuilder stringBuilder = new StringBuilder();
        if (isSharedSession) {
            stringBuilder.append(getDeclaringClassName());
        } else {
            stringBuilder.append(getDeclaringClassName()).append(':').append(getMethodName()).append('(').append(')');
        }
        if (parameters != null) {
            stringBuilder.append('[').append(parameters).append(']');
        }
        return stringBuilder.toString();
    }

    /**
     * A Method to start a new session.
     */
    public abstract void startSession();

    /**
     * A initializer that initializes the sub-class of {@link AbstractTestSession} based on the annotation.
     *
     * @param method
     *            - An {@link InvokedMethodInformation} object that represents the currently invoked method.
     */
    public abstract void initializeTestSession(InvokedMethodInformation method);

    /**
     * @return - A {@link WebDriverPlatform} object that represents the current platform.
     */
    public abstract WebDriverPlatform getPlatform();

    /**
     * A method that helps in closing off the current session.
     */
    public void closeSession() {
        logger.entering();

        if (isStarted() && (Grid.getTestSession() != null)) {
            new SauceLabsHelper().embedSauceLabsJobUrlToTestReport();
            // If driver.quit() throws some exception then rest of the listeners will not get invoked, To handle this
            // we are gobbling this exception
            try {
                Grid.driver().quit();
            } catch (Exception e) { // NOSONAR
                logger.log(Level.SEVERE, "An error occurred while closing the Selenium session: " + e.getMessage(), e);
            }
        }

        Grid.getThreadLocalWebDriver().set(null);
        Grid.getThreadLocalTestSession().set(null);
        this.isSessionStarted = false;
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
