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

package com.paypal.selion.platform.utilities;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Preconditions;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.PageValidationException;
import com.paypal.selion.platform.html.WebPage;
import com.paypal.selion.platform.html.support.HtmlElementUtils;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A utility class that provides a collection of methods that can be used in conjunction with WebDriver.
 *
 */
public final class WebDriverWaitUtils {

    private final static SimpleLogger logger = SeLionLogger.getLogger();

    private WebDriverWaitUtils() {
        // Utility class. So hide the constructor to defeat instantiation
    }

    private static String getTextFromBody() {
        WebElement bodyTag = Grid.driver().findElement(By.tagName("body"));
        return bodyTag.getText();
    }

    private static void waitForCondition(ExpectedCondition<?> condition) {
        waitForCondition(condition, timeoutInSeconds());
    }

    private static void waitForCondition(ExpectedCondition<?> condition, long timeoutInSeconds) {
        new WebDriverWait(Grid.driver(), timeoutInSeconds).until(condition);
    }

    private static void waitForConditionIgnoring(ExpectedCondition<?> condition, Class<? extends Throwable> ignoring) {
        waitForConditionIgnoring(condition, ignoring, timeoutInSeconds());
    }

    private static void waitForConditionIgnoring(ExpectedCondition<?> condition, Class<? extends Throwable> ignoring,
            long timeoutInSeconds) {
        new WebDriverWait(Grid.driver(), timeoutInSeconds).ignoring(ignoring).until(condition);
    }

    /**
     * Waits until element is cickable.
     *
     * @param elementLocator
     *            identifier of element to be cickable
     */
    public static void waitUntilElementIsClickable(final String elementLocator) {
        logger.entering(elementLocator);
        By by = HtmlElementUtils.resolveByType(elementLocator);
        ExpectedCondition<WebElement> condition = ExpectedConditions.elementToBeClickable(by);
        waitForCondition(condition);
        logger.exiting();
    }

    /**
     * Waits until element is either invisible or not present on the DOM.
     *
     * @param elementLocator
     *            identifier of element to be found
     */
    public static void waitUntilElementIsInvisible(final String elementLocator) {
        logger.entering(elementLocator);
        By by = HtmlElementUtils.resolveByType(elementLocator);
        ExpectedCondition<Boolean> condition = ExpectedConditions.invisibilityOfElementLocated(by);
        waitForCondition(condition);
        logger.exiting();
    }

    /**
     * Waits until element element is present on the DOM of a page. This does not necessarily mean that the element is
     * visible.
     * 
     * @param elementLocator
     *            identifier of element to be found
     */
    public static void waitUntilElementIsPresent(final String elementLocator) {
        logger.entering(elementLocator);
        By by = HtmlElementUtils.resolveByType(elementLocator);
        ExpectedCondition<WebElement> condition = ExpectedConditions.presenceOfElementLocated(by);
        waitForCondition(condition);
        logger.exiting();
    }

    /**
     * Waits until element is present on the DOM of a page and visible. Visibility means that the element is not only
     * displayed but also has a height and width that is greater than 0.
     *
     * @param elementLocator
     *            identifier of element to be visible
     */
    public static void waitUntilElementIsVisible(final String elementLocator) {
        logger.entering(elementLocator);
        By by = HtmlElementUtils.resolveByType(elementLocator);
        ExpectedCondition<WebElement> condition = ExpectedConditions.visibilityOfElementLocated(by);
        waitForCondition(condition);
        logger.exiting();
    }

    /**
     * Waits until the current page's title contains a case-sensitive substring of the given title.
     * 
     * @param pageTitle
     *            title of page expected to appear
     */
    public static void waitUntilPageTitleContains(final String pageTitle) {
        logger.entering(pageTitle);
        Preconditions
                .checkArgument(StringUtils.isNotEmpty(pageTitle), "Expected Page title cannot be null (or) empty.");
        ExpectedCondition<Boolean> condition = ExpectedConditions.titleContains(pageTitle);
        waitForCondition(condition);
        logger.exiting();
    }

    /**
     * Waits until text appears anywhere within the current page's &lt;body&gt; tag.
     *
     * @param searchString
     *            text will be waited for
     */
    public static void waitUntilTextPresent(final String searchString) {
        logger.entering(searchString);
        Preconditions.checkArgument(StringUtils.isNotEmpty(searchString), "Search string cannot be null (or) empty.");
        ExpectedCondition<Boolean> conditionToCheck = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return getTextFromBody().contains(searchString);
            }
        };
        waitForCondition(conditionToCheck);
        logger.exiting();
    }

    /**
     * Waits until both two elements appear at the page Waits until all the elements are present on the DOM of a page.
     * This does not necessarily mean that the element is visible.
     *
     * @param locators
     *            an array of strings that represents the list of elements to check.
     * 
     */
    public static void waitUntilAllElementsArePresent(final String... locators) {
        logger.entering(new Object[] { Arrays.toString(locators) });
        Preconditions.checkArgument(locators != null, "Please provide a valid set of locators.");
        for (String eachLocator : locators) {
            waitUntilElementIsPresent(eachLocator);
        }
        logger.exiting();
    }

    /**
     * Waits until all <code>pageValidators</code> for a given object pass. The {@link WebPage} instance must express
     * something that validates the page or calling this will cause a wait timeout.
     * 
     * @param pageObject
     *            a {@link WebPage} instance.
     */
    public static void waitUntilPageIsValidated(WebPage pageObject) {
        logger.entering(pageObject);
        Preconditions.checkArgument(pageObject != null, "Please provide a valid instance of WebPage.");
        final WebPage w = (WebPage) pageObject;
        ExpectedCondition<Boolean> conditionToCheck = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                w.validatePage();
                return true;
            }
        };
        waitForConditionIgnoring(conditionToCheck, PageValidationException.class);
        logger.exiting();
    }

    private static long timeoutInSeconds() {
        return Grid.getExecutionTimeoutValue() / 1000;
    }
}
