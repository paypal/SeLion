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

package com.paypal.selion.platform.mobile.ios;

import java.util.EnumMap;

import org.apache.commons.lang.ArrayUtils;
import org.openqa.selenium.By;
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
 * The <code> UIAElement </code> is the super class for all user interface elements in the context of the Automation
 * instrument for automating user interface testing of iOS apps. This interface defines more general methods that can be
 * used on any type of user interface elements.
 */
public class UIAElement implements UIAutomationElement {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    private String locator;

    private SeLionIOSBridgeDriver bridgeDriver;

    protected SeLionIOSBridgeDriver getBridgeDriver() {
        return bridgeDriver;
    }

    protected void setBridgeDriver(SeLionIOSBridgeDriver bridgeDriver) {
        this.bridgeDriver = bridgeDriver;
    }

    protected void setLocator(String locator) {
        this.locator = locator;
    }

    public UIAElement(String locator) {
        this.locator = locator;
        bridgeDriver = SeLionIOSBridgeDriver.class.cast(Grid.driver());
    }

    @Override
    public WebElement findElement(String locator) {
        logger.entering(locator);
        By by = HtmlElementUtils.getFindElementType(locator);
        WebElement webElement = bridgeDriver.findElementBy(by);
        logger.exiting(webElement);
        return webElement;
    }

    @Override
    public void doubleTap(Object... expected) {
        logger.entering(expected);
        WebElement webElement = findElement(locator);
        bridgeDriver.doubleTap(webElement);
        if (!ArrayUtils.isEmpty(expected)) {
            waitFor(expected);
        }
        logger.exiting();
    }

    @Override
    public void scrollToVisible() {
        logger.entering();
        WebElement webElement = findElement(locator);
        bridgeDriver.scrollToVisible(webElement);
        logger.exiting();
    }

    @Override
    public void tap(Object... expected) {
        logger.entering(expected);
        WebElement webElement = findElement(locator);
        bridgeDriver.tap(webElement);
        if (!ArrayUtils.isEmpty(expected)) {
            waitFor(expected);
        }
        logger.exiting();
    }

    @Override
    public void tapWithOptions(EnumMap<GestureOptions, String> gestureOptions, Object... expected) {
        logger.entering(new Object[] { gestureOptions, expected });
        WebElement webElement = findElement(locator);
        bridgeDriver.tapWithOptions(webElement, gestureOptions);
        if (!ArrayUtils.isEmpty(expected)) {
            waitFor(expected);
        }
        logger.exiting();
    }

    @Override
    public void twoFingerTap(Object... expected) {
        logger.entering(expected);
        WebElement webElement = findElement(locator);
        bridgeDriver.twoFingerTap(webElement);
        if (!ArrayUtils.isEmpty(expected)) {
            waitFor(expected);
        }
        logger.exiting();
    }

    @Override
    public String getLabel() {
        logger.entering();
        WebElement webElement = findElement(locator);
        String label = bridgeDriver.getLabel(webElement);
        logger.exiting(label);
        return label;
    }

    @Override
    public String getName() {
        logger.entering();
        WebElement webElement = findElement(locator);
        String name = bridgeDriver.getName(webElement);
        logger.exiting(name);
        return name;
    }

    @Override
    public String getValue() {
        logger.entering();
        WebElement webElement = findElement(locator);
        String value = bridgeDriver.getValue(webElement);
        logger.exiting(value);
        return value;
    }

    @Override
    public String getLocator() {
        return locator;
    }

    @SuppressWarnings("unchecked")
    protected void waitFor(Object... expected) {
        for (Object expect : expected) {
            if (expect instanceof UIAElement) {
                WebDriverWaitUtils.waitUntilElementIsPresent(UIAElement.class.cast(expect).getLocator());
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
    }
}
