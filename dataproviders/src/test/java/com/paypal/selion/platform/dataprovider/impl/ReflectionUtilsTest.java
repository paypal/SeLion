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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.annotations.Test;

import com.paypal.selion.platform.dataprovider.impl.ReflectionUtils;
import com.paypal.selion.platform.dataprovider.impl.ReflectionUtils.ReflectionException;

public class ReflectionUtilsTest {
    @Test(groups = "unit")
    public void testHasDefaultConstructor() {
        assertTrue(ReflectionUtils.hasDefaultConstructor(String.class));
    }

    @Test(groups = "unit")
    public void testHasDefaultConstructorFalseCondition() {
        assertFalse(ReflectionUtils.hasDefaultConstructor(int.class));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testHasDefaultConstructorErrorCondition() {
        assertTrue(ReflectionUtils.hasDefaultConstructor(null));
    }

    @Test(groups = "unit")
    public void testHasOneArgStringConstructor() {
        assertTrue(ReflectionUtils.hasDefaultConstructor(String.class));
    }

    @Test(groups = "unit")
    public void testHasOneArgStringConstructorFalseCondition() {
        assertFalse(ReflectionUtils.hasDefaultConstructor(int.class));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testHasOneArgStringConstructorErrorCondition() {
        assertTrue(ReflectionUtils.hasDefaultConstructor(null));
    }

    @Test(groups = "unit")
    public void testInstantiateDefaultCustomTypeArrayWithStaticMethod() throws NoSuchMethodException, SecurityException {
        Method instantiationMechanism = PhoneyClass.class.getMethod("newInstance", String.class);
        DefaultCustomType type = new DefaultCustomType(PhoneyClass.class, instantiationMechanism);
        Object obj = ReflectionUtils.instantiateDefaultCustomTypeArray(type, new String[] { "SeLion" });
        assertTrue(obj != null);
        assertTrue(obj instanceof PhoneyClass[]);
        assertTrue(((PhoneyClass[]) obj).length == 1);
        assertEquals(((PhoneyClass[]) obj)[0].toString(), "SeLion");
    }

    @Test(groups = "unit")
    public void testInstantiateDefaultCustomTypeArrayWithEnum() throws NoSuchMethodException, SecurityException {
        Method instantiationMechanism = PhoneyEnum.class.getMethod("getValue", String.class);
        DefaultCustomType type = new DefaultCustomType(PhoneyEnum.ONE, instantiationMechanism);
        Object obj = ReflectionUtils.instantiateDefaultCustomTypeArray(type, new String[] { "two" });
        assertTrue(obj != null);
        assertTrue(obj instanceof PhoneyEnum[]);
        assertTrue(((PhoneyEnum[]) obj).length == 1);
        assertEquals(((PhoneyEnum[]) obj)[0].getText(), "two");
    }

    @Test(groups = "unit")
    public void testInstantiateDefaultCustomTypeArrayWithConstructor() throws NoSuchMethodException, SecurityException {
        Constructor<?> instantiationMechanism = PhoneyClass.class.getDeclaredConstructor(String.class);
        DefaultCustomType type = new DefaultCustomType(instantiationMechanism);
        Object obj = ReflectionUtils.instantiateDefaultCustomTypeArray(type, new String[] { "two" });
        assertTrue(obj != null);
        assertTrue(obj instanceof PhoneyClass[]);
        assertTrue(((PhoneyClass[]) obj).length == 1);
        assertEquals(((PhoneyClass[]) obj)[0].toString(), "two");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateDefaultCustomTypeArrayErrorCondition1() {
        ReflectionUtils.instantiateDefaultCustomTypeArray(null, new String[] { "two" });
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateDefaultCustomTypeArrayErrorCondition2() throws NoSuchMethodException, SecurityException {
        Constructor<?> instantiationMechanism = PhoneyClass.class.getDeclaredConstructor(String.class);
        DefaultCustomType type = new DefaultCustomType(instantiationMechanism);
        ReflectionUtils.instantiateDefaultCustomTypeArray(type, null);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateDefaultCustomTypeArrayErrorCondition3() throws NoSuchMethodException, SecurityException {
        Constructor<?> instantiationMechanism = PhoneyClass.class.getDeclaredConstructor(String.class);
        DefaultCustomType type = new DefaultCustomType(instantiationMechanism);
        ReflectionUtils.instantiateDefaultCustomTypeArray(type, new String[] {});
    }

    @Test(groups = "unit", expectedExceptions = { ReflectionException.class })
    public void testInstantiateDefaultCustomTypeArrayErrorCondition4() throws NoSuchMethodException, SecurityException {
        Constructor<?> instantiationMechanism = PhoneyClass.class.getDeclaredConstructor(Integer.class);
        DefaultCustomType type = new DefaultCustomType(instantiationMechanism);
        ReflectionUtils.instantiateDefaultCustomTypeArray(type, new String[] { "SeLion" });
    }

    @Test(groups = "unit")
    public void testInstantiatePrimitiveArray() {
        Object obj = ReflectionUtils.instantiatePrimitiveArray(int[].class, new String[] { "1" });
        assertTrue(obj != null);
        assertTrue(obj instanceof int[]);
        assertTrue(((int[]) obj).length == 1);
        assertEquals(((int[]) obj)[0], 1);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiatePrimitiveArrayErrorCondition1() {
        ReflectionUtils.instantiatePrimitiveArray(String[].class, new String[] { "1" });
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiatePrimitiveArrayErrorCondition2() {
        ReflectionUtils.instantiatePrimitiveArray(null, new String[] { "1" });
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiatePrimitiveArrayErrorCondition3() {
        ReflectionUtils.instantiatePrimitiveArray(int[].class, null);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiatePrimitiveArrayErrorCondition4() {
        ReflectionUtils.instantiatePrimitiveArray(int[].class, new String[] {});
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiatePrimitiveArrayErrorCondition5() {
        ReflectionUtils.instantiatePrimitiveArray(int[][].class, new String[] { "1" });
    }

    @Test(groups = "unit", expectedExceptions = { ReflectionException.class })
    public void testInstantiatePrimitiveArrayErrorCondition6() {
        ReflectionUtils.instantiatePrimitiveArray(int[].class, new String[] { "one" });
    }

    @Test(groups = "unit")
    public void testInstantiatePrimitiveObject() {
        Object myint = ReflectionUtils.instantiatePrimitiveObject(int.class, new Integer(0), "5");
        assertTrue(myint != null);
        assertTrue(myint instanceof Integer);
        assertTrue(((Integer) myint).intValue() == 5);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiatePrimitiveObjectErrorCondition1() {
        ReflectionUtils.instantiatePrimitiveObject(null, new Integer(0), "5");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiatePrimitiveObjectErrorCondition2() {
        ReflectionUtils.instantiatePrimitiveObject(Integer.class, new Integer(0), "5");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiatePrimitiveObjectErrorCondition3() {
        ReflectionUtils.instantiatePrimitiveObject(int.class, null, "5");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiatePrimitiveObjectErrorCondition4() {
        ReflectionUtils.instantiatePrimitiveObject(int.class, new Integer(0), null);
    }

    @Test(groups = "unit", expectedExceptions = { ReflectionException.class })
    public void testInstantiatePrimitiveObjectErrorCondition5() {
        ReflectionUtils.instantiatePrimitiveObject(int.class, new Integer(0), "");
    }

    @Test(groups = "unit")
    public void testInstantiateWrapperArray() {
        Object wrapperArray = ReflectionUtils.instantiateWrapperArray(Integer[].class, new String[] { "1" });
        assertTrue(wrapperArray != null);
        assertTrue(wrapperArray instanceof Integer[]);
        assertTrue(((Integer[]) wrapperArray).length == 1);
        assertTrue(((Integer[]) wrapperArray)[0].intValue() == 1);
    }

    @Test(groups = "unit")
    public void testInstantiateWrapperArrayNonWrapperClass() {
        Object wrapperArray = ReflectionUtils.instantiateWrapperArray(PhoneyClass[].class, new String[] { "1" });
        assertTrue(wrapperArray != null);
        assertTrue(wrapperArray instanceof PhoneyClass[]);
        assertTrue(((PhoneyClass[]) wrapperArray).length == 1);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateWrapperArrayErrorCondition1() {
        ReflectionUtils.instantiateWrapperArray(Integer.class, new String[] { "1" });
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateWrapperArrayErrorCondition2() {
        ReflectionUtils.instantiateWrapperArray(null, new String[] { "1" });
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateWrapperArrayErrorCondition3() {
        ReflectionUtils.instantiateWrapperArray(DefaultCustomTypeTest.class, new String[] { "1" });
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateWrapperArrayErrorCondition4() {
        ReflectionUtils.instantiateWrapperArray(int.class, new String[] { "1" });
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateWrapperArrayErrorCondition5() {
        ReflectionUtils.instantiateWrapperArray(Integer[].class, null);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateWrapperArrayErrorCondition6() {
        ReflectionUtils.instantiateWrapperArray(Integer[].class, new String[] {});
    }

    @Test(groups = "unit", expectedExceptions = { ReflectionException.class })
    public void testInstantiateWrapperArrayErrorCondition7() {
        ReflectionUtils.instantiateWrapperArray(Integer[].class, new String[] { "selion" });
    }

    @Test(groups = "unit")
    public void testInstantiateWrapperObject() {
        Object myint = ReflectionUtils.instantiateWrapperObject(Integer.class, new Integer(0), "5");
        assertTrue(myint != null);
        assertTrue(myint instanceof Integer);
        assertTrue(((Integer) myint).intValue() == 5);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateWrapperObjectErrorCondition1() {
        ReflectionUtils.instantiateWrapperObject(String.class, new Integer(0), "5");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateWrapperObjectErrorCondition2() {
        ReflectionUtils.instantiateWrapperObject(null, new Integer(0), "5");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateWrapperObjectErrorCondition3() {
        ReflectionUtils.instantiateWrapperObject(Integer.class, null, "5");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testInstantiateWrapperObjectErrorCondition4() {
        ReflectionUtils.instantiateWrapperObject(Integer.class, new Integer(0), null);
    }

    @Test(groups = "unit", expectedExceptions = { ReflectionException.class })
    public void testInstantiateWrapperObjectErrorCondition5() {
        ReflectionUtils.instantiateWrapperObject(Integer.class, new Integer(0), "");
    }

    @Test(groups = "unit", expectedExceptions = { ReflectionException.class })
    public void testInstantiateWrapperObjectErrorCondition6() {
        ReflectionUtils.instantiateWrapperObject(Integer.class, new Integer(0), "selion");
    }

    @Test(groups = "unit")
    public void testIsPrimitiveArrayPositive() {
        assertTrue(ReflectionUtils.isPrimitiveArray(int[].class));
    }

    @Test(groups = "unit")
    public void testIsPrimitiveArrayNegative() {
        assertFalse(ReflectionUtils.isPrimitiveArray(Integer[].class));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testIsPrimitiveArrayErrorCondition() {
        ReflectionUtils.isPrimitiveArray(null);
    }

    @Test(groups = "unit")
    public void testIsWrapperArrayPositive() {
        assertTrue(ReflectionUtils.isWrapperArray(Integer[].class));
    }

    @Test(groups = "unit")
    public void testIsWrapperArrayNegative() {
        assertFalse(ReflectionUtils.isWrapperArray(int[].class));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testIsWrapperArrayErrorCondition() {
        ReflectionUtils.isWrapperArray(null);
    }

    public static enum PhoneyEnum {
        ONE("one"), TWO("two");
        private String text;

        private PhoneyEnum(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public static PhoneyEnum getValue(String text) {
            for (PhoneyEnum eachEnum : PhoneyEnum.values()) {
                if (eachEnum.text.equals(text)) {
                    return eachEnum;
                }
            }
            return null;
        }
    }

    public static class PhoneyClass {
        private final String s;

        public PhoneyClass(String s) {
            this.s = s;

        }

        public PhoneyClass(Integer i) {
            this.s = i.toString();
        }

        public static PhoneyClass newInstance(String s) {
            return new PhoneyClass(s);
        }

        public String toString() {
            return s;
        }
    }

}
