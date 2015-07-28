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

import java.util.Map;

import com.paypal.selion.platform.dataprovider.impl.XmlFileSystemResource;

/**
 * This interface declare the prototype for xml data source which will be used in XML DataProvider Impl.
 *
 */
public interface XmlDataSource extends DataResource {

    /**
     * Gets the map of {@code String} representing XPath and the type represented by the node evaluated by the XPath.
     *
     * @return A {@code Map<String, Class<?>>} map. Returns null if the instance was not initialized using
     *         {@link XmlFileSystemResource#XmlFileSystemResource(String, Map)} or
     *         {@link XmlFileSystemResource#XmlFileSystemResource(String, Class)} constructors.
     */
    Map<String, Class<?>> getXpathMap();

}
