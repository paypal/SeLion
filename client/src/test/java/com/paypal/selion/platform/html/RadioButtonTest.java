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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.grid.Grid;

public class RadioButtonTest {
	

    RadioButton baseRadioButton = new RadioButton(TestObjectRepository.RADIOBUTTON_SPUD_LOCATOR.getValue());
    RadioButton baseRiceRadioButton = new RadioButton(TestObjectRepository.RADIOBUTTON_RICE_LOCATOR.getValue(),
            "base-rice");

    @BeforeClass(groups = { "browser-tests" })
    public void setUp() {
        Config.setConfigProperty(Config.ConfigProperty.ENABLE_GUI_LOGGING, Boolean.TRUE.toString());
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void radioBtnTestIsChecked() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue(baseRadioButton.isChecked(), "Validate isChecked method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void radioBtnTestCheck() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        baseRiceRadioButton.check();
        assertTrue(baseRiceRadioButton.isChecked(), "Validate check method");

        /*
         * Validate to make sure the button is still checked even if checked more then once
         */
        baseRiceRadioButton.check();
        assertTrue(baseRiceRadioButton.isChecked(), "Validate check method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void radioBtnTestClick() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        baseRiceRadioButton.click();
        assertTrue(baseRiceRadioButton.isChecked(), "Validate Click method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void radioBtnTestClickAndWait() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        baseRiceRadioButton.click(TestObjectRepository.RADIOBUTTON_SPUD_LOCATOR.getValue());
        assertTrue(baseRiceRadioButton.isChecked(), "Validate Click method");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Config.setConfigProperty(Config.ConfigProperty.ENABLE_GUI_LOGGING, Boolean.FALSE.toString());
    }
}
