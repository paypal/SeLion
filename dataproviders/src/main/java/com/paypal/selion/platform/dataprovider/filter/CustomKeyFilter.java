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

package com.paypal.selion.platform.dataprovider.filter;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Field;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.dataprovider.DataProviderException;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This filter implementation filters the data based on the filter key and its corresponding values provided.
 * filterKeyName is one of the field name that is available in the resource and filterKeyValues are its corresponding
 * values to filter. This filter always collects the data whose respective filterKeyName value exist in the given
 * filterKeyValues.
 * 
 */
public class CustomKeyFilter implements DataProviderFilter {

    protected static final SimpleLogger logger = SeLionLogger.getLogger();
    private final String filterKeyName;
    private final String filterKeyValues;

    /**
     * @param keyName   - The key name that is to be used for filtering.
     * @param keyValues - The key values that are to be used for filtering.
     *                  It can be the format of"Ram,Raj,Ren" for Customer Name filterKey or "222,234,567"
     *                  for Customer Id filterkey.
     */
    public CustomKeyFilter(String keyName, String keyValues) {
        checkArgument(keyName != null, "Please specify a valid key.");
        checkArgument(keyValues != null, "Please specify values to use for filtering.");
        filterKeyName = keyName;
        filterKeyValues = keyValues;
    }

    /**
     * This function identifies whether the object falls in the filtering criteria or not based on the filterKeyName and
     * its corresponding filterKeyValues.
     * 
     * @param data
     *            the object to be filtered.
     * @return boolean - true if object falls in the filter criteria.
     */
    @Override
    public boolean filter(Object data) {
        logger.entering(data);
        String[] keyValues = filterKeyValues.split(",");
        String tempKey = null;
        Field field;

        try {
            field = data.getClass().getDeclaredField(filterKeyName);
            field.setAccessible(true);
            for (String keyValue : keyValues) {
                tempKey = keyValue;
                if (field.get(data) != null && field.get(data).toString().trim().equals(keyValue)) {
                    logger.exiting(true);
                    return true;
                }
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new DataProviderException("Row with key '" + tempKey + "' is not found for given filter key '"
                    + filterKeyName + "'", e);
        }
        logger.exiting(false);
        return false;
    }

    public String toString() {
        return "FilterKeyName:" + filterKeyName + ", FilterKeyValues :" + filterKeyValues;
    }
}
