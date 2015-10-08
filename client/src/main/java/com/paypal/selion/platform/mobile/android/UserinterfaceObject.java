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

import org.openqa.selenium.support.ui.ExpectedCondition;

import com.paypal.selion.platform.html.WebPage;

/**
 * <code>UserinterfaceObject</code> exposes the base interactions that can be carried on any Android UI automation
 * element. This class makes some minor changes to the methods exposed by UiObject of Android UiAutomator framework.
 * Refer to the below link for information on UiObject.
 * 
 * @see <a href="https://developer.android.com/reference/android/support/test/uiautomator/UiObject.html">UiObject</a>
 */
public interface UserinterfaceObject {

    /**
     * Clears the existing text contents in an editable field.
     */
    void clearText();

    /**
     * Performs a click at the center of the visible bounds of the UI element represented by this UiObject.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UiObject}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void click(Object... expected);

    /**
     * Clicks the bottom and right corner of the UI element.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UiObject}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void clickBottomRight(Object... expected);

    /**
     * Clicks the top and left corner of the UI element.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UiObject}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void clickTopLeft(Object... expected);

    /**
     * Reads the text property of the UI element.
     * 
     * @return Text property of this UI element.
     */
    String getText();

    /**
     * Checks if the UI element's checkable property is currently true. Returns false if the underlying property is
     * null.
     * 
     * @return Value of checkable property.
     */
    boolean isCheckable();

    /**
     * Checks if the UI element's checked property is currently true. Returns false if the underlying property is null.
     * 
     * @return Value of checked property.
     */
    boolean isChecked();

    /**
     * Checks if the UI element's clickable property is currently true. Returns false if the underlying property is
     * null.
     * 
     * @return Value of clickable property.
     */
    boolean isClickable();

    /**
     * Checks if the UI element's enabled property is currently true. Returns false if the underlying property is null.
     * 
     * @return Value of enabled property.
     */
    boolean isEnabled();

    /**
     * Checks if the UI element's focusable property is currently true. Returns false if the underlying property is
     * null.
     * 
     * @return Value of focusable property.
     */
    boolean isFocusable();

    /**
     * Checks if the UI element's focused property is currently true. Returns false if the underlying property is null.
     * 
     * @return Value of focused property.
     */
    boolean isFocused();

    /**
     * Checks if the view's long-clickable property is currently true. Returns false if the underlying property is null.
     * 
     * @return Value of long-clickable property.
     */
    boolean isLongClickable();

    /**
     * Checks if the view's scrollable property is currently true. Returns false if the underlying property is null.
     * 
     * @return Value of scrollable property.
     */
    boolean isScrollable();

    /**
     * Checks if the UI element's selected property is currently true. Returns false if the underlying property is null.
     * 
     * @return Value of selected property.
     */
    boolean isSelected();

    /**
     * Long clicks the center of the visible bounds of the UI element.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UiObject}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void longClick(Object... expected);

    /**
     * Long clicks bottom and right corner of the UI element.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UiObject}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void longClickBottomRight(Object... expected);

    /**
     * Long clicks on the top and left corner of the UI element.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UiObject}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void longClickTopLeft(Object... expected);

    /**
     * Sets the text in an editable field, after clearing the field's content.
     * 
     * @param text
     *            String to set.
     */
    void setText(String text);

    /**
     * Performs the swipe left action on the UiObject. The swipe gesture can be performed over any surface. The targeted
     * UI element does not need to be scrollable.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UiObject}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void swipeLeft(Object... expected);

    /**
     * Performs the swipe right action on the UiObject. The swipe gesture can be performed over any surface. The
     * targeted UI element does not need to be scrollable.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UiObject}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void swipeRight(Object... expected);

    /**
     * Performs the swipe up action on the UiObject. The swipe gesture can be performed over any surface. The targeted
     * UI element does not need to be scrollable.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UiObject}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void swipeUp(Object... expected);

    /**
     * Performs the swipe down action on the UiObject. The swipe gesture can be performed over any surface. The targeted
     * UI element does not need to be scrollable.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UiObject}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void swipeDown(Object... expected);

}
