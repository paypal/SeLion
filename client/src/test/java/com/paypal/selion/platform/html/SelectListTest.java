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
 * This class test the SelectList class methods
 */

import static com.paypal.selion.platform.asserts.SeLionAsserts.assertEquals;
import static com.paypal.selion.platform.asserts.SeLionAsserts.assertTrue;

import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.grid.Grid;

public class SelectListTest {
    static final String sByVal = "White";
    static final String sExpected = "Red";
    static final String sLabel = "black";
    SelectList normalSelectList = new SelectList(TestObjectRepository.SELECTLIST_LOCATOR.getValue());
    private final SelectList multiSelect = new SelectList("name=multiple_select");
    

    @BeforeClass(groups = {"browser-tests"})
    public void setUp() {
        Config.setConfigProperty(Config.ConfigProperty.ENABLE_GUI_LOGGING, Boolean.TRUE.toString());
    }

    @Test(groups = {"browser-tests"})
    @WebTest
    public void selectListTestSelectByValue() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        normalSelectList.selectByValue(sByVal);
        assertTrue(normalSelectList.getSelectedValue().matches(sByVal), "Validate SelectByValue method");

        normalSelectList.selectByValue(new String[]{"black", "Red", "White"});
        assertTrue(normalSelectList.getSelectedValue().matches("White"), "Validate SelectByValue method");

        normalSelectList.addSelectionByValue(sByVal);
        assertTrue(normalSelectList.getSelectedValue().matches(sByVal), "Validate SelectByValue method");

        String[] getSelectedValues = normalSelectList.getSelectedValues();
        assertTrue(getSelectedValues.length > 0, "Validate SelectByValue method");
        assertEquals(getSelectedValues[0],sByVal, "Validate SelectByValue method");
    }

    @Test(groups = {"browser-tests"})
    @WebTest
    public void selectListTestSelecByIndex() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        normalSelectList.selectByIndex(1);
        assertTrue(normalSelectList.getSelectedLabel().matches(sExpected), "Validate SelectByIndex method");

        normalSelectList.selectByIndex(new String[]{"1"});
        assertTrue(normalSelectList.getSelectedLabel().matches(sExpected), "Validate SelectByIndex method");

        normalSelectList.addSelectionByIndex("1");
        assertTrue(normalSelectList.getSelectedLabel().matches(sExpected), "Validate SelectByIndex method");
    }

    @Test(groups = {"browser-tests"})
    @WebTest
    public void selectListTestSelecByLabel() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        normalSelectList.selectByLabel(sLabel);
        assertTrue(normalSelectList.getSelectedValue().matches(sLabel), "Validate SelectByLabel method");

        normalSelectList.selectByLabel(new String[]{sLabel});
        assertTrue(normalSelectList.getSelectedValue().matches(sLabel), "Validate SelectByLabel method");

        normalSelectList.addSelectionByLabel(sLabel);
        assertTrue(normalSelectList.getSelectedValue().matches(sLabel), "Validate SelectByLabel method");
    }

    @Test(groups = {"browser-tests"})
    @WebTest
    public void testSelectedLabels() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        String[] getSelectedLabels = normalSelectList.getSelectedLabels();
        assertTrue(getSelectedLabels.length > 0, "Validate SelectedLabels method");
        assertEquals(getSelectedLabels[0],sLabel, "Validate SelectedLabels method");
    }

    @Test(groups = {"browser-tests"})
    @WebTest
    public void testGetContentValues() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        String[] values = normalSelectList.getContentValue();
        assertTrue(values != null && values.length == 3, "Validate GetContentValues method");
    }

    @Test(groups = {"browser-tests"})
    @WebTest
    public void testGetContentLabels() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        String[] labels = normalSelectList.getContentLabel();
        assertTrue(labels != null && labels.length == 3, "Validate GetContentLabels method");
    }

    @Test(groups = {"browser-tests"})
    @WebTest
    public void testSelectOptions() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        String[] selectOptions = normalSelectList.getSelectOptions();
        assertTrue(selectOptions != null && selectOptions.length == 3, "Validate SelectOptions method");
    }

    @Test(groups = {"browser-tests"}, expectedExceptions = {UnsupportedOperationException.class})
    @WebTest
    public void testDeselectAll() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        /**
         * Only allow deselectAll on multi-select. Should throw an exception
         */
        normalSelectList.deselectAll();
    }

    @Test(groups = {"browser-tests"})
    @WebTest
    public void testDeselectAllMultiSelect() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());

        multiSelect.selectByValue(new String[]{"volvo", "saab", "audi"});
        assertTrue(multiSelect.getSelectedValues().length == 3, "Validate SelectedValues method");

        multiSelect.deselectAll();
        assertTrue(multiSelect.getSelectedValue() == null, "Validate SelectByValue method");
        assertTrue(multiSelect.getSelectedValues().length == 0, "Validate SelectedValues method");
    }

    @Test(groups = {"browser-tests"})
    @WebTest
    public void testDeselectByIndex() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());

        multiSelect.selectByIndex(new String[]{"0","3"});
        assertTrue(multiSelect.getSelectedValues().length == 2, "Validate SelectByIndex method");
        assertTrue(multiSelect.getSelectedValues()[0].matches("volvo"), "Validate SelectedValue method");

        multiSelect.deselectByIndex(0);
        assertTrue(multiSelect.getSelectedValues().length == 1, "Validate DeselectByIndex method");
        assertTrue(multiSelect.getSelectedValue().matches("audi"), "Validate DeselectByIndex method");
    }

    @Test(groups = {"browser-tests"})
    @WebTest
    public void testDeselectByValue() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());

        multiSelect.selectByValue(new String[]{"volvo", "audi"});
        assertTrue(multiSelect.getSelectedValues().length == 2, "Validate SelectByIndex method");
        assertTrue(multiSelect.getSelectedValues()[0].matches("volvo"), "Validate SelectedValue method");

        multiSelect.deselectByValue("volvo");
        assertTrue(multiSelect.getSelectedValues().length == 1, "Validate SelectedValues method");
        assertTrue(multiSelect.getSelectedValue().matches("audi"), "Validate SelectedValues method");
    }

    @Test(groups = {"browser-tests"})
    @WebTest
    public void testDeselectByLabel() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());

        multiSelect.selectByLabel(new String[]{"volvo", "audi"});
        assertTrue(multiSelect.getSelectedValues().length == 2, "Validate SelectByIndex method");
        assertTrue(multiSelect.getSelectedValues()[0].matches("volvo"), "Validate SelectedValue method");

        multiSelect.deselectByLabel("volvo");
        assertTrue(multiSelect.getSelectedValues().length == 1, "Validate SelectedValues method");
        assertTrue(multiSelect.getSelectedValue().matches("audi"), "Validate SelectedValues method");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testSelectNullLocator() {
        String locator = null;
        normalSelectList.select(locator);
    }
    
    @Test(groups="unit", expectedExceptions={IllegalArgumentException.class})
    public void testSelectEmptyLocator(){
        String locator = "";
        normalSelectList.select(locator);
    }

    @Test(groups = "unit", expectedExceptions = {IllegalArgumentException.class})
    public void testSelectInvalidLocator() {
        String locator = "foo";
        normalSelectList.select(locator);
    }

    @Test(groups = "unit", expectedExceptions = {NoSuchElementException.class})
    public void testSelectInvalidLocator1() {
        String locator = "foo=bar";
        normalSelectList.select(locator);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Config.setConfigProperty(Config.ConfigProperty.ENABLE_GUI_LOGGING, Boolean.FALSE.toString());
    }

}
