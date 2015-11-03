/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.dataprovider.filter.CustomKeyFilter;
import com.paypal.selion.platform.dataprovider.filter.SimpleIndexInclusionFilter;
import com.paypal.selion.platform.dataprovider.impl.DataProviderHelper;
import com.paypal.selion.platform.dataprovider.impl.FileSystemResource;
import com.paypal.selion.platform.dataprovider.impl.InputStreamResource;
import com.paypal.selion.platform.dataprovider.pojos.yaml.ADDRESS;
import com.paypal.selion.platform.dataprovider.pojos.yaml.AREA_CODE;
import com.paypal.selion.platform.dataprovider.pojos.yaml.BANK;
import com.paypal.selion.platform.dataprovider.pojos.yaml.USER;
import com.paypal.selion.platform.utilities.FileAssistant;

import static org.testng.Assert.*;

public class YamlDataProviderTest {
    private static String documentSeparatedLists = "src/test/resources/DocumentSeparatedLists.yaml";
    private static String documentSeparatedAssociativeArrays = "src/test/resources/DocumentSeparatedAssociativeArrays.yaml";
    private static String documentSeparatedUsers = "src/test/resources/DocumentSeparatedUsers.yaml";
    private static String documentSeparatedUsers2 = "src/test/resources/DocumentSeparatedUsers.yaml";
    private static String userTaggedList = "src/test/resources/UserTaggedList.yaml";
    private static String list = "src/test/resources/List.yaml";
    private static String listOfLists = "src/test/resources/ListOfLists.yaml";
    private static String listOfAssociativeArrays = "src/test/resources/ListOfAssociativeArrays.yaml";
    private static String listOfUsers = "src/test/resources/ListOfUsers.yaml";
    private static String associativeArrays = "src/test/resources/AssociativeArrays.yaml";
    private static String associativeArrayOfLists = "src/test/resources/AssociativeArrayOfLists.yaml";
    private static String associativeArrayOfArrays = "src/test/resources/AssociativeArraysOfArrays.yaml";
    private static String associativeArrayOfUsers = "src/test/resources/AssociativeArrayOfUsers.yaml";
    private static USER user1 = new USER();
    private static USER user2 = new USER();
    private static USER user3 = new USER();
    private static USER user4 = new USER();
    private static USER user5 = new USER();
    private static USER user6 = new USER();
    private static ADDRESS addr1 = new ADDRESS("1234 Elm st");
    private static ADDRESS addr2 = new ADDRESS("12 Pico st");
    private static AREA_CODE ph1 = new AREA_CODE("501");
    private static AREA_CODE ph2 = new AREA_CODE("408");
    private static AREA_CODE ph3 = new AREA_CODE("650");
    private static AREA_CODE ph4 = new AREA_CODE("317");
    private static AREA_CODE ph5 = new AREA_CODE("301");
    private static AREA_CODE ph6 = new AREA_CODE("701");
    private static BANK bnk1 = new BANK("BOA", "checking", addr1);
    private static BANK bnk2 = new BANK("Well fargo", "savings", addr2);

    private com.paypal.test.utilities.logging.SimpleLogger logger;

    @BeforeSuite(groups = "unit")
    public void createUsers() {
        logger = SeLionLogger.getLogger();

        user1.setName("Thomas");
        user1.setPassword("password");
        user1.setAccountNumber((long) 78901);
        user1.setAmount(120.00);
        user1.setAreaCode(new AREA_CODE[]{ph1, ph5});
        user1.setBank(bnk2);
        user1.setPhoneNumber("1-408-666-5508");
        user1.setPreintTest(10);
        user1.setIsbooleanGood(false);
        user1.setDoubleTest(1340.24);
        user1.setLongTest((long) 599880);
        user1.setFloatTest((float) 0.002);
        user1.setByteTest((byte) 2);

        user2.setName("rama");
        user2.setPassword("abc123");
        user2.setAccountNumber((long) 123456);
        user2.setAmount(100.00);
        user2.setAreaCode(new AREA_CODE[]{ph1, ph2, ph3});
        user2.setBank(bnk1);
        user2.setPhoneNumber("1-408-225-8040");
        user2.setPreintTest(12);
        user2.setIsbooleanGood(true);
        user2.setDoubleTest(12.5);
        user2.setLongTest((long) 167045);
        user2.setFloatTest((float) 12.5);
        user2.setByteTest((byte) 2);

        user3.setName("binh");
        user3.setPassword("abc124");
        user3.setAccountNumber((long) 124567);
        user3.setAmount(200.50);
        user3.setAreaCode(new AREA_CODE[]{ph4, ph5, ph6});
        user3.setBank(bnk2);
        user3.setPhoneNumber("1-714-666-0043");
        user3.setPreintTest(14);
        user3.setIsbooleanGood(true);
        user3.setDoubleTest(13.5);
        user3.setLongTest((long) 1234);
        user3.setFloatTest((float) 13.5);
        user3.setByteTest((byte) 4);

        user4.setName("suri");
        user4.setPassword("abc124");
        user4.setAccountNumber((long) 1234567);
        user4.setAmount(300.75);
        user4.setAreaCode(new AREA_CODE[]{ph1, ph5, ph2});
        user4.setBank(bnk1);
        user4.setPhoneNumber("1-213-580-6070");
        user4.setPreintTest(17);
        user4.setIsbooleanGood(false);
        user4.setDoubleTest(14.5);
        user4.setLongTest((long) 456567);
        user4.setFloatTest((float) 14.5);
        user4.setByteTest((byte) 8);

        // user5 intentionally not set to any values except account number
        user5.setAccountNumber((long) 385749204);

        user6.setName("suri");
        user6.setPassword("abc124");
        user6.setAccountNumber((long) 1234567);
        user6.setAmount(300.75);
        user6.setAreaCode(new AREA_CODE[]{ph1, ph5, ph2});
        user6.setBank(bnk1);
        user6.setPhoneNumber("1-213-580-6070");
        user6.setPreintTest(17);
        user6.setIsbooleanGood(false);
        user6.setDoubleTest(14.5);
        user6.setLongTest((long) 456567);
        user6.setFloatTest((float) 14.5);
        user6.setByteTest((byte) 8);
    }

    @Test(groups = "unit")
    public void testSerializeObjectToYamlString() {
        logger.info("testSerializeObjectToYamlString");
        String yamlOutput = DataProviderHelper.serializeObjectToYamlString(user1);
        verifySerializedOutput(yamlOutput);
    }

    @Test(groups = "unit")
    public void testSerializeObjectToYamlStringAsList() {
        logger.info("testSerializeObjectToYamlStringAsList");
        String yamlOutput = DataProviderHelper.serializeObjectToYamlStringAsList(user1, user2);
        verifySerializedOutput(yamlOutput);
    }

    @Test(groups = "unit")
    public void testSerializeObjectToYamlStringAsMap() {
        logger.info("testSerializeObjectToYamlStringAsMap");
        String yamlOutput = DataProviderHelper.serializeObjectToYamlStringAsMap(user1, user2);
        verifySerializedOutput(yamlOutput);
    }

    @Test(groups = "unit")
    public void testSerializeObjectToYamlStringAsDocuments() {
        logger.info("testSerializeObjectToYamlStringAsDocuments");
        String yamlOutput = DataProviderHelper.serializeObjectToYamlStringAsDocuments(user1, user2);
        verifySerializedOutput(yamlOutput);
    }

    /**
     * The serialization methods are just calling snakeyaml dump method. Not much to test there so validation is pretty
     * simple/flexible. Just validate that some expected text exists so we know the dump call is still outputting
     * something.
     * 
     * @param output the output as a {@link String}
     */
    private void verifySerializedOutput(String output) {
        assertTrue(output.contains(user1.getName()));
        assertTrue(output.contains(user1.getPassword()));
        assertTrue(output.contains(Long.toString(user1.getAccountNumber())));
        assertTrue(output.contains(Double.toString(user1.getAmount())));

        AREA_CODE[] areaCodes = user1.getAreaCode();
        for (AREA_CODE a : areaCodes) {
            assertTrue(output.contains(a.getAreaCode()));
        }

        BANK bank = user1.getBank();
        assertTrue(output.contains(bank.getName()));
        assertTrue(output.contains(bank.getType()));

        ADDRESS address = bank.getAddress();
        assertTrue(output.contains(address.getStreet()));

        assertTrue(output.contains(Integer.toString(user1.getPreintTest())));
        assertTrue(output.contains(Boolean.toString(user1.getIsbooleanGood())));
        assertTrue(output.contains(Double.toString(user1.getDoubleTest())));
        assertTrue(output.contains(Long.toString(user1.getLongTest())));
        assertTrue(output.contains(Float.toString(user1.getFloatTest())));
        assertTrue(output.contains(Byte.toString(user1.getByteTest())));

    }

    @Test(groups = "unit")
    public void testGetAllDataFromDocuments() throws IOException {
        DataResource resource = new InputStreamResource(new BufferedInputStream(FileAssistant.loadFile(documentSeparatedUsers)),
                USER.class, "yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getAllData();
        List<String> fetchedNames = transferUserDataArrayInArrayIntoList(allUsers);
        arrayComparer(new String[] { "Thomas", "rama", "binh", "suri", null, "suri" }, fetchedNames.toArray());
    }

    @Test(groups = "unit")
    public void testGetAllDataFromMap() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getAllData();
        List<String> fetchedNames = transferUserDataArrayInArrayIntoList(allUsers);
        // Keys cannot be repeated in a map, so only expecting one "suri"
        arrayComparer(new String[] { "Thomas", "rama", "binh", "suri" }, fetchedNames.toArray());
    }

    @Test(groups = "unit")
    public void testGetAllDataFromList() throws IOException {
        DataResource resource = new InputStreamResource(new BufferedInputStream(
                FileAssistant.loadFile(listOfUsers)), USER.class, "yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getAllData();
        List<String> fetchedNames = transferUserDataArrayInArrayIntoList(allUsers);
        arrayComparer(new String[] { "Thomas", "rama", "binh", "suri", null, "suri" }, fetchedNames.toArray());
    }

    @Test(groups = "unit")
    public void testGetAllDataFromTaggedList() throws IOException {
        DataResource resource = new FileSystemResource(userTaggedList, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getAllData();
        List<String> fetchedNames = transferUserDataArrayInArrayIntoList(allUsers);
        arrayComparer(new String[] { "Thomas", "rama", "binh", "suri", null, "suri" }, fetchedNames.toArray());
    }

    @Test(groups = "unit")
    public void testGetAllDataFromStringList() throws IOException {
        DataResource resource = new FileSystemResource(list);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allStrings = dataProvider.getAllData();
        List<String> fetchedStrings = transferStringDataIntoList(allStrings);
        fetchedStrings.add((String) allStrings[0][0]);
        arrayComparer(new String[]{"string1", "string2", "string3"}, fetchedStrings.toArray());
    }

    @Test(groups = "unit")
    public void testGetAllDataAsHashtable() throws IOException {
        DataResource resource = new InputStreamResource(new BufferedInputStream(
                FileAssistant.loadFile(associativeArrayOfUsers)), USER.class, "yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Map<String, Object> allUsers = dataProvider.getDataAsHashtable();
        // Keys cannot be repeated in a map, so only expecting one "suri"
        assertTrue(((USER) allUsers.get("tom")).getName().equals("Thomas"));
        assertTrue(((USER) allUsers.get("1")).getName().equals("rama"));
        assertTrue(((USER) allUsers.get("binh")).getName().equals("binh"));
        assertTrue(((USER) allUsers.get("3")).getName().equals("suri"));
    }

    @Test(groups = "unit")
    public void testGetDataByKeys_Tom() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByKeys(new String[] { "tom" });
        List<String> fetchedNames = transferUserDataArrayInArrayIntoList(allUsers);
        arrayComparer(new String[] { "Thomas" }, fetchedNames.toArray());
    }

    @Test(groups = "unit")
    public void testGetDataByKeys_Three() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByKeys(new String[] { "3" });
        List<String> fetchedNames = transferUserDataArrayInArrayIntoList(allUsers);
        arrayComparer(new String[]{"suri"}, fetchedNames.toArray());
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = "unit")
    public void testGetDataByKeys_InvalidKey() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        dataProvider.getDataByKeys(new String[]{"selion"});
    }

    @Test(groups = "unit")
    public void testGetDataByKeys_MultipleKeys() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByKeys(new String[] { "tom", "binh" });
        List<String> fetchedNames = transferUserDataArrayInArrayIntoList(allUsers);
        arrayComparer(new String[]{"Thomas", "binh"}, fetchedNames.toArray());
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = "unit")
    public void testGetDataByKeys_MultipleKeysInvalidKey() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        dataProvider.getDataByKeys(new String[]{"Thomas", "selion"});
    }

    @Test(groups = "unit")
    public void testGetDataByIndex_One() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByIndex("1");
        List<String> fetchedNames = transferUserDataIteratorIntoList(allUsers);
        arrayComparer(new String[] { "Thomas" }, fetchedNames.toArray());
    }

    @Test(groups = "unit")
    public void testGetDataByIndex_Four() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByIndex("4");
        List<String> fetchedNames = transferUserDataIteratorIntoList(allUsers);
        arrayComparer(new String[]{"suri"}, fetchedNames.toArray());
    }

    @Test(expectedExceptions = { DataProviderException.class }, groups = "unit")
    public void testGetDataByIndex_InvalidIndex() throws IOException {
        DataResource resource = new InputStreamResource(new BufferedInputStream(
                FileAssistant.loadFile(documentSeparatedUsers)), USER.class, "yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        dataProvider.getDataByIndex("2~3");
    }

    @Test(groups = "unit")
    public void testGetDataByIndex_MultipleIndexes() throws IOException {
        DataResource resource = new FileSystemResource(documentSeparatedUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByIndex("2,3");
        List<String> fetchedNames = transferUserDataIteratorIntoList(allUsers);
        arrayComparer(new String[] { "rama", "binh" }, fetchedNames.toArray());
    }

    @Test(groups = "unit")
    public void testGetDataByIndex_RangeOfIndexes() throws IOException {
        DataResource resource = new FileSystemResource(documentSeparatedUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByIndex("1-2");
        List<String> fetchedNames = transferUserDataIteratorIntoList(allUsers);
        arrayComparer(new String[] { "Thomas", "rama" }, fetchedNames.toArray());
    }

    @Test(groups = "unit")
    public void testGetDataByIndex_IndividualAndRangeOfIndexes() throws IOException {
        DataResource resource = new InputStreamResource(new BufferedInputStream(
                FileAssistant.loadFile(documentSeparatedUsers)), USER.class, "yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByIndex("1-2,4,6");
        List<String> fetchedNames = transferUserDataIteratorIntoList(allUsers);
        arrayComparer(new String[] { "Thomas", "rama", "suri", "suri" }, fetchedNames.toArray());
    }

    @Test(groups = "unit")
    public void testGetDataByIndex_NullData() throws IOException {
        DataResource resource = new FileSystemResource(documentSeparatedUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByIndex("5");
        USER user = (USER) allUsers[0][0];
        assertEquals(user.getAmount(), null);
        assertEquals(user.getAreaCode(), null);
        assertEquals(user.getBank(), null);
        assertEquals(user.getByteTest(), 0);
        assertEquals(user.getDoubleTest(), (double) 0);
        assertEquals(user.getFloatTest(), (float) 0);
        assertFalse(user.getIsbooleanGood());
        assertEquals(user.getLongTest(), 0);
        assertEquals(user.getName(), null);
        assertEquals(user.getPassword(), null);
        assertEquals(user.getPhoneNumber(), null);
        assertEquals(user.getPreintTest(), 0);

    }

    private synchronized List<String> transferUserDataIteratorIntoList(Object[][] allUsers) {
        List<String> fetchedNames = new ArrayList<String>();
        for (Object[] object : allUsers) {
            USER user = (USER) object[0];
            fetchedNames.add(user.getName());
        }

        return fetchedNames;
    }

    private synchronized List<String> transferUserDataArrayInArrayIntoList(Object[][] allUsers) {
        List<String> fetchedNames = new ArrayList<String>();
        for (Object[] object : allUsers) {
            USER user = (USER) object[0];
            fetchedNames.add(user.getName());
        }

        return fetchedNames;
    }

    private synchronized List<String> transferStringDataIntoList(Object[][] allStrings) {
        List<String> fetchedStrings = new ArrayList<String>();
        for (Object[] object : allStrings) {
            fetchedStrings.add((String) object[0]);
        }
        return fetchedStrings;
    }

    private synchronized void arrayComparer(String[] expected, Object[] actual) {
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], (String) actual[i]);
        }
    }

    @Test(expectedExceptions = { RuntimeException.class }, groups = "unit")
    public void negativeTestsWithYamlResourceConstructor() throws Exception {
        DataResource resource = new FileSystemResource(null, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        dataProvider.getAllData();
    }

    @Test(expectedExceptions = { RuntimeException.class }, groups = "unit")
    public void negativeTestsInvalidFileName() throws IOException {
        DataResource resource = new FileSystemResource("IdontExist.yaml", USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        dataProvider.getAllData();
    }

    @DataProvider(name = "getList")
    public static Object[][] dataProviderGetList() throws IOException {
        DataResource resource = new FileSystemResource(list);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getListOfLists")
    public static Object[][] dataProviderGetListOfLists() throws IOException {
        DataResource resource = new InputStreamResource(new BufferedInputStream(
                FileAssistant.loadFile(listOfLists)), "yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getListOfAssociativeArrays")
    public static Object[][] dataProviderGetListOfAssociativeArrays() throws IOException {
        DataResource resource = new FileSystemResource(listOfAssociativeArrays);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getListOfUsers")
    public static Object[][] dataProviderGetListOfUsers() throws IOException {
        DataResource resource = new FileSystemResource(listOfUsers);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getAssociativeArray")
    public static Object[][] dataProviderGetAssociativeArray() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrays);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getAssociativeArrayOfLists")
    public static Object[][] dataProviderGetAssociativeArrayOfLists() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfLists);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getAssociativeArrayOfArrays")
    public static Object[][] dataProviderGetAssociativeArrayOfArrays() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfArrays);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getAssociativeArrayOfUsers")
    public static Object[][] dataProviderGetAssociativeArrayOfUsers() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getDocumentSeparatedLists")
    public static Object[][] dataProviderGetDocumentSeparatedLists() throws IOException {
        DataResource resource = new FileSystemResource(documentSeparatedLists);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getDocumentSeparatedAssociativeArrays")
    public static Object[][] dataProviderGetDocumentSeparatedAssociativeArrays() throws IOException {
        DataResource resource = new FileSystemResource(documentSeparatedAssociativeArrays);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getDocumentSeparatedUsers")
    public static Object[][] dataProviderGetDocumentSeparatedUsers() throws IOException {
        DataResource resource = new FileSystemResource(documentSeparatedUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getDataByKeys")
    public static Object[][] dataProviderGetDataByKeys() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getDataByKeys(new String[] { "tom", "1", "binh", "3" });
    }

    @DataProvider(name = "getDataByIndex")
    public static Object[][] dataProviderGetDataByIndex() throws IOException {
        DataResource resource = new FileSystemResource(listOfUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getDataByIndex("1-6");
    }

    @DataProvider(name = "getDataByIndexes")
    public static Object[][] dataProviderGetDataByIndexes() throws IOException {
        DataResource resource = new FileSystemResource(listOfUsers, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        int[] indexes = { 1, 2, 3, 4, 5, 6 };
        return dataProvider.getDataByIndex(indexes);
    }

    @DataProvider(name = "getDataFilterByIndexRange")
    public static Iterator<Object[]> dataProviderByFilterGetDataByIndex() throws IOException {
        DataResource resource = new FileSystemResource(listOfUsers, USER.class);
        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter("1-6");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getDataByFilter(filter);
    }

    @DataProvider(name = "getDataFilterByIndexRangeAndIndividual")
    public static Iterator<Object[]> dataProviderByFilterGetDataByIndexRange() throws IOException {
        DataResource resource = new FileSystemResource(listOfUsers, USER.class);
        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter("1-2,4,5");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getDataByFilter(filter);
    }

    @DataProvider(name = "getDataFilterByIndexIndividual")
    public static Iterator<Object[]> dataProviderByFilterGetDataByIndexIndividual() throws IOException {
        DataResource resource = new FileSystemResource(listOfUsers, USER.class);
        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter("1,3,5");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getDataByFilter(filter);
    }

    @DataProvider(name = "getDataByCustomKeysFilterByAccountNumber")
    public static Iterator<Object[]> dataProviderByFilterGetDataByCustomeByAccountNumber() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers, USER.class);
        CustomKeyFilter filter = new CustomKeyFilter("accountNumber", "123456,124567,1234567");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Iterator<Object[]> data = dataProvider.getDataByFilter(filter);
        return data;
    }

    @DataProvider(name = "getDataByCustomKeysFilterByName")
    public static Iterator<Object[]> dataProviderByFilterGetDataByCustomeByName() throws IOException {
        DataResource resource = new FileSystemResource(associativeArrayOfUsers, USER.class);
        CustomKeyFilter filter = new CustomKeyFilter("name", "Thomas,binh,suri");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Iterator<Object[]> data = dataProvider.getDataByFilter(filter);
        return data;
    }

    @DataProvider(name = "getMultipleStringArguments")
    public static Object[][] dataProviderGetMultipleStringArguments() throws IOException {
        List<DataResource> yamlResources = new ArrayList<DataResource>();
        yamlResources.add(new FileSystemResource(list));
        yamlResources.add(new FileSystemResource(list));
        return DataProviderHelper.getAllDataMultipleArgsFromYAML(yamlResources);
    }

    @DataProvider(name = "getMultipleArguments")
    public static Object[][] dataProviderGetMultipleArguments() throws IOException {
        List<DataResource> yamlResources = new ArrayList<DataResource>();
        yamlResources.add(new FileSystemResource(documentSeparatedUsers, USER.class));
        yamlResources.add(new FileSystemResource(documentSeparatedUsers2, USER.class));
        return DataProviderHelper.getAllDataMultipleArgsFromYAML(yamlResources);
    }

    @Test(groups = "unit", dataProvider = "getList")
    public void testDataProviderGetList(String str) {
        assertNotNull(str);
    }

    @Test(groups = "unit", dataProvider = "getListOfLists")
    public void testDataProviderGetListOfLists(ArrayList<?> test) {
        assertNotNull(test);
        assertEquals(test.size(), 3);
        for (Object obj : test) {
            assertNotNull(obj);
        }
    }

    @Test(groups = "unit", dataProvider = "getListOfAssociativeArrays")
    public void testDataProviderGetListOfAssociativeArrays(Map<?, ?> test) {
        assertNotNull(test);
        assertEquals(test.keySet().size(), 3);
        for (Entry<?, ?> entry : test.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    @Test(groups = "unit", dataProvider = "getListOfUsers")
    public void testDataProviderGetListOfUsers(USER user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(groups = "unit", dataProvider = "getAssociativeArray")
    public void testDataProviderGetAssociativeArray(Map<?, ?> test) {
        assertNotNull(test);
        assertEquals(test.keySet().size(), 3);
        for (Entry<?, ?> entry : test.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    @Test(groups = "unit", dataProvider = "getAssociativeArrayOfLists")
    public void testDataProviderGetAssociativeArrayOfLists(List<?> test) {
        assertNotNull(test);
        assertEquals(test.size(), 3);
        for (Object obj : test) {
            assertNotNull(obj);
        }
    }

    @Test(groups = "unit", dataProvider = "getAssociativeArrayOfArrays")
    public void testDataProviderGetAssociativeArrayOfArrays(Map<?, ?> test) {
        assertNotNull(test);
        assertEquals(test.keySet().size(), 3);
        for (Entry<?, ?> entry : test.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    @Test(groups = "unit", dataProvider = "getAssociativeArrayOfUsers")
    public void testDataProviderGetAssociativeArrayOfUsers(USER user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(groups = "unit", dataProvider = "getDocumentSeparatedLists")
    public void testDataProviderGetDocumentSeparatedLists(List<?> list) {
        assertNotNull(list);
        assertEquals(list.size(), 3);
        for (Object obj : list) {
            assertNotNull(obj);
        }
    }

    @Test(groups = "unit", dataProvider = "getDocumentSeparatedAssociativeArrays")
    public void testDataProviderGetDocumentSeparatedLists(Map<?, ?> map) {
        assertNotNull(map);
        assertEquals(map.keySet().size(), 3);
        for (Entry<?, ?> entry : map.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    @Test(groups = "unit", dataProvider = "getDocumentSeparatedUsers")
    public void testDataProviderGetDocumentSeparatedUsers(USER user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(groups = "unit", dataProvider = "getDataByKeys")
    public void testDataProviderGetDataByKeys(USER user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(groups = "unit", dataProvider = "getDataByCustomKeysFilterByAccountNumber")
    public void testDataProviderGetDataByCustomKeysFilterByAccountNumber(USER user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(groups = "unit", dataProvider = "getDataByCustomKeysFilterByName")
    public void testDataProviderGetDataByCustomKeysFilterByName(USER user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(groups = "unit", dataProvider = "getDataByIndex")
    public void testDataProviderGetDataByIndex(USER user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(groups = "unit", dataProvider = "getDataByIndexes")
    public void testDataProviderGetDataByIndexes(USER user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(groups = "unit", dataProvider = "getDataFilterByIndexRange")
    public void testDataProviderGetDataByFilterByIndex(USER user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(groups = "unit", dataProvider = "getDataFilterByIndexRangeAndIndividual")
    public void testDataProviderGetDataFilterByIndexRangeAndIndividual(USER user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(groups = "unit", dataProvider = "getDataFilterByIndexIndividual")
    public void testDataProviderGetDataFilterByIndexIndividual(USER user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(groups = "unit", dataProvider = "getMultipleStringArguments")
    public void testDataProviderGetStringList(String str1, String str2) {
        assertEquals(str1, str2);
    }

    @Test(groups = "unit", dataProvider = "getMultipleArguments")
    public void testDataProviderGetMultipleArguments(USER user1, USER user2) {
        assertNotNull(user1);
        assertNotNull(user2);
        if (user1.getBank() != null) {
            String user1Bank = user1.getBank().getName();
            String user2Bank = user2.getBank().getName();
            assertTrue(user1Bank.equals(user2Bank));
        } else {
            // Objects passed in should not be null, but they are allowed to
            // have members with a null or 0 value. User5 has all values defined
            // as null except account number.
            assertTrue(user1.getAccountNumber().equals(user5.getAccountNumber()));
            assertTrue(user2.getAccountNumber().equals(user5.getAccountNumber()));
        }

    }

}
