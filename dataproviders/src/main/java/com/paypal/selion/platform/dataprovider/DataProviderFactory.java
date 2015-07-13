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

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.dataprovider.impl.ExcelDataProviderImpl;
import com.paypal.selion.platform.dataprovider.impl.JsonDataProviderImpl;
import com.paypal.selion.platform.dataprovider.impl.XmlDataProviderImpl;
import com.paypal.selion.platform.dataprovider.impl.YamlDataProviderImpl;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This factory class is responsible for providing the data provider implementation instance based on data type.
 *
 */
public final class DataProviderFactory {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private DataProviderFactory() {
        // Utility class. So hide the constructor
    }

    /**
     * Load the Data provider implementation for the data file type
     *
     * @param resource - resource of the data file
     * @return Data provider Impl
     * @throws IOException
     */
    public static SeLionDataProvider getDataProvider(DataResource resource)
            throws IOException {
        logger.entering(resource);

        if(resource == null) {
            return null;
        }

        switch (resource.getType().toUpperCase()) {
        case "XML":
            return new XmlDataProviderImpl((XmlDataSource) resource);
        case "JSON":
            return new JsonDataProviderImpl(resource);
        case "YAML":
        case "YML":
            return new YamlDataProviderImpl(resource);
        case "XLSX":
        case "XLS":
            return new ExcelDataProviderImpl(resource);
        default:
            return null;
        }
     }
}