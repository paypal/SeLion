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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;

/**
 * Testing the date picker widget from a webpage.
 */
public class DatePickerTest {
	

    private final String datePickerLoc = "popupDatepicker";
    private final String nextLoc = "//a[contains(text(),'Next>')]";
    private final String prevLoc = "//a[contains(text(),'<Prev')]";
    private final String dateLoc = "popupDatepicker";

    private final DatePicker datePicker = new DatePicker(datePickerLoc, prevLoc, nextLoc, datePickerLoc);
    
    @Test(groups = { "browser-tests", "ie-broken-test" })
    @WebTest
    public void datePickerTest() throws Exception {
        Grid.open(TestServerUtils.getDatePickerURL());
        Grid.driver().findElement(By.id(dateLoc)).click();
        datePicker.setDate(2010, 05, 20);

        Assert.assertTrue(datePicker.getDate().compareTo("06/20/2010") == 0, "Unexpected Date returned from getDate.");

    }

    @Test(groups = { "browser-tests", "ie-broken-test" })
    @WebTest
    public void testPreviousNextMonth() throws Exception {
        Grid.open(TestServerUtils.getDatePickerURL());
        Grid.driver().findElement(By.id(dateLoc)).click();

        // test click previous 5 times
        for (int i = 0; i < 5; i++) {
            datePicker.clickPrevMonth();
        }

        // test click next month 5 times
        for (int i = 0; i < 5; i++) {
            datePicker.clickNextMonth();
        }
    }

    @Test(groups = { "browser-tests", "ie-broken-test" })
    @WebTest
    public void testReset() throws Exception {
        Grid.open(TestServerUtils.getDatePickerURL());
        Grid.driver().findElement(By.id(dateLoc)).click();

        // test reset
        datePicker.reset();

        Calendar cal = Calendar.getInstance();

        Assert.assertEquals(datePicker.getDayOfMonth(), String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
        Assert.assertEquals(datePicker.getMonth(), String.valueOf(cal.get(Calendar.MONTH)));
        Assert.assertEquals(datePicker.getYear(), String.valueOf(cal.get(Calendar.YEAR)));
    }

    @Test(groups = { "browser-tests", "ie-broken-test" })
    @WebTest
    public void testDefaultConstructor() throws Exception {
        Grid.open(TestServerUtils.getDatePickerURL());

        Grid.driver().findElement(By.id(dateLoc)).click();

        DatePicker datePicker = new DatePicker(datePickerLoc);
        datePicker.datePickerInit(prevLoc, nextLoc, datePickerLoc);

        Assert.assertTrue(datePicker != null);
    }

    @Test(groups = { "browser-tests", "ie-broken-test" })
    @WebTest
    public void testSetDate() throws ParseException {
        Grid.open(TestServerUtils.getDatePickerURL());
        Grid.driver().findElement(By.id(dateLoc)).click();

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = formatter.parse("01/30/2014");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // set to Jan 30th, 2014
        datePicker.setDate(calendar);

        Assert.assertEquals(datePicker.getDayOfMonth(), String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        Assert.assertEquals(datePicker.getMonth(), String.valueOf(calendar.get(Calendar.MONTH)));
        Assert.assertEquals(datePicker.getYear(), String.valueOf(calendar.get(Calendar.YEAR)));
    }
}
