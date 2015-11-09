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

package com.paypal.selion.platform.dataprovider.pojos.xml;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.jxpath.JXPathContext;

/**
 * Defines a complex type to represent a User entity in the unit tests. <br>
 * 
 * Note: The entity class needs to have explicit properties (getters/setters) defined in order to support JXPath. In
 * current implementation {@link User#getAddress}, {@link User#name} will be found by {@link JXPathContext#getValue(String)}, but
 * {@link User#accountNumber}, {@link User#amount} will not be found.
 *
 */
@XmlRootElement(name = "user")
public class User {

    public int id;
    public Long accountNumber;
    public double amount;
    private Address address;
    private String[] phoneNumbers;

    /**
     * @return the address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    private String name;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the phoneNumbers
     */
    @XmlElement(name="phoneNumber")
    public String[] getPhoneNumbers() {
        return Arrays.copyOf(phoneNumbers, phoneNumbers.length);
    }

    /**
     * @param phoneNumbers the phoneNumbers to set
     */
    public void setPhoneNumbers(String[] phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
