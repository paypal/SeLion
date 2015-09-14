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

package com.paypal.selion.internal.reports.excelreport;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;


/**
 * Extends BaseReport <br>
 * Displays data which is of the type {@link SummarizedData}</br>
 * 
 */
public class SummaryReport extends BaseReport<SummarizedData> {
    private static SimpleLogger logger = SeLionLogger.getLogger();

    public SummaryReport() {
        super();

        String[] sTitles = new String[] { "Name", "Total TC", "Passed", "Failed", "Skipped", "Time taken" };
        List<String> colTitles = new ArrayList<String>();

        for (String title : sTitles) {
            colTitles.add(title);
        }
        setColTitles(colTitles);
    }

    public SummaryReport(String reportName) {
        this();
        this.setReportName(reportName);
    }

    public List<SummarizedData> getLstEntities() {

        return (List<SummarizedData>) super.getLstEntities();
    }

    int fillData(HSSFSheet sheet, int rowNum, HSSFCellStyle style) {
        logger.entering(new Object[] { sheet, rowNum, style });

        HSSFRow row;

        for (SummarizedData ps : this.getLstEntities()) {
            row = sheet.createRow(rowNum);
            int iColNum = getStartColNum();
            // Setting styles for each column first
            for (int i = 0; i < this.getColTitles().size(); i++) {
                row.createCell(iColNum);
                row.getCell(iColNum++).setCellStyle(style);

            }
            int iSetDataCol = getStartColNum();
            // Filling in data - (CI)
            row.getCell(iSetDataCol).setCellValue(ps.getsName());
            row.getCell(++iSetDataCol).setCellValue(ps.getiTotal());
            row.getCell(++iSetDataCol).setCellValue(ps.getiPassedCount());
            row.getCell(++iSetDataCol).setCellValue(ps.getiFailedCount());
            row.getCell(++iSetDataCol).setCellValue(ps.getiSkippedCount());
            row.getCell(++iSetDataCol).setCellValue(formatMilliSecondTime(ps.getlRuntime()));
            rowNum++;

            for (int i = --iColNum; i >= getStartColNum(); i--) {
                sheet.autoSizeColumn(i);
            }

        }
        logger.exiting(rowNum);
        return rowNum;
    }
}
