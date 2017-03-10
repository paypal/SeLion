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

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;

import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.DeviceRotation;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CommandExecutor;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.mobile.android.SeLionAndroidBridgeDriver;
import com.paypal.selion.platform.mobile.android.UiObject;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * <code>SeLionAppiumAndroidDriver</code> provides facility to add custom {@link CommandExecutor} to
 * {@link AndroidDriver}. This class also implements the {@link SeLionAndroidBridgeDriver} interface to expose
 * methods for {@link UiObject} and its subclasses.
 */
public class SeLionAppiumAndroidDriver extends AndroidDriver<WebElement> implements SeLionAndroidBridgeDriver {

    private static final int OPERATION_DURATION_MILLI_SECONDS = 1000;
    private static final SimpleLogger logger = SeLionLogger.getLogger();

    public SeLionAppiumAndroidDriver(URL url, Capabilities caps) {
        super(url, caps);
    }

    public SeLionAppiumAndroidDriver(CommandExecutor commandExecutor, Capabilities caps, URL url) {
        super(url, caps);
        setCommandExecutor(commandExecutor);
    }

    @Override
    public void click(WebElement webElement) {
        logger.entering(webElement);
        this.tap(1, webElement, 1);
        logger.exiting();
    }

    @Override
    public void clickBottomRight(WebElement webElement) {
        logger.entering(webElement);
        Point currentPoint = webElement.getLocation();
        Dimension dimension = webElement.getSize();
        TouchAction clickBottomRight = new TouchAction(this);
        int newX = currentPoint.getX() + dimension.getWidth();
        int newY = currentPoint.getY() + dimension.getHeight();
        clickBottomRight.press(newX - 1, newY - 1).release().perform();
        logger.exiting();
    }

    @Override
    public void clickTopLeft(WebElement webElement) {
        logger.entering(webElement);
        Point currentPoint = webElement.getLocation();
        TouchAction clickTopLeft = new TouchAction(this);
        clickTopLeft.press(currentPoint.getX(), currentPoint.getY()).release().perform();
        logger.exiting();
    }

    @Override
    public String getText(WebElement webElement) {
        logger.entering(webElement);
        String text = webElement.getAttribute("text");
        logger.exiting(text);
        return text;
    }

    @Override
    public boolean isCheckable(WebElement webElement) {
        logger.entering(webElement);
        boolean result = Boolean.parseBoolean(webElement.getAttribute("checkable"));
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isChecked(WebElement webElement) {
        logger.entering(webElement);
        boolean result = Boolean.parseBoolean(webElement.getAttribute("checked"));
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isClickable(WebElement webElement) {
        logger.entering(webElement);
        boolean result = Boolean.parseBoolean(webElement.getAttribute("clickable"));
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isEnabled(WebElement webElement) {
        logger.entering(webElement);
        boolean result = Boolean.parseBoolean(webElement.getAttribute("enabled"));
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isFocusable(WebElement webElement) {
        logger.entering(webElement);
        boolean result = Boolean.parseBoolean(webElement.getAttribute("focusable"));
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isFocused(WebElement webElement) {
        logger.entering(webElement);
        boolean result = Boolean.parseBoolean(webElement.getAttribute("focused"));
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isLongClickable(WebElement webElement) {
        logger.entering(webElement);
        boolean result = Boolean.parseBoolean(webElement.getAttribute("longClickable"));
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isScrollable(WebElement webElement) {
        logger.entering(webElement);
        boolean result = Boolean.parseBoolean(webElement.getAttribute("scrollable"));
        logger.exiting(result);
        return result;
    }

    @Override
    public boolean isSelected(WebElement webElement) {
        logger.entering(webElement);
        boolean result = Boolean.parseBoolean(webElement.getAttribute("selected"));
        logger.exiting(result);
        return result;
    }

    @Override
    public void longClick(WebElement webElement) {
        logger.entering(webElement);
        this.tap(1, webElement, OPERATION_DURATION_MILLI_SECONDS);
        logger.exiting();
    }

    @Override
    public void longClickBottomRight(WebElement webElement) {
        logger.entering(webElement);
        Point currentPoint = webElement.getLocation();
        Dimension dimension = webElement.getSize();
        TouchAction clickBottomRight = new TouchAction(this);
        int newX = currentPoint.getX() + dimension.getWidth();
        int newY = currentPoint.getY() + dimension.getHeight();
        clickBottomRight.longPress(newX - 1, newY - 1).release().perform();
        logger.exiting();
    }

    @Override
    public void longClickTopLeft(WebElement webElement) {
        logger.entering(webElement);
        Point currentPoint = webElement.getLocation();
        TouchAction clickTopLeft = new TouchAction(this);
        clickTopLeft.longPress(currentPoint.getX(), currentPoint.getY()).release().perform();
        logger.exiting();
    }

    @Override
    public void swipeLeft(WebElement webElement) {
        logger.entering(webElement);
        Point currentLocation = webElement.getLocation();
        Dimension elementSize = webElement.getSize();
        int x =  currentLocation.getX() + elementSize.getWidth() - 1;
        int y = currentLocation.getY();
        int endx = currentLocation.getX();
        this.swipe(x, y, endx, y, OPERATION_DURATION_MILLI_SECONDS);
        logger.exiting();
    }

    @Override
    public void swipeRight(WebElement webElement) {
        logger.entering(webElement);
        Point currentLocation = webElement.getLocation();
        Dimension elementSize = webElement.getSize();
        int x = currentLocation.getX();
        int y = currentLocation.getY();
        int endx = x + elementSize.getWidth() - 1;
        this.swipe(x,y,endx, y, OPERATION_DURATION_MILLI_SECONDS);
        logger.exiting();
    }

    @Override
    public void swipeUp(WebElement webElement) {
        logger.entering(webElement);
        Point currentLocation = webElement.getLocation();
        Dimension elementSize = webElement.getSize();
        int x = currentLocation.getX();
        int y = currentLocation.getY() + elementSize.getHeight() - 1;
        int endy = currentLocation.getY();
        this.swipe(x, y, x, endy, OPERATION_DURATION_MILLI_SECONDS);
        logger.exiting();
    }

    @Override
    public void swipeDown(WebElement webElement) {
        logger.entering(webElement);
        Point currentLocation = webElement.getLocation();
        Dimension elementSize = webElement.getSize();
        int x = currentLocation.getX();
        int y = currentLocation.getY();
        int endy = y + elementSize.getHeight() - 1;
        this.swipe(x, y, x, endy, OPERATION_DURATION_MILLI_SECONDS);
        logger.exiting();
    }

    @Override
    public void clearText(WebElement webElement) {
        logger.entering(webElement);
        webElement.clear();
        logger.exiting();
    }

    @Override
    public void setText(WebElement webElement, String text) {
        logger.entering(webElement);
        //As per the UI Object API doc a text field will be cleared before setting value
        webElement.clear();
        webElement.sendKeys(text);
        logger.exiting();
    }

    @Override
    public void swipe(int startx, int starty, int endx, int endy) {
        //super.swipe(startx, starty, endx, endy, OPERATION_DURATION_MILLI_SECONDS );

        // On Appium Android we mimic swipe via one finger tap
        this.tap(1, starty, endx, OPERATION_DURATION_MILLI_SECONDS);
    }

    @Override
    public void rotate(DeviceRotation rotation) {
        // TODO
    }

    @Override
    public DeviceRotation rotation() {
        return null;
    }
}
