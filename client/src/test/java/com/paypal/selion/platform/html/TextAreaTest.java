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

import static com.paypal.selion.platform.asserts.SeLionAsserts.assertTrue;

import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;

public class TextAreaTest {
    static final String sTest = "Testing multi line text for TextArea object";
    static final String sLine = "Testing multi line" + "\n" + "text for TextArea" + "\n" + "object";
    TextField normalTextField = new TextField(TestObjectRepository.TEXT_AREA_LOCATOR.getValue());

    @Test(groups = { "browser-tests" })
    @WebTest
    public void textAreaTestTypeText() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        normalTextField.type(sTest);
        String s = normalTextField.getText();
        assertTrue(s.matches(sTest), "Validate SetText method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void textAreaTestGetText() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        normalTextField.type(sTest);
        assertTrue(normalTextField.getText().contains(sTest), "Validate GetText method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void textAreaTestClearText() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        normalTextField.type(sTest);
        assertTrue(normalTextField.getText().matches(sTest), "Validate Type method");
        normalTextField.clear();
        assertTrue(normalTextField.getText().length() == 0, "Validate ClearText method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void textAreaTestisEditable() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue(normalTextField.isEditable(), "Validate isEditable method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void textAreaTestTypeTextNewLine() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        normalTextField.type(sLine);
        assertTrue(normalTextField.getText().contains("Testing"), "Validate SetText method");
    }
}
