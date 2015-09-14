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

package com.paypal.selion.internal.reports.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.logging.Level;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class is responsible for providing formatting helpers to JSON generator, Runtime Reporter and HTML Reporter to
 * help parse and display the report data in appropriate format in each of their report formats.
 * 
 */
public class ReporterDateFormatter {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    public static final String CURRENTDATE = "currentDate";
    
    private static DateFormat getFormatter() {
        // DEVNOTE: Format required to support Internet Explorer browser.
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    /**
     * Return an ISO 8601 combined date and time string for specified date/time in host machine's default time zone.
     * 
     * @param date
     *            Date
     * @return String with format "yyyy-MM-dd'T'HH:mm:ss.SSS+XXXX where XXXX corresponds to the host's time zone."
     */
    public static String getISO8601String(Date date) {
        return getISO8601StringWithSpecificTimeZone(date, TimeZone.getDefault());
    }

    /**
     * Return an ISO 8601 combined date and time string for specified date/time. The returned date and time format is
     * compatible with JavaScript on Internet Explorer.
     * 
     * @param date
     *            Date
     * @param zone
     *            Time zone to be used.
     * @return String with format "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" where XXX corresponds to the input time zone. example:
     *         UTC time is represented as '2015-01-21T02:21:33.955Z' example: PST time is represented as
     *         '2015-01-20T18:21:33.955-08:00'
     */
    public static String getISO8601StringWithSpecificTimeZone(Date date, TimeZone zone) {
        DateFormat formatter = getFormatter();
        formatter.setTimeZone(zone);
        return formatter.format(date);
    }

    /**
     * Return an reader friendly date from ISO 8601 combined date and time string.
     * 
     * @param dateISOString
     *            String for date in ISO format.
     * @return String with format "yyyy-MM-dd'T'HH:mm:ss.SSS+XXXX where XXXX corresponds to the host's time zone."
     */
    public static String getStringFromISODateString(String dateISOString) {
        Date date;
        String formattedDate;
        DateFormat formatter = getFormatter();
        try {
            date = formatter.parse(dateISOString);
            formattedDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(date);
        } catch (ParseException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            formattedDate = dateISOString;
        }
        return formattedDate;
    }

    /**
     * Formats specific keys and values into readable form for HTML Reporter.
     * 
     * @param entryItem
     *      A single key value Entry to be formatted.
     * @return The formatted key value Entry or the original Entry, if no formatting is required
     */
    public static Entry<String, String> formatReportDataForBrowsableReports(Entry<String, String> entryItem) {
        String key = entryItem.getKey();
        String value = entryItem.getValue();

        String formattedKey = key;
        String formattedValue = value;

        switch (key) {
        case ReporterDateFormatter.CURRENTDATE:
            formattedKey = "Current Date";
            formattedValue = ReporterDateFormatter.getStringFromISODateString(value);
            break;
        default:
            break;
        }

        return new AbstractMap.SimpleImmutableEntry<String, String>(formattedKey, formattedValue);
    }

}
