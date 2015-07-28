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

import java.util.Collections;
import java.util.Map;

import com.paypal.selion.platform.dataprovider.XmlDataSource;


/**
 * Extends {@link FileSystemResource} class to support XML file as a DataProvider to be used by
 * {@link com.paypal.selion.platform.dataprovider.impl.XmlDataProviderImpl}.
 *
 */
public class XmlFileSystemResource extends FileSystemResource implements XmlDataSource {

    private Map<String, Class<?>> xpathMap = Collections.<String, Class<?>>emptyMap();

    /**
     * Constructor to accept full path of XML file, and declared type indicated by {@code cls} represented by the XML
     * data.
     * 
     * @param fileName
     *            The complete path of the file resource including the file name.
     * @param cls
     *            The declared type modeled by the XML content in the file at {@code fileName}.
     */
    public XmlFileSystemResource(String fileName, Class<?> cls) {
        super(fileName, cls);
    }

    /**
     * Constructor to accept full path of XML file, and multiple declared types at multiple XPaths represented by the
     * XML data.
     * 
     * @param fileName
     *            The complete path of the file resource including the file name.
     * @param xpathMap
     *            The map containing the XPath string and the type represented by the node evaluated using the XPath.
     */
    public XmlFileSystemResource(String fileName, Map<String, Class<?>> xpathMap) {
        super(fileName);
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
