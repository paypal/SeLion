/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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

package com.paypal.selion.platform.html;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import com.paypal.selion.testcomponents.BasicPageImpl;

/**
 * A generic interface for outlining the Parent traits of an HTML entity in SeLion.<br>
 * By Default, A Page {@link BasicPageImpl} is the parent to {@link AbstractElement} when loaded via SeLion. A parent
 * can also be a {@link AbstractContainer}
 * 
 */
public interface ParentTraits {

    /**
     * Returns the child element denoted by the locator
     * 
     * @param locator
     */
    RemoteWebElement locateChildElement(String locator);

    /**
     * Returns the {@link List} of WebElements {@link WebElement} denoted by the locator
     * 
     * @param locator
     * 
     */
    List<WebElement> locateChildElements(String locator);

    /**
     * Returns the current page {@link BasicPageImpl}
     * 
     */
    BasicPageImpl getCurrentPage();

}
