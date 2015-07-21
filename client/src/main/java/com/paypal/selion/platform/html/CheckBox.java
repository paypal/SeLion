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

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.platform.html.support.events.Checkable;
import com.paypal.selion.platform.html.support.events.Uncheckable;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/**
 * This class is the web element CheckBox wrapper.
 * <p>
 * In this class, the method 'check' and 'uncheck' are encapsulated and invoke a SeLion session to do the check/uncheck
 * against the specified element. The method 'isChecked' is to verify whether this element is checked.
 * </p>
 * 
 */
public class CheckBox extends AbstractElement implements Checkable, Uncheckable {

    /**
     * CheckBox Construction method<br>
     * <br>
     * <b>Usage:</b>
     * 
     * <pre>
     * private CheckBox chkAcceptReturn = new CheckBox(&quot;//input[@id='AcceptReturn']&quot;);
     * </pre>
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * 
     */
    public CheckBox(String locator) {
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
     */
    public CheckBox(String locator, String controlName) {
        super(locator, controlName);
    }

    /**
     * Use this constructor to create a CheckBox contained within a parent.
     * 
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * 
     */
    public CheckBox(ParentTraits parent, String locator) {
        super(parent, locator);
    }

    /**
     * Use this constructor to create a CheckBox contained within a parent.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            - the control name used for logging.
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * 
     */
    public CheckBox(String locator, String controlName, ParentTraits parent) {
        super(locator, controlName, parent);
    }

    /**
     * The CheckBox check function It invokes selenium session to handle the check action against the element.
     */
    public void check() {
        getDispatcher().beforeCheck(this);
        
        RemoteWebElement e = (RemoteWebElement) getElement();
        while (!e.isSelected()) {
            e.click();
        }
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIAction(UIActions.CHECKED);
        }
        
        getDispatcher().afterCheck(this);
    }

    /**
     * The CheckBox check function It invokes selenium session to handle the check action against the element. Waits
     * until element is found with given locator.
     */
    public void check(String locator) {
        getDispatcher().beforeCheck(this, locator);
        
        this.check();
        validatePresenceOfAlert();
        WebDriverWaitUtils.waitUntilElementIsPresent(locator);
        
        getDispatcher().afterUncheck(this, locator);
    }

    /**
     * The CheckBox uncheck function It invokes SeLion session to handle the uncheck action against the element.
     */
    public void uncheck() {
        getDispatcher().beforeUncheck(this);
        
        RemoteWebElement e = (RemoteWebElement) getElement();
        while (e.isSelected()) {
            e.click();
        }
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIAction(UIActions.UNCHECKED);
        }
        
        getDispatcher().afterUncheck(this);
    }

    /**
     * The CheckBox uncheck function It invokes SeLion session to handle the uncheck action against the element. Waits
     * until element is found with given locator.
     */
    public void uncheck(String locator) {
        getDispatcher().beforeUncheck(this, locator);
        
        this.uncheck();
        validatePresenceOfAlert();
        WebDriverWaitUtils.waitUntilElementIsPresent(locator);
        
        getDispatcher().afterUncheck(this, locator);
    }

    /**
     * The CheckBox click function and wait for page to load
     */
    public void click() {
        getDispatcher().beforeClick(this);
        
        getElement().click();
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIAction(UIActions.CLICKED);
        }
        
        getDispatcher().afterClick(this);
    }

    /**
     * The CheckBox click function and wait for object to load
     */
    public void click(String locator) {
        getDispatcher().beforeClick(this, locator);
        
        click();
        validatePresenceOfAlert();
        WebDriverWaitUtils.waitUntilElementIsPresent(locator);
        
        getDispatcher().afterClick(this, locator);
    }

    /**
     * The CheckBox isChecked function
     * 
     * It invokes SeLion session to handle the isChecked function against the element.
     */
    public boolean isChecked() {
        return getElement().isSelected();
    }

    /**
     * The CheckBox isEditable function
     * 
     * It invokes SeLion session to handle the isEditable function against the element.
     */
    public boolean isEnabled() {
        return getElement().isEnabled();
    }
}
