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

import static com.paypal.selion.platform.asserts.SeLionAsserts.assertFalse;
import static com.paypal.selion.platform.asserts.SeLionAsserts.assertTrue;

import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;

/**
 * This class test the CheckBox class methods.
 */
public class CheckBoxTest {

    CheckBox beansCheckBox = new CheckBox(TestObjectRepository.CHECKBOX_BEANS_LOCATOR.getValue());
    CheckBox chilliCheckBox = new CheckBox(TestObjectRepository.CHECKBOX_CHILLI_LOCATOR.getValue());
    

    @Test(groups = { "browser-tests" })
    @WebTest
    public void chkboxTestIsEnabled() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue(beansCheckBox.isEnabled(), "Validate isEnabled method");

    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void chkboxTestCheck() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        chilliCheckBox.check();
        assertTrue(chilliCheckBox.isChecked(), "Validate Check method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void chkboxTestUnCheck() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        beansCheckBox.uncheck();
        assertFalse(beansCheckBox.isChecked(), "Validate Uncheck method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void chkboxTestClick() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        chilliCheckBox.click();
        assertTrue(chilliCheckBox.isChecked(), "Validate Click method");

    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void chkboxTestClickAndWait() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        chilliCheckBox.click(beansCheckBox.getLocator());
        assertTrue(chilliCheckBox.isChecked(), "Validate Click(Object..expected) method");

    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void chkboxTestCheckAndWait() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        chilliCheckBox.check(beansCheckBox.getLocator());
        assertTrue(beansCheckBox.isChecked(), "Validate Check(Object...expected) method");

    }

    @Test(groups = { "browser-tests", "phantomjs-broken-test" })
    @WebTest
    public void chkboxTestUnCheckAndWait() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        beansCheckBox.uncheck(chilliCheckBox.getLocator());
        assertFalse(beansCheckBox.isChecked(), "Validate uncheck(Object...expected) method");
        AlertHandler.flushAllAlerts();
    }

}
