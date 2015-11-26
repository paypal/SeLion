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

import static com.paypal.selion.platform.asserts.SeLionAsserts.assertEquals;
import static com.paypal.selion.platform.asserts.SeLionAsserts.assertTrue;

import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;

/**
 * This class test the Label class methods
 */

public class LabelTest {
	

    Label editableTestField = new Label(TestObjectRepository.LABEL_EDITABLE.getValue());
    Label testLabelWithControlName = new Label(TestObjectRepository.LABEL_EDITABLE.getValue(), "normal_text");

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testLabel() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue(editableTestField.isTextPresent("Editable text-field"), "Validated isTextPresent method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testLabel2() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue(testLabelWithControlName.isTextPresent("Editable text-field"), "Validated isTextPresent method");
        assertEquals(testLabelWithControlName.getControlName(), "normal_text", "Validated isTextPresent method");

    }

}
