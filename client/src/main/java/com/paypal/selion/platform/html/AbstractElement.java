/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 PayPal                                                                                     |
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

package com.paypal.selion.platform.html;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.google.common.base.Function;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.internal.platform.grid.BrowserFlavors;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.support.HtmlElementUtils;
import com.paypal.selion.platform.html.support.ParentNotFoundException;
import com.paypal.selion.platform.html.support.events.Clickable;
import com.paypal.selion.platform.html.support.events.ElementEventListener;
import com.paypal.selion.platform.html.support.events.Hoverable;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import com.paypal.selion.reports.runtime.SeLionReporter;
import com.paypal.selion.testcomponents.BasicPageImpl;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Abstract element class for web elements.
 */
public abstract class AbstractElement implements Clickable, Hoverable {
    private static final String ALERTS_ARE_NOT_SUPPORTED_ERR_MSG = "Alerts are not supported in iPhone/iPad/PhantomJS as of 2.39.0.";
    private String locator;
    private String controlName;
    private ParentTraits parent;
    private final Map<String, String> propMap = new HashMap<String, String>();
    protected static final String LOG_DEMARKER = "&#8594;";

    private static SimpleLogger logger = SeLionLogger.getLogger();

    protected void setParent(ParentTraits parent) {
        this.parent = parent;
    }

    private final ElementEventListener dispatcher = (ElementEventListener) Proxy.newProxyInstance(
            ElementEventListener.class.getClassLoader(), new Class[] { ElementEventListener.class },
            new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    try {
                        List<ElementEventListener> eventListeners = Grid.getTestSession().getElementEventListeners();
                        for (ElementEventListener eventListener : eventListeners) {
                            method.invoke(eventListener, args);
                        }
                        return null;
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            });

    protected ElementEventListener getDispatcher() {
        return dispatcher;
    }

    /**
     * Instance method used to call static class method locateElement.
     * 
     * @return the web element found by locator
     */
    public RemoteWebElement getElement() {
        RemoteWebElement foundElement = null;
        try {
            if (parent == null) {
                foundElement = HtmlElementUtils.locateElement(getLocator());
            } else {
                foundElement = parent.locateChildElement(locator);
            }
        } catch (ParentNotFoundException p) {
            throw p;
        } catch (NoSuchElementException n) {

            addInfoForNoSuchElementException(n);
        }
        return foundElement;
    }

    /**
     * Instance method used to call static class method locateElements.
     * 
     * @return the list of web elements found by locator
     */
    public List<WebElement> getElements() {
        List<WebElement> foundElements = null;
        try {
            if (parent == null) {
                foundElements = HtmlElementUtils.locateElements(getLocator());
            } else {
                foundElements = parent.locateChildElements(getLocator());
            }
        } catch (NoSuchElementException n) {
            addInfoForNoSuchElementException(n);
        }

        return foundElements;
    }

    /**
     * A utility method to provide additional information to the user when a NoSuchElementException is thrown.
     * 
     * @param cause
     *            The associated cause for the exception.
     */
    private void addInfoForNoSuchElementException(NoSuchElementException cause) {
        if (parent == null) {
            throw cause;
        }

        BasicPageImpl page = this.parent.getCurrentPage();

        if (page == null) {
            throw cause;
        }

        String resolvedPageName = page.getClass().getSimpleName();

        // Find if page exists: This part is reached after a valid page instance is assigned to page variable. So its
        // safe to proceed!

        boolean pageExists = page.hasExpectedPageTitle();
        if (!pageExists) {
            // ParentType: Page does not exist: Sending the cause along with it
            throw new ParentNotFoundException(resolvedPageName + " : With Page Title {" + page.getActualPageTitle()
                    + "} Not Found.", cause);
        }
        // The page exists. So lets prepare a detailed error message before throwing the exception.

        StringBuilder msg = new StringBuilder("Unable to find webElement ");

        if (this.controlName != null) {
            msg.append(this.controlName).append(" on ");
        }

        if (resolvedPageName != null) {
            msg.append(resolvedPageName);
        }

        msg.append(" using the locator {").append(locator).append("}");
        throw new NoSuchElementException(msg.toString(), cause);
    }

    /**
     * Constructs an AbstractElement with locator.
     * 
     * @param locator
     */
    public AbstractElement(String locator) {
        this.locator = locator;
    }

    /**
     * Constructs an AbstractElement with locator and parent.
     * 
     * @param parent
     *            A {@link ParentTraits} object that represents the parent element for this element.
     * @param locator
     *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
     */
    public AbstractElement(ParentTraits parent, String locator) {
        this.parent = parent;
        this.locator = locator;
    }

    /**
     * Constructs an AbstractElement with locator and controlName.
     * 
     * @param locator
     *            the element locator
     * @param controlName
     *            the control name used for logging
     */
    public AbstractElement(String locator, String controlName) {
        this(locator, controlName, null);
    }

    /**
     * Constructs an AbstractElement with locator, parent, and controlName.
     * 
     * @param locator
     *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging
     * @param parent
     *            A {@link ParentTraits} object that represents the parent element for this element.
     */
    public AbstractElement(String locator, String controlName, ParentTraits parent) {
        this.locator = locator;
        this.parent = parent;
        this.controlName = controlName;
    }

    /**
     * Retrieves the locator (id/name/xpath/css locator) for the current {@link AbstractElement} element.
     * 
     * @return The value of locator.
     */
    public String getLocator() {
        return locator;
    }

    /**
     * Retrieves the control name for the current {@link AbstractElement} element.
     * 
     * @return The value of controlName.
     */
    public String getControlName() {
        return controlName;
    }

    /**
     * Retrieves the parent element for the current {@link AbstractElement} element.
     * 
     * @return A {@link ParentTraits} that represents the parent of the current {@link AbstractElement} element.
     */
    public ParentTraits getParent() {
        return parent;
    }

    /**
     * Finds element on the page and returns the visible (i.e. not hidden by CSS) innerText of this element, including
     * sub-elements, without any leading or trailing whitespace.
     * 
     * @return The innerText of this element.
     */
    public String getText() {
        return getElement().getText();
    }

    /**
     * Checks if element is present in the html dom. An element that is present in the html dom does not mean it is
     * visible. To check if element is visible, use {@link #getElement()} to get {@link WebElement} and then invoke
     * {@link WebElement#isDisplayed()}.
     * 
     * @return True if element is present, false otherwise.
     */
    public boolean isElementPresent() {
        logger.entering();
        boolean returnValue = false;
        try {
            if (getElement() != null) {
                returnValue = true;
            }
        } catch (NoSuchElementException e) {
            returnValue = false;
        }
        logger.exiting(returnValue);
        return returnValue;
    }

    /**
     * Is this element displayed or not? This method avoids the problem of having to parse an element's "style"
     * attribute.
     * 
     * @return Whether or not the element is displayed
     */
    public boolean isVisible() {
        return getElement().isDisplayed();
    }

    /**
     * Is the element currently enabled or not? This will generally return true for everything but disabled input
     * elements.
     * 
     * @return True if element is enabled, false otherwise.
     */
    public boolean isEnabled() {
        return getElement().isEnabled();
    }

    /**
     * Get the value of a the given attribute of the element. Will return the current value, even if this has been
     * modified after the page has been loaded. More exactly, this method will return the value of the given attribute,
     * unless that attribute is not present, in which case the value of the property with the same name is returned. If
     * neither value is set, null is returned. The "style" attribute is converted as best can be to a text
     * representation with a trailing semi-colon. The following are deemed to be "boolean" attributes, and will return
     * either "true" or null: async, autofocus, autoplay, checked, compact, complete, controls, declare, defaultchecked,
     * defaultselected, defer, disabled, draggable, ended, formnovalidate, hidden, indeterminate, iscontenteditable,
     * ismap, itemscope, loop, multiple, muted, nohref, noresize, noshade, novalidate, nowrap, open, paused, pubdate,
     * readonly, required, reversed, scoped, seamless, seeking, selected, spellcheck, truespeed, willvalidate. Finally,
     * the following commonly mis-capitalized attribute/property names are evaluated as expected: class, readonly
     * 
     * 
     * @param attributeName
     *            the attribute name to get current value
     * @return The attribute's current value or null if the value is not set.
     */
    public String getAttribute(String attributeName) {
        return getElement().getAttribute(attributeName);
    }

    /**
     * Gets the (whitespace-trimmed) value of an input field (or anything else with a value parameter). For
     * checkbox/radio elements, the value will be "on" or "off" depending on whether the element is checked or not.
     * 
     * @return the element value, or "on/off" for checkbox/radio elements
     */
    public String getValue() {
        return getAttribute("value");
    }

    /**
     * Gets value from property map {@link #propMap}.
     * 
     * @param key
     *            the key to retrieve a value from the property map
     * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    public String getProperty(String key) {
        return propMap.get(key);
    }

    /**
     * Sets value in property map {@link #propMap}.
     * 
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
        propMap.put(key, value);
    }

    protected String getWaitTime() {
        return ConfigManager.getConfig(Grid.getTestSession().getXmlTestName()).getConfigProperty(
                ConfigProperty.EXECUTION_TIMEOUT);
    }

    protected String resolveControlNameToUseForLogs() {
        String resolvedName = getControlName();
        if (resolvedName == null) {
            return getLocator();
        }
        return resolvedName;
    }

    protected void logUIAction(UIActions actionPerformed) {
        logUIActions(actionPerformed, null);
    }

    protected void logUIActions(UIActions actionPerformed, String value) {
        logger.entering(new Object[] { actionPerformed, value });
        String valueToUse = (value == null) ? "" : value + " in ";
        Reporter.log(LOG_DEMARKER + actionPerformed.getAction() + valueToUse + resolveControlNameToUseForLogs(), false);
        logger.exiting();
    }

    protected void processScreenShot() {
        logger.entering();
        processAlerts(Grid.getWebTestSession().getBrowser());

        String title = "Default Title";
        try {
            title = Grid.driver().getTitle();
        } catch (WebDriverException thrown) { // NOSONAR
            logger.log(Level.FINER, "An exception occured while getting page title", thrown);
        }
        boolean logPages = Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.LOG_PAGES));
        if (Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.AUTO_SCREEN_SHOT))) {
            SeLionReporter.log(title, true, logPages);
        } else {
            SeLionReporter.log(title, false, logPages);
        }
        logger.exiting();
    }

    private void processAlerts(String browser) {
        logger.entering(browser);
        if (doesNotHandleAlerts(browser)) {
            logger.exiting(ALERTS_ARE_NOT_SUPPORTED_ERR_MSG);
            return;
        }
        try {
            Grid.driver().switchTo().alert();
            logger.warning("Encountered an alert. Skipping processing of screenshots");
            logger.exiting();
            return;
        } catch (NoAlertPresentException exception) {
            // Gobble the exception and do nothing with it. No alert was triggered. So it is safe to proceed with taking 
            // screenshots.
        }

    }

    private boolean doesNotHandleAlerts(String browserFlavor) {
        logger.entering(browserFlavor);
        BrowserFlavors browser = BrowserFlavors.getBrowser(browserFlavor);
        boolean returnValue = Arrays.asList(BrowserFlavors.getBrowsersWithoutAlertSupport()).contains(browser);
        logger.exiting(returnValue);
        return returnValue;
    }

    protected void validatePresenceOfAlert() {
        String browser = Grid.getWebTestSession().getBrowser();
        logger.info(browser);
        if (doesNotHandleAlerts(browser)) {
            logger.info(ALERTS_ARE_NOT_SUPPORTED_ERR_MSG);
            return;
        }
        try {
            Grid.driver().switchTo().alert();
            String errorMsg = "Encountered an alert. Cannot wait for an element when an operation triggers an alert.";
            throw new InvalidElementStateException(errorMsg);
        } catch (NoAlertPresentException exception) {
            // Gobble the exception and do nothing with it. No alert was triggered. So it is safe to proceed ahead.
        }
    }

    /**
     * Basic click event on the Element. Functionally equivalent to {@link #clickonly()}
     */
    public void click() {
        clickonly();
    }

    /**
     * Basic click event on the Element. Doesn't wait for anything to load.
     * 
     */
    public void clickonly() {
        click(new Object[] {});
    }

    /**
     * The click function and wait for expected {@link Object} items to load.
     * 
     * @param expected
     *            parameters in the form of an element locator {@link String}, a {@link WebPage}, an
     *            {@link AbstractElement}, or an {@link ExpectedCondition}
     */
    @SuppressWarnings("unchecked")
    public void click(Object... expected) {
        dispatcher.beforeClick(this, expected);

        getElement().click();
        if (Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING))) {
            logUIAction(UIActions.CLICKED);
        }
        // If there are no expected objects, then it means user wants this
        // method to behave as a clickonly. So lets skip processing of alerts and leave
        // that to the user.
        if (expected == null || expected.length == 0) {
            return;
        }
        validatePresenceOfAlert();
        try {
            for (Object expect : expected) {
                if (expect instanceof AbstractElement) {
                    AbstractElement a = (AbstractElement) expect;
                    WebDriverWaitUtils.waitUntilElementIsPresent(a.getLocator());
                    continue;
                }
                if (expect instanceof String) {
                    String s = (String) expect;
                    WebDriverWaitUtils.waitUntilElementIsPresent(s);
                    continue;
                }
                if (expect instanceof ExpectedCondition<?>) {
                    long timeOutInSeconds = Grid.getExecutionTimeoutValue() / 1000;
                    WebDriverWait wait = new WebDriverWait(Grid.driver(), timeOutInSeconds);
                    wait.until(ExpectedCondition.class.cast(expect));
                    continue;
                }
                if (expect instanceof WebPage) {
                    WebDriverWaitUtils.waitUntilPageIsValidated((WebPage) expect);
                    continue;
                }
            }
        } finally {
            // Attempt at taking screenshots even when there are time-outs triggered from the wait* methods.
            processScreenShot();

            dispatcher.afterClick(this, expected);
        }
    }

    /**
     * The click function and wait based on the ExpectedCondition.
     * 
     * @param expectedCondition
     *            ExpectedCondition<?> instance to be passed.
     * 
     * @return The return value of
     *         {@link org.openqa.selenium.support.ui.FluentWait#until(com.google.common.base.Function)} if the function
     *         returned something different from null or false before the timeout expired.<br>
     * 
     *         <pre>
     * Grid.driver().get(&quot;https://www.paypal.com&quot;);
     * TextField userName = new TextField(&quot;login_email&quot;);
     * TextField password = new TextField(&quot;login_password&quot;);
     * Button btn = new Button(&quot;submit.x&quot;);
     * 
     * userName.type(&quot;exampleId@paypal.com&quot;);
     * password.type(&quot;123Abcde&quot;);
     * btn.clickAndExpect(ExpectedConditions.titleIs(&quot;MyAccount - PayPal&quot;));
     * </pre>
     */
    public Object clickAndExpect(ExpectedCondition<?> expectedCondition) {
        dispatcher.beforeClick(this, expectedCondition);

        getElement().click();
        if (Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING))) {
            logUIAction(UIActions.CLICKED);
        }
        validatePresenceOfAlert();
        long timeout = Grid.getExecutionTimeoutValue() / 1000;
        WebDriverWait wait = new WebDriverWait(Grid.driver(), timeout);
        Object variable = wait.until(expectedCondition);
        processScreenShot();

        dispatcher.afterClick(this, expectedCondition);

        return variable;
    }

    /**
     * Click function that will wait for one of the ExpectedConditions to match.
     * {@link org.openqa.selenium.TimeoutException} exception will be thrown if no conditions are matched within the
     * allowed time {@link ConfigProperty#EXECUTION_TIMEOUT}
     * 
     * @param conditions
     *            {@link List}&lt;{@link ExpectedCondition}&lt;?&gt;&gt; of supplied conditions passed.
     * @return first {@link org.openqa.selenium.support.ui.ExpectedCondition} that was matched
     */
    public ExpectedCondition<?> clickAndExpectOneOf(final List<ExpectedCondition<?>> conditions) {
        dispatcher.beforeClick(this, conditions);

        getElement().click();

        if (Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING))) {
            logUIAction(UIActions.CLICKED);
        }

        // If there are no expected objects, then it means user wants this method
        // to behave as a clickonly. So lets skip processing of alerts and leave
        // that to the user.
        if (conditions == null || conditions.size() <= 0) {
            return null;
        }
        validatePresenceOfAlert();

        long timeout = Grid.getExecutionTimeoutValue() / 1000;

        try {
            WebDriverWait wait = new WebDriverWait(Grid.driver(), timeout);
            wait.ignoring(NoSuchElementException.class);
            wait.ignoring(ExpectOneOfException.class);
            
            ExpectedCondition<?> matchedCondition = wait.until(new Function<WebDriver, ExpectedCondition<?>>() {

                // find the first condition that matches and return it
                @Override
                public ExpectedCondition<?> apply(WebDriver webDriver) {
                    StringBuilder sb = new StringBuilder();

                    int i = 1;
                    for (final ExpectedCondition<?> condition : conditions) {
                        try {
                            Object value = condition.apply(webDriver);
                            if (value instanceof Boolean) {
                                if (Boolean.TRUE.equals(value)) {
                                    return condition;
                                }
                            } else if (value != null) {
                                return condition;
                            }  
                        } catch(WebDriverException e) {
                            sb.append("\n\tObject " + i + ":\n");
                            sb.append("\t" + ExceptionUtils.getRootCauseMessage(e).split("\n")[0] + "\n");
                            sb.append("\t\t" + StringUtils.substringBetween(ExceptionUtils.getStackTrace(e), "\n"));
                        }
                        i++;
                    }
                    
                    throw new ExpectOneOfException(sb.toString());
                }
            });

            return matchedCondition;
        } finally {
            // Attempt at taking screenshots even when there are time-outs triggered from the wait* methods.
            processScreenShot();

            dispatcher.afterClick(this, conditions);
        }
    }

    /**
     * The click function and wait for one of the expected {@link Object} items to load.
     * 
     * @param expected
     *            parameters in the form of an element locator {@link String}, a {@link WebPage}, or an
     *            {@link AbstractElement}
     * @return the first object that was matched
     */
    public Object clickAndExpectOneOf(final Object... expected) {
        dispatcher.beforeClick(this, expected);

        getElement().click();
        if (Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING))) {
            logUIAction(UIActions.CLICKED);
        }

        // If there are no expected objects, then it means user wants this method
        // to behave as a clickonly. So lets skip processing of alerts and leave
        // that to the user.
        if (expected == null || expected.length == 0) {
            return null;
        }
        validatePresenceOfAlert();

        long timeout = Grid.getExecutionTimeoutValue() / 1000;

        try {

            WebDriverWait wait = new WebDriverWait(Grid.driver(), timeout);
            wait.ignoring(NoSuchElementException.class);
            wait.ignoring(PageValidationException.class);

            Object expectedObj = wait.ignoring(ExpectOneOfException.class).until(new Function<WebDriver, Object>() {

                // find the first object that is matched and return it
                @Override
                public Object apply(WebDriver webDriver) {
                    StringBuilder sb = new StringBuilder();

                    int i = 1;
                    for (Object expect : expected) {
                        try {
                            if (expect instanceof AbstractElement) {
                                AbstractElement element = (AbstractElement) expect;
                                if (HtmlElementUtils.locateElement(element.getLocator()) != null) {
                                    return expect;
                                }
                            } else if (expect instanceof String) {
                                String s = (String) expect;
                                if (HtmlElementUtils.locateElement(s) != null) {
                                    return expect;
                                }
                            } else if (expect instanceof WebPage) {
                                WebPage w = (WebPage) expect;

                                w.validatePage();
                                return expect;
                            }
                        } catch (NoSuchElementException | PageValidationException e) { // NOSONAR
                            sb.append("\n\tObject " + i + ": " + expect.getClass().getSimpleName() + "\n");
                            sb.append("\t" + ExceptionUtils.getRootCauseMessage(e) + "\n");
                            sb.append("\t\t" + StringUtils.substringBetween(ExceptionUtils.getStackTrace(e), "\n"));
                        }
                        i++;
                    }

                    throw new ExpectOneOfException(sb.toString());
                }
            });

            return expectedObj;

        } finally {
            // Attempt at taking screenshots even when there are time-outs triggered from the wait* methods.
            processScreenShot();

            dispatcher.afterClick(this, expected);
        }
    }

    /**
     * Moves the mouse pointer to the middle of the element. And waits for the expected elements to be visible.
     * 
     * @param expected
     *            parameters in the form of an element locator {@link String} or an {@link AbstractElement}
     */
    public void hover(final Object... expected) {
        dispatcher.beforeHover(this, expected);

        new Actions(Grid.driver()).moveToElement(getElement()).perform();

        try {
            for (Object expect : expected) {
                if (expect instanceof AbstractElement) {
                    AbstractElement a = (AbstractElement) expect;
                    WebDriverWaitUtils.waitUntilElementIsVisible(a.getLocator());
                } else if (expect instanceof String) {
                    String s = (String) expect;
                    WebDriverWaitUtils.waitUntilElementIsVisible(s);
                }
            }
        } finally {
            processScreenShot();

            dispatcher.afterHover(this, expected);
        }
    }
}
