/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2017 PayPal                                                                                     |
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

package com.paypal.selion.appium.platform.grid;

import io.appium.java_client.ios.IOSDriver;

import java.net.URL;
import java.util.EnumMap;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.DeviceRotation;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CommandExecutor;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.mobile.ios.GestureOptions;
import com.paypal.selion.platform.mobile.ios.SeLionIOSBridgeDriver;
import com.paypal.selion.platform.mobile.ios.UIAElement;
import com.paypal.test.utilities.logging.SimpleLogger;
import io.appium.java_client.TouchAction;

/**
 * <code>SeLionAppiumIOSDriver</code> provides facility to add custom {@link CommandExecutor} to {@link IOSDriver}. This
 * class also implements the {@link SeLionIOSBridgeDriver} interface to expose methods for {@link UIAElement} and its
 * subclasses.
 */
public class SeLionAppiumIOSDriver extends IOSDriver<WebElement> implements SeLionIOSBridgeDriver {

    private static final SimpleLogger logger = SeLionLogger.getLogger();
    private static final int TAP_DURATION = 100;
    private static final int SWIPE_DURATION = 1500;
    private static final int SWIPE_EDGE_OFFSET = 100;
    private static final int DOUBLE_TAP_WAIT_TIME = 100;
    private static final int MAX_SCROLL_COUNT = 9;

    public SeLionAppiumIOSDriver(URL url, Capabilities caps) {
        super(url, caps);
    }

    public SeLionAppiumIOSDriver(CommandExecutor commandExecutor, Capabilities caps, URL url) {
        super(url, caps);
        setCommandExecutor(commandExecutor);
    }

    @Override
    public WebElement findElementBy(By by) {
        logger.entering(by);
        WebElement webElement = findElement(by);
        logger.exiting(webElement);
        return webElement;
    }

    @Override
    public void doubleTap(WebElement webElement) {
        logger.entering(webElement);
        new TouchAction(this)
                .tap(webElement).release()
                .waitAction(DOUBLE_TAP_WAIT_TIME)
                .tap(webElement).release()
                .perform();
        logger.exiting();
    }

    @Override
    public void scrollToVisible(WebElement webElement) {
        logger.entering(webElement);
        Dimension dimension = manage().window().getSize();
        int height = dimension.getHeight() - SWIPE_EDGE_OFFSET;
        int startx = dimension.getWidth() / 2;
        boolean found = false;
        for (int i = 0; i < MAX_SCROLL_COUNT; i++) {
            if (webElement.isDisplayed()) {
                found = true;
                break;
            }
            this.doSwipe(startx, height, startx, SWIPE_EDGE_OFFSET - height, SWIPE_DURATION);
        }
        if (!found && !webElement.isDisplayed()) {
            // giving up scrolling for element to be displayed after MAX_SCROLL_COUNT reached.
            throw new RuntimeException("element was not visible after scrolling");
        }
        logger.exiting();
    }

    @Override
    public void tap(WebElement webElement) {
        logger.entering(webElement);
        super.tap(1, webElement, TAP_DURATION);
        logger.exiting();
    }

    @Deprecated
    @Override
    public void tapWithOptions(WebElement webElement, EnumMap<GestureOptions, String> gestureOptions) {
        logger.entering(webElement, gestureOptions);
        String s = gestureOptions.get(GestureOptions.TOUCH_COUNT);
        int touchCount = s == null ? 1 : Integer.parseInt(s);
        s = gestureOptions.get(GestureOptions.DURATION);
        int duration = s == null ? TAP_DURATION : Integer.parseInt(s);
        super.tap(touchCount, webElement, duration);
        logger.exiting();
    }

    @Override
    public void twoFingerTap(WebElement webElement) {
        logger.entering(webElement);
        super.tap(2, webElement, 1);
        logger.exiting();
    }

    @Override
    public void dragSliderToValue(WebElement webElement, double value) {
        logger.entering(webElement, value);
        String stringValue = String.valueOf(value);
        webElement.sendKeys(stringValue);
        logger.exiting();
    }

    @Override
    public void setPickerWheelValue(WebElement webElement, String value) {
        logger.entering(webElement, value);
        webElement.sendKeys(value);
        logger.exiting();
    }

    @Override
    public String getLabel(WebElement webElement) {
        logger.entering(webElement);
        String label = webElement.getAttribute("label");
        logger.exiting(label);
        return label;
    }

    @Override
    public String getName(WebElement webElement) {
        logger.entering(webElement);
        String name = webElement.getAttribute("name");
        logger.exiting(name);
        return name;
    }

    @Override
    public String getValue(WebElement webElement) {
        logger.entering(webElement);
        String value = webElement.getAttribute("value");
        logger.exiting(value);
        return value;
    }

    @Override
    public void rotate(DeviceRotation deviceRotation) {
        //TODO
    }

    @Override
    public DeviceRotation rotation() {
        return null;
    }
}
