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

import com.paypal.selion.platform.html.support.events.Submitable;

/**
 * This class is the web element Form wrapper.
 * <p>
 * In this class, the method 'submit' is encapsulated and invoke as SeLion session to do submit against the specified
 * element.
 * </p>
 * 
 */
public class Form extends AbstractElement implements Submitable {

    /**
     * Form Construction method<br>
     * <br>
     * <b>Usage:</b>
     * 
     * <pre>
     * private Form frmDummy = new Form(&quot;//form[@name='DummyForm']&quot;);
     * </pre>
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     */
    public Form(String locator) {
        super(locator);
    }

    /**
     * Use this constructor to override default controlName for logging purposes. Default controlName would be the
     * element locator.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            - the control name used for logging.
     * 
     */
    public Form(String locator, String controlName) {
        super(locator, controlName);
    }

    /**
     * Use this constructor to create a Form contained within a parent. Refer:{@link ParentTraits}
     * 
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * 
     */
    public Form(ParentTraits parent, String locator) {
        super(parent, locator);
    }

    /**
     * Use this constructor to create a Form contained within a parent. This constructor will also override default
     * controlName for logging purposes. Default controlName would be the element locator.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            - the control name used for logging.
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * 
     */
    public Form(String locator, String controlName, ParentTraits parent) {
        super(locator, controlName, parent);
    }

    /**
     * The Form submit function It invokes SeLion session to handle the submit action against the element.
     */
    public void submit() {
        getDispatcher().beforeSubmit(this);
        
        getElement().submit();
        
        getDispatcher().afterSubmit(this);
    }
}
