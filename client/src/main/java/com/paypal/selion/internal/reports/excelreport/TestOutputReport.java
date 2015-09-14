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

package com.paypal.selion.internal.reports.excelreport;

import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Hyperlink;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Extends BaseReport. <br>
 * Displays the test output messages, that include page sources and screenshot.</br>
 * 
 */
public class TestOutputReport extends BaseReport<List<String>> {
    private static SimpleLogger logger = SeLionLogger.getLogger();

    /**
     * Constructor sets the column header.
     */
    public TestOutputReport() {
        super();
        @SuppressWarnings("unchecked")
        List<String> colTitles = (List<String>) ListUtils.EMPTY_LIST;
        setColTitles(colTitles);
    }

    /**
     * Constructor that sets the column header, and sets the section name.
     * 
     * @param sReportName
     */
    public TestOutputReport(String sReportName) {
        this();
        this.setReportName(sReportName);

    }

    /**
     * Returns list of report contents.
     */
    protected List<List<String>> getLstEntities() {
        return (List<List<String>>) super.getLstEntities();
    }

    /**
     * Writes the report contents into the excel report file.
     */
    int fillData(HSSFSheet sheet, int rowNum, HSSFCellStyle style) {
        logger.entering(new Object[] { sheet, rowNum, style });

        HSSFRow row;
        HSSFCell col;

        for (List<String> dataString : this.getLstEntities()) {
            for (String output : dataString) {
                String[] outputLines = output.split(":", 2);
                String outputColumn = (outputLines.length > 1) ? outputLines[0] : "";
                String outputValue = (outputLines.length > 1) ? outputLines[1].trim() : output.trim();

                int colNum = getStartColNum();
                row = sheet.createRow(rowNum);
                col = row.createCell(colNum);
                col.setCellStyle(Styles.getSubHeading2StyleThinBorder());
                col.setCellValue(outputColumn);

                // Next column holds the values.
                colNum += 1;
                col = row.createCell(colNum);
                HSSFCellStyle rightCellStyle = (0 == dataString.indexOf(output)) ? Styles
                        .getSubHeading2StyleThinBorder() : Styles.getStyleBorderThinLeftTop();
                col.setCellStyle(rightCellStyle);
                if (outputValue.startsWith("file:") && (!SystemUtils.IS_OS_MAC)) {
                    // Do not hyperlink files in Mac OS, as the POI hyperlinks break there.
                    Hyperlink link = new HSSFHyperlink(Hyperlink.LINK_FILE);
                    link.setAddress(outputValue);
                    col.setCellStyle(Styles.getHyperLinkStyle());
                    col.setCellValue(outputValue);
                    col.setHyperlink(link);
                } else if (outputValue.startsWith("http:") || outputValue.startsWith("https:")) {
                    Hyperlink link = new HSSFHyperlink(Hyperlink.LINK_URL);
                    link.setAddress(outputValue);
                    col.setCellStyle(Styles.getHyperLinkStyle());
                    col.setCellValue(outputValue);
                    col.setHyperlink(link);
                } else {
                    col.setCellValue(outputValue);
                }
                rowNum += 1;
            }
            rowNum += 1;
        }

        sheet.autoSizeColumn(getStartColNum());
        sheet.autoSizeColumn(getStartColNum() + 1);

        logger.exiting(rowNum);
        return rowNum;
    }
}
