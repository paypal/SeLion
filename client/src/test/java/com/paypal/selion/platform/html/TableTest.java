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
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/**
 * This class test the Table class methods
 *
 */

public class TableTest {
    Table testTable = new Table(TestObjectRepository.TABLE_LOCATOR.getValue());
    CheckBox selectionCheck = new CheckBox(TestObjectRepository.CHECKBOX_LOCATOR.getValue());


    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableTestGetRowCounts() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue((testTable.getNumberOfRows() == 4), "Validate getNumberOfRows method");

    }


    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableTestGetColumnCounts() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue((testTable.getNumberOfColumns() == 3), "Validate getNumberOfColumns method");

    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableTestGetCellVlaue() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue(testTable.getValueFromCell(2, 2).matches("Payment"), "Validate getCellValue method");
        assertTrue(testTable.getValueFromCell(3, 1).matches("Sep 9,2011"), "Validate getCellValue method");

    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableTestGetRowText() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        WebDriverWaitUtils.waitUntilElementIsPresent(testTable.getLocator());
        assertTrue(testTable.getRowText(1).contains("Date"), "Validate getRowText method");

    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableTestClickLink() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        testTable.clickLinkInCell(2, 3);
        String title = Grid.driver().getTitle();
        assertTrue(title.matches("Success"), "Validate click Link in table cell");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableTestCheckCheckBox() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        testTable.checkCheckboxInCell(4, 1);
        assertTrue(selectionCheck.isChecked(), "Validate Checkbox Check method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableTestUnCheckCheckBox() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        testTable.checkCheckboxInCell(4, 1);
        testTable.uncheckCheckboxInCell(4, 1);
        assertTrue(!selectionCheck.isChecked(), "Validate Checkbox Uncheck method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableTestGetRowIndex() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        String rowValue[] = { "Sep 9,2011", "Payment", "Completed" };
        assertTrue((testTable.getRowIndex(rowValue) == 2), "Validate get row index");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableTestGetDataStartIndex() {
        // test all the methods that are affected by the DataStartIndex
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertEquals(testTable.getDataStartIndex(), 2);
        String[] search = { "Data" };
        assertEquals(testTable.getRowIndex(search), -1);
        assertEquals(testTable.getNumberOfColumns(), 3);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableTestMultipleColumnRowsTbody() {
        // test a table that has the columns in the tbody and multiple rows of colums
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Table table = new Table(TestObjectRepository.TABLE_MULTIPLEHEADERS_LOCATOR.getValue());
        assertEquals(table.getDataStartIndex(), 3);
        String[] search = { "Color" };
        assertEquals(table.getRowIndex(search), -1);
        String[] searchContents = { "Cucumber" };
        assertEquals(table.getRowIndex(searchContents), 4);
        assertEquals(table.getNumberOfColumns(), 4);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableTestColumnsInThead() {
        // test a table that has the columns in the tbody and multiple rows of colums
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Table table = new Table(TestObjectRepository.TABLE_THEAD_LOCATOR.getValue());
        assertEquals(table.getDataStartIndex(), 1);
        String[] search = { "Color" };
        assertEquals(table.getRowIndex(search), -1);
        String[] searchContents = { "Cucumber" };
        assertEquals(table.getRowIndex(searchContents), 2);
        assertEquals(table.getNumberOfColumns(), 4);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void tableEmptyTest() {
        // test a table that has the columns in the tbody and empty data
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Table table = new Table(TestObjectRepository.TABLE_EMPTYTABLE_LOCATOR.getValue());
        assertEquals(table.getDataStartIndex(), 2);
        String[] search = { "Color" };
        assertEquals(table.getRowIndex(search), -1);
        assertEquals(table.getNumberOfColumns(), 0);
    }

}
