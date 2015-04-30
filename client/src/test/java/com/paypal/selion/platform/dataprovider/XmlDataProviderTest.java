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

package com.paypal.selion.platform.dataprovider;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.jxpath.JXPathNotFoundException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.paypal.selion.platform.dataprovider.filter.CustomKeyFilter;
import com.paypal.selion.platform.dataprovider.filter.SimpleIndexInclusionFilter;
import com.paypal.selion.platform.dataprovider.pojos.KeyValueMap;
import com.paypal.selion.platform.dataprovider.pojos.KeyValuePair;
import com.paypal.selion.platform.dataprovider.pojos.xml.Address;
import com.paypal.selion.platform.dataprovider.pojos.xml.User;

/*
 * Unit tests for {@code XmlDataProvider}.
 */
public class XmlDataProviderTest {
    private static String pathName = "src/test/resources/testdata/dataprovider/";

    private static String listOfAddresses = "ListOfAddresses.xml";
    private static String listOfKeyValuePairs = "ListOfKeyValuePairs.xml";
    private static String listOfUsersWithInlineAddress = "ListOfUsersWithAddress.xml";
    private static String listOfMultipleInlineObjects = "SampleMultipleUsersPerDocument.xml";

    private static String addr1 = "1234 Elm st";
    private static String addr2 = "12 Pico st";
    private static String addr3 = "100 Never st";
    private static String addr4 = "2014 Open st";

    private static String[] expectedNames = { "Thomas", "rama" };

    private static String[] expectedKeys = { "k1", "k2", "k3" };
    private static String[] expectedValues = { "val1", "val2", "val3" };

    @DataProvider(name = "getListOfObjects")
    public static Object[][] dataProviderGetListOfAddresses() throws XPathExpressionException, IOException {
        XmlFileSystemResource resource = new XmlFileSystemResource(pathName, listOfAddresses, Address.class);
        Object[][] data = XmlDataProvider.getAllData(resource);
        return data;
    }

    @Test(groups = "unit", dataProvider = "getListOfObjects")
    public void testDataProviderGetListOfAddresses(Address address) {
        assertNotNull(address);

        String street = address.getStreet();
        assertTrue(street.equals(addr1) || street.equals(addr2));
    }

    @DataProvider(name = "getNameValueCollection")
    public static Object[][] dataProviderGetNameValueFromXmlResource() {
        XmlFileSystemResource resource = new XmlFileSystemResource(pathName, listOfKeyValuePairs, KeyValueMap.class);
        Object[][] data = XmlDataProvider.getAllKeyValueData(resource);
        return data;
    }

    @Test(groups = "unit", dataProvider = "getNameValueCollection")
    public void testDataProviderGetNameValueFromXmlResource(KeyValuePair keyValueItem) {
        assertNotNull(keyValueItem);
        assertTrue(Arrays.asList(expectedKeys).contains(keyValueItem.getKey()));
        assertTrue(Arrays.asList(expectedValues).contains(keyValueItem.getValue()));
    }

    @DataProvider(name = "getFilteredNameValueCollection")
    public static Object[][] dataProviderGetFilteredNameValueFromXmlResource() {
        XmlFileSystemResource resource = new XmlFileSystemResource(pathName, listOfKeyValuePairs, KeyValueMap.class);
        Object[][] data = XmlDataProvider.getDataByKeys(resource, new String[] { "k2" });
        return data;
    }

    @Test(groups = "unit", dataProvider = "getFilteredNameValueCollection")
    public void testDataProviderGetDataByKeys(KeyValuePair keyValueItem) {
        assertNotNull(keyValueItem);
        assertTrue("k2".equals(keyValueItem.getKey()));
        assertTrue("val2".equals(keyValueItem.getValue()));
    }

    @DataProvider(name = "getListFromNestedObjects")
    public static Object[][] dataProviderGetListOfUsers() throws XPathExpressionException, IOException {
        XmlFileSystemResource resource = new XmlFileSystemResource(pathName, listOfUsersWithInlineAddress, User.class);
        Object[][] data = XmlDataProvider.getAllData(resource);
        return data;
    }

    @Test(groups = "unit", dataProvider = "getListFromNestedObjects")
    public void testDataProviderGetListOfUsers(User user) {
        assertNotNull(user);
        assertTrue(Arrays.asList(expectedNames).contains(user.getName()));

        String street = XmlDataProvider.readObjectByXpath(user, String.class, "address/street");
        assertTrue(Arrays.asList(addr1, addr2).contains(street));
    }

    @DataProvider(name = "getMultipleObjectsUsingXpath")
    public static Object[][] dataProviderGetMultipleObjectsFromXmlResource() throws XPathExpressionException,
            IOException {
        Map<String, Class<?>> map = new LinkedHashMap<String, Class<?>>();
        map.put("//transactions/transaction/user[1]", User.class);
        map.put("//transactions/transaction/user[2]", User.class);

        XmlFileSystemResource resource = new XmlFileSystemResource(pathName, listOfMultipleInlineObjects, map);
        Object[][] data = XmlDataProvider.getAllData(resource);
        return data;
    }

    @Test(groups = "unit", dataProvider = "getMultipleObjectsUsingXpath")
    public void testDataProviderGetMultipleObjectsFromXmlResource(User fromUser, User toUser) {
        assertNotNull(fromUser);
        assertNotNull(toUser);

        String fromStreet = XmlDataProvider.readObjectByXpath(fromUser, String.class, "address/street");
        String toStreet = ((Address) XmlDataProvider.readObjectByXpath(toUser, Address.class, "address")).getStreet();

        assertTrue(Arrays.asList(addr1, addr3).contains(fromStreet));
        assertTrue(Arrays.asList(addr2, addr4).contains(toStreet));
    }

    @Test(groups = "unit")
    public void testReadObjectByXpath() {
        Address address = new Address("1234 Elm st");
        User user = new User();
        user.setName("Thomas");
        user.setAddress(address);

        String name = XmlDataProvider.readObjectByXpath(user, String.class, "name");
        String street = XmlDataProvider.readObjectByXpath(user, String.class, "address/street");
        Address readAddress = XmlDataProvider.readObjectByXpath(user, Address.class, "address");

        assertEquals(name, "Thomas");
        assertEquals(street, "1234 Elm st");
        assertEquals(readAddress.getStreet(), "1234 Elm st");
    }

    @Test(groups = "unit")
    public void testReadListByXpath() {
        Address address = new Address("1234 Elm st");
        User user = new User();
        user.setName("Thomas");
        user.setAddress(address);
        user.setPhoneNumbers(new String[] { "4081231234", "4081234321" });

        String name = XmlDataProvider.readObjectByXpath(user, String.class, "name");
        List<String> phones = XmlDataProvider.readListByXpath(user, String.class, "phoneNumbers");

        assertNotNull(phones);
        assertEquals(name, "Thomas");
        assertEquals(phones.size(), 2);
        assertEquals(phones.get(0), "4081231234");
        assertEquals(phones.get(1), "4081234321");
    }

    @Test(groups = "unit", expectedExceptions = { JXPathNotFoundException.class })
    public void testExceptionWhenReadObjectByXpath() {
        User user = new User();
        user.accountNumber = 123456789L;

        XmlDataProvider.readObjectByXpath(user, long.class, "accountNumber");
    }

    @DataProvider(name = "getDataFilterByIndexIndividual")
    public static Iterator<Object[]> dataProviderByFilterGetDataByIndexIndividual() throws IOException,
            DataProviderException {
        XmlFileSystemResource resource = new XmlFileSystemResource(pathName, listOfAddresses, Address.class);
        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter("1,3,5");
        Iterator<Object[]> data = XmlDataProvider.getDataByFilter(resource, filter);
        return data;
    }

    @Test(groups = "unit", dataProvider = "getDataFilterByIndexIndividual")
    public void testDataProviderGetDataFilterByIndexIndividual(Address address) {
        assertNotNull(address);

        String street = address.getStreet();
        assertTrue(street.equals(addr1));
    }

    @DataProvider(name = "getDataFromCustomKeyFilter")
    public static Iterator<Object[]> dataProviderUsingCustomKeyFilter() throws IOException, DataProviderException {
        XmlFileSystemResource resource = new XmlFileSystemResource(pathName, listOfAddresses, Address.class);
        CustomKeyFilter filter = new CustomKeyFilter("street", "1234 Elm st");
        Iterator<Object[]> data = XmlDataProvider.getDataByFilter(resource, filter);
        return data;
    }

    @Test(groups = "unit", dataProvider = "getDataFromCustomKeyFilter")
    public void testDataProviderUsingCustomKeyFilter(Address address) {
        assertNotNull(address);

        String street = address.getStreet();
        assertTrue(street.equals(addr1));
    }

}
