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

package ${package}.sample.selion;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.paypal.selion.platform.dataprovider.DataProviderFactory;
import com.paypal.selion.platform.dataprovider.DataResource;
import com.paypal.selion.platform.dataprovider.SeLionDataProvider;
import com.paypal.selion.platform.dataprovider.impl.DataProviderHelper;
import com.paypal.selion.platform.dataprovider.impl.FileSystemResource;
import ${package}.sample.dataobjects.AddressInformation;
import ${package}.sample.dataobjects.AreaCode;
import ${package}.sample.dataobjects.BankInformation;
import ${package}.sample.dataobjects.UserInformation;

/**
 * In this sample we will see how can SeLion be used for running data driven tests wherein the data for the data driven
 * tests are stored in Yaml files. For the sake of simplicity this TestNG based test will resort to just
 * running assertions on the data fetched from the Yaml files.
 *
 */
public class YamlPoweredDataDrivenTest {

    private static String documentSeparatedUsers = "src/test/resources/testdata/DocumentSeparatedUsers.yaml";
    private static String list = "src/test/resources/testdata/List.yaml";
    private static String listOfUsers = "src/test/resources/testdata/ListOfUsers.yaml";
    private static String associativeArrayOfUsers = "src/test/resources/testdata/AssociativeArrayOfUsers.yaml";
    private static UserInformation user1 = new UserInformation();
    private static UserInformation user2 = new UserInformation();
    private static UserInformation user3 = new UserInformation();
    private static UserInformation user4 = new UserInformation();
    private static UserInformation user5 = new UserInformation();
    private static UserInformation user6 = new UserInformation();
    private static AddressInformation addr1 = new AddressInformation("1234 Elm st");
    private static AddressInformation addr2 = new AddressInformation("12 Pico st");
    private static AreaCode ph1 = new AreaCode("501");
    private static AreaCode ph2 = new AreaCode("408");
    private static AreaCode ph3 = new AreaCode("650");
    private static AreaCode ph4 = new AreaCode("317");
    private static AreaCode ph5 = new AreaCode("301");
    private static AreaCode ph6 = new AreaCode("701");
    private static BankInformation bnk1 = new BankInformation("BOA", "checking", addr1);
    private static BankInformation bnk2 = new BankInformation("Well fargo", "savings", addr2);

    @BeforeClass
    public void initializeTestDataForComparison() {

        user1.setName("Nemo");
        user1.setPassword("password");
        user1.setAccountNumber(78901L);
        user1.setAmount(120.00);
        user1.setAreaCode(new AreaCode[] { ph1, ph5 });
        user1.setBank(bnk2);
        user1.setPhoneNumber("1-408-666-5508");
        user1.setPreintTest(10);
        user1.setIsbooleanGood(false);
        user1.setDoubleTest(1340.24);
        user1.setLongTest(599880L);
        user1.setFloatTest((float) 0.002);
        user1.setByteTest((byte) 2);

        user2.setName("Rambo");
        user2.setPassword("abc123");
        user2.setAccountNumber(123456L);
        user2.setAmount(100.00);
        user2.setAreaCode(new AreaCode[] { ph1, ph2, ph3 });
        user2.setBank(bnk1);
        user2.setPhoneNumber("1-408-225-8040");
        user2.setPreintTest(12);
        user2.setIsbooleanGood(true);
        user2.setDoubleTest(12.5);
        user2.setLongTest(167045L);
        user2.setFloatTest((float) 12.5);
        user2.setByteTest((byte) 2);

        user3.setName("Shifu");
        user3.setPassword("abc124");
        user3.setAccountNumber(124567L);
        user3.setAmount(200.50);
        user3.setAreaCode(new AreaCode[] { ph4, ph5, ph6 });
        user3.setBank(bnk2);
        user3.setPhoneNumber("1-714-666-0043");
        user3.setPreintTest(14);
        user3.setIsbooleanGood(true);
        user3.setDoubleTest(13.5);
        user3.setLongTest(1234L);
        user3.setFloatTest((float) 13.5);
        user3.setByteTest((byte) 4);

        user4.setName("Simba");
        user4.setPassword("abc124");
        user4.setAccountNumber(1234567L);
        user4.setAmount(300.75);
        user4.setAreaCode(new AreaCode[] { ph1, ph5, ph2 });
        user4.setBank(bnk1);
        user4.setPhoneNumber("1-213-580-6070");
        user4.setPreintTest(17);
        user4.setIsbooleanGood(false);
        user4.setDoubleTest(14.5);
        user4.setLongTest(456567L);
        user4.setFloatTest((float) 14.5);
        user4.setByteTest((byte) 8);

        // user5 intentionally not set to any values except account number
        user5.setAccountNumber(385749204L);

        user6.setName("Simba");
        user6.setPassword("abc124");
        user6.setAccountNumber(1234567L);
        user6.setAmount(300.75);
        user6.setAreaCode(new AreaCode[] { ph1, ph5, ph2 });
        user6.setBank(bnk1);
        user6.setPhoneNumber("1-213-580-6070");
        user6.setPreintTest(17);
        user6.setIsbooleanGood(false);
        user6.setDoubleTest(14.5);
        user6.setLongTest(456567L);
        user6.setFloatTest(new Float(14.5));
        user6.setByteTest((byte) 8);
    }

    @Test
    public void howToGetAllDataFromDocuments() throws IOException {
        FileSystemResource resource = new FileSystemResource(documentSeparatedUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getAllData();
        List<String> fetchedNames = getUserNames(allUsers);
        String[] expectedNames = new String[] { "Nemo", "Rambo", "Shifu", "Simba", null, "Simba" };
        assertEquals(fetchedNames.toArray(), expectedNames);
    }

    @Test
    public void howToGetAllDataFromMap() throws IOException {
        FileSystemResource resource = new FileSystemResource(associativeArrayOfUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getAllData();
        List<String> fetchedNames = getUserNames(allUsers);
        String[] expectedNames = { "Nemo", "Rambo", "Shifu", "Simba", null };
        assertEquals(fetchedNames.toArray(), expectedNames);
    }

    @Test
    public void howToGetAllDataFromList() throws IOException {
        FileSystemResource resource = new FileSystemResource(listOfUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getAllData();
        List<String> fetchedNames = getUserNames(allUsers);
        String[] expectedNames = { "Nemo", "Rambo", "Shifu", "Simba", null, "Simba" };
        assertEquals(fetchedNames.toArray(), expectedNames);
    }

    @Test
    public void howToGetAllDataFromTaggedList() throws IOException {
        FileSystemResource resource = new FileSystemResource(
                "src/test/resources/testdata/UserTaggedList.yaml",
                UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getAllData();
        List<String> fetchedNames = getUserNames(allUsers);
        String[] expectedNames = { "Nemo", "Rambo", "Shifu", "Simba", null, "Simba" };
        assertEquals(fetchedNames.toArray(), expectedNames);
    }

    @Test
    public void howToGetAllDataFromStringList() throws IOException {
        FileSystemResource resource = new FileSystemResource(list);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allStrings = dataProvider.getAllData();
        List<String> fetchedStrings = transferStringDataIntoList(allStrings);
        String[] expectedStrings = { "string1", "string2", "string3" };
        assertEquals(fetchedStrings.toArray(), expectedStrings);
    }

    @Test
    public void howToGetAllDataAsHashtable() throws IOException {
        FileSystemResource resource = new FileSystemResource(associativeArrayOfUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Hashtable<String, Object> allUsers = dataProvider.getDataAsHashtable();
        // Keys cannot be repeated in a map, so only expecting one "Simba"
        assertTrue(((UserInformation) allUsers.get("tom")).getName().equals("Nemo"));
        assertTrue(((UserInformation) allUsers.get("1")).getName().equals("Rambo"));
        assertTrue(((UserInformation) allUsers.get("Shifu")).getName().equals("Shifu"));
        assertTrue(((UserInformation) allUsers.get("3")).getName().equals("Simba"));
    }

    @Test
    public void howToGetDataByASingleKey() throws IOException {
        FileSystemResource resource = new FileSystemResource(associativeArrayOfUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByKeys(new String[] { "tom" });
        List<String> fetchedNames = getUserNames(allUsers);
        assertEquals(fetchedNames.toArray(), new String[] { "Nemo" });
    }

    @Test
    public void howToGetDataByMultipleKeys() throws IOException {
        FileSystemResource resource = new FileSystemResource(associativeArrayOfUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByKeys(new String[] { "tom", "Shifu" });
        List<String> fetchedNames = getUserNames(allUsers);
        assertEquals(fetchedNames.toArray(), new String[] { "Nemo", "Shifu" });
    }

    @Test
    public void howToGetDataByASingleIndex() throws IOException {
        FileSystemResource resource = new FileSystemResource(associativeArrayOfUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByIndex("1");
        List<String> fetchedNames = getUserNames(allUsers);
        assertEquals(fetchedNames.toArray(), new String[] { "Nemo" });
    }

    @Test
    public void howToGetDataByMultipleIndexes() throws IOException {
        FileSystemResource resource = new FileSystemResource(documentSeparatedUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByIndex("2,3");
        List<String> fetchedNames = getUserNames(allUsers);
        assertEquals(fetchedNames.toArray(), new String[] { "Rambo", "Shifu" });
    }

    @Test
    public void howToGetDataByARangeOfIndexes() throws IOException {
        FileSystemResource resource = new FileSystemResource(documentSeparatedUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByIndex("1-2");
        List<String> fetchedNames = getUserNames(allUsers);
        assertEquals(fetchedNames.toArray(), new String[] { "Nemo", "Rambo" });
    }

    @Test
    public void howToGetDataByIndividualAndRangeOfIndexes() throws IOException {
        FileSystemResource resource = new FileSystemResource(documentSeparatedUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] allUsers = dataProvider.getDataByIndex("1-2,4,6");
        List<String> fetchedNames = getUserNames(allUsers);
        assertEquals(fetchedNames.toArray(), new String[] { "Nemo", "Rambo", "Simba", "Simba" });
    }

    private List<String> getUserNames(Object[][] allUsers) {
        List<String> fetchedNames = new ArrayList<>();
        for (Object[] object : allUsers) {
            UserInformation user = (UserInformation) object[0];
            fetchedNames.add(user.getName());
        }
        return fetchedNames;
    }

    private List<String> transferStringDataIntoList(Object[][] allStrings) {
        List<String> fetchedStrings = new ArrayList<>();
        for (Object[] object : allStrings) {
            fetchedStrings.add((String) object[0]);
        }
        return fetchedStrings;
    }

    @DataProvider(name = "getList")
    public static Object[][] dataProviderGetList() throws IOException {
        FileSystemResource resource = new FileSystemResource(list);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getListOfLists")
    public static Object[][] dataProviderGetListOfLists() throws IOException {
        FileSystemResource resource = new FileSystemResource("src/test/resources/testdata/ListOfLists.yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getListOfAssociativeArrays")
    public static Object[][] dataProviderGetListOfAssociativeArrays() throws IOException {
        FileSystemResource resource = new FileSystemResource("src/test/resources/testdata/ListOfAssociativeArrays.yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getListOfUsers")
    public static Object[][] dataProviderGetListOfUsers() throws IOException {
        FileSystemResource resource = new FileSystemResource(listOfUsers);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getAssociativeArray")
    public static Object[][] dataProviderGetAssociativeArray() throws IOException {
        FileSystemResource resource = new FileSystemResource("src/test/resources/testdata/AssociativeArrays.yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getAssociativeArrayOfLists")
    public static Object[][] dataProviderGetAssociativeArrayOfLists() throws IOException {
        FileSystemResource resource = new FileSystemResource(
                "src/test/resources/testdata/AssociativeArrayOfLists.yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getAssociativeArrayOfArrays")
    public static Object[][] dataProviderGetAssociativeArrayOfArrays() throws IOException {
        FileSystemResource resource = new FileSystemResource(
                "src/test/resources/testdata/AssociativeArraysOfArrays.yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getAssociativeArrayOfUsers")
    public static Object[][] dataProviderGetAssociativeArrayOfUsers() throws IOException {
        FileSystemResource resource = new FileSystemResource(associativeArrayOfUsers);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getDocumentSeparatedLists")
    public static Object[][] dataProviderGetDocumentSeparatedLists() throws IOException {
        FileSystemResource resource = new FileSystemResource(
                "src/test/resources/testdata/DocumentSeparatedLists.yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getDocumentSeparatedAssociativeArrays")
    public static Object[][] dataProviderGetDocumentSeparatedAssociativeArrays() throws IOException
             {
        FileSystemResource resource = new FileSystemResource(
                "src/test/resources/testdata/DocumentSeparatedAssociativeArrays.yaml");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getDocumentSeparatedUsers")
    public static Object[][] dataProviderGetDocumentSeparatedUsers() throws IOException {
        FileSystemResource resource = new FileSystemResource(documentSeparatedUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getAllData();
    }

    @DataProvider(name = "getDataByKeys")
    public static Object[][] dataProviderGetDataByKeys() throws IOException {
        FileSystemResource resource = new FileSystemResource(associativeArrayOfUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getDataByKeys(new String[] { "tom", "1", "Shifu", "3" });
    }

    @DataProvider(name = "getDataByIndex")
    public static Object[][] dataProviderGetDataByIndex() throws IOException {
        FileSystemResource resource = new FileSystemResource(listOfUsers, UserInformation.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        return dataProvider.getDataByIndex("1-6");
    }

    @DataProvider(name = "getMultipleStringArguments")
    public static Object[][] dataProviderGetMultipleStringArguments() throws IOException {
        List<DataResource> yamlResources = new ArrayList<>();
        yamlResources.add(new FileSystemResource(list));
        yamlResources.add(new FileSystemResource("src/test/resources/testdata/list.yaml"));

        return DataProviderHelper.getAllDataMultipleArgsFromYAML(yamlResources);
    }

    @DataProvider(name = "getMultipleArguments")
    public static Object[][] dataProviderGetMultipleArguments() throws IOException {
        List<DataResource> yamlResources = new ArrayList<>();
        yamlResources.add(new FileSystemResource(documentSeparatedUsers, UserInformation.class));
        yamlResources.add(new FileSystemResource(
                "src/test/resources/testdata/DocumentSeparatedUsers.yaml",
                UserInformation.class));

        return DataProviderHelper.getAllDataMultipleArgsFromYAML(yamlResources);

    }

    @Test(dataProvider = "getList")
    public void testDataProviderGetList(String str) {
        assertNotNull(str);
    }

    @Test(dataProvider = "getListOfLists")
    public void testDataProviderGetListOfLists(ArrayList<?> test) {
        assertNotNull(test);
        assertEquals(test.size(), 3);
        for (Object obj : test) {
            assertNotNull(obj);
        }
    }

    @Test(dataProvider = "getListOfAssociativeArrays")
    public void testDataProviderGetListOfAssociativeArrays(Map<?, ?> test) {
        assertNotNull(test);
        assertEquals(test.keySet().size(), 3);
        for (Map.Entry<?, ?> entry : test.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    @Test(dataProvider = "getListOfUsers")
    public void testDataProviderGetListOfUsers(UserInformation user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(dataProvider = "getAssociativeArray")
    public void testDataProviderGetAssociativeArray(Map<?, ?> test) {
        assertNotNull(test);
        assertEquals(test.keySet().size(), 3);
        for (Map.Entry<?, ?> entry : test.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    @Test(dataProvider = "getAssociativeArrayOfLists")
    public void testDataProviderGetAssociativeArrayOfLists(List<?> test) {
        assertNotNull(test);
        assertEquals(test.size(), 3);
        for (Object obj : test) {
            assertNotNull(obj);
        }
    }

    @Test(dataProvider = "getAssociativeArrayOfArrays")
    public void testDataProviderGetAssociativeArrayOfArrays(Map<?, ?> test) {
        assertNotNull(test);
        assertEquals(test.keySet().size(), 3);
        for (Map.Entry<?, ?> entry : test.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    @Test(dataProvider = "getAssociativeArrayOfUsers")
    public void testDataProviderGetAssociativeArrayOfUsers(UserInformation user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(dataProvider = "getDocumentSeparatedLists")
    public void testDataProviderGetDocumentSeparatedLists(ArrayList<?> list) {
        assertNotNull(list);
        assertEquals(list.size(), 3);
        for (Object obj : list) {
            assertNotNull(obj);
        }
    }

    @Test(dataProvider = "getDocumentSeparatedAssociativeArrays")
    public void testDataProviderGetDocumentSeparatedLists(LinkedHashMap<?, ?> map) {
        assertNotNull(map);
        assertEquals(map.keySet().size(), 3);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    @Test(dataProvider = "getDocumentSeparatedUsers")
    public void testDataProviderGetDocumentSeparatedUsers(UserInformation user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(dataProvider = "getDataByKeys")
    public void testDataProviderGetDataByKeys(UserInformation user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(dataProvider = "getDataByIndex")
    public void testDataProviderGetDataByIndex(UserInformation user) {
        assertNotNull(user);
        if (user.getBank() != null) {
            String bankName = user.getBank().getName();
            assertTrue(bankName.equals(bnk1.getName()) || bankName.equals(bnk2.getName()));
        } else {
            assertTrue(user.getAccountNumber().equals(user5.getAccountNumber()));
        }
    }

    @Test(dataProvider = "getMultipleStringArguments")
    public void testDataProviderGetStringList(String str1, String str2) {
        assertEquals(str1, str2);
    }

    @Test(dataProvider = "getMultipleArguments")
    public void testDataProviderGetMultipleArguments(UserInformation user1, UserInformation user2) {
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
