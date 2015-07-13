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

import com.paypal.selion.platform.dataprovider.impl.XmlFileSystemResource;
import com.paypal.selion.platform.dataprovider.pojos.KeyValueMap;
import com.paypal.selion.platform.dataprovider.pojos.KeyValuePair;

/**
 * This interface defines prototype to implement xml data provider implementation to parse the xml format data file.
 */
public interface XmlDataProvider extends SeLionDataProvider {

    /**
     * Generates a two dimensional array for TestNG DataProvider from the XML data representing a map of name value
     * collection.
     * 
     * This method needs the referenced {@link XmlFileSystemResource} to be instantiated using its constructors with
     * parameter {@code Class<?> cls} and set to {@code KeyValueMap.class}. The implementation in this method is tightly
     * coupled with {@link KeyValueMap} and {@link KeyValuePair}.
     * 
     * The hierarchy and name of the nodes are strictly as instructed. A name value pair should be represented as nodes
     * 'key' and 'value' as child nodes contained in a parent node named 'item'. A sample data with proper tag names is
     * shown here as an example :-
     *
     * <pre>
     * <items>
     *     <item>
     *         <key>k1</key>
     *         <value>val1</value>
     *     </item>
     *     <item>
     *         <key>k2</key>
     *         <value>val2</value>
     *     </item>
     *     <item>
     *         <key>k3</key>
     *         <value>val3</value>
     *     </item>
     * </items>
     * </pre>
     *
     * @return A two dimensional object array.
     */
    Object[][] getAllKeyValueData();
}
