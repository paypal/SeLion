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

import java.io.InputStream;

/**
 * This interface declare the prototype for data source which will be used in DataProvider Impl.
 */
public interface DataResource {

    /**
     * Load the input stream of the data file
     *
     * @return The inputStream
     */
    InputStream getInputStream();

    /**
     * Fetch the user defined POJO class
     *
     * @return The class
     */
    Class<?> getCls();

    /**
     * Set the user defined POJO class to map data
     *
     * @param cls
     */
    void setCls(Class<?> cls);

    /**
     * Fetch the data file extension
     *
     * @return The type
     */
    String getType();
}
