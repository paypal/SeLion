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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.paypal.selion.platform.html.WebPage;

/**
 * <code> UIAutomationElement </code> is the super interface for all user interface elements in the context of the
 * Automation instrument for automating user interface testing of iOS apps. This interface defines more general methods
 * that can be used on any type of user interface elements.
 */
public interface UIAutomationElement {

    /**
     * Finds the corresponding web element using the string locator. The locator is internally converted into {@link By}
     * entity and fed to {@link RemoteWebElement#findElement(By)} for further processing.
     * 
     * @param locator
     *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @return Instance of {@link WebElement} found by the locator.
     */
    WebElement findElement(String locator);

    /**
     * Double-taps the specified element.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UIAElement}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void doubleTap(Object... expected);

    /**
     * Scrolls until the specified element is visible in a container view.
     */
    void scrollToVisible();

    /**
     * Taps the specified element and optionally waits for the provided expected elements.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UIAElement}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void tap(Object... expected);

    /**
     * Performs the specified gesture on the specified element using a {@link EnumMap} to specify gesture attributes.
     * Refer {@link GestureOptions} for specifying gesture options.
     * 
     * @param gestureOptions
     *            {@link EnumMap} specifying the gesture attributes.
     * @param expected
     *            Expected entities in the form of objects extending {@link UIAElement}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void tapWithOptions(EnumMap<GestureOptions, String> gestureOptions, Object... expected);

    /**
     * Performs a two-finger (two-touch) tap on this element.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UIAElement}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void twoFingerTap(Object... expected);

    /**
     * Returns a string containing the label attribute of the element.
     * 
     * @return The label of the element.
     */
    String getLabel();

    /**
     * Returns a string containing the name attribute of the element.
     * 
     * @return The name of the element.
     */
    String getName();

    /**
     * Returns a string containing a value attribute specific to the type of element.
     * 
     * @return The value of the element.
     */
    String getValue();

    /**
     * Returns a string containing the locator used for the element
     * 
     * @return The locator for the element
     */
    String getLocator();

}
