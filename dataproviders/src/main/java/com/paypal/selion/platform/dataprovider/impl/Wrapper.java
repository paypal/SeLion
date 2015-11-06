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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents the root of the XML document wrapping a bound type list of generic objects obtained when an XML is unmarshalled. .
 * @param <T>
 *  The bound type.
 */
@XmlRootElement
public class Wrapper<T> {

    private final List<T> list;

    /**
     * Default constructor initializes empty list. 
     */
    public Wrapper() {
        list = new ArrayList<T>();
    }

    /**
     * Initializes instance with list.
     * @param items
     */
    public Wrapper(List<T> items) {
        this.list = items;
    }

    /**
     * Returns list
     * @return
     *  The list.
     */
    @XmlAnyElement(lax = true)
    public List<T> getList() {
        return list;
    }
}