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

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A utility class that helps perform various Reflection related tasks.
 */
final class ReflectionUtils {
    /**
     * The Exception that is raised whenever {@link ReflectionUtils} encounters problems.
     * 
     */
    public static class ReflectionException extends RuntimeException {

        /**
         * 
         */
        private static final long serialVersionUID = 5497283095000698874L;

        public ReflectionException() {
            super();
        }

        public ReflectionException(Throwable e) {
            super(e);
        }

    }

    private static final List<Class<?>> PRIMITIVE_ARRAY_TYPES = Arrays.asList(new Class<?>[] { int[].class,
            float[].class, char[].class, boolean[].class, short[].class, double[].class, byte[].class });
    private static final List<Class<?>> WRAPPER_ARRAY_TYPES = Arrays.asList(new Class<?>[] { Integer[].class,
            Float[].class, Character[].class, Boolean[].class, Short[].class, Double[].class, Byte[].class });

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private static Method getParserMethod(Class<?> type) {
        logger.entering(type);
        checkArgument(type != null, "Type cannot be null.");
        Class<?> wrapperType = ClassUtils.primitiveToWrapper(type);
        for (Method eachMethod : wrapperType.getDeclaredMethods()) {
            boolean isParserMethod = eachMethod.getName().startsWith("parse");
            boolean hasOneArg = eachMethod.getParameterTypes().length == 1;
            boolean acceptsStringAsParam = (hasOneArg ? (Arrays.asList(eachMethod.getParameterTypes())
                    .contains(String.class)) : false);
            if (isParserMethod && hasOneArg && acceptsStringAsParam) {
                logger.exiting(eachMethod);
                return eachMethod;
            }
        }
        logger.exiting(new Object[] { null });
        return null;
    }

    /**
     * @param type
     *            The type to check.
     * @return <code>true</code> If the Class represented by the type has a 1 argument constructor defined that
     *         accepts a String.
     */
    public static boolean hasOneArgStringConstructor(Class<?> type) {
        logger.entering(type);

        checkArgument(type != null, "type cannot be null.");

        boolean flag = false;
        try {
            flag = (type.getConstructor(new Class<?>[] { String.class }) != null);
        } catch (NoSuchMethodException | SecurityException e) { // NOSONAR
            // Gobble exception and do nothing with it.
        }
        logger.exiting(flag);
        return flag;

    }

    /**
     * @param type
     *            The type to check.
     * @return <code>true</code> If the Class represented by the type has a default constructor defined.
     */
    public static boolean hasDefaultConstructor(Class<?> type) {
        logger.entering(type);

        checkArgument(type != null, "type cannot be null.");

        boolean flag = false;
        try {
            flag = (type.getConstructor(new Class<?>[] {}) != null);
        } catch (NoSuchMethodException | SecurityException e) { // NOSONAR
            // Gobble exception and do nothing with it.
        }
        logger.exiting(flag);
        return flag;
    }

    /**
     * This helper method facilitates creation of {@link DefaultCustomTypeTest} arrays and pre-populates them with the
     * set of String values provided.
     * 
     * @param type
     *            A {@link DefaultCustomType} object that represents the type of the array to be instantiated.
     * @param values
     *            A {@link String} array that represents the set of values that should be used to pre-populate the
     *            newly constructed array.
     * @return An array of type {@link DefaultCustomType}
     */
    public static Object instantiateDefaultCustomTypeArray(DefaultCustomType type, String[] values) {
        logger.entering(new Object[] { type, values });

        checkArgument(type != null, "type cannot be null.");
        checkArgument((values != null && values.length != 0), "The values  cannot be null (or) empty.");

        Object arrayToReturn = Array.newInstance(type.getCustomTypeClass(), values.length);
        for (int i = 0; i < values.length; i++) {
            Array.set(arrayToReturn, i, type.instantiateObject(values[i]));
        }
        logger.exiting(arrayToReturn);
        return arrayToReturn;
    }

    /**
     * This helper method facilitates creation of primitive arrays and pre-populates them with the set of String values
     * provided.
     * 
     * @param type
     *            The type of the array to create. Note that this method will only accept primitive types (as the name
     *            suggests) i.e., only int[],float[], boolean[] and so on.
     * @param values
     *            A {@link String} array that represents the set of values that should be used to pre-populate the
     *            newly constructed array.
     * 
     * @return An array of the type that was specified.
     */
    public static Object instantiatePrimitiveArray(Class<?> type, String[] values) {
        logger.entering(new Object[] { type, values });

        validateParams(type, values);
        checkArgument(isPrimitiveArray(type), type + " is NOT a primitive array type.");

        Class<?> componentType = type.getComponentType();
        Object arrayToReturn = Array.newInstance(componentType, values.length);
        Method parserMethod = getParserMethod(componentType);
        for (int i = 0; i < values.length; i++) {
            try {
                Array.set(arrayToReturn, i, parserMethod.invoke(arrayToReturn, values[i]));
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | IllegalAccessException
                    | InvocationTargetException e) {
                throw new ReflectionException(e);
            }
        }
        logger.exiting(arrayToReturn);
        return arrayToReturn;
    }

    /**
     * This helper method facilitates creation of primitive data type object and initialize it with the provided value.
     * 
     * @param type
     *            The type to instantiate. It has to be only a primitive data type [ such as int, float, boolean
     *            etc.,]
     * @param objectToInvokeUpon
     *            The object upon which the invocation is to be carried out.
     * @param valueToAssign
     *            The value to initialize with.
     * @return An initialized object that represents the primitive data type.
     */
    public static Object instantiatePrimitiveObject(Class<?> type, Object objectToInvokeUpon, String valueToAssign) {
        logger.entering(new Object[] { type, objectToInvokeUpon, valueToAssign });

        validateParams(type, objectToInvokeUpon, valueToAssign);
        checkArgument(type.isPrimitive(), type + " is NOT a primitive data type.");

        try {
            Object objectToReturn = getParserMethod(type).invoke(objectToInvokeUpon, valueToAssign);
            logger.exiting(objectToInvokeUpon);
            return objectToReturn;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * This helper method facilitates creation of wrapper arrays and pre-populates them with the set of String values
     * provided. E.g., of wrapper arrays include Integer[], Character[], Boolean[] and so on. This method can also be
     * used to create arrays of types which has a 1 argument String constructor defined.
     * 
     * @param type
     *            The type of the desired array.
     * @param values
     *            A {@link String} array that represents the set of values that should be used to pre-populate the
     *            newly constructed array.
     * 
     * @return An array of the type that was specified.
     */
    public static Object instantiateWrapperArray(Class<?> type, String[] values) {
        logger.entering(new Object[] { type, values });
        validateParams(type, values);
        boolean condition = (isWrapperArray(type) || hasOneArgStringConstructor(type.getComponentType()));
        checkArgument(condition, type.getName()
                + " is neither awrapper type nor has a 1 arg String constructor defined.");
        Class<?> componentType = type.getComponentType();
        Object arrayToReturn = Array.newInstance(componentType, values.length);
        for (int i = 0; i < values.length; i++) {
            try {
                Array.set(arrayToReturn, i, componentType.getConstructor(String.class).newInstance(values[i]));
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | InstantiationException
                    | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new ReflectionException(e);
            }
        }
        logger.exiting(arrayToReturn);
        return arrayToReturn;
    }

    /**
     * This helper method facilitates creation of Wrapper data type object and initialize it with the provided value.
     * 
     * @param type
     *            The type to instantiate. It has to be only a Wrapper data type [ such as Integer, Float, Boolean
     *            etc.,]
     * @param objectToInvokeUpon
     *            The object upon which the invocation is to be carried out.
     * @param valueToAssign
     *            The value to initialize with.
     * @return An initialized object that represents the Wrapper data type.
     */
    public static Object instantiateWrapperObject(Class<?> type, Object objectToInvokeUpon, String valueToAssign) {
        logger.entering(new Object[] { type, objectToInvokeUpon, valueToAssign });

        validateParams(type, objectToInvokeUpon, valueToAssign);
        checkArgument(ClassUtils.isPrimitiveWrapper(type), type.getName() + " is NOT a wrapper data type.");

        try {
            Object objectToReturn = type.getConstructor(new Class<?>[] { String.class }).newInstance(valueToAssign);
            logger.exiting(objectToInvokeUpon);
            return objectToReturn;
        } catch (InstantiationException | NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * This method confirms if a type is of primitive array type ( int[], float[], boolean[] and so on).
     * 
     * @param type
     *            The type which is to be checked.
     * @return <code>true</code> if type is a primitive array type.
     */
    public static boolean isPrimitiveArray(Class<?> type) {
        logger.entering(type);
        checkArgument(type != null, "Type cannot be null.");
        logger.exiting(PRIMITIVE_ARRAY_TYPES.contains(type));
        return PRIMITIVE_ARRAY_TYPES.contains(type);
    }

    /**
     * This method confirms if a type is of primitive array type ( Integer[], Float[], Boolean[] and so on).
     * 
     * @param type
     *            The type which is to be checked.
     * @return <code>true</code> if type is a wrapper array type.
     */
    public static boolean isWrapperArray(Class<?> type) {
        logger.entering(type);
        checkArgument(type != null, "Type cannot be null.");
        logger.exiting(WRAPPER_ARRAY_TYPES.contains(type));
        return WRAPPER_ARRAY_TYPES.contains(type);
    }

    private static void validateParams(Class<?> dataType, Object objectToInvokeUpon, Object valueToAssign) {
        checkArgument(dataType != null, "Data type cannot be null.");
        checkArgument(objectToInvokeUpon != null, "The Object upon which invocation is to be done cannot be null.");
        checkArgument(valueToAssign != null, "The value to be assigned to cannot be null.");
    }

    private static void validateParams(Class<?> type, String[] val) {
        checkArgument(type != null, "The field type cannot be null.");
        checkArgument(val != null && val.length != 0, "The values  cannot be null (or) empty.");

        checkArgument(type.isArray(), type.getName() + " is not an array");
        if (type.getComponentType() != null && type.getComponentType().getComponentType() != null) {
            checkArgument(type.getComponentType().getComponentType().isArray(),
                    "Multi dimensional arrays are not supported");
        }
    }

    private ReflectionUtils() {

    }

}
