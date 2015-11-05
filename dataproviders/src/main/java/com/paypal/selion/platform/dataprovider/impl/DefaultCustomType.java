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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class represents a custom type that should automatically be handled. The intention is basically to grab the
 * Object using which the instantiation is to be carried out and the instantiation mechanism (i.e., the {@link Method}
 * which would be responsible for instantiating new Objects. A typical scenario wherein this would be required is when
 * dealing with {@link Enum} wherein some custom method within the {@link Enum} is responsible for creation of new
 * Objects. Other use cases would include, the involvement of a class with one or more custom methods for new object
 * instantiations (Factories are good examples of this).
 */
public class DefaultCustomType {
    private Object objectToUseForInstantiation;
    private Method instantiationMechanism;
    private Constructor<?> constructor;
    private Class<?> customTypeClass;

    /**
     * @param objectToUseForInstantiation
     *            - The {@link Object} upon which the invocation is to be carried out.
     * @param instantiationMechanism
     *            - The {@link Method} which should be invoked when a new {@link Object} is to be created.
     */
    public DefaultCustomType(Object objectToUseForInstantiation, Method instantiationMechanism) {
        checkArgument(objectToUseForInstantiation != null,
                "The object upon which invocation should be done cannot be null.");
        checkArgument(instantiationMechanism != null, "The instantiation mechanism cannot be null.");
        this.objectToUseForInstantiation = objectToUseForInstantiation;
        this.instantiationMechanism = instantiationMechanism;
        if (objectToUseForInstantiation != null) {
            this.customTypeClass = objectToUseForInstantiation.getClass();
        }
    }

    /**
     * Use this constructor when you have static method that is involved in creating instances.
     * 
     * @param typeToUseForInstantiation
     *            - The {@link Class} in which the static method resides.
     * @param instantiationMechanism
     *            - The {@link Method} which should be invoked when a new {@link Object} is to be created.
     * 
     */
    public DefaultCustomType(Class<?> typeToUseForInstantiation, Method instantiationMechanism) {
        checkArgument(typeToUseForInstantiation != null, "The type  should be done cannot be null.");
        checkArgument(instantiationMechanism != null, "The instantiation mechanism cannot be null.");
        this.objectToUseForInstantiation = null;
        this.instantiationMechanism = instantiationMechanism;
        this.customTypeClass = typeToUseForInstantiation;
    }

    /**
     * Use this constructor when you would like to have a new object created via a custom constructor.
     * 
     * @param constructorToInvoke
     *            - The {@link Constructor} that should be invoked.
     * 
     */
    public DefaultCustomType(Constructor<?> constructorToInvoke) {
        checkArgument(constructorToInvoke != null, "The Constructor  should be done cannot be null.");
        this.constructor = constructorToInvoke;
        this.customTypeClass = constructorToInvoke.getDeclaringClass();
    }

    /**
     * @param args
     *            - A {@link Object} array that represents the set of parameters to be passed to, when instantiating a
     *            new Object.
     * @return - An {@link Object} that was instantiated using the already provided instantiation mechanism.
     */
    public Object instantiateObject(Object... args) {
        try {
            if (instantiationMechanism != null) {
                return instantiationMechanism.invoke(objectToUseForInstantiation, args);
            }
            return constructor.newInstance(args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            throw new ReflectionUtils.ReflectionException(e);
        }
    }

    /**
     * @return - A {@link Class} that represents the type of the {@link Object} using which instantiation would be
     *         carried out.
     */
    public Class<?> getCustomTypeClass() {
        if (customTypeClass != null) {
            return customTypeClass;
        }
        return constructor.getClass();
    }
}
