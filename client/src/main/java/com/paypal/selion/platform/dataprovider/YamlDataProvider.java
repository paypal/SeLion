/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 eBay Software Foundation                                                                        |
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

public final class YamlDataProvider {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    /**
     * Hiding constructor for class that contains only static methods
     */
    private YamlDataProvider() {
        super();
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
     * <li>{@link #serializeObjectToYamlString(Object)}
     * <li>{@link #serializeObjectToYamlStringAsList(Object...)}
     * <li>{@link #serializeObjectToYamlStringAsMap(Object...)}
     * <li>{@link #serializeObjectToYamlStringAsDocuments(Object...)}
     * </ul>
     * <br>
     * <br>
     * 
     * @param resource - A {@link FileSystemResource} that represents a data source.
     * @return Object[][] two dimensional object to be used with TestNG DataProvider
     * @throws IOException
     * @throws YamlDataProviderException
     */
    public static Object[][] getAllData(FileSystemResource resource) throws IOException, YamlDataProviderException {
        logger.entering(resource);

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
                throw new YamlDataProviderException("Error reading YAML data", composerException);
            }
        }

        Object[][] objArray = DataProviderHelper.convertToObjectArray(yamlObject);

        logger.exiting(objArray);
        return objArray;
    }

    /**
     * Gets yaml data by applying the given filter.
     * 
     * @param resource  A {@link FileSystemResource} that represents a data source.
     * @param dataFilter
     *            an implementation class of {@link DataProviderFilter}
     * @return An iterator over a collection of Object Array to be used with TestNG DataProvider
     * @throws IOException
     * @throws DataProviderException 
     */
    public static Iterator<Object[]> getDataByFilter(FileSystemResource resource, DataProviderFilter dataFilter)
            throws IOException, DataProviderException {
        logger.entering(new Object[] { resource, dataFilter });
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
            msg = (msg == null) ? "": msg;
            if (msg.toLowerCase().contains("expected a single document")) {
                inputStream.reset();
                yamlObject = loadDataFromDocuments(yaml, inputStream);
            } else {
                throw new YamlDataProviderException("Error reading YAML data", composerException);
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
     * @param resource
     *            - A {@link FileSystemResource} object that represents the yaml resource to be read from.
     * @param keys
     *            - A String array that represents the keys.
     * 
     * @return Object[][] two dimensional object to be used with TestNG DataProvider
     */
    public static Object[][] getDataByKeys(FileSystemResource resource, String[] keys) {
        logger.entering(new Object[] { resource, Arrays.toString(keys) });

        InputStream inputStream = resource.getInputStream();
        Yaml yaml = constructYaml(resource.getCls());

        LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) yaml.load(inputStream);

        Object[][] objArray = DataProviderHelper.getDataByKeys(map, keys);

        logger.exiting(objArray);
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
     * @param resource  A {@link FileSystemResource} that represents a data source.
     * @return yaml data in form of a Hashtable.
     */
    public static Hashtable<String, Object> getDataAsHashtable(FileSystemResource resource) {
        logger.entering(resource);

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
     * @param resource  A {@link FileSystemResource} that represents a data source.
     * @param indexes - The indexes for which data is to be fetched
     * 
     * @return Object[][] Two dimensional object to be used with TestNG DataProvider
     * @throws IOException
     * @throws DataProviderException
     */
    public static Object[][] getDataByIndex(FileSystemResource resource, String indexes) throws IOException,
            DataProviderException {
        logger.entering(new Object[] { resource, indexes });

        Object[][] yamlObj = getAllData(resource);
        Object[][] yamlObjRequested = DataProviderHelper.getDataByIndex(yamlObj, indexes);

        logger.exiting(yamlObjRequested);
        return yamlObjRequested;
    }

    /**
     * Gets yaml data for tests that require multiple arguments. Saves a tester from needing to define another JavaBean
     * just to get multiple arguments passed in as one.
     * 
     * <br>
     * <br>
     * Example dataprovider:
     * 
     * <pre>
     * public static Object[][] dataProviderGetMultipleArguments() throws IOException {
     *     Object[][] data = null;
     *     List&lt;YamlResource&gt; yamlResources = new ArrayList&lt;YamlResource&gt;();
     *     yamlResources.add(new YamlResource(pathName, userDocuments, USER.class));
     *     yamlResources.add(new YamlResource(pathName, user2Documents, USER.class));
     * 
     *     data = new YamlDataProvider().getAllDataMultipleArgs(yamlResources);
     * 
     *     return data;
     * }
     * </pre>
     * 
     * Test method signature example:
     * 
     * <pre>
     * public void testExample(USER user1, USER user2)
     * </pre>
     * 
     * @param resources -  A List of {@link FileSystemResource} that represents data sources.
     * @return Object[][] Two dimensional object to be used with TestNG DataProvider
     * @throws IOException
     * @throws YamlDataProviderException
     */
    public static Object[][] getAllDataMultipleArgs(List<FileSystemResource> resources) throws IOException,
            YamlDataProviderException {
        logger.entering(resources);
        List<Object[][]> dataproviders = new ArrayList<>();
        Object[][] data;

        for (FileSystemResource r : resources) {
            Object[][] resourceData = getAllData(r);
            dataproviders.add(resourceData);
        }

        int maxLength = 0;
        for (Object[][] d : dataproviders) {
            if (d.length > maxLength) {
                maxLength = d.length;
            }
        }

        data = new Object[maxLength][resources.size()];

        int i = 0;
        for (Object[][] d : dataproviders) {
            for (int j = 0; j < maxLength; j++) {
                try {
                    data[j][i] = d[j][0];
                } catch (ArrayIndexOutOfBoundsException ex) {
                    data[j][i] = null;
                }
            }
            i++;
        }

        logger.exiting(data);
        return data;
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
     * @param yaml - A {@link Yaml} object that represents a Yaml document.
     * @param inputStream - A {@link InputStream} object.
     * @return an List containing multiple yaml documents loaded by SnakeYaml
     */
    private static List<Object> loadDataFromDocuments(Yaml yaml, InputStream inputStream) {
        logger.entering(new Object[] { yaml, inputStream });
        Iterator<?> documents = yaml.loadAll(inputStream).iterator();
        List<Object> objList = new ArrayList<>();

        while (documents.hasNext()) {
            objList.add(documents.next());
        }

        logger.exiting(objList);
        return objList;
    }

    private static Yaml constructYaml(Class<?> cls) {
        if (cls != null) {
            Constructor constructor = new Constructor();
            constructor.addTypeDescription(new TypeDescription(cls, "!" + cls.getSimpleName()));
            return new Yaml(constructor);
        }

        return new Yaml();
    }

    /**
     * Use this utility method to print and return a yaml string to help serialize the object passed in.
     * 
     * @param object - The Object that is to be serialised.
     * @return a yaml string representation of the object passed in
     */
    public static String serializeObjectToYamlString(Object object) {
        logger.entering(object);
        Yaml yaml = new Yaml();
        String output = yaml.dump(object);
        logger.exiting(output);
        return output;
    }

    /**
     * Use this utility method to print and return a yaml string to help serialize the object passed in as an ArrayList.
     * 
     * @param objects - One or more objects that are to be serialised.
     * @return a yaml string representation of the object(s) passed in
     */
    public static String serializeObjectToYamlStringAsList(Object... objects) {
        logger.entering(new Object[] { objects });
        String output = serializeObjectToYamlString(Arrays.asList(objects).iterator());
        logger.exiting(output);
        return output;
    }

    /**
     * Use this utility method to print and return a yaml string to help serialize the object passed in as a
     * LinkedHashMap.
     * 
     * @param objects - One or more objects that are to be serialised.
     * @return a yaml string representation of the object(s) passed in
     */
    public static String serializeObjectToYamlStringAsDocuments(Object... objects) {
        logger.entering(new Object[] { objects });
        Yaml yaml = new Yaml();
        String output = yaml.dumpAll(Arrays.asList(objects).iterator());
        logger.exiting(output);
        return output;
    }

    /**
     * Use this utility method to print and return a yaml string to help serialize the object passed in as multiple
     * documents.
     * 
     * @param objects - The objects that are to be serialised.
     * @return a yaml string representation of the object(s) passed in
     */
    public static String serializeObjectToYamlStringAsMap(Object... objects) {
        logger.entering(new Object[] { objects });
        HashMap<String, Object> objMap = new LinkedHashMap<>();

        String key;
        int i = 0;
        for (Object obj : objects) {
            key = "uniqueKey" + Integer.toString(i);
            objMap.put(key, obj);
            i++;
        }

        String output = serializeObjectToYamlString(objMap);
        logger.exiting(output);
        return output;
    }

}