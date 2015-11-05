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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.paypal.selion.platform.html.support.HtmlElementUtils;

/**
 * This class is the web element Table wrapper 
 * <p>
 * Keep in mind that table indexes start with row 1, not 0 in XPATH convention. Therefore all methods in this class that
 * reference a row/column index as an argument or return value will treat these values as an index that starts from 1.
 * </p>
 */
public class Table extends AbstractElement {

    // the index of the row where the table's data starts.
    private Integer dataStartIndex;

    /**
     * Table Construction method <br>
     * <br>
     * <b>Usage:</b>
     * 
     * <pre>
     * private Table tblTransactionTable = new Table("//table[@id='transactionTable']")
     * </pre>
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * 
     */
    public Table(String locator) {
        super(locator);
    }

    /**
     * Use this constructor to override default controlName for logging purposes. Default controlName would be the
     * element locator.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging
     */
    public Table(String locator, String controlName) {
        super(locator, controlName);
    }

    /**
     * Use this constructor to create a Table contained within a parent.
     * 
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element. the parent of the
     *            element
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     */
    public Table(ParentTraits parent, String locator) {
        super(parent, locator);
    }

    /**
     * Use this constructor to create a Table contained within a parent. This constructor will also override default
     * controlName for logging purposes. Default controlName would be the element locator.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * 
     */
    public Table(String locator, String controlName, ParentTraits parent) {
        super(locator, controlName, parent);
    }

    /**
     * The row index where the contents of the table's data start.<br/>
     */
    public synchronized int getDataStartIndex() {
        if (dataStartIndex == null) {
            dataStartIndex = 1;
            // check all the <tr>'s in the <tbody> until we find the 1st <tr> that has <td>'s. Then we know we have a
            // row where the data starts. If there are columns in the <tbody>, there will be <th>'s for those rows.
            List<WebElement> allRows = null;
            String xPath = getXPathBase() + "tr";
            try {
                allRows = HtmlElementUtils.locateElements(xPath);
            } catch (NoSuchElementException e) {
                // if there is no tr in this table, then the tbody must be empty, therefore columns are not in the tbody
                return 1;
            }

            if (allRows != null) {
                for (WebElement row : allRows) {
                    if (!row.findElements(By.xpath("td")).isEmpty()) {
                        break;
                    }
                    dataStartIndex++;
                }
            }
        }

        return dataStartIndex;
    }

    /**
     * Returns the number of rows in a table. This will include the column row if that is the 1st row of the tbody.
     * 
     * @return int number of rows
     */
    public int getNumberOfRows() {
        String xPath = getXPathBase() + "tr";
        return HtmlElementUtils.locateElements(xPath).size();
    }

    /**
     * Returns the number of columns in a table. If the table is empty, column count cannot be determined and 0 will be
     * returned.
     * 
     * @return int number of columns
     */
    public int getNumberOfColumns() {
        List<WebElement> cells;
        String xPath = getXPathBase() + "tr";

        List<WebElement> elements = HtmlElementUtils.locateElements(xPath);

        if (elements.size() > 0 && getDataStartIndex() - 1 < elements.size()) {
            cells = elements.get(getDataStartIndex() - 1).findElements(By.xpath("td"));
            return cells.size();
        }

        return 0;
    }

    /**
     * Searches all rows from a table for the occurrence of the input search strings and returns the index to the row
     * containing all the search strings. The search will NOT be performed on the column's row(s).<br>
     * <br>
     * <b>Usage:</b>
     * 
     * <pre>
     * String[] search = { &quot;Payment To&quot;, &quot;-$7.00 USD&quot; };
     * int searchRow = findRowNumber(search);
     * </pre>
     * 
     * @param searchKeys
     *            String[] array with as many values as need to identify the row
     * @return int number of first row where all conditions were met <br>
     *         Negative number indicates that row was not found
     */
    public int getRowIndex(String[] searchKeys) {
        int numKey = searchKeys.length;
        int rowCount = getNumberOfRows();

        String xPathBase, xPath, value;
        xPathBase = getXPathBase();

        int rowIndex = -1;
        for (int i = getDataStartIndex(); i <= rowCount; i++) {
            xPath = xPathBase + "tr[" + i + "]";
            // get table row as text
            value = HtmlElementUtils.locateElement(xPath).getText();

            // search the table row for the key words
            if (value.length() > 0) {
                for (int s = 0; s < numKey; s++) {
                    if (searchKeys[s] != null && ((String) searchKeys[s]).length() > 0) {
                        if (value.contains((CharSequence) searchKeys[s])) {
                            rowIndex = i;
                        } else {
                            rowIndex = -1;
                            break;
                        }
                    }
                }
            }
            if (rowIndex > 0) {
                break;
            }
        }

        return rowIndex;
    }

    /**
     * Finds value of a cell in a table indicated by row and column indices. <br/>
     * <br/>
     * 
     * @param row
     *            int number of row for cell
     * @param column
     *            int number of column for cell
     * @return String value of cell with row and column. Null if cannot be found.
     */
    public String getValueFromCell(int row, int column) {
        if (row < 1 || column < 1) {
            throw new IllegalArgumentException("Row and column must start from 1");
        }
        List<WebElement> elements = HtmlElementUtils.locateElements(getXPathBase() + "tr");
        List<WebElement> cells = elements.get(row - 1).findElements(By.xpath(".//td"));

        if (cells.size() > 0) {
            return cells.get(column - 1).getText();
        }

        return null;
    }

    /**
     * Goes to the cell addressed by row and column indices and clicks link in that cell. Performs wait until page would
     * be loaded
     * 
     * @param row
     *            int number of row for cell
     * @param column
     *            int number of column for cell
     */
    public void clickLinkInCell(int row, int column) {
        String xPath = getXPathBase() + "tr[" + row + "]/td[" + column + "]/a";
        new Link(xPath).click();
    }

    /**
     * Generates xPath from element locator and path to the row (&lt;tr&gt; tag)<br>
     * Checks if table has &lt;TBODY&gt; tag and adds it to the xPath<br>
     * 
     * @return String with beginning of xPath<br>
     */
    public String getXPathBase() {
        String xPathBase = "";
        if (this.getElement() != null) {

            String locator = getLocator();
            if (!locator.startsWith("link=") && !locator.startsWith("xpath=") && !locator.startsWith("/")) {
                if (locator.startsWith("id=") || locator.startsWith("name=")) {
                    String tmp = locator.substring(locator.indexOf('=', 1) + 1);
                    if (locator.startsWith("id=")) {
                        xPathBase = "//table[@id='" + tmp + "']/tbody/";
                    } else {
                        xPathBase = "//*[@name='" + tmp + "']/tbody/";
                    }

                } else {
                    xPathBase = "//*[@id='" + locator + "']/tbody/";
                }

            } else {
                if (locator.startsWith("xpath=")) {
                    locator = locator.substring(locator.indexOf('=', 1) + 1);
                }

                if (HtmlElementUtils.locateElements(locator + "/tbody").size() > 0) {
                    xPathBase = locator + "/tbody/";
                } else {
                    xPathBase = locator + "//";
                }
            }
        } else {
            throw new NoSuchElementException("Table" + this.getLocator() + " does not exist.");
        }
        return xPathBase;
    }

    /**
     * Returns the single row of a table as a long string of text using the input row index.
     * 
     * @param rowIndex
     *            the index to the row which text is about to retrieve.
     * @return rowText a text string represents the single row of a table
     */
    public String getRowText(int rowIndex) {
        String rowText = null;
        String xPath = getXPathBase() + "tr[" + rowIndex + "]";

        rowText = HtmlElementUtils.locateElement(xPath).getText();

        return rowText;
    }

    /**
     * Tick the checkbox in a cell of a table indicated by input row and column indices
     * 
     * @param row
     *            int number of row for cell
     * @param column
     *            int number of column for cell
     */
    public void checkCheckboxInCell(int row, int column) {
        String checkboxLocator = getXPathBase() + "tr[" + row + "]/td[" + column + "]/input";
        CheckBox cb = new CheckBox(checkboxLocator);
        cb.check();
    }

    /**
     * Untick a checkbox in a cell of a table indicated by the input row and column indices.
     * 
     * @param row
     *            int number of row for cell
     * @param column
     *            int number of column for cell
     */
    public void uncheckCheckboxInCell(int row, int column) {
        String checkboxLocator = getXPathBase() + "tr[" + row + "]/td[" + column + "]/input";
        CheckBox cb = new CheckBox(checkboxLocator);
        cb.uncheck();
    }
}
