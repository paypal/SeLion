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

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

//TODO: Revisit this implementation. It doesnt sound right to have a static class with all static data members and then
//these static data members are set from outside. This class definitely needs to be reviewed again and perhaps refactored
/**
 * Creates different styles applied to different cells in the ExcelSheet
 */
final class Styles {

    private static HSSFWorkbook wb1;
    private static HSSFCellStyle headingStyle, subHeading1Style, subHeading2Style, subHeading2StyleThinBorder,
            thinBorderStyle, styleBorderThinCenter, styleBorderThinLeftTop, hyperLinkStyle;

    private Styles() {
        // Utility class. So hide the constructor
    }

    private static HSSFCellStyle createCustomStyle(HSSFFont fontStyle, Short... alignment) {
        HSSFCellStyle style = wb1.createCellStyle();
        style.setFont(fontStyle);
        if (alignment.length > 0) {
            style.setAlignment(alignment[0]);
        }
        return style;
    }

    private static HSSFFont createCustomFont(short colorIndex, Byte underlineWeight) {
        HSSFFont font = wb1.createFont();
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setFontHeightInPoints((short) 10);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(colorIndex);
        font.setUnderline(underlineWeight);
        return font;
    }

    private static HSSFCellStyle setAllBorders(short borderWeight, HSSFCellStyle existingStyle) {
        existingStyle.setBorderBottom(borderWeight);
        existingStyle.setBorderTop(borderWeight);
        existingStyle.setBorderLeft(borderWeight);
        existingStyle.setBorderRight(borderWeight);
        return existingStyle;
    }

    public static void initStyles(HSSFWorkbook wb) {
        wb1 = wb;
        setHeadingStyle(createCustomStyle(createCustomFont(HSSFColor.LEMON_CHIFFON.index, HSSFFont.U_NONE),
                HSSFCellStyle.ALIGN_CENTER));
        getHeadingStyle().setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        getHeadingStyle().setFillForegroundColor(new HSSFColor.BLUE_GREY().getIndex());
        setHeadingStyle(setAllBorders(HSSFCellStyle.BORDER_DOUBLE, getHeadingStyle()));

        subHeading1Style = createCustomStyle(createCustomFont(HSSFColor.LIGHT_BLUE.index, HSSFFont.U_NONE));
        subHeading1Style = setAllBorders(HSSFCellStyle.BORDER_THIN, subHeading1Style);

        setSubHeading2Style(createCustomStyle(createCustomFont(HSSFColor.BROWN.index, HSSFFont.U_NONE),
                HSSFCellStyle.ALIGN_CENTER));
        setSubHeading2Style(setAllBorders(HSSFCellStyle.BORDER_MEDIUM, getSubHeading2Style()));

        setSubHeading2StyleThinBorder(createCustomStyle(createCustomFont(HSSFColor.BROWN.index, HSSFFont.U_NONE),
                HSSFCellStyle.ALIGN_LEFT));
        setSubHeading2StyleThinBorder(setAllBorders(HSSFCellStyle.BORDER_THIN, getSubHeading2StyleThinBorder()));

        setThinBorderStyle(wb.createCellStyle());
        setThinBorderStyle(setAllBorders(HSSFCellStyle.BORDER_THIN, getThinBorderStyle()));

        setStyleBorderThinCenter(wb.createCellStyle());
        setStyleBorderThinCenter(setAllBorders(HSSFCellStyle.BORDER_THIN, getStyleBorderThinCenter()));
        getStyleBorderThinCenter().setAlignment(HSSFCellStyle.ALIGN_CENTER);

        setStyleBorderThinLeftTop(wb.createCellStyle());
        setStyleBorderThinLeftTop(setAllBorders(HSSFCellStyle.BORDER_THIN, getStyleBorderThinLeftTop()));
        getStyleBorderThinLeftTop().setAlignment(HSSFCellStyle.ALIGN_LEFT);
        getStyleBorderThinLeftTop().setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);

        setHyperLinkStyle(wb.createCellStyle());
        setHyperLinkStyle(setAllBorders(HSSFCellStyle.BORDER_THIN, getHyperLinkStyle()));
        HSSFFont hyperLinkFont = createCustomFont(HSSFColor.BLUE.index, HSSFFont.U_SINGLE);
        hyperLinkFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        getHyperLinkStyle().setFont(hyperLinkFont);
    }

    public static HSSFCellStyle getHeadingStyle() {
        return headingStyle;
    }

    private static void setHeadingStyle(HSSFCellStyle headingStyle) {
        Styles.headingStyle = headingStyle;
    }

    public static HSSFCellStyle getSubHeading2Style() {
        return subHeading2Style;
    }

    private static void setSubHeading2Style(HSSFCellStyle subHeading2Style) {
        Styles.subHeading2Style = subHeading2Style;
    }

    /**
     * Cell Style used by the Test Output worksheet.
     * 
     * @return HSSF supported cell style.
     */
    public static HSSFCellStyle getSubHeading2StyleThinBorder() {
        return subHeading2StyleThinBorder;
    }

    private static void setSubHeading2StyleThinBorder(HSSFCellStyle style) {
        Styles.subHeading2StyleThinBorder = style;
    }

    /**
     * Cell Style used by the Summary worksheets viz. TestSummary and Detailed Groupwise Summary.
     * 
     * @return HSSF supported cell style.
     */
    public static HSSFCellStyle getStyleBorderThinCenter() {
        return styleBorderThinCenter;
    }

    private static void setStyleBorderThinCenter(HSSFCellStyle styleBorderThinCenter) {
        Styles.styleBorderThinCenter = styleBorderThinCenter;
    }

    /**
     * Cell style used by the Detailed worksheets viz. TestCasewise Report and Failure List.
     * 
     * @return HSSF supported cell style.
     */
    public static HSSFCellStyle getStyleBorderThinLeftTop() {
        return styleBorderThinLeftTop;
    }

    private static void setStyleBorderThinLeftTop(HSSFCellStyle styleBorderThinLeftTop) {
        Styles.styleBorderThinLeftTop = styleBorderThinLeftTop;
    }

    /**
     * Cell style used for values like owner of run, current datetime on the TestSummary sheet.
     * 
     * @return HSSF supported cell style.
     */
    public static HSSFCellStyle getThinBorderStyle() {
        return thinBorderStyle;
    }

    private static void setThinBorderStyle(HSSFCellStyle thinBorderStyle) {
        Styles.thinBorderStyle = thinBorderStyle;
    }

    /**
     * Cell style used for cells that contain a hyperlink.
     * 
     * @return HSSF supported cell style.
     */
    public static HSSFCellStyle getHyperLinkStyle() {
        return hyperLinkStyle;
    }

    private static void setHyperLinkStyle(HSSFCellStyle hyperLinkStyle) {
        Styles.hyperLinkStyle = hyperLinkStyle;
    }
}