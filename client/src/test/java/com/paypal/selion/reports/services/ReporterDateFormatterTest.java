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

package com.paypal.selion.reports.services;

import static org.testng.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateFormatUtils;
import org.testng.annotations.Test;

import com.paypal.selion.internal.reports.services.ReporterDateFormatter;

public class ReporterDateFormatterTest {

    @Test(groups = "unit")
    public void testGetISO8601StringWithSpecificTimeZone() {

        Date dateValue = new Date(1421806893955L);
        String resultDateString = ReporterDateFormatter.getISO8601StringWithSpecificTimeZone(dateValue,
                TimeZone.getTimeZone("UTC"));
        assertEquals(resultDateString, "2015-01-21T02:21:33.955Z");

        dateValue = new Date(401324999000L);
        resultDateString = ReporterDateFormatter.getISO8601StringWithSpecificTimeZone(dateValue,
                TimeZone.getTimeZone("PST"));
        assertEquals(resultDateString, "1982-09-19T16:09:59.000-07:00");
    }

    @Test(groups = "unit")
    public void testGetStringFromISODateString() throws ParseException {

        String expectedString = "Jan 20, 2015 6:21 PM";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
        Date date = sdf.parse(expectedString);
        // convert expected string from PST to local TZ.
        expectedString = DateFormatUtils.format(date, "MMM dd, yyyy h:mm a", TimeZone.getDefault());

        String dateString = "2015-01-21T02:21:33.955Z";
        String resultDateString = ReporterDateFormatter.getStringFromISODateString(dateString);
        assertEquals(resultDateString, expectedString);

        dateString = "2015-01-20T18:21:33.955-08:00";
        resultDateString = ReporterDateFormatter.getStringFromISODateString(dateString);
        assertEquals(resultDateString, expectedString);

    }

}
