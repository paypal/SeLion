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
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.annotations.Test;

import com.paypal.selion.platform.dataprovider.impl.ReflectionUtils.ReflectionException;
import com.paypal.selion.platform.dataprovider.impl.ReflectionUtilsTest.PhoneyClass;
import com.paypal.selion.platform.dataprovider.impl.ReflectionUtilsTest.PhoneyEnum;

public class DefaultCustomTypeTest {
    @Test(groups = "unit")
    public void testInstantiationUsingInstanceMethod() throws NoSuchMethodException, SecurityException {
        Object objectToUseForInstantiation = PhoneyEnum.ONE;
        Method instantiationMechanism = PhoneyEnum.class.getMethod("getValue", String.class);
        DefaultCustomType type = new DefaultCustomType(objectToUseForInstantiation, instantiationMechanism);
        Object objCreated = type.instantiateObject("two");
        assertTrue(objCreated != null);
        assertTrue(objCreated instanceof PhoneyEnum);
        assertEquals(((PhoneyEnum) objCreated).getText(), "two");
        assertEquals(type.getCustomTypeClass(), PhoneyEnum.class);
    }

    @Test(groups = "unit")
    public void testInstantiationUsingStaticMethod() throws NoSuchMethodException, SecurityException {
        Class<?> typeToUse = PhoneyClass.class;
        Method instantiationMechanism = PhoneyClass.class.getMethod("newInstance", String.class);
        DefaultCustomType type = new DefaultCustomType(typeToUse, instantiationMechanism);
        Object objCreated = type.instantiateObject("Hello");
        assertTrue(objCreated != null);
        assertTrue(objCreated instanceof PhoneyClass);
        assertEquals(((PhoneyClass) objCreated).toString(), "Hello");
        assertEquals(type.getCustomTypeClass(), PhoneyClass.class);
    }

    @Test(groups = "unit")
    public void testInstantiationUsingConstructor() throws NoSuchMethodException, SecurityException {
        Constructor<?> constructorToInvoke = PhoneyClass.class.getConstructor(Integer.class);
        DefaultCustomType type = new DefaultCustomType(constructorToInvoke);
        Object objCreated = type.instantiateObject(new Integer("1"));
        assertTrue(objCreated != null);
        assertTrue(objCreated instanceof PhoneyClass);
        assertEquals(((PhoneyClass) objCreated).toString(), "1");
        assertEquals(Integer.parseInt(((PhoneyClass) objCreated).toString()), 1);
        assertEquals(type.getCustomTypeClass(), PhoneyClass.class);
    }

    @Test(groups = "unit", expectedExceptions = { ReflectionException.class })
    public void testInstantiationErrorCondition() throws NoSuchMethodException, SecurityException {
        Constructor<?> constructorToInvoke = PhoneyClass.class.getConstructor(Integer.class);
        new DefaultCustomType(constructorToInvoke).instantiateObject("selion");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorErrorCondition1() throws NoSuchMethodException, SecurityException {
        new DefaultCustomType(null, PhoneyEnum.class.getMethod("getValue", String.class));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorErrorCondition2() throws NoSuchMethodException, SecurityException {
        new DefaultCustomType(PhoneyEnum.ONE, null);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorErrorCondition3() throws NoSuchMethodException, SecurityException {
        new DefaultCustomType(null, PhoneyEnum.class.getMethod("getValue", String.class));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorErrorCondition4() throws NoSuchMethodException, SecurityException {
        new DefaultCustomType(PhoneyEnum.class, null);
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testConstructorErrorCondition5() throws NoSuchMethodException, SecurityException {
        new DefaultCustomType(null);
    }

}
