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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.testng.annotations.Test;

import com.google.gson.internal.LinkedTreeMap;
import com.paypal.selion.platform.dataprovider.filter.CustomKeyFilter;
import com.paypal.selion.platform.dataprovider.filter.SimpleIndexInclusionFilter;
import com.paypal.selion.platform.dataprovider.impl.FileSystemResource;
import com.paypal.selion.platform.dataprovider.impl.InputStreamResource;
import com.paypal.selion.platform.dataprovider.pojos.yaml.USER;

public class JsonDataProviderTest {
    private static String jsonPojoArrayDataFile = "src/test/resources/PojoArrayData.json";
    private static String jsonRawDataFile = "src/test/resources/RawJsonData.json";

    // Use cases for parsing json to a user defined pojo
    @Test(groups = "unit")
    public void jsonPojoParseByIndexTest() throws IOException {
        DataResource jsonResource = new FileSystemResource(jsonPojoArrayDataFile, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(jsonResource);
        Object[][] requestedData = dataProvider.getDataByIndex("1,3,4");
        for (int i = 0; i < requestedData.length; i++) {
            USER userData = (USER) requestedData[i][0];
            assertNotNull(userData);
            switch (i) {
            case 0: {
                assertTrue(userData.getName().equals("Optimus Prime"));
                assertTrue(userData.getBank().getAddress().getStreet().equals("1234 Some St"));
                break;
            }
            case 1: {
                assertTrue(userData.getName().equals("Alonso"));
                assertTrue(userData.getBank().getName().equals("Bank3"));
                break;
            }
            case 2: {
                assertTrue(userData.getPhoneNumber().equals("1111111111"));
                assertTrue(userData.getAreaCode()[1].getAreaCode().equals("area8"));
                break;
            }
            }
        }
    }

    @Test(groups = "unit")
    public void jsonPojoParseByIndexesTest() throws IOException {
        DataResource jsonResource = new InputStreamResource(new FileInputStream(new File(jsonPojoArrayDataFile)),
                USER.class, "json");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(jsonResource);
        int[] indexes = {1, 3, 4};
        Object[][] requestedData = dataProvider.getDataByIndex(indexes);
        for (int i = 0; i < requestedData.length; i++) {
            USER userData = (USER) requestedData[i][0];
            assertNotNull(userData);
            switch (i) {
            case 0: {
                assertTrue(userData.getName().equals("Optimus Prime"));
                assertTrue(userData.getBank().getAddress().getStreet().equals("1234 Some St"));
                break;
            }
            case 1: {
                assertTrue(userData.getName().equals("Alonso"));
                assertTrue(userData.getBank().getName().equals("Bank3"));
                break;
            }
            case 2: {
                assertTrue(userData.getPhoneNumber().equals("1111111111"));
                assertTrue(userData.getAreaCode()[1].getAreaCode().equals("area8"));
                break;
            }
            }
        }
    }

    @Test(groups = "unit")
    public void getDataAsHashTableTest() throws IOException {
        DataResource resource = new InputStreamResource(new FileInputStream(new File(jsonRawDataFile)), "json");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Hashtable<String, Object> dataRequested = dataProvider.getDataAsHashtable();
        assertNotNull(dataRequested);
        assertNotNull(dataRequested.get("test1"));
        Object[] test1Obj = (Object[]) dataRequested.get("test1");
        Hashtable<?, ?> test1Hash = (Hashtable<?, ?>) test1Obj[0];
        assertTrue(test1Hash.get("accountNumber").equals("9999999999"));
        assertNotNull(dataRequested.get("test2"));
        Object[] test2Obj = (Object[]) dataRequested.get("test2");
        Hashtable<?, ?> test2Hash = (Hashtable<?, ?>) test2Obj[0];
        assertNotNull(test2Hash.get("bank"));
        assertNotNull(dataRequested.get("test3"));
        Object[] test3Obj = (Object[]) dataRequested.get("test3");
        Hashtable<?, ?> test3Hash = (Hashtable<?, ?>) test3Obj[0];
        assertNotNull(test3Hash.get("bank"));
        // Reading a object stored in hash table and asserting the same
        LinkedTreeMap<?, ?> sample = (LinkedTreeMap<?, ?>) test3Hash.get("bank");
        assertTrue(sample.get("name").equals("Bank3"));
    }

    @Test(groups = "unit")
    public void testgetAllJsonData() throws IOException {
        DataResource resource = new InputStreamResource(new FileInputStream(new File(jsonPojoArrayDataFile)),
                USER.class, "json");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        Object[][] dataObject = dataProvider.getAllData();
        for (int i = 0; i < dataObject.length; i++) {
            USER data = (USER) dataObject[i][0];
            assertNotNull(data);
            assertNotNull(data.getName());
            assertNotNull(data.getBank().getAddress().getStreet());
        }
    }

    @Test(groups = "unit")
    public void testgetJsonDataByIndexFilter() throws IOException {
        DataResource resource = new FileSystemResource(jsonPojoArrayDataFile, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter("1,3,4");
        Iterator<Object[]> dataObject = dataProvider.getDataByFilter(filter);
        for (int i = 0; dataObject.hasNext(); i++) {
            USER userData = (USER) dataObject.next()[0];
            switch (i) {
            case 0: {
                assertTrue(userData.getName().equals("Optimus Prime"));
                assertTrue(userData.getBank().getAddress().getStreet().equals("1234 Some St"));
                break;
            }
            case 1: {
                assertTrue(userData.getName().equals("Alonso"));
                assertTrue(userData.getBank().getName().equals("Bank3"));
                break;
            }
            case 2: {
                assertTrue(userData.getPhoneNumber().equals("1111111111"));
                assertTrue(userData.getAreaCode()[1].getAreaCode().equals("area8"));
                break;
            }
            }
        }
    }

    @Test(groups = "unit")
    public void testgetJsonDataByCustomKeyAccountNumberFilter() throws IOException {
        DataResource resource = new InputStreamResource(new FileInputStream(new File(jsonPojoArrayDataFile)),
                USER.class, "json");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        CustomKeyFilter filter = new CustomKeyFilter("accountNumber", "8888888888,123456789");
        Iterator<Object[]> dataObject = dataProvider.getDataByFilter(filter);
        int i = 0;
        while (dataObject.hasNext()) {
            USER userData = (USER) dataObject.next()[0];
            assertTrue(userData.getAccountNumber().equals(Long.valueOf(123456789))
                    || userData.getAccountNumber().equals(Long.valueOf(8888888888L)));
            assertTrue(userData.getName().equals("Megatron") || userData.getName().equals("Alonso"));
            i++;
        }
        assertEquals(i, 2);
    }

    @Test(groups = "unit")
    public void testgetJsonDataByCustomKeyPhoneNumberFilter() throws IOException {
        DataResource resource = new FileSystemResource(jsonPojoArrayDataFile, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        CustomKeyFilter filter = new CustomKeyFilter("phoneNumber", "3333333333,2222222222");
        Iterator<Object[]> dataObjects = dataProvider.getDataByFilter(filter);
        int i = 0;
        while (dataObjects.hasNext()) {
            USER userData = (USER) dataObjects.next()[0];
            assertTrue(userData.getAccountNumber().equals(Long.valueOf(123456789))
                    || userData.getAccountNumber().equals(Long.valueOf(8888888888L)));
            assertTrue(userData.getName().equals("Megatron") || userData.getName().equals("Alonso"));
            i++;
        }
        assertEquals(i, 2);
    }

    // Negative use cases
    @Test(expectedExceptions = { DataProviderException.class }, expectedExceptionsMessageRegExp = "Error while parsing Json Data as a Hash table. Root cause: Unable to find a key named id. Please refer Javadoc", groups = "unit")
    public void getDataAsHashTableTest_invalidKey() throws IOException {
        DataResource resource = new FileSystemResource(jsonPojoArrayDataFile);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        dataProvider.getDataAsHashtable();
    }

    @Test(expectedExceptions = { NullPointerException.class }, groups = "unit")
    public void negativeTestsWithNullFileName() throws IOException {
        DataResource resource = new FileSystemResource(null, USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        dataProvider.getAllData();
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = "unit")
    public void negativeTestsInvalidFileName() throws IOException {
        DataResource resource = new FileSystemResource("invalidFile.json", USER.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        dataProvider.getAllData();
    }

    @Test(expectedExceptions = { IndexOutOfBoundsException.class }, groups = "unit")
    public void testGetDataByIndex_OutOfBoundsIndex() throws IOException {
        DataResource resource = new InputStreamResource(new FileInputStream(new File(jsonPojoArrayDataFile)),
                USER.class, "json");
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        dataProvider.getDataByIndex("50");
    }

    @Test(groups = "unit")
    public void testGetDataByKeys() throws IOException {
        DataResource resource = new FileSystemResource(jsonRawDataFile, HashMap[].class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);
        String[] keys = {"test1", "test2"};
        Object[][] dataObjects = dataProvider.getDataByKeys(keys );
        assertNotNull(dataObjects);
        assertEquals(dataObjects.length, 2);
    }
}
