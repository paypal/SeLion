/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

package com.paypal.selion.platform.mobile;

import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.platform.mobile.elements.MobileElement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class finds correct implementation for mobile interfaces and create a new instance for it or throw exception. It
 * will try to find class in follow locations: 
 * 1. if class is not an interfaces, it will use class itself 
 * 2. if class is annotated with <code>Implementor</code> it reads info from it. 
 * 3. if none of the above, it will throw exception
 */
@SuppressWarnings("unchecked")
public class MobileImplementationFinder {
    private MobileImplementationFinder() {
    }

    public static <T extends MobileElement> T instantiate(WebDriverPlatform platform, Class<T> tClass, String locator) {
        try {
            final Class<T> mobileElementClass = find(tClass, platform);
            final Constructor<T> constructor = mobileElementClass.getConstructor(String.class);
            return constructor.newInstance(locator);
        } catch (ClassCastException | ClassNotFoundException | InstantiationException | IllegalAccessException
                | NoSuchMethodException | InvocationTargetException e) {
            throw new MobileObjectInstantiationException(String.format(
                    "Problem instantiating class %s for platform %s", tClass.getName(), platform), e);
        }
    }

    private static <T extends MobileElement> Class<T> find(Class<T> aClass, WebDriverPlatform platform)
            throws ClassNotFoundException {
        if (!aClass.isInterface()) {
            return aClass;
        }
        Implementor implementor = aClass.getAnnotation(Implementor.class);
        if (implementor == null) {
            throw new MobileObjectInstantiationException(String.format(
                    "Interface {%s} does not implements @Implementor", aClass.getName()));
        }
        switch (platform) {
        case IOS:
            return (Class<T>) implementor.ios();
        case ANDROID:
            return (Class<T>) implementor.android();
        default:
            throw new MobileObjectInstantiationException("unSupported Platform");
        }
    }
}
