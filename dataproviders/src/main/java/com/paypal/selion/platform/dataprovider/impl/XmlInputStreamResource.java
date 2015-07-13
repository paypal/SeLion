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

package com.paypal.selion.platform.dataprovider.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import com.paypal.selion.platform.dataprovider.XmlDataSource;

/**
 * Extends {@link InputStreamResource} class to support XML file as a DataProvider to be used by
 * {@link com.paypal.selion.platform.dataprovider.impl.XmlDataProviderImpl}.
 *
 */
public class XmlInputStreamResource extends InputStreamResource implements XmlDataSource {

    private Map<String, Class<?>> xpathMap = Collections.<String, Class<?>>emptyMap();

    /**
     * Constructor to accept input stream of XML file, and multiple declared types at multiple XPaths represented by the
     * XML data.
     *
     * @param stream
     *            The input stream of the resource.
     * @param cls
     *            User defined POJO to map with data file
     * @param type
     *            Type of the stream
     */
    public XmlInputStreamResource(InputStream stream, Class<?> cls, String type) {
        super(stream, cls, type);
    }

    /**
     * Constructor to accept input stream of XML file, and multiple declared types at multiple XPaths represented by the
     * XML data.
     *
     * @param stream
     *            The input stream of the resource.
     * @param xpathMap
     *            The map containing the XPath string and the type represented by the node evaluated using the XPath.
     */
    public XmlInputStreamResource(InputStream stream, Map<String, Class<?>> xpathMap, String type) {
        super(stream, type);
        this.xpathMap = xpathMap;
    }

    /**
     * Gets the map of {@code String} representing XPath and the type represented by the node evaluated by the XPath.
     *
     * @return A {@code Map<String, Class<?>>} map. Returns null if the instance was not initialized using
     *         {@link XmlFileSystemResource#XmlFileSystemResource(String, Map)} or
     *         {@link XmlFileSystemResource#XmlFileSystemResource(String, Class)} constructors.
     */
    @Override
    public Map<String, Class<?>> getXpathMap() {
        return xpathMap.isEmpty() ? null : xpathMap;
    }

}
