/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ClassUtils;

import com.google.common.base.Preconditions;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

import org.apache.poi.ss.usermodel.Row;

/**
 * This class provide several methods to retrieve test data from an Excel workbook. Users can get a single row of data
 * by providing the excel filename, the data sheet name, and they key. Or get the whole data sheet by providing excel
 * file name and the data sheet name.
 */

public abstract class AbstractExcelDataProvider {

    protected ExcelReader excelReader;

    protected static final SimpleLogger logger = SeLionLogger.getLogger();
    private List<DefaultCustomType> customTypes = new ArrayList<DefaultCustomType>();

    public AbstractExcelDataProvider(String pathName, String fileName) throws IOException {
        excelReader = new ExcelReader(pathName, fileName);
    }

    /**
     * @param type
     *            - A {@link DefaultCustomType} that represents custom types that need to be taken into consideration
     *            when generating an Object that represents every row of data from the excel sheet.
     */
    public final void addCustomTypes(DefaultCustomType type) {
        Preconditions.checkArgument(type != null, "Type cannot be null.");
        customTypes.add(type);
    }

    /**
     * @param myObj
     *            - the user defined type object which provide details structure.
     * @return an object of type {@link Hashtable} that represents the excel sheet data in form of hashTable.
     */
    public abstract Hashtable<String, Object> getAllRowsAsHashTable(Object myObj);

    /**
     * This method fetches a specific row from an excel sheet which can be identified using a key and returns the data
     * as an Object which can be cast back into the user's actual data type.
     * 
     * @param userObj
     *            - An Object into which data is to be packed into
     * @param key
     *            - A string that represents a key to search for in the excel sheet
     * @return - An Object which can be cast into the user's actual data type.
     */
    public abstract Object getSingleExcelRow(Object userObj, String key);

    /**
     * This method fetches a specific row from an excel sheet which can be identified using a key and returns the data
     * as an Object which can be cast back into the user's actual data type.
     * 
     * @param userObj
     *            - An Object into which data is to be packed into
     * @param key
     *            - A string that represents a key to search for in the excel sheet
     * @param isExternalCall
     *            - A boolean that helps distinguish internally if the call is being made internally or by the user. For
     *            external calls the index of the row would need to be bumped up,because the first row is to be ignored
     *            always.
     * @return - An Object which can be cast into the user's actual data type.
     */
    protected Object getSingleExcelRow(Object userObj, String key, boolean isExternalCall) {
        logger.entering(new Object[] { userObj, key, isExternalCall });
        Class<?> cls;
        try {
            cls = Class.forName(userObj.getClass().getName());
        } catch (ClassNotFoundException e) {
            throw new DataProviderException("Unable to find class of type + '" + userObj.getClass().getName() + "'", e);
        }
        int rowIndex = excelReader.getRowIndex(cls.getSimpleName(), key);

        if (rowIndex == -1) {
            throw new DataProviderException("Row with key '" + key + "' is not found");
        }
        Object object = getSingleExcelRow(userObj, rowIndex, isExternalCall);
        logger.exiting(object);
        return object;

    }

    /**
     * @param userObj
     *            - The User defined object into which the data is to be packed into.
     * @param index
     *            - The row number from the excel sheet that is to be read. For e.g., if you wanted to read the 2nd row
     *            (which is where your data exists) in your excel sheet, the value for index would be 1. <b>This method
     *            assumes that your excel sheet would have a header which it would EXCLUDE.</b> When specifying index
     *            value always remember to ignore the header, since this method will look for a particular row ignoring
     *            the header row.
     * @param isExternalCall
     *            - A boolean that helps distinguish internally if the call is being made internally or by the user.
     * 
     * @return - An object that represents the data for a given row in the excel sheet.
     * 
     */
    protected Object getSingleExcelRow(Object userObj, int index, boolean isExternalCall) {
        if (isExternalCall) {
            index++;

        }
        logger.entering(new Object[] { userObj, index });
        Object obj = null;

        Class<?> cls;
        try {
            cls = Class.forName(userObj.getClass().getName());
        } catch (ClassNotFoundException e) {
            throw new DataProviderException("Unable to find class of type + '" + userObj.getClass().getName() + "'", e);
        }
        Field[] fields = cls.getDeclaredFields();

        List<String> excelRowData = getRowContents(cls.getSimpleName(), index, fields.length);
        if (excelRowData != null && excelRowData.size() != 0) {
            try {
                obj = prepareObject(userObj, fields, excelRowData);
            } catch (IllegalAccessException e) {
                throw new DataProviderException("Unable to create instance of type '" + userObj.getClass().getName()
                        + "'", e);
            }
        } else {
            throw new DataProviderException("Row with key '" + index + "' is not found");
        }

        logger.exiting(obj);

        return obj;
    }

    /**
     * This method can be used to fetch a particular row from an excel sheet.
     * 
     * @param userObj
     *            - The User defined object into which the data is to be packed into.
     * @param index
     *            - The row number from the excel sheet that is to be read. For e.g., if you wanted to read the 2nd row
     *            (which is where your data exists) in your excel sheet, the value for index would be 1. <b>This method
     *            assumes that your excel sheet would have a header which it would EXCLUDE.</b> When specifying index
     *            value always remember to ignore the header, since this method will look for a particular row ignoring
     *            the header row.
     * @return - An object that represents the data for a given row in the excel sheet.
     */
    public abstract Object getSingleExcelRow(Object userObj, int index);

    /**
     * This function will use the input string representing the indexes to collect and return the correct excel sheet
     * data rows as two dimensional object to be used as TestNG DataProvider.
     * 
     * @param myData
     *            the user defined type object which provide details structure to this function.
     * @param indexes
     *            the string represent the keys for the search and return the wanted rows. It is in the format of: <li>
     *            "1, 2, 3" for individual indexes. <li>"1-4, 6-8, 9-10" for ranges of indexes. <li>
     *            "1, 3, 5-7, 10, 12-14" for mixing individual and range of indexes.
     * @return Object[][] Two dimensional object to be used with TestNG DataProvider
     */
    public abstract Object[][] getExcelRows(Object myData, String indexes);

    /**
     * This function will use the input string representing the keys to collect and return the correct excel sheet data
     * rows as two dimensional object to be used as TestNG DataProvider.
     * 
     * @param myObj
     *            the user defined type object which provides details structure to this function.
     * @param keys
     *            the string represents the list of key for the search and return the wanted row. It is in the format of
     *            {"row1", "row3", "row5"}
     * @return Object[][] two dimensional object to be used with TestNG DataProvider
     */
    public abstract Object[][] getExcelRows(Object myObj, String[] keys);

    /**
     * This function will read the whole excel sheet and map the data into two-dimensional array of object which is
     * compatible with TestNG DataProvider to provide real test data driven development. This function will ignore all
     * rows in which keys are preceded by "#" as a comment character.
     * 
     * For the function to work, the sheet names have to be exactly named as the user defined data type.
     * 
     * 
     * @param myObj
     *            the user defined type object which provide details structure to this function.
     * @return Object[][] a two-dimensional object to be used with TestNG DataProvider
     */
    public abstract Object[][] getAllExcelRows(Object myObj);

    private DefaultCustomType fetchMatchingCustomType(Class<?> type) {
        for (DefaultCustomType eachCustomType : customTypes) {
            if (type.equals(eachCustomType.getCustomTypeClass())) {
                return eachCustomType;
            }
        }
        return null;
    }

    /**
     * Currently this function will handle these data types:
     * <ul>
     * <li>1. Primitive data type: int, boolean, double, float, long</li>
     * <li>2. Object data type: String, Integer, Double, Float, Long</li>
     * <li>3. Array of primitive data type: int[], boolean[], double[], float[], long[]</li>
     * <li>4. Array of object data type: String[], Integer[], Boolean[], Double[], Float[], Long[]</li>
     * <li>5. User defined data type.</li>
     * <li>6. Array of user defined data type.</li>
     * </ul>
     * 
     * 
     * @param userObj
     *            this object is used by the function to extract the object info, such as class name, objects
     *            declarations, object data structure...
     * @param fields
     *            the array contains the list of name in the specify data structure
     * @param excelRowData
     *            the raw data read from the excel sheet to be extracted and filled up the object before return the full
     *            object to the caller.
     * @return Object which can be cast into a user defined type to get access to its fields
     */
    protected Object prepareObject(Object userObj, Field[] fields, List<String> excelRowData)
            throws IllegalAccessException {
        logger.entering(new Object[] { userObj, fields, excelRowData });
        Object objectToReturn = createObjectToUse(userObj);
        int index = 0;
        for (Field eachField : fields) {
            // If the data is not present in excel sheet then skip it
            String data = excelRowData.get(index++);
            if (StringUtils.isEmpty(data)) {
                continue;
            }

            Class<?> eachFieldType = eachField.getType();

            if (eachFieldType.isInterface()) {
                // We cannot work with Interfaces because for instantiating them we would need to use Proxy
                // and also build in assumptions on what type of the implementation we are going to be providing back to
                // the user.
                // things get complex if the user supplies us with an interface of which we dont have any idea.
                // so lets just throw an error and bail out.
                throw new IllegalArgumentException(eachField.getName()
                        + " is an interface. Interfaces are not supported.");
            }

            eachField.setAccessible(true);
            boolean isArray = eachFieldType.isArray();
            DataMemberInformation memberInfo = new DataMemberInformation(eachField, userObj, objectToReturn, data);
            if (isArray) {
                try {
                    setValueForArrayType(memberInfo);
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | InstantiationException e) {
                    throw new DataProviderException(e.getMessage(), e);
                }
            } else {
                try {
                    setValueForNonArrayType(memberInfo);
                } catch (InstantiationException | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    throw new DataProviderException(e.getMessage(), e);
                }
            }
        }
        logger.exiting(objectToReturn);
        return objectToReturn;
    }

    private Object createObjectToUse(Object userObject) throws IllegalAccessException {
        try {
            // Create a new instance of the data so we can
            // store it here before return everything to the users.
            return userObject.getClass().newInstance();
        } catch (InstantiationException e1) {
            String msg = String.format(
                    "Unable to instantiate an object of class %s bcoz it doesn't have a default constructor. ",
                    userObject.getClass().getCanonicalName());
            throw new DataProviderException(msg, e1);
        }

    }

    /**
     * A utility method that setups up data members which are arrays.
     * 
     * @param memberInfo
     *            - A {@link DataMemberInformation} object that represents values pertaining to every data member.
     * @throws IllegalAccessException
     * @throws ArrayIndexOutOfBoundsException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     */
    private void setValueForArrayType(DataMemberInformation memberInfo) throws IllegalAccessException,
            ArrayIndexOutOfBoundsException, IllegalArgumentException, InstantiationException {
        logger.entering(memberInfo);
        Field eachField = memberInfo.getField();
        Object objectToSetDataInto = memberInfo.getObjectToSetDataInto();
        String data = memberInfo.getDataToUse();
        Class<?> eachFieldType = eachField.getType();

        // We are dealing with arrays
        String[] arrayData = data.split(",");
        Object arrayObject = null;
        // Check if its an array of primitive data type
        if (ReflectionUtils.isPrimitiveArray(eachFieldType)) {
            arrayObject = ReflectionUtils.instantiatePrimitiveArray(eachFieldType, arrayData);
            eachField.set(objectToSetDataInto, arrayObject);
            logger.exiting();
            return;
        }
        if (ReflectionUtils.isWrapperArray(eachFieldType)
                || ReflectionUtils.hasOneArgStringConstructor(eachFieldType.getComponentType())) {
            // Check if its an array of either Wrapper classes or classes that have a 1 arg string constructor
            arrayObject = ReflectionUtils.instantiateWrapperArray(eachFieldType, arrayData);
            eachField.set(objectToSetDataInto, arrayObject);
            logger.exiting();
            return;
        }
        DefaultCustomType customType = fetchMatchingCustomType(eachFieldType);
        if (customType != null) {
            // Maybe it belongs to one of the custom types
            arrayObject = ReflectionUtils.instantiateDefaultCustomTypeArray(customType, arrayData);
            eachField.set(objectToSetDataInto, arrayObject);
            logger.exiting();
            return;
        }
        // If we are here then it means that the field is a Pojo class that points to another sheet in the excel sheet
        arrayObject = Array.newInstance(eachFieldType.getComponentType(), arrayData.length);
        for (int counter = 0; counter < arrayData.length; counter++) {
            Array.set(arrayObject, counter,
                    getSingleExcelRow(eachFieldType.getComponentType().newInstance(), arrayData[counter].trim(), true));
        }
        eachField.set(objectToSetDataInto, arrayObject);
        logger.exiting();
    }

    /**
     * A utility method that setups up data members which are NOT arrays.
     * 
     * @param memberInfo
     *            - A {@link DataMemberInformation} object that represents values pertaining to every data member.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    private void setValueForNonArrayType(DataMemberInformation memberInfo) throws IllegalAccessException,
            InstantiationException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException {
        logger.entering(memberInfo);
        Field eachField = memberInfo.getField();
        Class<?> eachFieldType = eachField.getType();
        Object objectToSetDataInto = memberInfo.getObjectToSetDataInto();
        Object userProvidedObject = memberInfo.getUserProvidedObject();
        String data = memberInfo.getDataToUse();

        boolean isPrimitive = eachFieldType.isPrimitive();
        if (isPrimitive) {
            // We found a primitive data type such as int, float, char etc.,
            eachField.set(objectToSetDataInto,
                    ReflectionUtils.instantiatePrimitiveObject(eachFieldType, userProvidedObject, data));
            logger.exiting();
            return;
        }
        if (ClassUtils.isPrimitiveWrapper(eachFieldType)) {
            // We found a wrapper data type such as Float, Integer, Character etc.,
            eachField.set(objectToSetDataInto,
                    ReflectionUtils.instantiateWrapperObject(eachFieldType, userProvidedObject, data));
            logger.exiting();
            return;
        }
        if (ReflectionUtils.hasOneArgStringConstructor(eachFieldType)) {
            // We found a class that has a 1 arg constructor. String.class is an example for that.
            Object objToSet = eachFieldType.getConstructor(new Class<?>[] { String.class }).newInstance(data);
            eachField.set(objectToSetDataInto, objToSet);
            logger.exiting();
            return;
        }
        DefaultCustomType customType = fetchMatchingCustomType(eachFieldType);
        if (customType != null) {
            // If we are here then it means that the field is one of the predefined custom types that was given to us.
            eachField.set(objectToSetDataInto, customType.instantiateObject(data));
            logger.exiting();
            return;
        }
        // If eventually we land here, then we have found a pojo class given by the user that points to another
        // sheet in the excel sheet.
        eachField.set(objectToSetDataInto, getSingleExcelRow(eachFieldType.newInstance(), data, true));
        logger.exiting();
        return;
    }

    /**
     * Using the specified rowIndex to search for the row from the specified Excel sheet, then return the row contents
     * in a list of string format.
     * 
     * @param rowIndex
     *            - The row number from the excel sheet that is to be read. For e.g., if you wanted to read the 2nd row
     *            (which is where your data exists) in your excel sheet, the value for index would be 1. <b>This method
     *            assumes that your excel sheet would have a header which it would EXCLUDE.</b> When specifying index
     *            value always remember to ignore the header, since this method will look for a particular row ignoring
     *            the header row.
     * @param size
     *            - The number of columns to read, including empty and blank column.
     * @return List<String> String array contains the row data.
     */
    public List<String> getRowContents(String sheetName, int rowIndex, int size) {
        return excelReader.getRowContents(sheetName, rowIndex, size);
    }

    /**
     * Get all excel rows from a specified sheet.
     * 
     * @param sheetName
     *            - A String that represents the Sheet name from which data is to be read
     * @param heading
     *            - If true, will return all rows along with the heading row. If false, will return all rows except the
     *            heading row.
     * @return - A List of {@link Row} that are read.
     */
    public List<Row> getAllRawExcelRows(String sheetName, boolean heading) {
        return excelReader.getAllExcelRows(sheetName, heading);
    }
}
