/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

package com.paypal.selion.internal.platform.grid;

import java.lang.reflect.Method;

import org.testng.IInvokedMethod;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;

/**
 * A Factory that is capable of producing config objects that represent {@literal @}WebTest (or) the {@literal @}
 * MobileTest annotation.
 * 
 */
class TestSessionFactory {
    /**
     * @param method
     *            - A {@link Method} object that represents the currently invoked method.
     * @return - A subclass of {@link AbstractTestSession} which represents the annotation object and its capabilities.
     * 
     *         This method returns an instance of {@link BasicTestSession} if the {@link IInvokedMethod} method doesn't
     *         have either
     *         <ul>
     *         <li>{@literal @}{@link WebTest} (or)
     *         <li>{@literal @}{@link MobileTest} annotation.
     *         </ul>
     */
    public static AbstractTestSession newInstance(IInvokedMethod method) {
        if (method == null) {
            return null;
        }
        boolean webTest = method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(WebTest.class) != null
                || method.getTestMethod().getInstance().getClass().getAnnotation(WebTest.class) != null;
        if (webTest) {
            return new WebTestSession();
        }
        boolean appTest = method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(MobileTest.class) != null
                || method.getTestMethod().getInstance().getClass().getAnnotation(MobileTest.class) != null;
        if (appTest) {
            return new MobileTestSession();
        }
        return new BasicTestSession();
    }

    /**
     * @param annotation
     *            - The annotation based upon which a test config instance is to be created.
     * @return - A subclass of {@link AbstractTestSession} which represents the annotation object and its capabilities.
     *         This method returns an instance of {@link BasicTestSession} if the input annotation class is NOT
     *         <ul>
     *         <li>{@literal @}{@link WebTest} (or)
     *         <li>{@literal @}{@link MobileTest} annotation.
     *         </ul>
     */
    public static AbstractTestSession newInstance(Class<?> annotation) {
        if (annotation == null) {
            return null;
        }
        if (annotation == WebTest.class) {
            return new WebTestSession();
        }
        if (annotation == MobileTest.class) {
            return new MobileTestSession();
        }
        return new BasicTestSession();
    }

    /**
     * @return - An array of the supported annotations that are understandable by the SeLion framework. Any new
     *         annotations for which a config object is supported by this factory needs to find an entry here as well.
     */
    public static Class<?>[] getSupportedAnnotations() {
        return new Class<?>[] { WebTest.class, MobileTest.class };
    }

}
