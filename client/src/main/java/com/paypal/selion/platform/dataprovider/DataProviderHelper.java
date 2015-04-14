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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.dataprovider.filter.DataProviderFilter;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A utility class intended to serve as a helper class for miscellaneous operations being done by
 * {@link SimpleExcelDataProvider} and {@link YamlDataProvider} and {@link XmlDataProvider}.
 * 
 */
// TODO: This should perhaps be moved into an internal package because this internal class now is exposed outside.
public final class DataProviderHelper {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    // Hidden default constructor for class that provides static methods.
    private DataProviderHelper() {

    }

    /**
     * This function will parse the index string into separated individual indexes as needed. Calling the method with a
     * string containing "1, 3, 5-7, 11, 12-14, 8" would return a list of Integers {1, 3, 5, 6, 7, 11, 12, 13, 14, 8}
     * 
     * @param value
     *            the input string represent the indexes to be parse.
     * @return a list of indexes represented as Integers
     * @throws DataProviderException
     */
    public static List<Integer> parseIndexString(String value) throws DataProviderException {
        logger.entering(value);

        List<Integer> indexes = new ArrayList<Integer>();
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
                    String msg = "Index '" + index + "' is invalid. Please "
                            + "provide either individual numbers or ranges.";
                    msg += "Range needs to be de-marked by '-'";
                    throw new DataProviderException(msg, e);
                }
            }
        }

        logger.exiting(indexes);
        return indexes;
    }

    /**
     * Converts any object into 2 dimensional array representing TestNG DataProvider.
     * 
     * Following are the results for various types represented by {@code object}, in the order the {@code object} is
     * processed :-
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
        if (rootClass.equals(LinkedHashMap.class)) {
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
        else if (rootClass.equals(ArrayList.class)) {
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
                if (eachArrayListObject.getClass().equals(LinkedHashMap.class)) {
                    LinkedHashMap<?, ?> eachArrayListObjectAsHashMap = (LinkedHashMap<?, ?>) eachArrayListObject;
                    for (Object eachEntry : eachArrayListObjectAsHashMap.values()) {
                        if (eachEntry.getClass().equals(LinkedHashMap.class)
                                || eachEntry.getClass().equals(ArrayList.class)) {
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
        logger.exiting();
        return objArray;
    }

    /**
     * Converts in particular LinkedHashMap of Objects, ArrayList of Objects, Array of Object into ArrayList after
     * applying the given filter.
     * 
     * @param object
     *            Object of any type.
     * @return List<Object[]> a ArrayList of objects to be used with TestNG DataProvider
     */
    public static List<Object[]> filterToListOfObjects(Object object, DataProviderFilter dataFilter)
            throws DataProviderException {
        logger.entering(object);
        List<Object[]> objs = new ArrayList<Object[]>();
        Class<?> rootClass = object.getClass();

        // Convert a LinkedHashMap (e.g. Yaml Associative Array) to an array list after applying filter.
        if (rootClass.equals(LinkedHashMap.class)) {
            LinkedHashMap<?, ?> objAsLinkedHashMap = (LinkedHashMap<?, ?>) object;
            Collection<?> allValues = objAsLinkedHashMap.values();
            for (Object eachValue : allValues) {
                if (dataFilter.filter(eachValue)) {
                    objs.add(new Object[] { eachValue });
                }
            }
        }
        // Converts an ArrayList to an array list after applying filter.
        else if (rootClass.equals(ArrayList.class)) {
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
                if (eachArrayListObject.getClass().equals(LinkedHashMap.class)) {
                    LinkedHashMap<?, ?> eachArrayListObjectAsHashMap = (LinkedHashMap<?, ?>) eachArrayListObject;
                    for (Object eachEntry : eachArrayListObjectAsHashMap.values()) {
                        if (eachEntry.getClass().equals(LinkedHashMap.class)
                                || eachEntry.getClass().equals(ArrayList.class)) {
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
     * Filters data set by one-based indexes specified in textual representation.
     * 
     * @param dataProvider2dArray
     * @param indexes
     * @return Object[][] two dimensional object to be used with TestNG DataProvider.
     * @throws DataProviderException
     */
    public static Object[][] getDataByIndex(Object[][] dataProvider2dArray, String indexes)
            throws DataProviderException {
        logger.entering(new Object[] { dataProvider2dArray, indexes });

        List<Integer> listIndexes = DataProviderHelper.parseIndexString(indexes);
        Object[][] dataProvider2dArrayRequested = DataProviderHelper.getDataByIndexList(dataProvider2dArray,
                listIndexes);
        logger.exiting();
        return dataProvider2dArrayRequested;
    }

    /**
     * Filters data set by one-based indexes specified as a list.
     * 
     * @param dataProvider2dArray
     * @param listIndexes
     * @return Object[][] two dimensional object to be used with TestNG DataProvider.
     */
    public static Object[][] getDataByIndexList(Object[][] dataProvider2dArray, List<Integer> indexList) {
        logger.entering(new Object[] { dataProvider2dArray, indexList });
        if ((null == indexList) || indexList.isEmpty()) {
            throw new IllegalArgumentException("Keys cannot be null or empty.");
        }

        Object[][] dataProvider2dArrayRequested = new Object[indexList.size()][];

        int i = 0;
        for (Integer index : indexList) {
            dataProvider2dArrayRequested[i++] = dataProvider2dArray[--index];
        }

        Object[][] objectArray = dataProvider2dArrayRequested;

        logger.exiting();
        return objectArray;
    }

    /**
     * Filters a map by keys specified as a list.
     * 
     * @param map
     *            The Map containing keys.
     * @param keys
     *            Non-empty array of string keys.
     * @return Object[][] two dimensional object to be used with TestNG DataProvider.
     */
    public static Object[][] getDataByKeys(Map<?, ?> map, String[] keys) {
        logger.entering(new Object[] { map, keys });

        if (ArrayUtils.isEmpty(keys)) {
            throw new IllegalArgumentException("Keys cannot be null or empty.");
        }

        Map<String, Object> requestedMap = new LinkedHashMap<String, Object>();

        for (String key : keys) {
            Object obj = map.get(key);
            if (obj == null) {
                throw new IllegalArgumentException("Key not found, returned null value: " + key);
            }
            requestedMap.put(key, obj);
        }

        Object[][] objArray = DataProviderHelper.convertToObjectArray(requestedMap);

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
     * @param resources
     * @return Object[][] Two dimensional object to be used with TestNG DataProvider
     */
    public static Object[][] getAllDataMultipleArgs(Object[][]... dataproviders) {
        logger.entering();

        Object[][] data = null;
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

        logger.exiting();
        return data;
    }
}
