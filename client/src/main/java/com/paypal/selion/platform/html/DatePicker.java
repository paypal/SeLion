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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.openqa.selenium.By;

import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.support.HtmlElementUtils;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/**
 * DatePicker is a widget which shows up a monthly calendar when clicked. It comes with "previous month" and
 * "next month" navigators, and a tablet of clickable days in a month. Users can navigate by clicking these item to
 * arrive at a desired date.
 * 
 * How to make DatePicker work in excel page data declaration? Since the DatePicker needs other three object to operate
 * properly (Previous Button, Next Button, and Date Label) these object also need to declare and initialize before the
 * DatePicker can work. To initialize this DatePicker a user needs to call datePickerInit before perform any actions on
 * the DatePicker.
 * 
 */
public class DatePicker extends AbstractElement {

    private String prevMonthLocator;
    private String nextMonthLocator;
    private String dateTextLocator;
    private Calendar calendar;

    /**
     * Using this constructor will create a DatePicker object with default prev month, next month, day of month, and
     * date text (month and year) locators.
     * 
     * <b>Usage:</b>
     * 
     * <pre>
     * DatePicker datePicker = new DatePicker(&quot;//input[@id='datePicker']&quot;);
     * </pre>
     * 
     * @param datePickerLocator
     *            the calendar field locator, not the widget
     */
    public DatePicker(String datePickerLocator) {
        super(datePickerLocator);
        initDateWidgetLocators(datePickerLocator);
    }

    /**
     * Use this constructor to override default controlName for logging purposes. Default controlName would be the
     * element locator.
     * 
     * @param datePickerLocator
     *            the calendar field element locator, not the widget
     * @param controlName
     *            the control name used for logging
     */
    public DatePicker(String datePickerLocator, String controlName) {
        super(datePickerLocator, controlName);
        initDateWidgetLocators(datePickerLocator);
    }

    /**
     * Use this constructor to create a DatePicker contained within a parent.
     * 
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * 
     * @param datePickerLocator
     *            - the calendar field element locator, not the widget.
     */
    public DatePicker(ParentTraits parent, String datePickerLocator) {
        super(parent, datePickerLocator);
        initDateWidgetLocators(datePickerLocator);
    }

    /**
     * Use this constructor to create a DatePicker contained within a parent.
     * 
     * @param datePickerLocator
     *            the calendar field element locator, not the widget
     * @param controlName
     *            the control name used for logging
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * 
     */
    public DatePicker(String datePickerLocator, String controlName, ParentTraits parent) {
        super(datePickerLocator, controlName, parent);
        initDateWidgetLocators(datePickerLocator);
    }

    private void initDateWidgetLocators(String datePickerLocator) {
        this.prevMonthLocator = datePickerLocator + "/div/div/a[1]/span";
        this.nextMonthLocator = datePickerLocator + "/div/div/a[2]/span";
        this.dateTextLocator = "//div[contains(@class,'ui-datepicker-title')]";
        this.calendar = Calendar.getInstance();
    }

    /**
     * This constructor provides a way for users to initialize DatePicker for prev month, next month, and date text
     * (month and year) locators.
     * 
     * @param datePickerLocator
     *            calendar field locator, not the widget
     * @param prevMonthLocator
     *            calendar widget prev month
     * @param nextMonthLocator
     *            calendar widget next month
     * @param dateTextLocator
     *            calendar widget month and year text
     */
    public DatePicker(String datePickerLocator, String prevMonthLocator, String nextMonthLocator, String dateTextLocator) {
        super(datePickerLocator);
        this.prevMonthLocator = prevMonthLocator;
        this.nextMonthLocator = nextMonthLocator;
        this.dateTextLocator = dateTextLocator;
        this.calendar = Calendar.getInstance();

    }

    /**
     * Advances one month to the future.
     */
    public void clickNextMonth() {
        HtmlElementUtils.locateElement(nextMonthLocator).click();
    }

    /**
     * Goes back one month in history.
     */
    public void clickPrevMonth() {
        HtmlElementUtils.locateElement(prevMonthLocator).click();
    }

    /**
     * Click on the day-of-month tablet to select the desired day of the month to set date.
     * 
     * @param dayOfMonth
     *            day to select from calendar widget
     */
    public void clickDay(int dayOfMonth) {
        String dayLocator = "//a[contains(text(),'" + dayOfMonth + "')]";
        HtmlElementUtils.locateElement(dayLocator).click();
    }

    /**
     * Gets the current setting month and year from the calendar header.
     * 
     * @return current setting month and year
     */
    public String getDateText() {
        String value = null;
        value = HtmlElementUtils.locateElement(dateTextLocator).getText();
        return value;
    }

    private void navigateMonth(Calendar from, Calendar to) {
        int monthNav;
        int yearNav;
        int monthCount;

        monthNav = to.get(Calendar.MONTH) - from.get(Calendar.MONTH);
        yearNav = to.get(Calendar.YEAR) - from.get(Calendar.YEAR);

        monthCount = (12 * yearNav) + monthNav;

        // if the month count is negative, the "to" date
        // is earlier than the "from" date, we have to go
        // back in time to arrive at the "to" date.
        if (monthCount < 0) {
            monthCount = Math.abs(monthCount);
            for (int i = 0; i < monthCount; i++) {
                clickPrevMonth();
                WebDriverWaitUtils.waitUntilElementIsVisible(this.prevMonthLocator);
            }
        } else {
            for (int i = 0; i < monthCount; i++) {
                clickNextMonth();
                WebDriverWaitUtils.waitUntilElementIsVisible(this.nextMonthLocator);
            }
        }
    }

    /**
     * This is the main function of the DatePicker object to select the date specified by the input parameter. It will
     * calculate how many time needed to click on the "next month" or "previous month" button to arrive at the correct
     * month and year in the input parameters. It then click on the day-of-month tablet to select the correct
     * day-of-month as specified in the input param.
     * 
     * @param to
     *            destination date as Java Calendar format.
     */
    public void setDate(Calendar to) {
        // Navigate from the current date
        // to the new date
        navigateMonth(calendar, to);

        // Select the day-of-month.
        clickDay(to.get(Calendar.DATE));

        Calendar cal = calendar;
        cal.set(Calendar.YEAR, to.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, to.get(Calendar.MONTH));
        cal.set(Calendar.DATE, to.get(Calendar.DATE));
    }

    /**
     * This function set the date on the DatePicker using the input paramter in the format of "MM.dd.yyyy" string
     * 
     * @param date
     *            string input date format of "MM/dd/yyyy"
     */
    public void setDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty.");
        }
        try {
            Date dateToSet = new SimpleDateFormat("MM/dd/yyyy").parse(date);
            Calendar to = Calendar.getInstance();
            to.setTime(dateToSet);
            setDate(to);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * This function set the date on the DatePicker using the year, month and day provided. <b>Note:</b> month is
     * 0-based (January - 0)
     * 
     * @param year
     *            The full year.
     * @param month
     *            The month. (0-based, 0 for January)
     * @param day
     *            The day of the month.
     */
    public void setDate(int year, int month, int day) {
        Calendar to = Calendar.getInstance();
        to.set(year, month, day);
        setDate(to);
    }

    /**
     * This function return the current date of a date picker in the format of "MM/dd/yyyy"
     * 
     * @return the current date
     */
    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String date = formatter.format(calendar.getTime());

        return date;
    }

    /**
     * Get the current month of a date picker.
     * 
     * @return the current month
     */
    public String getMonth() {
        return String.valueOf(calendar.get(Calendar.MONTH));
    }

    /**
     * Get the current year of a date picker.
     * 
     * @return the current year
     */
    public String getYear() {
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    /**
     * Get the current day of month of a date picker.
     * 
     * @return the current day of month
     */
    public String getDayOfMonth() {
        return String.valueOf(calendar.get(Calendar.DATE));
    }

    /**
     * DatePicker comes with default locators for widget controls previous button, next button, and date text. This
     * method gives access to override these default locators.
     * 
     * @param prevMonthLocator
     *            calendar widget prev month
     * @param nextMonthLocator
     *            calendar widget next month
     * @param dateTextLocator
     *            calendar widget month and year text
     */
    public void datePickerInit(String prevMonthLocator, String nextMonthLocator, String dateTextLocator) {
        this.prevMonthLocator = prevMonthLocator;
        this.nextMonthLocator = nextMonthLocator;
        this.dateTextLocator = dateTextLocator;
    }

    /**
     * Clears the date picker. Some browsers require clicking on an element outside of the date picker field to properly
     * reset the calendar to today's date.
     */
    public void reset() {
        this.getElement().clear();
        Grid.driver().findElement(By.tagName("body")).click();
        this.calendar = Calendar.getInstance();
        this.getElement().click();
    }
}
