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

package com.paypal.selion.platform.dataprovider.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.composer.ComposerException;
import org.yaml.snakeyaml.constructor.Constructor;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.dataprovider.DataProviderException;
import com.paypal.selion.platform.dataprovider.DataResource;
import com.paypal.selion.platform.dataprovider.SeLionDataProvider;
import com.paypal.selion.platform.dataprovider.filter.DataProviderFilter;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class provides several methods to retrieve test data from yaml files. Users can get data returned in an Object
 * 2D array by loading the yaml file with Snakeyaml. If the entire yaml file is not needed then specific data entries
 * can be retrieved by indexes. If the yaml file is formatted to return a LinkedHashMap data type from Snakeyaml, user
 * can get an Object 2D array containing data for select keys or get the entire contents of the yaml file in a Hashtable
 * instead of an Object 2D array.
 *
 */

public final class YamlDataProviderImpl implements SeLionDataProvider {

    private static SimpleLogger logger = SeLionLogger.getLogger();
    private final DataResource resource;

    public YamlDataProviderImpl(DataResource resource) {
        this.resource = resource;
    }

    /**
     * Converts a yaml file into an Object 2D array for <a
     * href="http://testng.org/doc/documentation-main.html#parameters-dataproviders"> TestNG Dataprovider</a>
     * consumption. User-defined objects can be passed in to be added/mapped into the Snakeyaml constructor. <br>
     * <br>
     * YAML file example: Block List Of Strings
     *
     * <pre>
     * -US - GB - AU
     * </pre>
     *
     * Object array returned:
     *
     * <pre>
     * Object[0][0] = US
     * Object[1][0] = GB
     * Object[2][0] = AU
     * </pre>
     *
     * Test method signature example:
     *
     * <pre>
     * public void testExample(String countryCode)
     * </pre>
     *
     * <br>
     * <br>
     * YAML file example: Inline List Of Strings
     *
     * <pre>
     * [US, GB, AU]
     * </pre>
     *
     * Object array returned:
     *
     * <pre>
     * Object[0][0] = US
     * Object[1][0] = GB
     * Object[2][0] = AU
     * </pre>
     *
     * Test method signature example:
     *
     * <pre>
     * public void testExample(String countryCode)
     * </pre>
     *
     * <br>
     * <br>
     * YAML file example: Block List of Inline Associative Arrays
     *
     * <pre>
     * - {name: 1, userEmail: user1@paypal.com, userId: 10686626}
     * - {name: 2, email: user2@paypal.com, userId: 10686627}
     *
     * </pre>
     *
     * Object array returned (LinkedHashMap):
     *
     * <pre>
     * Object[0][0] = {name=1, email=user1@paypal.com, userId=10686626}
     * Object[1][0] = {name=2, email=user2@paypal.com, userId=10686627}
     * </pre>
     *
     * Test method signature example:
     *
     * <pre>
     * public void testExample(LinkedHashMap<?, ?> test)
     * </pre>
     *
     * <br>
     * <br>
     * YAML file example: Block Associative Arrays of Associative Arrays
     *
     * <pre>
     * test1:
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * test2:
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686627
     * </pre>
     *
     * Object array returned (contains LinkedHashMap):
     *
     * <pre>
     * Object[0][0] = {name=1, email=user1@paypal.com, userId=10686626}
     * Object[1][0] = {name=2, email=user2@paypal.com, userId=10686627}
     * </pre>
     *
     * Test method signature example:
     *
     * <pre>
     * public void testExample(LinkedHashMap<?, ?> test)
     * </pre>
     *
     * <br>
     * <br>
     * YAML file example: Document separated Inline Associative Arrays
     *
     * <pre>
     * ---
     * {name: 1, email: user1@paypal.com, userId: 10686626}
     * ---
     * {name: 2, email: user2@paypal.com, userId: 10686627}
     * </pre>
     *
     * Object array returned (contains LinkedHashMap):
     *
     * <pre>
     * Object[0][0] = {name=1, email=user1@paypal.com, userId=10686626}
     * Object[1][0] = {name=2, email=user2@paypal.com, userId=10686627}
     * </pre>
     *
     * Test method signature example:
     *
     * <pre>
     * public void testExample(LinkedHashMap<?, ?> test)
     * </pre>
     *
     * <br>
     * <br>
     * <br>
     * <b>Abstract User-Defined Objects</b> <br>
     * <br>
     * User-defined objects can be passed into this method so the type can be mapped in the Snakeyaml constructor with a
     * new tag. Tag is automatically set to the simple name of the class. If there are multiple objects with the same
     * simple name, then the full path must be used in the yaml file to differentiate between the two. <br>
     * <br>
     * <br>
     * A proper <a href="https://code.google.com/p/snakeyaml/wiki/Documentation#JavaBeans">JavaBean</a> must be defined
     * for the user-defined object or else an exception will be thrown while attempting to load the yaml file. <br>
     * <br>
     * YAML file example: List of MyObject
     *
     * <pre>
     * - !!com.paypal.test.resources.MyObject
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * - !!com.paypal.test.resources.MyObject
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686626
     * </pre>
     *
     * <br>
     * YAML file example: List of MyObject mapped with tag "MyObject"
     *
     * <pre>
     * - !MyObject
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * - !MyObject
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686626
     * </pre>
     *
     * Object array returned:
     *
     * <pre>
     * Object[1][0] = com.paypal.test.dataobject.MyObject@54bb7759
     * Object[2][0] = com.paypal.test.dataobject.MyObject@5f989f84
     * </pre>
     *
     * Test method signature example:
     *
     * <pre>
     * public void testExample(MyObject myObject)
     * </pre>
     *
     * <br>
     * <br>
     * For sample yaml formats, use utility methods:
     * <ul>
     * <li>{@link DataProviderHelper#serializeObjectToYamlString(Object)}
     * <li>{@link DataProviderHelper#serializeObjectToYamlStringAsList(Object...)}
     * <li>{@link DataProviderHelper#serializeObjectToYamlStringAsMap(Object...)}
     * <li>{@link DataProviderHelper#serializeObjectToYamlStringAsDocuments(Object...)}
     * </ul>
     * <br>
     * <br>
     *
     * @return Object[][] two dimensional object to be used with TestNG DataProvider
     * @throws IOException
     */
    @Override
    public Object[][] getAllData() throws IOException {
        logger.entering();

        InputStream inputStream = resource.getInputStream();
        Yaml yaml = constructYaml(resource.getCls());

        Object yamlObject;

        // Mark the input stream in case multiple documents has been detected
        // so we can reset it.
        inputStream.mark(100);

        try {
            yamlObject = yaml.load(inputStream);
        } catch (ComposerException composerException) {
            if (composerException.getMessage().contains("expected a single document")) {
                inputStream.reset();
                yamlObject = loadDataFromDocuments(yaml, inputStream);
            } else {
                throw new DataProviderException("Error reading YAML data", composerException);
            }
        }

        Object[][] objArray = DataProviderHelper.convertToObjectArray(yamlObject);

        logger.exiting((Object[]) objArray);
        return objArray;
    }

    /**
     * Gets yaml data by applying the given filter. Throws {@link DataProviderException} when unexpected error occurs
     * during processing of YAML file data by filter
     *
     * @param dataFilter
     *            an implementation class of {@link DataProviderFilter}
     * @return An iterator over a collection of Object Array to be used with TestNG DataProvider
     * @throws IOException
     */
    @Override
    public Iterator<Object[]> getDataByFilter(DataProviderFilter dataFilter)
            throws IOException {
        logger.entering(dataFilter);
        InputStream inputStream = resource.getInputStream();
        Yaml yaml = constructYaml(resource.getCls());

        Object yamlObject;

        // Mark the input stream in case multiple documents has been detected
        // so we can reset it.
        inputStream.mark(100);

        try {
            yamlObject = yaml.load(inputStream);
        } catch (ComposerException composerException) {
            String msg = composerException.getMessage();
            msg = (msg == null) ? "" : msg;
            if (msg.toLowerCase().contains("expected a single document")) {
                inputStream.reset();
                yamlObject = loadDataFromDocuments(yaml, inputStream);
            } else {
                throw new DataProviderException("Error reading YAML data", composerException);
            }
        }
        return DataProviderHelper.filterToListOfObjects(yamlObject, dataFilter).iterator();
    }

    /**
     * Gets yaml data by key identifiers. Only compatible with a yaml file formatted to return a map. <br>
     * <br>
     * YAML file example:
     *
     * <pre>
     * test1:
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * test2:
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686627
     * </pre>
     *
     * @param keys
     *            A String array that represents the keys.
     *
     * @return Object[][] two dimensional object to be used with TestNG DataProvider
     */
    @Override
    public Object[][] getDataByKeys(String[] keys) {
        logger.entering(Arrays.toString(keys));

        InputStream inputStream = resource.getInputStream();
        Yaml yaml = constructYaml(resource.getCls());

        LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) yaml.load(inputStream);

        Object[][] objArray = DataProviderHelper.getDataByKeys(map, keys);

        logger.exiting((Object[]) objArray);
        return objArray;
    }

    /**
     * Gets yaml data and returns in a hashtable instead of an Object 2D array. Only compatible with a yaml file
     * formatted to return a map. <br>
     * <br>
     * YAML file example:
     *
     * <pre>
     * test1:
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * test2:
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686627
     * </pre>
     *
     * @return yaml data in form of a Hashtable.
     */
    @Override
    public Hashtable<String, Object> getDataAsHashtable() {
        logger.entering();

        InputStream inputStream = resource.getInputStream();
        Yaml yaml = constructYaml(resource.getCls());

        Hashtable<String, Object> yamlHashTable = new Hashtable<>();

        LinkedHashMap<?, ?> yamlObject = (LinkedHashMap<?, ?>) yaml.load(inputStream);

        for (Entry<?, ?> entry : yamlObject.entrySet()) {
            yamlHashTable.put((String) entry.getKey(), entry.getValue());
        }

        logger.exiting(yamlHashTable);
        return yamlHashTable;
    }

    /**
     * Gets yaml data for requested indexes.
     *
     * @param indexes
     *            the input string represent the indexes to be parse
     *
     * @return Object[][] Two dimensional object to be used with TestNG DataProvider
     * @throws IOException
     */
    @Override
    public Object[][] getDataByIndex(String indexes) throws IOException,
            DataProviderException {

        logger.entering(indexes);
        int[] arrayIndex = DataProviderHelper.parseIndexString(indexes);
        Object[][] yamlObjRequested = getDataByIndex(arrayIndex);
        logger.exiting((Object[]) yamlObjRequested);
        return yamlObjRequested;
    }

    /**
     * Generates an object array in iterator as TestNG DataProvider from the YAML data filtered per given indexes. This
     * method may throw {@link DataProviderException} when an unexpected error occurs during data provision from YAML
     * file.
     *
     * @param indexes
     *            The indexes for which data is to be fetched as a conforming string pattern.
     *
     * @return An Object[][] object to be used with TestNG DataProvider.
     * @throws IOException
     */
    @Override
    public Object[][] getDataByIndex(int[] indexes) throws IOException {
        logger.entering(indexes);

        Object[][] yamlObj = getAllData();
        Object[][] yamlObjRequested = new Object[indexes.length][yamlObj[0].length];

        int i = 0;
        for (Integer index : indexes) {
            index--;
            yamlObjRequested[i] = yamlObj[index];
            i++;
        }

        logger.exiting((Object[]) yamlObjRequested);
        return yamlObjRequested;
    }

    /**
     * Converts a yaml file into an Object 2D array for <a
     * href="http://testng.org/doc/documentation-main.html#parameters-dataproviders"> TestNG Dataprovider</a>
     * consumption.
     *
     * <br>
     * A proper <a href="https://code.google.com/p/snakeyaml/wiki/Documentation#JavaBeans">JavaBean</a> must be defined
     * or else an exception will be thrown while attempting to load the yaml file. <br>
     * <br>
     * YAML file example:
     *
     * <pre>
     * ---
     * MyObject:
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * ---
     * MyObject:
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686626
     * </pre>
     *
     * Object array returned:
     *
     * <pre>
     * Object[1][0] = com.paypal.test.dataobject.MyObject@54bb7759
     * Object[2][0] = com.paypal.test.dataobject.MyObject@5f989f84
     * </pre>
     *
     * Test method signature example:
     *
     * <pre>
     * public void testExample(MyObject myObject)
     * </pre>
     *
     * @param yaml
     *            A {@link Yaml} object that represents a Yaml document.
     * @param inputStream
     *            A {@link InputStream} object.
     * @return an List containing multiple yaml documents loaded by SnakeYaml
     */
    private List<Object> loadDataFromDocuments(Yaml yaml, InputStream inputStream) {
        logger.entering(new Object[] { yaml, inputStream });
        Iterator<?> documents = yaml.loadAll(inputStream).iterator();
        List<Object> objList = new ArrayList<>();

        while (documents.hasNext()) {
            objList.add(documents.next());
        }

        logger.exiting(objList);
        return objList;
    }

    private Yaml constructYaml(Class<?> cls) {
        if (cls != null) {
            Constructor constructor = new Constructor();
            constructor.addTypeDescription(new TypeDescription(cls, "!" + cls.getSimpleName()));
            return new Yaml(constructor);
        }

        return new Yaml();
    }

}