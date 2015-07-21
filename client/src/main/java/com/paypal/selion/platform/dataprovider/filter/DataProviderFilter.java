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


/**
 * This Interface provides the facility to provide the custom filtering logic based on the needs. user can create the
 * custom filter class by implementing this filter and invoke the filter based methods to apply the filter. <br>
 * <br>
 * Example dataproviderfilter:
 * 
 * <pre>
 * public class SimpleIndexInclusionDataProviderFilter implements IDataProviderFilter {
 * 
 *     &#064;Override
 *     public Object[][] filter(Object[][] data, String... args) {
 *         // filtering logic here
 *         return filteredData;
 *     }
 * 
 * }
 * </pre>
 */
public interface DataProviderFilter {

    /**
     * This function identifies whether the given object falls in the injected filter criteria.
     * 
     * @param data
     *            Object the object to be filtered.
     * @return boolean - true if object falls in the filter criteria.
     */
    boolean filter(Object data);
}
