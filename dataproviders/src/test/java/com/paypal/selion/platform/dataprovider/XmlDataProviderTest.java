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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.paypal.selion.platform.dataprovider.filter.CustomKeyFilter;
import com.paypal.selion.platform.dataprovider.filter.SimpleIndexInclusionFilter;
import com.paypal.selion.platform.dataprovider.impl.XmlFileSystemResource;
import com.paypal.selion.platform.dataprovider.impl.XmlInputStreamResource;
import com.paypal.selion.platform.dataprovider.pojos.KeyValueMap;
import com.paypal.selion.platform.dataprovider.pojos.KeyValuePair;
import com.paypal.selion.platform.dataprovider.pojos.xml.Address;
import com.paypal.selion.platform.dataprovider.pojos.xml.User;
import com.paypal.selion.platform.utilities.FileAssistant;

/*
 * Unit tests for {@code XmlDataProvider}.
 */
public class XmlDataProviderTest {

    private static String listOfAddresses = "src/test/resources/testdata/dataprovider/ListOfAddresses.xml";
    private static String listOfKeyValuePairs = "src/test/resources/testdata/dataprovider/ListOfKeyValuePairs.xml";
    private static String listOfUsersWithInlineAddress = "src/test/resources/testdata/dataprovider/ListOfUsersWithAddress.xml";
    private static String listOfMultipleInlineObjects = "src/test/resources/testdata/dataprovider/SampleMultipleUsersPerDocument.xml";

    private static String addr1 = "1234 Elm st";
    private static String addr2 = "12 Pico st";

    private static String[] expectedKeys = { "k1", "k2", "k3" };
    private static String[] expectedValues = { "val1", "val2", "val3" };

    @DataProvider(name = "getListOfObjects")
    public static Object[][] dataProviderGetListOfAddresses() throws XPathExpressionException, IOException {
        XmlDataSource resource = new XmlFileSystemResource(listOfAddresses, Address.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] data = dataProvider.getAllData();
        return data;
    }

    @Test(groups = "unit", dataProvider = "getListOfObjects")
    public void testDataProviderGetListOfAddresses(Address address) {
        assertNotNull(address);

        String street = address.getStreet();
        assertTrue(street.equals(addr1) || street.equals(addr2));
    }

    @DataProvider(name = "getNameValueCollection")
    public static Object[][] dataProviderGetNameValueFromXmlResource() throws IOException {
        XmlDataSource resource = new XmlFileSystemResource(listOfKeyValuePairs, KeyValueMap.class);
        XmlDataProvider dataProvider = (XmlDataProvider) DataProviderFactory.getDataProvider(resource);
        Object[][] data = dataProvider.getAllKeyValueData();
        return data;
    }

    @Test(groups = "unit", dataProvider = "getNameValueCollection")
    public void testDataProviderGetNameValueFromXmlResource(KeyValuePair keyValueItem) {
        assertNotNull(keyValueItem);
        assertTrue(Arrays.asList(expectedKeys).contains(keyValueItem.getKey()));
        assertTrue(Arrays.asList(expectedValues).contains(keyValueItem.getValue()));
    }

    @DataProvider(name = "getFilteredNameValueCollection")
    public static Object[][] dataProviderGetFilteredNameValueFromXmlResource() throws IOException {
        XmlDataSource resource = new XmlFileSystemResource(listOfKeyValuePairs, KeyValueMap.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] data = dataProvider.getDataByKeys(new String[] { "k2" });
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
        XmlDataSource resource = new XmlInputStreamResource(new BufferedInputStream(
                FileAssistant.loadFile(listOfUsersWithInlineAddress)), User.class, "xml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] data = dataProvider.getAllData();
        return data;
    }

    @DataProvider(name = "getMultipleObjectsUsingXpath")
    public static Object[][] dataProviderGetMultipleObjectsFromXmlResource() throws XPathExpressionException,
            IOException {
        Map<String, Class<?>> map = new LinkedHashMap<String, Class<?>>();
        map.put("//transactions/transaction/user[1]", User.class);
        map.put("//transactions/transaction/user[2]", User.class);

        XmlDataSource resource = new XmlFileSystemResource(listOfMultipleInlineObjects, map);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] data = dataProvider.getAllData();
        return data;
    }

    @DataProvider(name = "getDataFilterByIndexIndividual")
    public static Iterator<Object[]> dataProviderByFilterGetDataByIndexIndividual() throws IOException,
            DataProviderException {
        XmlDataSource resource = new XmlInputStreamResource(new BufferedInputStream(
                FileAssistant.loadFile(listOfAddresses)), Address.class, "xml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter("1,3,5");
        Iterator<Object[]> data = dataProvider.getDataByFilter(filter);
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
        XmlDataSource resource = new XmlFileSystemResource(listOfAddresses, Address.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        CustomKeyFilter filter = new CustomKeyFilter("street", "1234 Elm st");
        Iterator<Object[]> data = dataProvider.getDataByFilter(filter);
        return data;
    }

    @Test(groups = "unit", dataProvider = "getDataFromCustomKeyFilter")
    public void testDataProviderUsingCustomKeyFilter(Address address) {
        assertNotNull(address);

        String street = address.getStreet();
        assertTrue(street.equals(addr1));
    }

    @Test
    public void getDataAsHashtable() throws XPathExpressionException, IOException {
        XmlDataSource resource = new XmlFileSystemResource(listOfKeyValuePairs, KeyValueMap.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Hashtable<String, Object> data = dataProvider.getDataAsHashtable();
        assertNotNull(data);
        assertNotNull(data.get("k1"));
        KeyValuePair k1 = (KeyValuePair) data.get("k1");
        assertNotNull(k1.getKey());
        assertNotNull(k1.getValue());
        assertNotNull(data.get("k2"));
        KeyValuePair k2 = (KeyValuePair) data.get("k2");
        assertNotNull(k2.getKey());
        assertNotNull(k2.getValue());
        assertNotNull(data.get("k3"));
        KeyValuePair k3 = (KeyValuePair) data.get("k3");
        assertNotNull(k3.getKey());
        assertNotNull(k3.getValue());
    }
}
