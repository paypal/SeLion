/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.platform.dataprovider;

import java.util.ArrayList;
import java.util.List;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A utility class intended to serve as a helper class for miscellaneous operations being done by
 * {@link SimpleExcelDataProvider} and {@link YamlDataProvider}.
 * 
 */
final class DataProviderHelper {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private DataProviderHelper() {

    }

    /**
     * This function will parse the index string into separated individual indexes as needed. Calling the method with a
     * string containing "1, 3, 5-7, 11, 12-14, 8" would return a list of Integers {1, 3, 5, 6, 7, 11, 12, 13, 14, 8}
     * 
     * @param value
     *            the input string represent the indexes to be parse.
     * @return a list of indexes represented as Integers
     * @throws DataProviderException
     */
    public static List<Integer> parseIndexString(String value) throws DataProviderException {
        logger.entering(value);
        List<Integer> rows = new ArrayList<Integer>();
        int begin, end;
        String[] parsed;
        String[] parsedIndex = value.split(",");
        for (String index : parsedIndex) {
            if (index.contains("-")) {
                parsed = index.split("-");
                begin = Integer.parseInt(parsed[0].trim());
                end = Integer.parseInt(parsed[1].trim());
                for (int i = begin; i <= end; i++) {
                    rows.add(i);
                }
            } else {
                try {
                    rows.add(Integer.parseInt(index.trim()));
                } catch (NumberFormatException e) {
                    String msg = "Index '" + index + "' is invalid. Please "
                            + "provide either individual numbers or ranges.";
                    msg += "Range needs to be de-marked by '-'";
                    throw new DataProviderException(msg, e);
                }
            }

        }
        logger.exiting(rows);
        return rows;
    }

}
