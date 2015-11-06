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

package com.paypal.selion.platform.dataprovider;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.paypal.selion.platform.dataprovider.filter.CustomKeyFilter;
import com.paypal.selion.platform.dataprovider.filter.SimpleIndexInclusionFilter;
import com.paypal.selion.platform.dataprovider.impl.DefaultCustomType;
import com.paypal.selion.platform.dataprovider.impl.ExcelDataProviderImpl;
import com.paypal.selion.platform.dataprovider.impl.FileSystemResource;
import com.paypal.selion.platform.dataprovider.impl.InputStreamResource;
import com.paypal.selion.platform.dataprovider.pojos.excel.AREA_CODE;
import com.paypal.selion.platform.dataprovider.pojos.excel.USER;
import com.paypal.selion.platform.utilities.FileAssistant;

public class ExcelDataProviderTest {

    public static class MyCustomClass {
        private final String name;

        public MyCustomClass(String name) {
            this.name = name;

        }

        public String getName() {
            return name;
        }
    }

    private static String fileName = "src/test/resources/User.xlsx";
    private static final String assertFailedMsg = "Assert condition failed.";
    private ExcelDataProvider dataSource;

    @BeforeClass(alwaysRun = true)
    public void init() throws IOException {
        DataResource resource = new FileSystemResource(fileName, USER.class);
        dataSource = (ExcelDataProvider) DataProviderFactory.getDataProvider(resource);
    }

    public static class ColorsData {
        /**
         * @return the productName
         */
        public String getProductName() {
            return productName;
        }

        /**
         * @param productName
         *            the productName to set
         */
        public void setProductName(String productName) {
            this.productName = productName;
        }

        /**
         * @return the whatColor
         */
        public Colors getWhatColor() {
            return whatColor;
        }

        /**
         * @param whatColor
         *            the whatColor to set
         */
        public void setWhatColor(Colors whatColor) {
            this.whatColor = whatColor;
        }

        private String productName;
        private Colors whatColor;

    }

    public static class TweakedColorsData {
        /**
         * @return the productName
         */
        public String getProductName() {
            return productName;
        }

        /**
         * @param productName
         *            the productName to set
         */
        public void setProductName(String productName) {
            this.productName = productName;
        }

        /**
         * @return the whatColor
         */
        public List<String> getWhatColor() {
            return whatColor;
        }

        /**
         * @param whatColor
         *            the whatColor to set
         */
        public void setWhatColor(List<String> whatColor) {
            this.whatColor = whatColor;
        }

        private String productName;
        private List<String> whatColor;
    }

    @Test(groups = "unit")
    public void testInjectCustomData() throws IOException, NoSuchMethodException, SecurityException {
        DataResource resource = new InputStreamResource(new BufferedInputStream(
                FileAssistant.loadFile("src/test/resources/sampleData.xlsx")), ColorsData.class, "xlsx");
        ExcelDataProvider provider = (ExcelDataProvider) DataProviderFactory.getDataProvider(resource);
        DefaultCustomType type = new DefaultCustomType(Colors.class, Colors.class.getMethod("whatColor", String.class));
        provider.addCustomTypes(type);
        Object[][] data = provider.getAllData();
        List<Colors> expectedValues = Arrays.asList(Colors.values());
        assertTrue(data.length == 3);
        for (Object[] eachObjectRow : data) {
            ColorsData tData = (ColorsData) eachObjectRow[0];
            assertTrue(expectedValues.contains(tData.whatColor));
        }
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testBehaviorWhenPojoClassHasInterfaces() throws IOException {
        DataResource resource = new InputStreamResource(new BufferedInputStream(
                FileAssistant.loadFile("src/test/resources/sampleData.xlsx")), TweakedColorsData.class, "xlsx");
        SeLionDataProvider provider = DataProviderFactory.getDataProvider(resource);
        provider.getAllData();
    }

    @Test(groups = "unit")
    public void testGetSingleExcelRowWithIndexFirstRowCondition() {
        Object[][] allUsers = new Object[][] { { dataSource.getSingleExcelRow(1) } };
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "Thomas" }, fetchedNames.toArray()), assertFailedMsg);
    }

    @Test(groups = "unit")
    public void testGetSingleExcelRowWithIndex() {
        Object[][] allUsers = new Object[][] { { dataSource.getSingleExcelRow(4) } };
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "suri" }, fetchedNames.toArray()), assertFailedMsg);
    }

    @Test(groups = "unit")
    public void testGetSingleExcelRowWithKeyFirstRowCondition() {
        Object[][] allUsers = new Object[][] { { dataSource.getSingleExcelRow("tom") } };
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "Thomas" }, fetchedNames.toArray()), assertFailedMsg);
    }

    @Test(groups = "unit")
    public void testGetSingleExcelRowWithKey() {
        Object[][] allUsers = new Object[][] { { dataSource.getSingleExcelRow("3") } };
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "suri" }, fetchedNames.toArray()), assertFailedMsg);
    }

    @Test(expectedExceptions = { DataProviderException.class }, groups = "unit")
    public void testGetSingleExcelRowWithInvalidKey() {
        dataSource.getSingleExcelRow("selion");
    }

    @Test(groups = "unit", expectedExceptions = { DataProviderException.class })
    public void testGetSingleExcelRowWithInvalidIndex() {
        assertNull(dataSource.getSingleExcelRow(100), "Returned data should have been null");
    }

    @Test(expectedExceptions = { DataProviderException.class }, groups = "unit")
    public void testGetExcelRowsNegativeConditions() throws IOException {
        dataSource.getDataByIndex("2~3");
    }

    @Test(groups = "unit")
    public void testGetExcelRowsWithKeys() {
        Object[][] allUsers = dataSource.getDataByKeys(new String[] { "tom", "binh" });
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "Thomas", "binh" }, fetchedNames.toArray()), assertFailedMsg);
    }

    @Test(expectedExceptions = { DataProviderException.class }, groups = "unit")
    public void testGetExcelRowsWithInvalidKeys() {
        dataSource.getDataByKeys(new String[] { "selion" });

    }

    @Test(groups = "unit")
    public void testGetExcelRowsWithIndividualIndexes() throws IOException {
        Object[][] allUsers = dataSource.getDataByIndex("2,3");
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "rama", "binh" }, fetchedNames.toArray()), assertFailedMsg);
    }

    @Test(groups = "unit")
    public void testGetExcelRowsWithIndividualIndexesArray() throws IOException {
        int[] index = { 2, 3 };
        Object[][] allUsers = dataSource.getDataByIndex(index);
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "rama", "binh" }, fetchedNames.toArray()), assertFailedMsg);
    }

    public List<String> transformExcelDataIntoList(Object[][] allUsers) {
        List<String> fetchedNames = new ArrayList<String>();
        for (Object[] object : allUsers) {
            USER user = (USER) object[0];
            fetchedNames.add(user.getName());
        }
        return fetchedNames;
    }

    public List<String> transformExcelDataIntoList(Iterator<Object[]> allUsers) {
        List<String> fetchedNames = new ArrayList<String>();
        while (allUsers.hasNext()) {
            USER user = (USER) allUsers.next()[0];
            fetchedNames.add(user.getName());
        }
        return fetchedNames;
    }

    @Test(groups = "unit")
    public void testGetExcelRowsWithRangeOfIndexes() throws IOException {
        Object[][] allUsers = dataSource.getDataByIndex("1-2");
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "Thomas", "rama" }, fetchedNames.toArray()), assertFailedMsg);
    }

    @Test(groups = "unit")
    public void testGetExcelRowsWithIndividualAndRangeOfIndexes() throws IOException {
        Object[][] allUsers = dataSource.getDataByIndex("1-2,4,6");
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "Thomas", "rama", "suri", "suri" }, fetchedNames.toArray()),
                assertFailedMsg);
    }

    @Test(groups = "unit", expectedExceptions = { DataProviderException.class })
    public void testGetExcelRowsWhereRowIsNull() throws IOException {
        Object[][] allUsers = dataSource.getDataByIndex("5");
        assertNull(allUsers[0][0], assertFailedMsg);

    }

    @Test(groups = "unit")
    public void testGetExcelRowsWithSimpleInclusionDataProviderFilterWithRangeOfIndexes() throws IOException {
        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter("1-2");
        Iterator<Object[]> allUsers = dataSource.getDataByFilter(filter);
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "Thomas", "rama" }, fetchedNames.toArray()), assertFailedMsg);
    }

    @Test(groups = "unit")
    public void testGetExcelRowsWithSimpleInclusionDataProviderFilterWithIndividualAndRangeOfIndexes() throws IOException {
        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter("1-2,4,5");
        Iterator<Object[]> allUsers = dataSource.getDataByFilter(filter);
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "Thomas", "rama", "suri", "suri" }, fetchedNames.toArray()),
                assertFailedMsg);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class },
            expectedExceptionsMessageRegExp = "Please provide valid indexes for filtering")
    public void testGetExcelRowsWithSimpleInclusionDataProviderFilterWithNullIndexes() {
        // Passing just null will give compilation error.
        new SimpleIndexInclusionFilter((String) null);
    }

    @Test(groups = "unit")
    public void testGetExcelRowsWithSimpleInclusionDataProviderFilterWhereNoValuesReturns() throws IOException {
        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter("6");
        Iterator<Object[]> allUsers = dataSource.getDataByFilter(filter);
        assertFalse(allUsers.hasNext(), assertFailedMsg);
    }

    @Test(groups = "unit")
    public void testGetExcelRowsWithCustomKeyInclusionDataProviderFilterWithAccountNumber() throws IOException {
        CustomKeyFilter filter = new CustomKeyFilter("accountNumber", "78901,124567");
        Iterator<Object[]> allUsers = dataSource.getDataByFilter(filter);
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "Thomas", "binh" }, fetchedNames.toArray()), assertFailedMsg);
    }

    @Test(groups = "unit")
    public void testGetExcelRowsWithCustomKeyInclusionDataProviderFilterWithPhoneNumber() throws IOException {
        CustomKeyFilter filter = new CustomKeyFilter("phoneNumber", "1-408-666-5508,1-408-225-8040,1-714-666-0043");
        Iterator<Object[]> allUsers = dataSource.getDataByFilter(filter);
        List<String> fetchedNames = transformExcelDataIntoList(allUsers);
        assertTrue(arrayComparer(new String[] { "Thomas", "rama", "binh" }, fetchedNames.toArray()), assertFailedMsg);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class },
            expectedExceptionsMessageRegExp = "Please specify values to use for filtering.")
    public void testGetExcelRowsWithCustomKeyInclusionDataProviderFilterWithNullFilterKeyValues() {
        @SuppressWarnings("unused")
        CustomKeyFilter filter = new CustomKeyFilter("phoneNumber", null);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class },
            expectedExceptionsMessageRegExp = "Please specify a valid key.")
    public void testGetExcelRowsWithCustomKeyInclusionDataProviderFilterWithNullFilterKey() {
        new CustomKeyFilter(null, "1-408-666-5508,1-408-225-8040,1-714-666-0043");
    }

    private synchronized boolean arrayComparer(String[] expected, Object[] actual) {
        boolean isSame = false;
        for (int i = 0; i < expected.length; i++) {
            isSame = expected[i].matches((String) actual[i]);
        }
        return isSame;
    }

    @Test(groups = "unit")
    public void testGetAllExcelRows() throws IOException {
        Object[][] allUsers = dataSource.getAllData();
        assertNotNull(allUsers, "Data read from excel sheet failed");
        // Reduce 2 from the actual count, since the test excel sheet has 1 blank row
        // and 1 row for header
        assertEquals(allUsers.length, getRowCountFromSheet(USER.class.getSimpleName()) - 1,
                "Failed reading all rows from spreadsheet");
    }

    @Test(groups = "unit")
    public void testGetAllRowsAsHashTable() {
        Hashtable<String, Object> allValues = dataSource.getDataAsHashtable();
        assertNotNull(allValues, "Data read from excel sheet failed");
        assertEquals(allValues.size(), getRowCountFromSheet(USER.class.getSimpleName()) - 2,
                "Failed reading all rows from spreadsheet");
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = "unit")
    public void testGetAllRowsAsHashTableInvalidSheetName() throws IOException {
        Student student = new ExcelDataProviderTest().new Student();
        DataResource resource = new FileSystemResource(fileName, student.getClass());
        SeLionDataProvider provider = DataProviderFactory.getDataProvider(resource);
        provider.getDataAsHashtable();
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = "unit")
    public void testGetallExcelRowsInvalidSheetName() throws IOException {
        Student student = new ExcelDataProviderTest().new Student();
        DataResource resource = new FileSystemResource(fileName, student.getClass());
        SeLionDataProvider provider = DataProviderFactory.getDataProvider(resource);
        provider.getAllData();
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = "unit")
    public void negativeTestsWithExcelDataProviderConstructor() throws IOException {
        new ExcelDataProviderImpl(null);
    }

    @Test(expectedExceptions = { IOException.class }, groups = "unit")
    public void negativeTestsInvalidFileName() throws IOException {
        DataResource resource = new FileSystemResource("IdontExist.xls", ColorsData.class);
        DataProviderFactory.getDataProvider(resource);
    }

    @Test(groups = "unit")
    public void getAllRowsAsHash() {
        assertNotNull(dataSource.getDataAsHashtable());
    }

    @Test(groups = "unit")
    public void getSheetAsHashByKeyTest1() {
        USER user = (USER) dataSource.getDataAsHashtable().get("binh");
        assertData(user);
    }

    @Test(groups = "unit")
    public void getSheetAsHashByKeyTest2() {
        USER user = (USER) dataSource.getDataAsHashtable().get("1");
        assertData(user);
    }

    @DataProvider(parallel = true)
    public Object[][] getExcelDataRowsByKeys() {
        return dataSource.getDataByKeys(new String[] { "1", "binh" });

    }

    @Test(dataProvider = "getExcelDataRowsByKeys", groups = "unit")
    public void getExcelDataRowsByKeys(USER myData) {
        assertData(myData);
        for (AREA_CODE eachArea : myData.getAreaCode()) {
            assertNotNull(eachArea.getAreaCode(), "Area code should not have been null");
        }
    }

    @DataProvider(parallel = true)
    public Object[][] getExcelDataRowsByIndexes() throws IOException {
        return dataSource.getDataByIndex("2, 3-4");

    }

    @Test(dataProvider = "getExcelDataRowsByIndexes", groups = "unit")
    public void getExcelDataRowsByIndexes(USER myData) {
        assertData(myData);
        for (AREA_CODE eachArea : myData.getAreaCode()) {
            assertNotNull(eachArea.getAreaCode(), "Area code should not have been null");
        }

    }

    @DataProvider(parallel = true)
    public Object[][] getAllExcelRows() throws IOException {
        return dataSource.getAllData();

    }

    @Test(dataProvider = "getAllExcelRows", groups = "unit")
    public void getAllExcelRows(USER myData) {
        assertData(myData);
        for (AREA_CODE eachArea : myData.getAreaCode()) {
            assertNotNull(eachArea.getAreaCode(), "Area code should not have been null");
        }
    }

    private void assertData(USER data) {
        assertNotNull(data);
        assertNotNull(data.getName());
        assertNotNull(data.getPassword());
        assertNotNull(data.getAreaCode()[0].getAreaCode());
    }

    private int getRowCountFromSheet(String sheetName) {
        int rowCount = 0;
        try {
            XSSFWorkbook workBook = new XSSFWorkbook(fileName);
            rowCount = workBook.getSheet(sheetName).getPhysicalNumberOfRows();
        } catch (IOException e) {
            // do nothing with the exception here
        }

        return rowCount;
    }

    public class Student {
        private String studentName;

        public void setStudentName(String name) {
            this.studentName = name;
        }

        public String getStudentName() {
            return this.studentName;
        }
    }

    @Test(groups = "unit")
    public void testGetRowContents() {
        Field[] fields = USER.class.getDeclaredFields();

        List<String> rowContents = dataSource.getRowContents("User", 3, fields.length);
        assertNotNull(rowContents);
        assertTrue("[rama, abc123, 123456, 100.00, ph1,ph2,ph3, bnk1, 1-408-225-8040, 12, true, 12.5, 167045, 12.5, 2]"
                .equals(rowContents.toString()));
    }

    @Test(groups = "unit")
    public void testGetAllRawExcelRows() {
        List<Row> rows = dataSource.getAllRawExcelRows("User", true);
        assertNotNull(rows);

        Row singleRow = rows.get(3);
        assertTrue(singleRow.getCell(1).getStringCellValue().equals("binh"));
        assertTrue(singleRow.getCell(2).getStringCellValue().equals("abc124"));
    }
}
