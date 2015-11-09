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

package com.paypal.selion.internal.reports.excelreport;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Hyperlink;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Extends BaseReport <br>
 * Any generic report that gives data as List of String
 */
public class DetailsReport extends BaseReport<List<String>> {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    /**
     * Initializes report with TestCase details column titles
     */
    public DetailsReport() {
        super();
        String[] sTitles = new String[] { "Class Name", "Method/Testcase id", "Test Description", "Group[s]",
                "Time taken", "Output Logs" };

        List<String> colTitles = new ArrayList<String>();
        for (String title : sTitles) {
            colTitles.add(title);
        }
        super.setColTitles(colTitles);
    }

    public DetailsReport(String sReportName) {
        this();
        this.setReportName(sReportName);
    }

    public List<List<String>> getLstEntities() {

        return (List<List<String>>) super.getLstEntities();
    }

    int fillData(HSSFSheet sheet, int rowNum, HSSFCellStyle style) {
        logger.entering(new Object[] { sheet, rowNum, style });
        HSSFRow row;
        // Overriding style for Details Reports, as these do not have counts.
        // The details content are the test logs and stack trace.
        HSSFCellStyle newStyle = Styles.getStyleBorderThinLeftTop();

        for (List<String> dataString : this.getLstEntities()) {
            row = sheet.createRow(rowNum);
            int iColNum = getStartColNum();
            for (int i = 0; i < this.getColTitles().size(); i++) {
                row.createCell(iColNum);
                row.getCell(iColNum).setCellStyle(newStyle);

                // Displaying time after converting to minutes
                if (this.getColTitles().get(i).contains("Time")) {
                    Long timeInMilli = Long.parseLong(dataString.get(i));
                    row.getCell(iColNum).setCellValue(formatMilliSecondTime(timeInMilli));
                }
                else if (this.getColTitles().get(i).contains("Output Logs")) {
                    Hyperlink link = new HSSFHyperlink(Hyperlink.LINK_DOCUMENT);
                    link.setAddress(dataString.get(i));
                    row.getCell(iColNum).setCellStyle(Styles.getHyperLinkStyle());
                    row.getCell(iColNum).setCellValue("Link to details");
                    row.getCell(iColNum).setHyperlink(link);
                }
                else {
                    row.getCell(iColNum).setCellStyle(newStyle);
                    row.getCell(iColNum).setCellValue(dataString.get(i));
                }
                sheet.autoSizeColumn(iColNum++);
            }

            rowNum++;

        }
        logger.exiting(rowNum);
        return rowNum;

    }
}
