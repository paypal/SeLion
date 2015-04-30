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
import java.util.Hashtable;
import java.util.Iterator;

import org.testng.annotations.Test;

import com.google.gson.internal.LinkedTreeMap;
import com.paypal.selion.platform.dataprovider.filter.CustomKeyFilter;
import com.paypal.selion.platform.dataprovider.filter.SimpleIndexInclusionFilter;
import com.paypal.selion.platform.dataprovider.pojos.yaml.USER;

public class JsonDataProviderTest {
    private static String filePathPrefix = "src/test/resources/";
    private static String jsonPojoArrayDataFile = "PojoArrayData.json";
    private static String jsonRawDataFile = "RawJsonData.json";

    // Use cases for parsing json to a user defined pojo
    @Test(groups = "unit")
    public void jsonPojoParseByIndexTest() {
        FileSystemResource jsonResource = new FileSystemResource(filePathPrefix, jsonPojoArrayDataFile, USER.class);
        Object[][] requestedData = JsonDataProvider.getJsonObjectByIndex(jsonResource, "1,3,4");
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
        FileSystemResource resource = new FileSystemResource(filePathPrefix, jsonRawDataFile);
        Hashtable<String, Hashtable<?, ?>> dataRequested = JsonDataProvider.getJsonDataAsHashTable(resource);
        assertNotNull(dataRequested);
        assertNotNull(dataRequested.get("test1"));
        assertTrue(dataRequested.get("test1").get("accountNumber").equals("9999999999"));
        assertNotNull(dataRequested.get("test2"));
        assertNotNull(dataRequested.get("test2").get("bank"));
        assertNotNull(dataRequested.get("test3"));
        assertNotNull(dataRequested.get("test3").get("bank"));
        // Reading a object stored in hash table and asserting the same
        LinkedTreeMap<?, ?> sample = (LinkedTreeMap<?, ?>) dataRequested.get("test3").get("bank");
        assertTrue(sample.get("name").equals("Bank3"));
    }

    @Test(groups = "unit")
    public void testgetAllJsonData() throws IOException {
        FileSystemResource resource = new FileSystemResource(filePathPrefix, jsonPojoArrayDataFile, USER.class);
        Object[][] dataObject = JsonDataProvider.getAllJsonData(resource);
        for (int i = 0; i < dataObject.length; i++) {
            USER data = (USER) dataObject[i][0];
            assertNotNull(data);
            assertNotNull(data.getName());
            assertNotNull(data.getBank().getAddress().getStreet());
        }
    }

    @Test(groups = "unit")
    public void testgetJsonDataByIndexFilter() {
        FileSystemResource resource = new FileSystemResource(filePathPrefix, jsonPojoArrayDataFile, USER.class);
        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter("1,3,4");
        Iterator<Object[]> dataObject = JsonDataProvider.getJsonObjectByFilter(resource, filter);
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
    public void testgetJsonDataByCustomKeyAccountNumberFilter() {
        FileSystemResource resource = new FileSystemResource(filePathPrefix, jsonPojoArrayDataFile, USER.class);
        CustomKeyFilter filter = new CustomKeyFilter("accountNumber", "8888888888,123456789");
        Iterator<Object[]> dataObject = JsonDataProvider.getJsonObjectByFilter(resource, filter);
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
    public void testgetJsonDataByCustomKeyPhoneNumberFilter() {
        FileSystemResource resource = new FileSystemResource(filePathPrefix, jsonPojoArrayDataFile, USER.class);
        CustomKeyFilter filter = new CustomKeyFilter("phoneNumber", "3333333333,2222222222");
        Iterator<Object[]> dataObjects = JsonDataProvider.getJsonObjectByFilter(resource, filter);
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
        FileSystemResource resource = new FileSystemResource(filePathPrefix, jsonPojoArrayDataFile);
        JsonDataProvider.getJsonDataAsHashTable(resource);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = "unit")
    public void negativeTestsWithNullFileName() throws DataProviderException, IOException {
        FileSystemResource resource = new FileSystemResource(null, null, USER.class);
        JsonDataProvider.getAllJsonData(resource);
    }

    @Test(expectedExceptions = { RuntimeException.class }, expectedExceptionsMessageRegExp = "File Not Found", groups = "unit")
    public void negativeTestsInvalidFileName() throws IOException {
        FileSystemResource resource = new FileSystemResource("invalidFile.json", USER.class);
        JsonDataProvider.getAllJsonData(resource);
    }

    @Test(expectedExceptions = { IndexOutOfBoundsException.class }, groups = "unit")
    public void testGetDataByIndex_OutOfBoundsIndex() throws IOException {
        FileSystemResource resource = new FileSystemResource(filePathPrefix, jsonPojoArrayDataFile, USER.class);
        JsonDataProvider.getJsonObjectByIndex(resource, "50");
    }
}
