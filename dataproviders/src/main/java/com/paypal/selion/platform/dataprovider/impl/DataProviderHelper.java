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
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.dataprovider.DataProviderException;
import com.paypal.selion.platform.dataprovider.DataProviderFactory;
import com.paypal.selion.platform.dataprovider.DataResource;
import com.paypal.selion.platform.dataprovider.SeLionDataProvider;
import com.paypal.selion.platform.dataprovider.filter.DataProviderFilter;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A utility class intended to serve as a helper class for miscellaneous operations being done by
 * {@link ExcelDataProviderImpl} and {@link YamlDataProviderImpl} and {@link XmlDataProviderImpl}.
 * 
 */
public final class DataProviderHelper {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    // Hidden default constructor for class that provides static methods.
    private DataProviderHelper() {

    }

    /**
     * This function will parse the index string into separated individual indexes as needed. Calling the method with a
     * string containing "1, 3, 5-7, 11, 12-14, 8" would return an list of integers {1, 3, 5, 6, 7, 11, 12, 13, 14, 8}.
     * Use ',' to separate values, and use '-' to specify a continuous range. Presence of an invalid character would
     * result in {@link DataProviderException}.
     * 
     * @param value
     *            the input string represent the indexes to be parse.
     * @return a list of indexes as an integer array
     */
    public static int[] parseIndexString(String value) {
        logger.entering(value);

        List<Integer> indexes = new ArrayList<>();
        int begin, end;
        String[] parsed;
        String[] parsedIndex = value.split(",");
        for (String index : parsedIndex) {
            if (index.contains("-")) {
                parsed = index.split("-");
                begin = Integer.parseInt(parsed[0].trim());
                end = Integer.parseInt(parsed[1].trim());
                for (int i = begin; i <= end; i++) {
                    indexes.add(i);
                }
            } else {
                try {
                    indexes.add(Integer.parseInt(index.trim()));
                } catch (NumberFormatException e) {
                    String msg = new StringBuilder("Index '").append(index)
                            .append("' is invalid. Please provide either individual numbers or ranges.")
                            .append("\n Range needs to be de-marked by '-'").toString();
                    throw new DataProviderException(msg, e);
                }
            }
        }

        int[] indexArray = Ints.toArray(indexes);
        logger.exiting(indexArray);
        return indexArray;
    }

    /**
     * Converts any object into 2 dimensional array representing TestNG DataProvider.
     *
     * Following are the results for various types represented by {@code object}, in the order the {@code object} is
     * processed:
     *
     * <pre>
     * Object - By default, an object is returned at position [0][0] of Object[1][1].
     * </pre>
     *
     * <pre>
     * LinkedHashMap - When
     * the root type of object is a {@link LinkedHashMap} (with <i>n<i> number of key-value mappings), the value at each
     * <i>index</i> of the map is returned at position [index][0] of Object[<i>n</i>][1].
     *
     * </pre>
     *
     * <pre>
     * ArrayList - When
     * the root type of object is an {@link ArrayList} (with <i>n<i> number of items), the item at each
     * <i>index</i> of the list is returned at position [index][0] of Object[<i>n</i>][1].
     * 
     * When the value is {@link LinkedHashMap} having child value as type of {@link LinkedHashMap} or {@link ArrayList}, the child value is returned instead.
     * </pre>
     *
     * <pre>
     * Array of primitive types - When
     * the root type of object is single dimensional {@link Array} (with <i>n<i> number of items), the value at each
     * <i>index</i> of the array is returned at position [index][0] of Object[<i>n</i>][1].
     * </pre>
     *
     * <pre>
     * Array of Object types - When
     * the root type of object is single dimensional {@link Array} (with <i>n<i> number of objects), the object at each
     * <i>index</i> of the array is returned at position [index][0] of Object[<i>n</i>][1].
     * </pre>
     *
     * @param object
     *            Object of any type.
     * @return Object[][] two dimensional object to be used with TestNG DataProvider
     */
    public static Object[][] convertToObjectArray(Object object) {
        logger.entering(object);

        // Converts single instance of any type as an object at position [0][0] in an Object double array of size
        // [1][1].
        Object[][] objArray = new Object[][] { { object } };

        Class<?> rootClass = object.getClass();

        // Convert a LinkedHashMap (e.g. Yaml Associative Array) to an Object double array.
        if (rootClass.equals(LinkedHashMap.class)) { // NOSONAR
            LinkedHashMap<?, ?> objAsLinkedHashMap = (LinkedHashMap<?, ?>) object;
            Collection<?> allValues = objAsLinkedHashMap.values();
            objArray = new Object[allValues.size()][1];
            int i = 0;
            for (Object eachValue : allValues) {
                objArray[i][0] = eachValue;
                i++;
            }
        }

        // Converts an ArrayList (e.g. Yaml List) to an Object double array.
        else if (rootClass.equals(ArrayList.class)) { // NOSONAR
            ArrayList<?> objAsArrayList = (ArrayList<?>) object;
            objArray = new Object[objAsArrayList.size()][1];

            int i = 0;
            for (Object eachArrayListObject : objAsArrayList) {

                /*
                 * Handles LinkedHashMap nested in a LinkedHashMap (e.g. Yaml associative array). This block removes the
                 * first mapping since that data serves as visual organization of data within a Yaml. If the parent is a
                 * LinkedHashMap and the child is a LinkedHashMap or an ArrayList, then assign the child to the Object
                 * double array instead of the parent.
                 */
                objArray[i][0] = eachArrayListObject;
                if (eachArrayListObject.getClass().equals(LinkedHashMap.class)) { // NOSONAR
                    LinkedHashMap<?, ?> eachArrayListObjectAsHashMap = (LinkedHashMap<?, ?>) eachArrayListObject;
                    for (Object eachEntry : eachArrayListObjectAsHashMap.values()) {
                        if (eachEntry.getClass().equals(LinkedHashMap.class) // NOSONAR
                                || eachEntry.getClass().equals(ArrayList.class)) { // NOSONAR
                            objArray[i][0] = eachEntry;
                        }
                    }
                }
                i++;
            }
        }

        // Converts an List of simple types to an Object double array.
        else if (rootClass.isArray()) {
            int i = 0;

            if (!(object instanceof Object[])) {
                if (object instanceof int[]) {
                    objArray = new Object[((int[]) object).length][1];
                    for (int item : (int[]) object) {
                        objArray[i++][0] = item;
                    }
                } else if (object instanceof char[]) {
                    objArray = new Object[((char[]) object).length][1];
                    for (char item : (char[]) object) {
                        objArray[i++][0] = item;
                    }
                } else if (object instanceof short[]) {
                    objArray = new Object[((short[]) object).length][1];
                    for (short item : (short[]) object) {
                        objArray[i++][0] = item;
                    }
                } else if (object instanceof boolean[]) {
                    objArray = new Object[((boolean[]) object).length][1];
                    for (boolean item : (boolean[]) object) {
                        objArray[i++][0] = item;
                    }
                } else if (object instanceof long[]) {
                    objArray = new Object[((long[]) object).length][1];
                    for (long item : (long[]) object) {
                        objArray[i++][0] = item;
                    }
                } else if (object instanceof double[]) {
                    objArray = new Object[((double[]) object).length][1];
                    for (double item : (double[]) object) {
                        objArray[i++][0] = item;
                    }
                } else if (object instanceof float[]) {
                    objArray = new Object[((float[]) object).length][1];
                    for (float item : (float[]) object) {
                        objArray[i++][0] = item;
                    }
                } else if (object instanceof byte[]) {
                    objArray = new Object[((byte[]) object).length][1];
                    for (byte item : (byte[]) object) {
                        objArray[i++][0] = item;
                    }
                }
            }

            // Converts unknown object Array to an Object double array.
            else {
                objArray = new Object[((Object[]) object).length][1];
                for (Object item : (Object[]) object) {
                    objArray[i++][0] = item;
                }
            }
        }

        // Passing no arguments to exiting() because implementation to print 2D array could be highly recursive.
        logger.exiting();
        return objArray;
    }

    /**
     * Converts in particular LinkedHashMap of Objects, ArrayList of Objects, Array of Object into ArrayList after
     * applying the given filter.
     *
     * @param object
     *            Object of any type.
     * @return List<Object> a ArrayList of objects to be used with TestNG DataProvider
     */
    public static List<Object[]> filterToListOfObjects(Object object, DataProviderFilter dataFilter) {
        logger.entering(object);
        List<Object[]> objs = new ArrayList<>();
        Class<?> rootClass = object.getClass();

        // Convert a LinkedHashMap (e.g. Yaml Associative Array) to an array list after applying filter.
        if (rootClass.equals(LinkedHashMap.class)) { // NOSONAR
            LinkedHashMap<?, ?> objAsLinkedHashMap = (LinkedHashMap<?, ?>) object;
            Collection<?> allValues = objAsLinkedHashMap.values();
            for (Object eachValue : allValues) {
                if (dataFilter.filter(eachValue)) {
                    objs.add(new Object[] { eachValue });
                }
            }
        }
        // Converts an ArrayList to an array list after applying filter.
        else if (rootClass.equals(ArrayList.class)) { // NOSONAR
            ArrayList<?> objAsArrayList = (ArrayList<?>) object;
            for (Object eachArrayListObject : objAsArrayList) {
                /*
                 * Handles LinkedHashMap nested in a LinkedHashMap (e.g. Yaml/xml associative array). This block removes
                 * the first mapping since that data serves as visual organization of data within a Yaml/xml. If the
                 * parent is a LinkedHashMap and the child is a LinkedHashMap or an ArrayList, then assign the child to
                 * the Object double array instead of the parent.
                 */
                if (dataFilter.filter(eachArrayListObject)) {
                    objs.add(new Object[] { eachArrayListObject });
                }
                if (eachArrayListObject.getClass().equals(LinkedHashMap.class)) { // NOSONAR
                    LinkedHashMap<?, ?> eachArrayListObjectAsHashMap = (LinkedHashMap<?, ?>) eachArrayListObject;
                    for (Object eachEntry : eachArrayListObjectAsHashMap.values()) {
                        if (eachEntry.getClass().equals(LinkedHashMap.class)
                                || eachEntry.getClass().equals(ArrayList.class)) { // NOSONAR
                            if (dataFilter.filter(eachEntry)) {
                                objs.add(new Object[] { eachEntry });
                            }
                        }
                    }
                }
            }
        }
        // Converts an List of simple types to an array list after applying filter.
        else if (rootClass.isArray()) {
            if (!(object instanceof Object[])) {
                if (object instanceof int[]) {
                    for (int item : (int[]) object) {
                        if (dataFilter.filter(item)) {
                            objs.add(new Object[] { item });
                        }
                    }
                } else if (object instanceof char[]) {
                    for (char item : (char[]) object) {
                        if (dataFilter.filter(item)) {
                            objs.add(new Object[] { item });
                        }
                    }
                } else if (object instanceof short[]) {
                    for (short item : (short[]) object) {
                        if (dataFilter.filter(item)) {
                            objs.add(new Object[] { item });
                        }
                    }
                } else if (object instanceof boolean[]) {
                    for (boolean item : (boolean[]) object) {
                        if (dataFilter.filter(item)) {
                            objs.add(new Object[] { item });
                        }
                    }
                } else if (object instanceof long[]) {
                    for (long item : (long[]) object) {
                        if (dataFilter.filter(item)) {
                            objs.add(new Object[] { item });
                        }
                    }
                } else if (object instanceof double[]) {
                    for (double item : (double[]) object) {
                        if (dataFilter.filter(item)) {
                            objs.add(new Object[] { item });
                        }
                    }
                } else if (object instanceof float[]) {
                    for (float item : (float[]) object) {
                        if (dataFilter.filter(item)) {
                            objs.add(new Object[] { item });
                        }
                    }
                } else if (object instanceof byte[]) {
                    for (byte item : (byte[]) object) {
                        if (dataFilter.filter(item)) {
                            objs.add(new Object[] { item });
                        }
                    }
                }
            }
            // Converts unknown object Array to an array list after applying filter.
            else {
                for (Object item : (Object[]) object) {
                    if (dataFilter.filter(item)) {
                        objs.add(new Object[] { item });
                    }
                }
            }
        }
        logger.exiting(objs);
        return objs;
    }

    /**
     * Filters a map by keys specified as a list.
     *
     * @param map
     *            The Map containing keys.
     * @param keys
     *            Non-empty array of string keys.
     * @return Object[][] two dimensional object to be used with TestNG DataProvider.
     * @throws IllegalArgumentException
     *             When the argument to {@code keys} is null, or any keys is not contained by the {@code map}.
     */
    public static Object[][] getDataByKeys(Map<?, ?> map, String[] keys) {
        logger.entering(new Object[] { map, keys });

        if (ArrayUtils.isEmpty(keys)) {
            throw new IllegalArgumentException("Keys cannot be null or empty.");
        }

        Map<String, Object> requestedMap = new LinkedHashMap<>();

        for (String key : keys) {
            Object obj = map.get(key);
            if (obj == null) {
                throw new IllegalArgumentException("Key not found, returned null value: " + key);
            }
            requestedMap.put(key, obj);
        }

        Object[][] objArray = DataProviderHelper.convertToObjectArray(requestedMap);

        // Passing no arguments to exiting() because implementation to print 2D array could be highly recursive.
        logger.exiting();
        return objArray;
    }

    /**
     * Converts multiple data from multiple 2D DataProvider arrays of various types into one DataProvider 2D array. This
     * helps when test data is managed in multiple resource files.
     *
     * <br>
     * Example DataProvider:
     *
     * <pre>
     * public static Object[][] dataProviderGetMultipleArguments() throws IOException {
     *     Object[][] data = null;
     *     FileSystemResource resource1 = new FileSystemResource(pathName, listOfUsersInYaml, User.class);
     *     FileSystemResource resource2 = new FileSystemResource(pathName, listOfAddressesInXml, Address.class);
     * 
     *     Object[][] data1 = YamlDataProvider.getAllData(resource1);
     *     Object[][] data2 = XmlDataProvider.getAllData(resource2);
     * 
     *     data = new DataProviderHelper.getAllDataMultipleArgs(data1, data2);
     * 
     *     return data;
     * }
     * </pre>
     *
     *
     * Test method signature example 1:
     *
     * <pre>
     * public void testExample(User user, Address address)
     * </pre>
     *
     * Test method signature example 2:
     *
     * <pre>
     * public void testExample(User fromUser, User toUser)
     * </pre>
     *
     * Test method signature example 2:
     *
     * <pre>
     * public void testExample(User user, Address address, CreditCardInfo creditCard)
     * </pre>
     *
     *
     * @param dataproviders
     *            An array of multiple 2D DataProvider arrays of various types that are to be clubbed together.
     * @return Object[][] Two dimensional object to be used with TestNG DataProvider
     */
    public static Object[][] getAllDataMultipleArgs(Object[][]... dataproviders) {
        logger.entering();

        Object[][] data;
        int maxLength = 0;
        for (Object[][] d : dataproviders) {
            if (d.length > maxLength) {
                maxLength = d.length;
            }
        }

        data = new Object[maxLength][dataproviders.length];

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

        // Passing no arguments to exiting() because implementation to print 2D array could be highly recursive.
        logger.exiting();
        return data;
    }

    /**
     * Utility method to convert raw Json strings into a type.
     *
     * @param jsonString
     *            The Json data as a {@link String}
     * @param typeToMap
     *            The type to which the jsonString must be mapped to
     * @return An {@link Object} that corresponds to the type specified.
     */
    public static Object convertJsonStringToObject(String jsonString, Type typeToMap) {
        Preconditions.checkArgument(typeToMap != null, "typeToMap argument cannot be null");
        Preconditions.checkArgument(!StringUtils.isEmpty(jsonString),
                "A valid string is required to convert the Json to Object");
        logger.entering(new Object[] { jsonString, typeToMap });
        Gson jsonParser = new Gson();
        Object parsedData = jsonParser.fromJson(jsonString, typeToMap);
        logger.exiting(parsedData);
        return parsedData;
    }

    /**
     * Traverses the object graph by following an XPath expression and returns the desired type from object matched at
     * the XPath.
     *
     * Supports single object retrieval. Also see {@link DataProviderHelper#readListByXpath(Object, Class, String)}.
     *
     * Note: Need {@code object} and {@code cls} to have getter and setter properties defined to allow object graph
     * traversal.
     *
     * @param object
     *            An object of any type
     * @param cls
     *            Type of the property being evaluated at the given {@code xpath}.
     * @param xpath
     *            The XPath expression equivalent to the object graph.
     * @return An object of desired type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T readObjectByXpath(Object object, Class<T> cls, String xpath) {
        logger.entering(new Object[] { object, cls, xpath });
        JXPathContext context = JXPathContext.newContext(object);
        T value = (T) context.getValue(xpath);
        logger.exiting(value);
        return value;
    }

    /**
     * Traverses the object graph by following an XPath expression and returns a list of desired type from object
     * matched at the XPath.
     *
     * Only supports multiple object retrieval as a list. See // *
     * {@link DataProviderHelper#readObjectByXpath(Object, Class, String)} for single object retrieval.
     *
     * Note: Need {@code object} and {@code cls} to have getter and setter properties defined to allow object graph
     * traversal.
     *
     * @param object
     *            An object of any type
     * @param cls
     *            Type of the property being evaluated at the given {@code xpath}.
     * @param xpath
     *            The XPath expression equivalent to the object graph.
     * @return An object of desired type.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> List<T> readListByXpath(Object object, Class<T> cls, String xpath) {
        logger.entering(new Object[] { object, cls, xpath });
        JXPathContext context = JXPathContext.newContext(object);
        List<T> values = new ArrayList<>();
        for (Iterator iter = context.iterate(xpath); iter.hasNext();) {
            T value = (T) iter.next();
            values.add(value);
        }
        logger.exiting(values);
        return values;
    }

    /**
     * Use this utility method to print and return a yaml string to help serialize the object passed in.
     *
     * @param object
     *            The Object that is to be serialised.
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
     * @param objects
     *            One or more objects that are to be serialised.
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
     * @param objects
     *            One or more objects that are to be serialised.
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
     * @param objects
     *            The objects that are to be serialised.
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
     *     data = DataProviderHelper.getAllDataMultipleArgsFromYAML(yamlResources);
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
     * @return Object[][] Two dimensional object to be used with TestNG DataProvider
     * @throws IOException
     */
    public static Object[][] getAllDataMultipleArgsFromYAML(List<DataResource> resources) throws IOException {
        logger.entering(resources);

        if (resources == null) {
            throw new DataProviderException("Resource can not be null");
        }

        List<Object[][]> dataproviders = new ArrayList<>();
        int maxLength = 0;

        for (DataResource r : resources) {
            SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(r);
            Object[][] resourceData = dataProvider.getAllData();
            dataproviders.add(resourceData);
            if (resourceData.length > maxLength) {
                maxLength = resourceData.length;
            }
        }

        Object[][] data = new Object[maxLength][resources.size()];

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

        logger.exiting((Object[]) data);
        return data;
    }
}
