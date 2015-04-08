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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Hashtable;

import org.testng.annotations.Test;

import com.google.gson.internal.LinkedTreeMap;
import com.paypal.selion.platform.dataprovider.pojos.yaml.USER;

public class JsonDataProviderTest {
    private static String filePathPrefix = "src/test/resources/";
    private static String jsonPojoArrayDataFile = "PojoArrayData.json";
    private static String jsonRawDataFile = "RawJsonData.json";

    // Use cases for parsing json to a user defined pojo
    @Test(groups = "unit")
    public void jsonPojoParseByIndexTest() throws JsonDataProviderException {
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
    public void getDataAsHashTableTest() throws JsonDataProviderException, IOException {
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
    public void testgetAllJsonData() throws JsonDataProviderException, IOException {
        FileSystemResource resource = new FileSystemResource(filePathPrefix, jsonPojoArrayDataFile, USER.class);
        Object[][] dataObject = JsonDataProvider.getAllJsonData(resource);
        for (int i = 0; i < dataObject.length; i++) {
            USER data = (USER) dataObject[i][0];
            assertNotNull(data);
            assertNotNull(data.getName());
            assertNotNull(data.getBank().getAddress().getStreet());
        }
    }

    // Negative use cases
    @Test(expectedExceptions={JsonDataProviderException.class},expectedExceptionsMessageRegExp="Error while parsing Json Data as a Hash table. Root cause: Unable to find a key named id. Please refer Javadoc", groups = "unit")
    public void getDataAsHashTableTest_invalidKey() throws JsonDataProviderException, IOException {
        FileSystemResource resource = new FileSystemResource(filePathPrefix, jsonPojoArrayDataFile);
        JsonDataProvider.getJsonDataAsHashTable(resource);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = "unit")
    public void negativeTestsWithNullFileName() throws JsonDataProviderException, IOException {
        FileSystemResource resource = new FileSystemResource(null, null, USER.class);
        JsonDataProvider.getAllJsonData(resource);
    }

    @Test(expectedExceptions = { RuntimeException.class },expectedExceptionsMessageRegExp="File Not Found", groups = "unit")
    public void negativeTestsInvalidFileName() throws IOException, JsonDataProviderException {
        FileSystemResource resource = new FileSystemResource("invalidFile.json", USER.class);
        JsonDataProvider.getAllJsonData(resource);
    }

    @Test(expectedExceptions = { IndexOutOfBoundsException.class }, groups = "unit")
    public void testGetDataByIndex_OutOfBoundsIndex() throws IOException, DataProviderException {
        FileSystemResource resource = new FileSystemResource(filePathPrefix, jsonPojoArrayDataFile, USER.class);
        JsonDataProvider.getJsonObjectByIndex(resource, "50");
    }
}
