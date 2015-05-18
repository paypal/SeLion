/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

package com.paypal.selion.platform.dataprovider.pojos;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mappable class for a collection of KeyValuePair
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class KeyValueMap {


    private LinkedHashMap<String, KeyValuePair> map;

    public LinkedHashMap<String, KeyValuePair> getMap() {
        if (map == null) {
            map = new LinkedHashMap<String, KeyValuePair>();
        }
        return map;
    }

    @XmlElement(name = "item")
    private List<KeyValuePair> getItems() {
        return new ArrayList<KeyValuePair>(getMap().values());
    }

    public List<Map.Entry<String, String>> getEntries() {
        List<Map.Entry<String, String>> returned = new ArrayList<Map.Entry<String, String>>(getMap().values().size());
        for (KeyValuePair item : getMap().values()) {
            returned.add(new AbstractMap.SimpleImmutableEntry<String, String>(item.getKey(), item.getValue()));
        }
        return returned;
    }

    @SuppressWarnings("unused")
    private void setItems(final List<KeyValuePair> items) {
        getMap().clear();
        for (KeyValuePair item : items) {
            getMap().put(item.getKey(), item);
        }
    }

}