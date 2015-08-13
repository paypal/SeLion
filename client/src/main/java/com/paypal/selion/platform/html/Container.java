/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

import java.util.Map;

/**
 * This class is a web element Container wrapper.
 * <p>
 * In this class, a container element can be leveraged to find descendant elements at a specified index. Upon
 * initialization, index equals 0.
 * </p>
 * 
 * Example usage to search for a child element at an indexed container.
 * 
 * <pre>
 * Container container = new Container(&quot;//div&quot;);
 * WebElement child = container.locateElement(3, &quot;.//a&quot;);
 * </pre>
 * 
 */
public class Container extends AbstractContainer {

    /**
     * Container Construction method<br>
     * <br>
     * <b>Usage:</b>
     * 
     * <pre>
     * private Container container = new Container(&quot;//div&quot;);
     * </pre>
     * 
     * @param locator
     *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * 
     */
    public Container(String locator) {
        super(locator);
    }

    /**
     * Use this constructor to override default controlName for logging purposes. Default controlName would be the
     * element locator.
     * 
     * @param locator
     *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging.
     * 
     */
    public Container(String locator, String controlName) {
        super(locator, controlName);
    }

    /**
     * Use this constructor to override default controlName and assign a parent
     * 
     * @param locator
     *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging.
     * @param parent
     *            A {@link ParentTraits} object that represents the parent element for this element.
     * 
     */
    public Container(String locator, String controlName, ParentTraits parent) {
        super(locator, controlName, parent);
    }

    /**
     * Constructs a {@link Container} with locator, controlName, parent and containerElements
     * 
     * @param locator
     *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging.
     * @param parent
     *            A {@link ParentTraits} object that represents the parent element for this element.
     * @param containerElements
     *            A {@link Map} containing the locators for elements inside this container.
     */
    public Container(String locator, String controlName, ParentTraits parent, Map<String, String> containerElements) {
        super(locator, controlName, parent, containerElements);
    }

    /**
     * Set a parent of type {@link ParentTraits} to this {@link Container}
     * 
     * @param parent
     *            A {@link ParentTraits} object that represents the parent element for this element.
     */
    public void setParentForContainer(ParentTraits parent) {
        setParent(parent);
    }

}
