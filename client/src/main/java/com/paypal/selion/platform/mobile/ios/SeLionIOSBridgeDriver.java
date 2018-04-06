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

package com.paypal.selion.platform.mobile.ios;

import java.util.EnumMap;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * <code>SeLionIOSBridgeDriver</code> acts as single level of abstraction between different mobile driver
 * implementations. {@link UIAElement} class depends on this abstraction to delegate calls to the respective
 * implementations.
 */
public interface SeLionIOSBridgeDriver {

    /**
     * Constant representing one finger
     */
    String ONE_FINGER = "1";

    /**
     * Constant representing two fingers
     */
    String TWO_FINGERS = "2";

    /**
     * Constant representing single tap
     */
    String SINGLE_TAP = "1";

    /**
     * Constant representing double tap
     */
    String DOUBLE_TAP = "2";

    /**
     * Constant representing tap gesture
     */
    String TAP = "0";

    /**
     * Constant representing touch and hold gesture
     */
    String TOUCH_AND_HOLD = "1";

    /**
     * Finds the {@link WebElement} using the underlying {@link RemoteWebDriver} implementation.
     * 
     * @param by
     *            {@link By} instance
     * @return {@link WebElement} found for the corresponding {@link By}
     */
    WebElement findElementBy(By by);

    /**
     * Double tap the {@link WebElement}
     * 
     * @param webElement
     *            {@link WebElement} instance.
     */
    void doubleTap(WebElement webElement);

    /**
     * Scrolls until the specified {@link WebElement} is visible in a container view.
     * 
     * @param webElement
     *            {@link WebElement} instance.
     */
    void scrollToVisible(WebElement webElement);

    /**
     * Taps the specified {@link WebElement}.
     * 
     * @param webElement
     *            {@link WebElement} instance.
     */
    void tap(WebElement webElement);

    /**
     * Performs the specified gesture on the specified {@link WebElement} using a {@link EnumMap} to specify gesture
     * attributes. Refer {@link GestureOptions} for specifying gesture options.
     * 
     * @param webElement
     *            {@link WebElement} instance.
     * 
     * @param gestureOptions
     *            {@link EnumMap} specifying the gesture attributes.
     */
    void tapWithOptions(WebElement webElement, EnumMap<GestureOptions, String> gestureOptions);

    /**
     * Performs a two-finger (two-touch) tap on the {@link WebElement}.
     * 
     * @param webElement
     *            {@link WebElement} instance.
     */
    void twoFingerTap(WebElement webElement);

    /**
     * Drags the {@link UIAutomationSlider} to the specified value.
     * 
     * @param webElement
     *            {@link WebElement} representing the {@link UIAutomationSlider}
     * @param value
     *            double value.
     */
    void dragSliderToValue(WebElement webElement, double value);

    /**
     * Sets the value of {@link UIAutomationPicker} to the specified value.
     * 
     * @param webElement
     *            {@link WebElement} representing the {@link UIAutomationPicker}
     * @param value
     *            String value.
     */
    void setPickerWheelValue(WebElement webElement, String value);

    /**
     * Returns a string containing the label attribute of the {@link WebElement}.
     * 
     * @param webElement
     *            {@link WebElement} instance.
     * @return Label of the {@link WebElement}
     */
    String getLabel(WebElement webElement);

    /**
     * Returns a string containing the name attribute of the {@link WebElement}.
     * 
     * @param webElement
     *            {@link WebElement} instance.
     * @return Name of the {@link WebElement}
     */
    String getName(WebElement webElement);

    /**
     * Returns a string containing the value attribute of the {@link WebElement}.
     * 
     * @param webElement
     *            {@link WebElement} instance.
     * @return Value of the {@link WebElement}
     */
    String getValue(WebElement webElement);

}
