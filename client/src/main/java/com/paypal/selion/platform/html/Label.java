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

/**
 * This class is the web Text wrapper.
 * <p>
 * It is mainly to verify the Text on the web page.
 * </p>
 * 
 */
public class Label extends AbstractElement {

    /**
     * Label Construction method<br>
     * <br>
     * <b>Usage:</b>
     * 
     * <pre>
     * private Label lblWelcome = new Label(&quot;//div[@id='Welcome']&quot;);
     * </pre>
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * 
     */
    public Label(String locator) {
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
    public Label(String locator, String controlName) {
        super(locator, controlName);
    }

    /**
     * Use this constructor to create a Label contained within a parent.
     * 
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * 
     */
    public Label(ParentTraits parent, String locator) {
        super(parent, locator);
    }

    /**
     * Use this constructor to create a Label contained within a parent. This constructor will also override default
     * controlName for logging purposes. Default controlName would be the element locator.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     */
    public Label(String locator, String controlName, ParentTraits parent) {
        super(locator, controlName, parent);
    }

    /**
     * It is to check whether the element's text matches with the specified pattern.
     * 
     * @param pattern
     *            regular expression
     * 
     * @return boolean <b>true</b> - the element's text matches with the pattern. <br>
     *         <b>false</b> the element's text doesn't match with the pattern.
     */
    public boolean isTextPresent(String pattern) {
        String text = getElement().getText();
        return (text != null && (text.contains(pattern) || text.matches(pattern)));
    }

}
