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

package com.paypal.selion.platform.dataprovider;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import com.paypal.selion.platform.dataprovider.filter.DataProviderFilter;

/**
 * This interface defines prototype to implement own data provider implementation to parse the specific format data
 * file. User can create own data provider by implementing this interface.
 */
public interface SeLionDataProvider {

    /**
     * Generates a two dimensional array for TestNG DataProvider from the data file.
     * 
     * @return A two dimensional object array
     * @throws IOException
     */
    Object[][] getAllData() throws IOException;

    /**
     * Generates an object array in iterator as TestNG DataProvider from the data filtered per {@code dataFilter}.
     * 
     * @param dataFilter
     *            an implementation class of {@link DataProviderFilter}
     * @return An iterator over a collection of Object Array to be used with TestNG DataProvider
     * @throws IOException
     */
    Iterator<Object[]> getDataByFilter(DataProviderFilter dataFilter) throws IOException;

    /**
     * Generates an object array in iterator as TestNG DataProvider from the data filtered per given indexes string.
     * This method may throw {@link DataProviderException} when an unexpected error occurs during data provision from
     * data file.
     * 
     * @param indexes
     *            The indexes for which data is to be fetched as a conforming string pattern.
     * 
     * @return Object[][] to be used with TestNG DataProvider.
     */
    Object[][] getDataByIndex(String indexes) throws IOException;

    /**
     * Generates an object array in iterator as TestNG DataProvider from the data filtered per given indexes.
     * This method may throw {@link DataProviderException} when an unexpected error occurs during data provision from
     * data file.
     *
     * @param indexes
     *            The indexes for which data is to be fetched as a conforming string pattern.
     *
     * @return Object[][] to be used with TestNG DataProvider.
     */
    Object[][] getDataByIndex(int[] indexes) throws IOException;

    /**
     * Generates a two dimensional array for TestNG DataProvider from the data representing a map of name value
     * collection filtered by keys.
     * 
     * @param keys
     *            The string keys to filter the data.
     * @return A two dimensional object array.
     */
    Object[][] getDataByKeys(String[] keys);

    /**
     * A utility method to give output data as HashTable.
     * 
     * @return The data as a {@link Hashtable}
     */
    Hashtable<String, Object> getDataAsHashtable();

}
