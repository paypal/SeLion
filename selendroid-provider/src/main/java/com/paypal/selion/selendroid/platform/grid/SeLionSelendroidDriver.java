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

package com.paypal.selion.selendroid.platform.grid;

import io.selendroid.client.SelendroidDriver;

import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.DeviceRotation;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.remote.CommandExecutor;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.mobile.android.SeLionAndroidBridgeDriver;
import com.paypal.selion.platform.mobile.android.UiObject;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * <code>SeLionSelendroidDriver</code> provides facility to add custom {@link CommandExecutor} to
 * {@link SelendroidDriver}. This class also implements the {@link SeLionAndroidBridgeDriver} interface to expose
 * methods for {@link UiObject} and its subclasses.
 */
public class SeLionSelendroidDriver extends SelendroidDriver implements SeLionAndroidBridgeDriver {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    private static final int SHORT_TAP_TIME_MILLIS = 10;

    private static final int LONG_TAP_TIME_MILLIS = 1000;

    private static final String SCROLLVIEW_CLASS = "android.widget.ScrollView";

    public SeLionSelendroidDriver(Capabilities caps) throws Exception {
        super(caps);
    }

    public SeLionSelendroidDriver(URL url, Capabilities caps) throws Exception {
        super(url, caps);
    }

    public SeLionSelendroidDriver(CommandExecutor commandExecutor, Capabilities caps) throws Exception {
        super(caps);
        setCommandExecutor(commandExecutor);
    }

    @Override
    public void clearText(WebElement webElement) {
        logger.entering(webElement);
        webElement.clear();
        logger.exiting();
    }

    @Override
    public void click(WebElement webElement) {
        logger.entering(webElement);
        Point centerPoint = getElementCenter(webElement);
        performShortClickAction(centerPoint);
        logger.exiting();
    }

    @Override
    public void clickBottomRight(WebElement webElement) {
        logger.entering(webElement);
        Point bottomRightPoint = getElementBottomRight(webElement);
        performShortClickAction(bottomRightPoint);
        logger.exiting();

    }

    @Override
    public void clickTopLeft(WebElement webElement) {
        logger.entering(webElement);
        Point topLeftPoint = webElement.getLocation();
        performShortClickAction(topLeftPoint);
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

        // This method does not seem to return true for a truly scrollable element in the app.
        // This method may return false for elements that appear with scrollable=true in uiautomatorviewer
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
        Point centerPoint = getElementCenter(webElement);
        performLongClickAction(centerPoint);
        logger.exiting();
    }

    @Override
    public void longClickBottomRight(WebElement webElement) {
        logger.entering(webElement);
        Point bottomRightPoint = getElementBottomRight(webElement);
        performLongClickAction(bottomRightPoint);
        logger.exiting();
    }

    @Override
    public void longClickTopLeft(WebElement webElement) {
        logger.entering(webElement);
        Point topLeftPoint = webElement.getLocation();
        performLongClickAction(topLeftPoint);
        logger.exiting();
    }

    @Override
    public void setText(WebElement webElement, String text) {
        logger.entering(new Object[] { webElement, text });
        webElement.clear();
        webElement.sendKeys(text);
        logger.exiting();
    }

    /**
     * Scroll the screen to the left. The underlying application should have atleast one scroll view belonging to the
     * class 'android.widget.ScrollView'.
     */
    public void scrollLeft() {
        logger.entering();
        WebElement webElement = this.findElement(By.className(SCROLLVIEW_CLASS));
        swipeLeft(webElement);
        logger.exiting();
    }

    /**
     * Scroll the screen to the right. The underlying application should have atleast one scroll view belonging to the
     * class 'android.widget.ScrollView'.
     */
    public void scrollRight() {
        logger.entering();
        WebElement webElement = this.findElement(By.className(SCROLLVIEW_CLASS));
        swipeRight(webElement);
        logger.exiting();
    }

    /**
     * Scroll the screen up. The underlying application should have atleast one scroll view belonging to the class
     * 'android.widget.ScrollView'.
     */
    public void scrollUp() {
        logger.entering();
        WebElement webElement = this.findElement(By.className(SCROLLVIEW_CLASS));
        swipeUp(webElement);
        logger.exiting();
    }

    /**
     * Scroll the screen down. The underlying application should have atleast one scroll view belonging to the class
     * 'android.widget.ScrollView'.
     */
    public void scrollDown() {
        logger.entering();
        WebElement webElement = this.findElement(By.className(SCROLLVIEW_CLASS));
        swipeDown(webElement);
        logger.exiting();
    }

    @Override
    public void swipeLeft(WebElement webElement) {
        logger.entering(webElement);
        Point point = webElement.getLocation();
        Dimension dimension = webElement.getSize();
        Point start = new Point(point.getX() + dimension.getWidth() - 1, point.getY());
        Point end = new Point(point.getX(), point.getY());
        performSwipeAction(start, end);
        logger.exiting();
    }

    @Override
    public void swipeRight(WebElement webElement) {
        logger.entering(webElement);
        Point point = webElement.getLocation();
        Dimension dimension = webElement.getSize();
        Point start = new Point(point.getX(), point.getY());
        Point end = new Point(point.getX() + dimension.getWidth() - 1, point.getY());
        performSwipeAction(start, end);
        logger.exiting();
    }

    @Override
    public void swipeUp(WebElement webElement) {
        logger.entering(webElement);
        Point point = webElement.getLocation();
        Dimension dimension = webElement.getSize();
        Point start = new Point(point.getX(), point.getY() + dimension.getHeight() - 1);
        Point end = new Point(point.getX(), point.getY());
        performSwipeAction(start, end);
        logger.exiting();
    }

    @Override
    public void swipeDown(WebElement webElement) {
        logger.entering(webElement);
        Point point = webElement.getLocation();
        Dimension dimension = webElement.getSize();
        Point start = new Point(point.getX(), point.getY());
        Point end = new Point(point.getX(), point.getY() + dimension.getHeight() - 1);
        performSwipeAction(start, end);
        logger.exiting();
    }

    @Override
    public void swipe(int startx, int starty, int endx, int endy) {
        Point start = new Point(startx, starty);
        Point end = new Point(endx, endy);
        logger.entering(start, end);
        performSwipeAction(start, end);
        logger.exiting();
    }

    private Point getElementCenter(WebElement webElement) {
        Point point = webElement.getLocation();
        Dimension dimension = webElement.getSize();
        int x = point.getX() + dimension.getWidth() / 2;
        int y = point.getY() + dimension.getHeight() / 2;
        return new Point(x, y);
    }

    private Point getElementBottomRight(WebElement webElement) {
        Point point = webElement.getLocation();
        Dimension dimension = webElement.getSize();
        int x = point.getX() + dimension.getWidth() - 1;
        int y = point.getY() + dimension.getHeight() - 1;
        return new Point(x, y);
    }

    private void performShortClickAction(Point point) {
        try {
            new TouchActions(this).down(point.getX(), point.getY()).perform();
            Thread.sleep(SHORT_TAP_TIME_MILLIS);
            new TouchActions(this).up(point.getX(), point.getY()).perform();
        } catch (InterruptedException exe) {
            throw new WebDriverException("InterruptedException occurred during shortClick", exe);
        }
    }

    private void performLongClickAction(Point point) {
        try {
            new TouchActions(this).down(point.getX(), point.getY()).perform();
            Thread.sleep(LONG_TAP_TIME_MILLIS);
            new TouchActions(this).up(point.getX(), point.getY()).perform();
        } catch (InterruptedException exe) {
            throw new WebDriverException("InterruptedException occurred during longClick", exe);
        }
    }

    private void performSwipeAction(Point start, Point end) {
        new TouchActions(this).down(start.getX(), start.getY()).move(end.getX(), end.getY()).up(end.getX(), end.getY()).perform();
    }

    @Override
    public void rotate(DeviceRotation deviceRotation) {
        //TODO
    }

    @Override
    public DeviceRotation rotation() {
        //TODO
        return null;
    }
}
