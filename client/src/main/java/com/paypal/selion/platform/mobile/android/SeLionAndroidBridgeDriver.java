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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * <code>SeLionAndroidBridgeDriver</code> acts as a single level of abstraction for the page object model implementation
 * for android mobile drivers. {@link UserinterfaceObject} class depends on this abstraction to delegate calls to the
 * respective driver implementations.
 */
public interface SeLionAndroidBridgeDriver {

    /**
     * Finds the {@link WebElement} using the underlying {@link RemoteWebDriver} implementation.
     * 
     * @param by
     *            Instance of {@link By}.
     * @return {@link WebElement} found for the corresponding {@link By}
     */
    WebElement findElement(By by);

    /**
     * Clears the existing text contents represented by the {@link WebElement}.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     */
    void clearText(WebElement webElement);

    /**
     * Performs a click at the center of the visible bounds of the UI element represented by this UiObject.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     */
    void click(WebElement webElement);

    /**
     * Clicks the bottom and right corner of the UI element.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     */
    void clickBottomRight(WebElement webElement);

    /**
     * Clicks the top and left corner of the UI element.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     */
    void clickTopLeft(WebElement webElement);

    /**
     * Reads the text property of the UI element.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     * @return Text property of this UI element.
     */
    String getText(WebElement webElement);

    /**
     * Checks if the UI element's checkable property is currently true. Returns false if the underlying property is
     * null.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     * @return Value of checkable property.
     */
    boolean isCheckable(WebElement webElement);

    /**
     * Checks if the UI element's checked property is currently true. Returns false if the underlying property is null.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     * @return Value of checked property.
     */
    boolean isChecked(WebElement webElement);

    /**
     * Checks if the UI element's clickable property is currently true. Returns false if the underlying property is
     * null.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     * @return Value of clickable property.
     */
    boolean isClickable(WebElement webElement);

    /**
     * Checks if the UI element's enabled property is currently true. Returns false if the underlying property is null.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     * @return Value of enabled property.
     */
    boolean isEnabled(WebElement webElement);

    /**
     * Checks if the UI element's focusable property is currently true. Returns false if the underlying property is
     * null.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     * @return Value of focusable property.
     */
    boolean isFocusable(WebElement webElement);

    /**
     * Checks if the UI element's focused property is currently true. Returns false if the underlying property is null.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     * @return Value of focused property.
     */
    boolean isFocused(WebElement webElement);

    /**
     * Checks if the view's long-clickable property is currently true. Returns false if the underlying property is null.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     * @return Value of long-clickable property.
     */
    boolean isLongClickable(WebElement webElement);

    /**
     * Checks if the view's scrollable property is currently true. Returns false if the underlying property is null.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     * @return Value of scrollable property.
     */
    boolean isScrollable(WebElement webElement);

    /**
     * Checks if the UI element's selected property is currently true. Returns false if the underlying property is null.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     * @return Value of selected property.
     */
    boolean isSelected(WebElement webElement);

    /**
     * Long clicks the center of the visible bounds of the UI element.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     */
    void longClick(WebElement webElement);

    /**
     * Long clicks bottom and right corner of the UI element.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     */
    void longClickBottomRight(WebElement webElement);

    /**
     * Long clicks on the top and left corner of the UI element.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     */
    void longClickTopLeft(WebElement webElement);

    /**
     * Sets the text in an editable field represented by {@link WebElement}, after clearing the field's content.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     * @param text
     *            String to set.
     */
    void setText(WebElement webElement, String text);

    /**
     * Performs the swipe left action on the {@link WebElement}. The swipe gesture can be performed over any surface.
     * The targeted UI element does not need to be scrollable.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     */
    void swipeLeft(WebElement webElement);

    /**
     * Performs the swipe right action on the {@link WebElement}. The swipe gesture can be performed over any surface.
     * The targeted UI element does not need to be scrollable.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     */
    void swipeRight(WebElement webElement);

    /**
     * Performs the swipe up action on the {@link WebElement}. The swipe gesture can be performed over any surface. The
     * targeted UI element does not need to be scrollable.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     */
    void swipeUp(WebElement webElement);

    /**
     * Performs the swipe down action on the {@link WebElement}. The swipe gesture can be performed over any surface.
     * The targeted UI element does not need to be scrollable.
     * 
     * @param webElement
     *            Instance of {@link WebElement}.
     */
    void swipeDown(WebElement webElement);

    /**
     * preform a swipe action on the device
     * 
     * @param startx
     *            x of start point
     * @param starty
     *            y of start point
     * @param endx
     *            x of end point
     * @param endy
     *            y of end point
     */
    void swipe(int startx, int starty, int endx, int endy);
}
