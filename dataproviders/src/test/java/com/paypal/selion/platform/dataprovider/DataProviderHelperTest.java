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

import static org.apache.commons.lang.ArrayUtils.contains;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.jxpath.JXPathNotFoundException;
import org.testng.annotations.Test;

import com.paypal.selion.platform.dataprovider.impl.DataProviderHelper;
import com.paypal.selion.platform.dataprovider.pojos.xml.Address;
import com.paypal.selion.platform.dataprovider.pojos.xml.User;

/**
 * Unit tests for {@link DataProviderHelper}.
 *
 */
public class DataProviderHelperTest {

    private static String addr1 = "1234 Elm st";
    private static String addr2 = "12 Pico st";
    private static String addr3 = "100 Never st";
    private static String addr4 = "2014 Open st";

    private static String[] expectedNames = { "Thomas", "rama" };

    @Test(groups = "unit")
    public void testParseIndexString() {
        String indexes = "1-3, 5, 7-8";
        int[] arrayIndex = DataProviderHelper.parseIndexString(indexes);
        assertEquals(arrayIndex.length, 6);
        assertFalse(contains(arrayIndex, 6));
        assertTrue(contains(arrayIndex,7));
    }

    @Test(groups = "unit", expectedExceptions = { DataProviderException.class })
    public void testExceptionWhenParseIndexString() {
        String indexes = "1-3, 5, 7_8";
        DataProviderHelper.parseIndexString(indexes);
    }

    @Test(groups = "unit")
    public void testConvertToObjectArray() {
        Object[][] converted = DataProviderHelper.convertToObjectArray("Selion");
        assertEquals(converted.length, 1);
        assertEquals(converted[0].length, 1);

        converted = DataProviderHelper.convertToObjectArray(2014);
        assertEquals(converted.length, 1);
        assertEquals(converted[0].length, 1);

        converted = DataProviderHelper.convertToObjectArray(true);
        assertEquals(converted.length, 1);
        assertEquals(converted[0].length, 1);

        converted = DataProviderHelper.convertToObjectArray(new Date());
        assertEquals(converted.length, 1);
        assertEquals(converted[0].length, 1);

        converted = DataProviderHelper.convertToObjectArray(123.4d);
        assertEquals(converted.length, 1);
        assertEquals(converted[0].length, 1);

        converted = DataProviderHelper.convertToObjectArray(999_99_9999L);
        assertEquals(converted.length, 1);
        assertEquals(converted[0].length, 1);

        converted = DataProviderHelper.convertToObjectArray(3.14f);
        assertEquals(converted.length, 1);
        assertEquals(converted[0].length, 1);

    }

    @Test(groups = "unit")
    public void testConvertSimpleArrayToObjectArray() {
        String[] strings = { "Selion", "OpenSource" };
        Object[][] converted = DataProviderHelper.convertToObjectArray(strings);
        assertEquals(converted.length, 2, "String array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], strings[0]);
        assertEquals(converted[1][0], strings[1]);

        int[] integers = { 2014, 2015, 2016 };
        converted = DataProviderHelper.convertToObjectArray(integers);
        assertEquals(converted.length, 3, "Integer array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], integers[0]);
        assertEquals(converted[1][0], integers[1]);
        assertEquals(converted[2][0], integers[2]);

        Integer[] integersAsObject = { 2014, 2015, 2016 };
        converted = DataProviderHelper.convertToObjectArray(integersAsObject);
        assertEquals(converted.length, 3, "Integer object array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], integersAsObject[0]);
        assertEquals(converted[1][0], integersAsObject[1]);
        assertEquals(converted[2][0], integersAsObject[2]);

        char[] chars = { 'a', '@', '?' };
        converted = DataProviderHelper.convertToObjectArray(chars);
        assertEquals(converted.length, 3, "Char array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], chars[0]);
        assertEquals(converted[1][0], chars[1]);
        assertEquals(converted[2][0], chars[2]);

        short[] shorts = { 2014, 2015, 2016 };
        converted = DataProviderHelper.convertToObjectArray(shorts);
        assertEquals(converted.length, 3, "Short array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], shorts[0]);
        assertEquals(converted[1][0], shorts[1]);
        assertEquals(converted[2][0], shorts[2]);

        boolean[] booleans = { true, false, false, false };
        converted = DataProviderHelper.convertToObjectArray(booleans);
        assertEquals(converted.length, 4, "Boolean array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], booleans[0]);
        assertEquals(converted[1][0], booleans[1]);
        assertEquals(converted[2][0], booleans[2]);
        assertEquals(converted[3][0], booleans[3]);

        Boolean[] booleanAsObject = { true, false, false, false };
        converted = DataProviderHelper.convertToObjectArray(booleanAsObject);
        assertEquals(converted.length, 4, "Boolean object array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], booleanAsObject[0]);
        assertEquals(converted[1][0], booleanAsObject[1]);
        assertEquals(converted[2][0], booleanAsObject[2]);
        assertEquals(converted[3][0], booleanAsObject[3]);

        Date[] dates = { new Date(), new Date() };
        converted = DataProviderHelper.convertToObjectArray(dates);
        assertEquals(converted.length, 2, "Date array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], dates[0]);
        assertEquals(converted[1][0], dates[1]);

        double[] doubles = { 123.4d, 1.234e2 };
        converted = DataProviderHelper.convertToObjectArray(doubles);
        assertEquals(converted.length, 2, "Double array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], doubles[0]);
        assertEquals(converted[1][0], doubles[1]);

        Double[] doublesAsObject = { 123.4d, 1.234e2 };
        converted = DataProviderHelper.convertToObjectArray(doublesAsObject);
        assertEquals(converted.length, 2, "Double object array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], doublesAsObject[0]);
        assertEquals(converted[1][0], doublesAsObject[1]);

        long[] ssns = { 999_99_9999L, 4000000L, 123456789L };
        converted = DataProviderHelper.convertToObjectArray(ssns);
        assertEquals(converted.length, 3, "Long array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], ssns[0]);
        assertEquals(converted[1][0], ssns[1]);
        assertEquals(converted[2][0], ssns[2]);

        float[] constants = { 3.14f, 9.807f };
        converted = DataProviderHelper.convertToObjectArray(constants);
        assertEquals(converted.length, 2, "Float array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], constants[0]);
        assertEquals(converted[1][0], constants[1]);

        byte[] bytes = { 10, 100 };
        converted = DataProviderHelper.convertToObjectArray(bytes);
        assertEquals(converted.length, 2, "Bytes array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);
        assertEquals(converted[0][0], bytes[0]);
        assertEquals(converted[1][0], bytes[1]);

        Object[] objects = { new Address("First street"), new Address("Second street") };
        converted = DataProviderHelper.convertToObjectArray(objects);
        assertEquals(converted.length, 2, "Typed Object array to Object[][] conversion failed.");
        assertEquals(converted[0].length, 1);

    }

    @Test(groups = "unit")
    public void testGetAllDataMultipleArgs() {
        Object[][] data = null;

        Object[][] dividends = { { 2 }, { 12 }, { 5 }, { 7 } };
        Object[][] divisors = { { 1 }, { 3 }, { 5 }, { 8 } };
        Object[][] quotientsExpected = { { 2 }, { 4 }, { 1 }, { 0 } };

        data = DataProviderHelper.getAllDataMultipleArgs(dividends, divisors, quotientsExpected);

        assertNotNull(data);

        assertEquals(data.length, 4);
        assertEquals(data[0].length, 3);
        assertEquals((int) data[2][2], 1);
    }

    @Test(groups = "unit")
    public void testGetDataByKeys() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("tom", 1f);
        map.put("1", "One");
        map.put("actor", new Date());

        String[] keys = { "tom", "actor" };
        Object[][] objArray = DataProviderHelper.getDataByKeys(map, keys);

        assertNotNull(objArray);
        assertEquals(objArray[0].length, 1);
    }

    @Test(groups = "unit", dataProvider = "getListFromNestedObjects", dataProviderClass = XmlDataProviderTest.class)
    public void testDataProviderGetListOfUsers(User user) {
        assertNotNull(user);
        assertTrue(Arrays.asList(expectedNames).contains(user.getName()));

        String street = DataProviderHelper.readObjectByXpath(user, String.class, "address/street");
        assertTrue(Arrays.asList(addr1, addr2).contains(street));
    }

    @Test(groups = "unit", dataProvider = "getMultipleObjectsUsingXpath", dataProviderClass = XmlDataProviderTest.class)
    public void testDataProviderGetMultipleObjectsFromXmlResource(User fromUser, User toUser) {
        assertNotNull(fromUser);
        assertNotNull(toUser);

        String fromStreet = DataProviderHelper.readObjectByXpath(fromUser, String.class, "address/street");
        String toStreet = ((Address) DataProviderHelper.readObjectByXpath(toUser, Address.class, "address")).getStreet();

        assertTrue(Arrays.asList(addr1, addr3).contains(fromStreet));
        assertTrue(Arrays.asList(addr2, addr4).contains(toStreet));
    }

    @Test(groups = "unit")
    public void testReadObjectByXpath() {
        Address address = new Address("1234 Elm st");
        User user = new User();
        user.setName("Thomas");
        user.setAddress(address);

        String name = DataProviderHelper.readObjectByXpath(user, String.class, "name");
        String street = DataProviderHelper.readObjectByXpath(user, String.class, "address/street");
        Address readAddress = DataProviderHelper.readObjectByXpath(user, Address.class, "address");

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

        String name = DataProviderHelper.readObjectByXpath(user, String.class, "name");
        List<String> phones = DataProviderHelper.readListByXpath(user, String.class, "phoneNumbers");

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

        DataProviderHelper.readObjectByXpath(user, long.class, "accountNumber");
    }
}
