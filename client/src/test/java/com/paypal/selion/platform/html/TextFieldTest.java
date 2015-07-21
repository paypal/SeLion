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
import static org.testng.Assert.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.grid.Grid;

/**
 * This class test the TextField class methods
 */
public class TextFieldTest {
    static final String sTest = "TestString";

    TextField normalTextField = new TextField(TestObjectRepository.TEXTFIELD_LOCATOR.getValue());
    TextField disabledTextField = new TextField(TestObjectRepository.TEXTFIELD_DISABLED_LOCATOR.getValue(),
            "disabled_text");

    @BeforeClass(groups = { "browser-tests" })
    public void setUp() {
        Config.setConfigProperty(Config.ConfigProperty.ENABLE_GUI_LOGGING, Boolean.TRUE.toString());
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void txtFieldTestTypeText() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        normalTextField.type(sTest);
        assertTrue(normalTextField.getText().matches(sTest), "Validate SetText method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void txtFieldTestGetText() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        normalTextField.type(sTest);
        assertTrue(normalTextField.getText().contains(sTest), "Vlaidate GetText method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void txtFieldTestClearText() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        normalTextField.type(sTest);
        assertTrue(normalTextField.getText().matches(sTest), "Validate Type method");
        normalTextField.clear();
        assertTrue(normalTextField.getText().length() == 0, "Validate ClearText method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void txtFieldTestisEditable() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue(normalTextField.isEditable(), "Validate isEditable method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void txtFieldTesGetControlName() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertEquals(disabledTextField.getControlName(), "disabled_text", "Validate GetControlName method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void txtFieldTesGetParent() {
        Container dummyParent = new Container(TestObjectRepository.TEXTFIELD_DISABLED_LOCATOR.getValue());
        TextField dummyTextField = new TextField(dummyParent, TestObjectRepository.TEXTFIELD_LOCATOR.getValue());

        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertEquals(dummyTextField.getParent().hashCode(), dummyParent.hashCode(), "Validate GetParent method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void txtFieldTestIsElementPresent() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue(normalTextField.isElementPresent(), "Validate IsElementPresent method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void txtFieldTestIsVisible() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue(normalTextField.isVisible(), "Validate IsVisible method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void txtFieldTestIsEnabled() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue(normalTextField.isEnabled(), "Validate IsEnabled method");
        assertFalse(disabledTextField.isEnabled(), "Validate IsEnabled method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void txtFieldTestGetSetProperty() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        TextField dummyTextField = new TextField(TestObjectRepository.TEXTFIELD_LOCATOR.getValue());
        dummyTextField.setProperty("name", "dummyTextField");
        assertEquals(dummyTextField.getProperty("name"), "dummyTextField", "Validate Get/SetProperty");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void txtFieldTestTypeTextKeepExistingText() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        normalTextField.type("Test1");
        assertTrue(normalTextField.getText().matches("Test1"), "Validate Type() method");

        normalTextField.type("Test2", false);
        assertTrue(normalTextField.getText().matches("Test2"), "Validate Type(value, isKeepExistingText) method");

        normalTextField.type("Test3", true);
        assertTrue(normalTextField.getText().matches("Test2Test3"), "Validate Type(value, isKeepExistingText) method");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Config.setConfigProperty(Config.ConfigProperty.ENABLE_GUI_LOGGING, Boolean.FALSE.toString());
    }

}
