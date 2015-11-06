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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.dataprovider.DataResource;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A utility class to read rows of excel files.
 */
class ExcelReader {

    /**
     * In memory representation of whole excel file.
     */
    private Workbook workBook;

    /**
     * Logging object
     */
    private final SimpleLogger logger = SeLionLogger.getLogger();

    /**
     * Use this constructor when a file that is available in the classpath is to be read by the ExcelDataProvider for
     * supporting Data Driven Tests.
     * 
     * @param fileStream
     *            the stream of the excel file to be read.
     * @throws IOException
     *             If the file cannot be located, or cannot read by the method.
     */
    public ExcelReader(DataResource resource) throws IOException {

        logger.entering(resource);

        if (resource == null || StringUtils.isBlank(resource.getType())) {
            throw new IllegalArgumentException("resource cannot be null/empty");
        }

        try {
            if (resource.getType().toLowerCase().endsWith("xlsx")) {
                workBook = new XSSFWorkbook(resource.getInputStream());
            } else if (resource.getType().toLowerCase().endsWith("xls")) {
                workBook = new HSSFWorkbook(resource.getInputStream());
            }
            IOUtils.closeQuietly(resource.getInputStream());
        } catch (IOException e) {
            // We are never going to end up with an IOException because FileAssistant.loadFile() tests this part
            // explicitly and throws a RuntimeException. So no point in throwing an IOException when it is never
            // going to be raised.
            // but still lets at-least throw back a runtime exception and get out of the checked exception mode
            // which is basically going to spoof a user's test.
            throw new RuntimeException(e); // NOSONAR
        } catch (RuntimeException e) {
            // This is bad. But unfortunately only FileAssistant will detect if the file was valid or not.
            // and it tells this by throwing a RuntimeException. But downstream our clients are expecting
            // to get an IOException. So lets throw them one
            throw new IOException(e);
        }

        logger.exiting();
    }

    /**
     * Get all excel rows from a specified sheet.
     * 
     * @param sheetName
     *             A String that represents the Sheet name
     * @param heading
     *            If true, will return all rows along with the heading row. If false, will return all rows
     *            except the heading row.
     * @return rows that are read.
     */
    public List<Row> getAllExcelRows(String sheetName, boolean heading) {
        logger.entering(new Object[] { sheetName, heading });
        Sheet sheet = fetchSheet(sheetName);
        int numRows = sheet.getPhysicalNumberOfRows();
        List<Row> rows = new ArrayList<Row>();
        int currentRow = 1;
        if (heading) {
            currentRow = 0;
        }
        int rowCount = 1;
        while (currentRow <= numRows) {
            Row row = sheet.getRow(currentRow);
            if (row != null) {
                Cell cell = row.getCell(0);
                if (cell != null) {
                    // Did the user mark the current row to be excluded by adding a # ?
                    if (!cell.toString().contains("#")) {
                        rows.add(row);
                    }
                    rowCount = rowCount + 1;
                }
            }
            currentRow = currentRow + 1;
        }
        logger.exiting(rows);
        return rows;
    }

    /**
     * A utility method, which returns {@link Sheet} for a given sheet name.
     * 
     * @param sheetName - A string that represents a valid sheet name.
     * @return - An object of type {@link Sheet}
     */
    protected Sheet fetchSheet(String sheetName) {
        logger.entering(sheetName);
        Sheet sheet = workBook.getSheet(sheetName);
        if (sheet == null) {
            IllegalArgumentException e = new IllegalArgumentException("Sheet '" + sheetName + "' is not found.");
            throw e;
        }
        logger.exiting(sheet);
        return sheet;
    }

    /**
     * A Utility method to find if a sheet exists in the workbook
     * 
     * @param sheetName - A String that represents the Sheet name
     * @return true if the sheet exists, false otherwise
     */
    public boolean sheetExists(String sheetName) {
        return (workBook.getSheet(sheetName) != null);
    }

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
     * @return List<String> String array contains the read data.
     */
    public List<String> getRowContents(String sheetName, int rowIndex, int size) {
        logger.entering(new Object[] { sheetName, rowIndex, size });
        Sheet sheet = fetchSheet(sheetName);

        int actualExcelRow = rowIndex - 1;
        Row row = sheet.getRow(actualExcelRow);

        List<String> rowData = getRowContents(row, size);

        logger.exiting(rowData);
        return rowData;
    }

    /**
     * Return the row contents of the specified row in a list of string format.
     * 
     * @param size - The number of columns to read, including empty and blank column.
     * @return List<String> String array contains the read data.
     */
    public List<String> getRowContents(Row row, int size) {
        logger.entering(new Object[] { row, size });
        List<String> rowData = new ArrayList<String>();
        if (row != null) {
            for (int i = 1; i <= size; i++) {
                String data = null;
                if (row.getCell(i) != null) {
                    data = row.getCell(i).toString();
                }
                rowData.add(data);
            }
        }
        logger.exiting(rowData);
        return rowData;
    }

    /**
     * Search for the input key from the specified sheet name and return the index position of the row that contained
     * the key
     * 
     * @param sheetName
     *            - A String that represents the Sheet name from which data is to be read
     * @param key
     *            - A String that represents the key for the row for which search is being done.
     * @return - An int that represents the row number for which the key matches. Returns -1 if the search did not yield
     *         any results.
     * 
     */
    public int getRowIndex(String sheetName, String key) {
        logger.entering(new Object[] { sheetName, key });
        int index = -1;
        Sheet sheet = fetchSheet(sheetName);

        int rowCount = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            String cellValue = row.getCell(0).toString();
            if ((key.compareTo(cellValue) == 0) && (!cellValue.contains("#"))) {
                index = i;
                break;
            }
        }
        logger.exiting(index);
        return index;
    }

    /**
     *
     * @param sheetName -  A String that represents the Sheet name from which data is to be read
     * @param rowNumber - The row number from the excel sheet that is to be read
     * @return - Single Excel row that was read
     */
    public Row getAbsoluteSingeExcelRow(String sheetName, int rowNumber) {
        logger.entering(new Object[] { sheetName, rowNumber });
        Sheet sheet = fetchSheet(sheetName);
        Row row = sheet.getRow(rowNumber);
        logger.exiting(row);
        return row;
    }

}