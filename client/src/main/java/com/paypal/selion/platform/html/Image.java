/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

import org.openqa.selenium.remote.RemoteWebElement;

/**
 * This class is the web element Image wrapper.
 * <p>
 * In this class, the method 'click' is encapsulated and invokes a SeLion session to do click against the specified
 * element.
 * </p>
 * 
 */
public class Image extends AbstractElement {
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";

    /**
     * Image Construction method<br>
     * <br>
     * <b>Usage:</b>
     * 
     * <pre>
     * private Image imgIcon = new Image(&quot;//Image[@id='Icon']&quot;);
     * </pre>
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * 
     */
    public Image(String locator) {
        super(locator);
    }

    /**
     * Use this constructor to override default controlName for logging purposes. Default controlName would be the
     * element locator.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging
     */
    public Image(String locator, String controlName) {
        super(locator, controlName);
    }

    /**
     * Use this constructor to create an Image contained within a parent.
     * 
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     */
    public Image(ParentTraits parent, String locator) {
        super(parent, locator);
    }

    /**
     * Use this constructor to create an Image contained within a parent. Refer:{@link ParentTraits}<br>
     * This constructor will also override default controlName for logging purposes. Default controlName would be the
     * element locator.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     */
    public Image(String locator, String controlName, ParentTraits parent) {
        super(locator, controlName, parent);
    }

    /**
     * This function is to get image's width.
     * 
     * @return the image's width
     * 
     * @throws WebElementException
     */
    public int getWidth() {
        try {
            return ((RemoteWebElement) getElement()).getSize().width;
        } catch (NumberFormatException e) {
            throw new WebElementException("Attribute " + WIDTH + " not found for Image " + getLocator(), e);
        }
    }

    /**
     * This function is to get image's height.
     * 
     * @return the image's height
     * 
     * @throws WebElementException
     */
    public int getHeight() {
        try {
            return ((RemoteWebElement) getElement()).getSize().height;
        } catch (NumberFormatException e) {
            throw new WebElementException("Attribute " + HEIGHT + " not found for Image " + getLocator(), e);
        }
    }

}
