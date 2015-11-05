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

import com.paypal.selion.platform.dataprovider.DataResource;

/**
/**
 * InputStreamResource defines input stream of the data source to be used for data provider consumption. Loading a
 * file (Yaml, Xml, Json, xls for e.g.,) containing user-defined (complex) objects also requires passing in the object class.
 * Passing in a complex object with nested complex objects does not require any additional parameters.
 *
 */
public class InputStreamResource implements DataResource {

    /**
     * Input stream of the data file
     */
    private InputStream stream;

    /**
     * User defined POJO to map with data file
     */
    private Class<?> cls;

    /**
     * Type of the data file which means extension of the file
     */
    private String type;

    public InputStreamResource(InputStream stream, Class<?> cls, String type) {
        this.stream = stream;
        this.cls = cls;
        this.type = type;
    }

    public InputStreamResource(InputStream stream, String type) {
        this(stream, null, type);
    }

    @Override
    public InputStream getInputStream() {
        return this.stream;
    }

    @Override
    public Class<?> getCls() {
        return this.cls;
    }

    @Override
    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    @Override
    public String getType() {
        return this.type;
    }

}
