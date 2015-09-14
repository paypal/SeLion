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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * 
 * Parent class to support all Reports. The data that it contains has to be specified as T. Right now works with
 * SummarizedData and List of Strings as Data
 * 
 * @param <T>
 */
public abstract class BaseReport<T> {

    private String reportName;
    private List<String> colTitles;
    private List<T> lstData = new ArrayList<T>();
    private int startColNum = 1;
    private static SimpleLogger logger = SeLionLogger.getLogger();

    protected int getStartColNum() {
        return startColNum;
    }

    protected void setStartColNum(int startColNum) {
        this.startColNum = startColNum;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public List<String> getColTitles() {
        return colTitles;
    }

    public void setColTitles(List<String> colTitles) {
        this.colTitles = colTitles;
    }

    public List<String> addColTitle(String newColTitle, int iIndex) {
        this.getColTitles().add(iIndex, newColTitle);
        return this.getColTitles();
    }

    public List<String> removeColTitle(String toRemoveTitle) {
        this.getColTitles().remove(toRemoveTitle);
        return this.getColTitles();
    }

    public List<String> removeColTitle(int iIndex) {
        this.getColTitles().remove(iIndex);
        return this.getColTitles();
    }

    protected List<T> getLstEntities() {
        return lstData;
    }

    public void setLstEntities(List<T> lstData) {
        this.lstData = lstData;
    }

    public void generateRep(HSSFWorkbook wb, String sheetName, List<BaseReport<?>> lstReports) {
        logger.entering(new Object[] { wb, sheetName, lstReports });
        int rowNum = 0;
        int iColStart = startColNum;
        HSSFSheet sheet;
        if (wb.getSheet(sheetName) == null) {
            sheet = wb.createSheet(sheetName);
        } else {
            sheet = wb.getSheet(sheetName);
            rowNum = sheet.getPhysicalNumberOfRows() + 2;
        }

        for (BaseReport<?> br : lstReports) {
            br.createReportName(sheet, rowNum++, iColStart, Styles.getHeadingStyle());
            br.createTitles(sheet, rowNum++, iColStart, Styles.getSubHeading2Style());

            rowNum = br.fillData(sheet, rowNum++, Styles.getStyleBorderThinCenter());
            rowNum += 3;
        }
        logger.exiting();
    }

    private void createReportName(HSSFSheet sheet, int rowNum, int iColStart, HSSFCellStyle style) {
        logger.entering(new Object[] { sheet, rowNum, iColStart, style });
        HSSFRow row = sheet.createRow(rowNum);
        HSSFCell newCell;

        int iColEnd = (this.getColTitles().size() > 0) ? (this.getColTitles().size() + iColStart - 1)
                : (iColStart + 1);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, iColStart, iColEnd));
        for (int iTempCol = iColStart; iTempCol <= iColEnd; iTempCol++) {
            newCell = row.createCell(iTempCol);
            newCell.setCellStyle(style);
            newCell.setCellValue(this.reportName);
        }

        logger.exiting();
    }

    private void createTitles(HSSFSheet sheet, int rowNum, int iColStart, HSSFCellStyle style) {
        logger.entering(new Object[] { sheet, rowNum, iColStart, style });
        HSSFRow row = sheet.createRow(rowNum);
        HSSFCell newCell;

        for (int iTitle = 0; iTitle < this.colTitles.size(); iTitle++) {
            newCell = row.createCell(iColStart);
            newCell.setCellValue(colTitles.get(iTitle));
            newCell.setCellStyle(style);
            sheet.autoSizeColumn(iColStart++);
        }
        logger.exiting();
    }

    abstract int fillData(HSSFSheet sheet, int rowNum, HSSFCellStyle style);

    String formatMilliSecondTime(Long timeInMilliseconds) {
        return (String.format(
                "%d min, %d sec",
                MILLISECONDS.toMinutes(timeInMilliseconds),
                MILLISECONDS.toSeconds(timeInMilliseconds)
                        - MINUTES.toSeconds(MILLISECONDS.toMinutes(timeInMilliseconds))));
    }
}
