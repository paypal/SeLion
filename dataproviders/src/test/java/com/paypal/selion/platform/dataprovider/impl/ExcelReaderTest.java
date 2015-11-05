/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

package com.paypal.selion.platform.dataprovider.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.platform.dataprovider.impl.ExcelReader;
import com.paypal.selion.platform.dataprovider.pojos.excel.USER;

public class ExcelReaderTest {
    private static String fileName_User = "src/test/resources/User.xlsx";
    private ExcelReader excelReader;

    @BeforeClass(alwaysRun = true)
    public void init() throws IOException {
        excelReader = new ExcelReader(new FileSystemResource(fileName_User));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testNegativeCaseEmptyFileName() throws IOException {
        new ExcelReader(new FileSystemResource(""));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testNegativeCaseNullFileName() throws IOException {
        new ExcelReader(null);
    }

    @Test(groups = "unit", expectedExceptions = { IOException.class })
    public void testFileDoesntExist() throws IOException {
        new ExcelReader(new FileSystemResource("YouCantSeeMe.xls"));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInvalidSheetName() {
        excelReader.getAllExcelRows("IAmNotThere", false);
    }

    @Test(groups = "unit")
    public void testGetAllExcelRowsSkippingOfRows() {
        assertTrue(excelReader.getAllExcelRows("Sheet1", false).isEmpty());
    }

    @Test(groups = "unit")
    public void testGetAllExcelRowsAndCheckIfHeadingRowIsRead() {
        assertTrue(excelReader.getAllExcelRows("Sheet1", true).size() == 1);
    }

    @Test(groups = "unit")
    public void testGetAllExcelRows() {
        List<Row> row = excelReader.getAllExcelRows("USER", true);
        assertEquals(row.size(), 6, "Failed reading all rows from spreadsheet");
        row = excelReader.getAllExcelRows("ADDRESS", false);
        assertEquals(row.size(), 3, "Failed reading all rows from spreadsheet");
    }

    @Test(groups = "unit")
    public void testGetAbsoluteSingeExcelRow() {
        Row row = excelReader.getAbsoluteSingeExcelRow("USER", 2);
        assertNotNull(row);
        assertTrue("[rama, abc123, 123456, 100.00, ph1,ph2,ph3, bnk1, 1-408-225-8040, 12, true, 12.5, 167045, 12.5, 2, null]"
                .equals(excelReader.getRowContents(row, row.getLastCellNum()).toString()));
    }

    @Test(groups = "unit")
    public void testSheetExist() {
        assertTrue(excelReader.sheetExists("USER"));
        assertFalse(excelReader.sheetExists("RandomSheet"));
    }

    @Test(groups = "unit")
    public void testGetRowContents() {
        Field[] fields = USER.class.getDeclaredFields();

        List<String> rowContents = excelReader.getRowContents("USER", 3, fields.length);
        assertNotNull(rowContents);
        assertTrue("[rama, abc123, 123456, 100.00, ph1,ph2,ph3, bnk1, 1-408-225-8040, 12, true, 12.5, 167045, 12.5, 2]"
                .equals(rowContents.toString()));
        // with input as Row
        Row row = excelReader.getAbsoluteSingeExcelRow("User", 3);
        rowContents = excelReader.getRowContents(row, fields.length);
        assertNotNull(rowContents);
        assertTrue("[binh, abc124, 124567, 200.50, ph4,ph5, ph6, bnk2, 1-714-666-0043, 14, true, 13.5, 1234, 13.5, 4]"
                .equals(rowContents.toString()));
    }

    @Test(groups = "unit")
    public void testGetRowIndex() {
        int index = excelReader.getRowIndex(USER.class.getSimpleName(), "tom");
        assertEquals(index, 1);
    }

    @Test(groups = "unit")
    public void testGetRowIndexInvalidKey() {
        int index = excelReader.getRowIndex(USER.class.getSimpleName(), "harry");
        assertEquals(index, -1);
    }

    @Test(groups = "unit")
    public void testGetRowIndexKeyExistsButRowIsMarkedToBeExcluded() {
        int index = excelReader.getRowIndex("Sheet1", "#1");
        assertEquals(index, -1);
    }
}
