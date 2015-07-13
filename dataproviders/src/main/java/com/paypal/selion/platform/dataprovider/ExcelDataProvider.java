/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import com.paypal.selion.platform.dataprovider.impl.DefaultCustomType;

/**
 * This interface defines prototype to implement excel data provider implementation to parse excel format data file.
 */
public interface ExcelDataProvider extends SeLionDataProvider {

    /**
     * This method fetches a specific row from an excel sheet which can be identified using a key and returns the data
     * as an Object which can be cast back into the user's actual data type.
     *
     * @param key
     *            - A string that represents a key to search for in the excel sheet
     * @return - An Object which can be cast into the user's actual data type.
     */
    Object getSingleExcelRow(String key);

    /**
     * This method can be used to fetch a particular row from an excel sheet.
     *
     * @param index
     *            - The row number from the excel sheet that is to be read. For e.g., if you wanted to read the 2nd row
     *            (which is where your data exists) in your excel sheet, the value for index would be 1. <b>This method
     *            assumes that your excel sheet would have a header which it would EXCLUDE.</b> When specifying index
     *            value always remember to ignore the header, since this method will look for a particular row ignoring
     *            the header row.
     * @return - An object that represents the data for a given row in the excel sheet.
     */
    Object getSingleExcelRow(int index);

    /**
     * Using the specified rowIndex to search for the row from the specified Excel sheet, then return the row contents
     * in a list of string format.
     *
     * @param rowIndex
     *            - The row number from the excel sheet that is to be read. For e.g., if you wanted to read the 2nd row
     *            (which is where your data exists) in your excel sheet, the value for index would be 1. <b>This method
     *            assumes that your excel sheet would have a header which it would EXCLUDE.</b> When specifying index
     *            value always remember to ignore the header, since this method will look for a particular row ignoring
     *            the header row.
     * @param size
     *            - The number of columns to read, including empty and blank column.
     * @return List<String> String array contains the row data.
     */
    List<String> getRowContents(String sheetName, int rowIndex, int size);

    /**
     * Get all excel rows from a specified sheet.
     *
     * @param sheetName
     *            - A String that represents the Sheet name from which data is to be read
     * @param heading
     *            - If true, will return all rows along with the heading row. If false, will return all rows except the
     *            heading row.
     * @return - A List of {@link Row} that are read.
     */
    List<Row> getAllRawExcelRows(String sheetName, boolean heading);

    /**
     * @param type
     *            - A {@link DefaultCustomType} that represents custom types that need to be taken into consideration
     *            when generating an Object that represents every row of data from the excel sheet.
     */
    void addCustomTypes(DefaultCustomType type);
}
