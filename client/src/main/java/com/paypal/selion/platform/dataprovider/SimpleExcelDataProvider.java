/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.paypal.selion.platform.dataprovider.filter.DataProviderFilter;

/**
 * This class provide several methods to retrieve test data from an Excel workbook. Users can get a single row of data
 * by providing the excel filename, the data sheet name, and they key. Or get the whole data sheet by providing excel
 * file name and the data sheet name.
 * 
 * The first column is reserved for keys which are not part of the actual test data. Read <a
 * href="http://paypal.github.io/SeLion/html/documentation.html#excel-data-provider">Excel Data Provider
 * Documentation</a> for more information on the required format of the Excel sheet.
 * 
 */

public class SimpleExcelDataProvider extends AbstractExcelDataProvider {

    /**
     * The constructor will use the path name and the file name of the Excel workbook to initialize the input stream
     * before the stream is being used by several methods to get the test data from the Excel workbook.
     * 
     * If pathName is not null then the users deliberately specified the resource file in other location than the
     * classpaths. If pathName is null, then the resouce file can be found using the classpath.
     * 
     * <h3>Sample usage:</h3>
     * 
     * <pre>
     * String   pathName = "src/test/java";
     * String   fileName = "DataReaderTest.xls"
     * LOCAL_DATA myData = new LOCAL_DATA();
     * Object [][] myObj;
     * 
     * // To get a single row of excel sheet using a key associated with the data
     * myData = (LOCAL_DATA) SimpleExcelDataProvider dataProvider = new SimpleExcelDataProvider(
     *                          pathName, fileName).getSingleExcelRow(myData, "4");
     * 
     * // To get a whole sheet of excel data. This will not need key.
     * myObj =  new SimpleExcelDataProvider(pathName,
     *                 fileName).getAllExcelRows(myData);
     * myData = (LOCAL_DATA)myObj[1][0];
     * System.out.println(myObj.seller.bank[0].name);
     * </pre>
     * 
     * @param pathName
     *            the path where the excel file is located.
     * @param fileName
     *            the name of the excel file to be read.
     * @throws IOException
     */
    public SimpleExcelDataProvider(String pathName, String fileName) throws IOException {
        super(pathName, fileName);
    }

    /**
     * Use this constructor when a file that is available in the classpath is to be read by the SimpleExcelDataProvider
     * for supporting Data Driven Tests.
     * 
     * @param fileName
     *            the name of the excel file to be read.
     * @throws IOException
     */
    public SimpleExcelDataProvider(String fileName) throws IOException {
        this(null, fileName);
    }

    /**
     * This function will read all rows of a specified excel sheet and store the data to a hash table. Users can get a
     * row of data from the hash table by call a get with a specified key. This excel reader function is for users who
     * want to control the data feed to the test cases manually without the benefit of TestNG DataProvider. <br>
     * <br>
     * <b>Note:</b> Unlike {@link SimpleExcelDataProvider#getAllExcelRows(Object)} this method will skip ALL blank rows
     * that may occur in between data in the spreadsheet. <br>
     * Hence the number of rows that are fetched by this method and
     * {@link SimpleExcelDataProvider#getAllExcelRows(Object)} <b>NEED NOT</b> be the same.
     * 
     * <h3>Example:</h3>
     * 
     * <pre>
     * ...
     * MyDataStructure myObj = new MyDataStructure();
     * HashTable&lt;String, Object&gt; myExcelTableData;
     * ...
     * myExceltableData = SimpleExcelDataProvider.getAllRowAsHasTable(myObj);
     * </pre>
     * 
     * @param myObj
     *            the user defined type object which provide details structure to this function.
     * @return an object of type {@link Hashtable} that represents the excel sheet data in form of hashTable.
     */
    public Hashtable<String, Object> getAllRowsAsHashTable(Object myObj) {
        logger.entering(myObj);
        Hashtable<String, Object> hashTable = new Hashtable<>();

        Sheet sheet = excelReader.fetchSheet(myObj.getClass().getSimpleName());
        int numRows = sheet.getPhysicalNumberOfRows();

        for (int i = 2; i <= numRows; i++) {
            Row row = sheet.getRow(i - 1);
            if ((row != null) && (row.getCell(0) != null)) {
                Object obj = getSingleExcelRow(myObj, i, false);
                String key = row.getCell(0).toString();
                if ((key != null) && (obj != null)) {
                    hashTable.put(key, obj);
                }
            }
        }
        logger.exiting(hashTable);
        return hashTable;
    }

    /**
     * This method fetches a specific row from an excel sheet which can be identified using a key and returns the data
     * as an Object which can be cast back into the user's actual data type.
     * 
     * @param userObj
     *            - An Object into which data is to be packed into
     * @param key
     *            - A string that represents a key to search for in the excel sheet
     * @return - An Object which can be cast into the user's actual data type.
     * 
     */
    @Override
    public Object getSingleExcelRow(Object userObj, String key) {
        return getSingleExcelRow(userObj, key, true);
    }

    /**
     * This method can be used to fetch a particular row from an excel sheet.
     * 
     * @param userObj
     *            - The User defined object into which the data is to be packed into.
     * @param index
     *            - The row number from the excel sheet that is to be read. For e.g., if you wanted to read the 2nd row
     *            (which is where your data exists) in your excel sheet, the value for index would be 1. <b>This method
     *            assumes that your excel sheet would have a header which it would EXCLUDE.</b> When specifying index
     *            value always remember to ignore the header, since this method will look for a particular row ignoring
     *            the header row.
     * @return - An object that represents the data for a given row in the excel sheet.
     */
    @Override
    public Object getSingleExcelRow(Object userObj, int index) {
        return getSingleExcelRow(userObj, index, true);
    }

    /**
     * This function will use the input string representing the indexes to collect and return the correct excel sheet
     * data rows as two dimensional object to be used as TestNG DataProvider.
     * 
     * @param myData
     *            the user defined type object which provide details structure to this function.
     * @param indexes
     *            the string represent the keys for the search and return the wanted rows. It is in the format of: <li>
     *            "1, 2, 3" for individual indexes. <li>"1-4, 6-8, 9-10" for ranges of indexes. <li>
     *            "1, 3, 5-7, 10, 12-14" for mixing individual and range of indexes.
     * @return Object[][] Two dimensional object to be used with TestNG DataProvider
     */
    @Override
    public Object[][] getExcelRows(Object myData, String indexes) {
        logger.entering(new Object[] { myData, indexes });
        int[] arrayIndex;
        try {
            arrayIndex = DataProviderHelper.parseIndexString(indexes);
        } catch (DataProviderException e) {
            throw new DataProviderException(e.getMessage(), e);
        }

        Object[][] obj = new Object[arrayIndex.length][1];
        for (int i = 0; i < arrayIndex.length; i++) {
            int actualIndex = arrayIndex[i] + 1;
            obj[i][0] = getSingleExcelRow(myData, actualIndex, false);

        }
        logger.exiting(obj);
        return obj;
    }

    /**
     * This function will use the input string representing the keys to collect and return the correct excel sheet data
     * rows as two dimensional object to be used as TestNG DataProvider.
     * 
     * @param myObj
     *            the user defined type object which provides details structure to this function.
     * @param keys
     *            the string represents the list of key for the search and return the wanted row. It is in the format of
     *            {"row1", "row3", "row5"}
     * @return Object[][] two dimensional object to be used with TestNG DataProvider
     */
    @Override
    public Object[][] getExcelRows(Object myObj, String[] keys) {
        logger.entering(new Object[] { myObj, keys });
        Object[][] obj = new Object[keys.length][1];

        for (int i = 0; i < keys.length; i++) {
            obj[i][0] = getSingleExcelRow(myObj, keys[i], true);
        }
        logger.exiting(obj);
        return obj;
    }

    /**
     * This function will read the whole excel sheet and map the data into two-dimensional array of object which is
     * compatible with TestNG DataProvider to provide real test data driven development. This function will ignore all
     * rows in which keys are preceded by "#" as a comment character.
     * 
     * For the function to work, the sheet names have to be exactly named as the user defined data type. In the example
     * below, there must be a sheet name "LOCAL_DATA" in the workbook.
     * 
     * <h3>Example how to use TestNG DataProvider:</h3>
     * 
     * <pre>
     * '@DataProvider(name = "dataProvider1")'
     * public Object[][] createData1() throws Exception {
     * 
     *     // Declare your objects
     *     String pathName = "src/test/java/com/paypal/test/datareader";
     *     String fileName = "DataReader.xls";
     * 
     *     // Declare your data block
     *     LOCAL_DATA myData = new LOCAL_DATA();
     * 
     *     // Pass your data block to "getAllExcelRows"
     *     Object[][] object = new SimpleExcelDataProvider(pathName, fileName)
     *         .getAllExcelRows(myData);
     * 
     *     // return the two-dimensional array object
     *     return object;
     * }
     * 
     * // Specify our TestNG DataProvider
     * '@Test(dataProvider = "dataProvider1")'
     * public void verifyLocalData1(LOCAL_DATA data) {
     *     // Your data will be distribute to your test case
     *     // one row per instance, and all can be run at the same time.
     *     System.out.println("Name: " + data.name);
     *     System.out.println("Password: " + data.password);
     *     System.out.println("the bank: " + data.bank.bankName);
     * 
     *     System.out.println("Ph1: " + data.phone.areaCode);
     *     System.out.println("Ph2: " + data.cell.areaCode);
     *     System.out.println("Bank Address: " + data.bank.address.street);
     *     System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
     * }
     * </pre>
     * 
     * @param myObj
     *            the user defined type object which provide details structure to this function.
     * @return Object[][] a two-dimensional object to be used with TestNG DataProvider
     */
    @Override
    public Object[][] getAllExcelRows(Object myObj) {
        logger.entering(myObj);
        int i;
        Object[][] obj = null;
        Field[] fields = myObj.getClass().getDeclaredFields();

        // Extracting number of rows of data to read
        // Notice that numRows is returning the actual
        // number of non-blank rows. Thus if there are
        // blank rows in the sheet then we will miss
        // some last rows of data.
        List<Row> rowToBeRead = excelReader.getAllExcelRows(myObj.getClass().getSimpleName(), false);
        if (!rowToBeRead.isEmpty()) {
            i = 0;
            obj = new Object[rowToBeRead.size()][1];
            for (Row row : rowToBeRead) {
                List<String> excelRowData = excelReader.getRowContents(row, fields.length);
                if (excelRowData.size() != 0) {
                    try {
                        obj[i++][0] = prepareObject(myObj, fields, excelRowData);
                    } catch (IllegalAccessException e) {
                        throw new DataProviderException("Unable to create instance of type '"
                                + myObj.getClass().getName() + "'", e);
                    }
                }
            }
        }
        logger.exiting(obj);
        return obj;
    }

    /**
     * Gets data from Excel sheet by applying the given filter.
     * 
     * @param myObj
     *            the user defined type object which provide details structure to this function.
     * @param dataFilter
     *            an implementation class of {@link DataProviderFilter}
     * @return An iterator over a collection of Object Array to be used with TestNG DataProvider
     */
    public Iterator<Object[]> getExcelRowsByFilter(Object myObj, DataProviderFilter dataFilter) {
        logger.entering(new Object[] { myObj, dataFilter });
        List<Object[]> objs = new ArrayList<>();
        Field[] fields = myObj.getClass().getDeclaredFields();

        // Extracting number of rows of data to read
        // Notice that numRows is returning the actual number of non-blank rows.
        // Thus if there are blank rows in the sheet then we will miss some last rows of data.
        List<Row> rowToBeRead = excelReader.getAllExcelRows(myObj.getClass().getSimpleName(), false);
        for (Row row : rowToBeRead) {
            List<String> excelRowData = excelReader.getRowContents(row, fields.length);
            if (excelRowData.size() != 0) {
                try {
                    Object temp = prepareObject(myObj, fields, excelRowData);
                    if (dataFilter.filter(temp)) {
                        objs.add(new Object[] { temp });
                    }
                } catch (IllegalAccessException e) {
                    throw new DataProviderException("Unable to create instance of type '" + myObj.getClass().getName()
                            + "'", e);
                } catch (DataProviderException e) {
                    throw new DataProviderException(e.getMessage(), e);
                }
            }
        }
        logger.exiting(objs.iterator());
        return objs.iterator();
    }

}