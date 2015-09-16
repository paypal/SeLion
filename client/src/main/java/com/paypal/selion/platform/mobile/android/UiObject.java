/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.WebPage;
import com.paypal.selion.platform.html.support.HtmlElementUtils;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * <code>UiObject</code> implements {@link UserinterfaceObject} and forms the base class of all Android UI automation
 * elements.
 */
public class UiObject implements UserinterfaceObject {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    protected String locator; // NOSONAR

    protected SeLionAndroidBridgeDriver driver; // NOSONAR

    public UiObject(String locator) {
        this.locator = locator;
    }

    @Override
    public void clearText() {
        logger.entering();
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.clearText(webElement);
        logger.exiting();
    }

    @Override
    public void click(Object... expected) {
        logger.entering(expected);
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.click(webElement);
            waitFor(expected);
        logger.exiting();
    }

    @Override
    public void clickBottomRight(Object... expected) {
        logger.entering(expected);
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.clickBottomRight(webElement);
            waitFor(expected);
        logger.exiting();

    }

    @Override
    public void clickTopLeft(Object... expected) {
        logger.entering(expected);
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.clickTopLeft(webElement);
            waitFor(expected);
        logger.exiting();

    }

    @Override
    public String getText() {
        logger.entering();
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        String text = driver.getText(webElement);
        logger.exiting(text);
        return text;
    }

    @Override
    public boolean isCheckable() {
        logger.entering();
        getBridgeDriver();
        boolean result = false;
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        result = driver.isCheckable(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isChecked() {
        logger.entering();
        getBridgeDriver();
        boolean result = false;
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        result = driver.isChecked(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isClickable() {
        logger.entering();
        getBridgeDriver();
        boolean result = false;
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        result = driver.isClickable(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isEnabled() {
        logger.entering();
        getBridgeDriver();
        boolean result = false;
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        result = driver.isEnabled(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isFocusable() {
        logger.entering();
        getBridgeDriver();
        boolean result = false;
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        result = driver.isFocusable(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isFocused() {
        logger.entering();
        getBridgeDriver();
        boolean result = false;
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        result = driver.isFocused(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isLongClickable() {
        logger.entering();
        getBridgeDriver();
        boolean result = false;
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        result = driver.isLongClickable(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isScrollable() {
        logger.entering();
        getBridgeDriver();
        boolean result = false;
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        result = driver.isScrollable(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isSelected() {
        logger.entering();
        getBridgeDriver();
        boolean result = false;
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        result = driver.isSelected(webElement);
        logger.exiting(result);
        return result;
    }

    @Override
    public void longClick(Object... expected) {
        logger.entering(expected);
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.longClick(webElement);
            waitFor(expected);
        logger.exiting();
    }

    @Override
    public void longClickBottomRight(Object... expected) {
        logger.entering(expected);
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.longClickBottomRight(webElement);
            waitFor(expected);
        logger.exiting();
    }

    @Override
    public void longClickTopLeft(Object... expected) {
        logger.entering(expected);
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.longClickTopLeft(webElement);
            waitFor(expected);
        logger.exiting();
    }

    @Override
    public void setText(String text) {
        logger.entering(text);
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.clearText(webElement);
        driver.setText(webElement, text);
        logger.exiting();
    }

    @Override
    public void swipeLeft(Object... expected) {
        logger.entering(expected);
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.swipeLeft(webElement);
            waitFor(expected);
        logger.exiting();
    }

    @Override
    public void swipeRight(Object... expected) {
        logger.entering(expected);
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.swipeRight(webElement);
            waitFor(expected);
        logger.exiting();

    }

    @Override
    public void swipeUp(Object... expected) {
        logger.entering(expected);
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.swipeUp(webElement);
            waitFor(expected);
        logger.exiting();
    }

    @Override
    public void swipeDown(Object... expected) {
        logger.entering(expected);
        getBridgeDriver();
        WebElement webElement = driver.findElement(HtmlElementUtils.getFindElementType(locator));
        driver.swipeDown(webElement);
            waitFor(expected);
        logger.exiting();
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
                continue;
            }
        }
        logger.exiting();
    }
    
    private SeLionAndroidBridgeDriver getBridgeDriver(){
        driver = SeLionAndroidBridgeDriver.class.cast(Grid.driver());
        return driver;
    }
}
