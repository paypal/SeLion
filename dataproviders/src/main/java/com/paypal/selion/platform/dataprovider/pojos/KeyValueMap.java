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

package com.paypal.selion.platform.dataprovider.pojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mappable class for a collection of KeyValuePair
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class KeyValueMap {

    private List<KeyValuePair> items;

    @XmlElement(name = "item")
    public List<KeyValuePair> getItems() {
        return items;
    }

    public void setItems(List<KeyValuePair> items) {
        this.items = items;
    }

    public Map<String, KeyValuePair> getMap() {
        Map<String, KeyValuePair> returned = new LinkedHashMap<>();
        for (KeyValuePair item : getItems()) {
            returned.put(item.getKey(), item);
        }
        return returned;
    }

}