/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

package com.paypal.selion.platform.mobile.android;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.mobile.elements.MobileElement;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.WebPage;
import com.paypal.selion.platform.html.support.HtmlElementUtils;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import com.paypal.test.utilities.logging.SimpleLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * <code>UiObject</code> implements {@link UserinterfaceObject} and forms the base class of all Android UI automation
 * elements.
 */
public class UiObject implements UserinterfaceObject, MobileElement {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    protected String locator; // NOSONAR

    protected SeLionAndroidBridgeDriver driver; // NOSONAR

    public UiObject(String locator) {
        this.locator = locator;
    }

    @Override
    public void clearText() {
        logger.entering();
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.clearText(webElement);
        logger.exiting();
    }

    @Override
    public void click(Object... expected) {
        logger.entering(expected);
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.click(webElement);
        waitFor(expected);
        logger.exiting();
    }

    @Override
    public void clickBottomRight(Object... expected) {
        logger.entering(expected);
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.clickBottomRight(webElement);
        waitFor(expected);
        logger.exiting();

    }

    @Override
    public void clickTopLeft(Object... expected) {
        logger.entering(expected);
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.clickTopLeft(webElement);
        waitFor(expected);
        logger.exiting();

    }

    @Override
    public String getText() {
        logger.entering();
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        String text = driver.getText(webElement);
        logger.exiting(text);
        return text;
    }

    @Override
    public boolean isCheckable() {
        logger.entering();
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        boolean result = driver.isCheckable(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isChecked() {
        logger.entering();
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        boolean result = driver.isChecked(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isClickable() {
        logger.entering();
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        boolean result = driver.isClickable(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isEnabled() {
        logger.entering();
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        boolean result = driver.isEnabled(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isFocusable() {
        logger.entering();
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        boolean result = driver.isFocusable(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isFocused() {
        logger.entering();
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        boolean result = driver.isFocused(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isLongClickable() {
        logger.entering();
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        boolean result = driver.isLongClickable(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isScrollable() {
        logger.entering();
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        boolean result = driver.isScrollable(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isSelected() {
        logger.entering();
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        boolean result = driver.isSelected(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public void longClick(Object... expected) {
        logger.entering(expected);
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.longClick(webElement);
        waitFor(expected);
        logger.exiting();
    }

    @Override
    public void longClickBottomRight(Object... expected) {
        logger.entering(expected);
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.longClickBottomRight(webElement);
        waitFor(expected);
        logger.exiting();
    }

    @Override
    public void longClickTopLeft(Object... expected) {
        logger.entering(expected);
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.longClickTopLeft(webElement);
        waitFor(expected);
        logger.exiting();
    }

    @Override
    public void setText(String text) {
        logger.entering(text);
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.setText(webElement, text);
        logger.exiting();
    }

    @Override
    public void swipeLeft(Object... expected) {
        logger.entering(expected);
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.swipeLeft(webElement);
        waitFor(expected);
        logger.exiting();
    }

    @Override
    public void swipeRight(Object... expected) {
        logger.entering(expected);
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.swipeRight(webElement);
        waitFor(expected);
        logger.exiting();

    }

    @Override
    public void swipeUp(Object... expected) {
        logger.entering(expected);
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.swipeUp(webElement);
        waitFor(expected);
        logger.exiting();
    }

    @Override
    public void swipeDown(Object... expected) {
        logger.entering(expected);
        initBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.resolveByType(locator));
        driver.swipeDown(webElement);
        waitFor(expected);
        logger.exiting();
    }

    @Override
    public WebElement findElement(String locator) {
        logger.entering(locator);
        By by = HtmlElementUtils.resolveByType(locator);
        initBridgeDriver();
        WebElement webElement = driver.findElement(by);
        logger.exiting(webElement);
        return webElement;
    }

    @Override
    public void tap(Object... expected) {
        click(expected);
    }

    @Override
    public String getValue() {
        return getText();
    }

    /**
     * Returns the locator.
     *
     * @return Locator of this {@link UiObject}
     */
    public String getLocator() {
        return locator;
    }

    @SuppressWarnings("unchecked")
    protected void waitFor(Object... expected) {
        logger.entering(expected);
        for (Object expect : expected) {
            if (expect instanceof UiObject) {
                WebDriverWaitUtils.waitUntilElementIsPresent(UiObject.class.cast(expect).getLocator());
                continue;
            }
            if (expect instanceof String) {
                WebDriverWaitUtils.waitUntilElementIsPresent(String.valueOf(expect));
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
            }
        }
        logger.exiting();
    }

    protected void initBridgeDriver() {
        driver = SeLionAndroidBridgeDriver.class.cast(Grid.driver());
    }
}
