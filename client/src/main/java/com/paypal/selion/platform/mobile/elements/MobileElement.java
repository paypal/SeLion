/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

package com.paypal.selion.platform.mobile.elements;

import com.paypal.selion.platform.html.WebPage;
import com.paypal.selion.platform.mobile.Implementor;
import com.paypal.selion.platform.mobile.android.UiObject;
import com.paypal.selion.platform.mobile.ios.UIAElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * <code> MobileElement </code> is the super interface for all user interface elements in the context of the
 * Automation instrument for automating user interface testing. This interface defines more general methods
 * that can be used on any type of user interface elements.
 */
@Implementor(ios = UIAElement.class, android = UiObject.class)
public interface MobileElement {

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
     * Taps the specified element and optionally waits for the provided expected elements.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UIAElement}, xpath location in the form of
     *            {@link String}, instances of {@link ExpectedCondition}, or an instance of a {@link WebPage}
     */
    void tap(Object... expected);

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
