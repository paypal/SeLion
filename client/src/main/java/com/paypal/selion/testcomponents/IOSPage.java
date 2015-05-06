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

package com.paypal.selion.testcomponents;

import com.paypal.selion.platform.mobile.ios.UIAElement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A Base class from which all page classes denoting a IOS page should be derived.
 * 
 * It contains the code to initialize pages, load values to the "ObjectMap".
 */
public abstract class IOSPage extends AbstractPage {

    protected IOSPage() {
        super();
    }

    public abstract IOSPage getPage();

    protected void initializeMobileElements(Object whichClass, Map<String, String> objectMap) {

        List<Field> fields = new ArrayList<>();
        Class<?> incomingClass = whichClass.getClass();

        do {
            fields.addAll(Arrays.asList(incomingClass.getDeclaredFields()));
            incomingClass = incomingClass.getSuperclass();
        } while (incomingClass != null);
        String errorDesc = " Error while initializing fields from the object map for IOS Elements. Root cause:";
        try {
            for (Field field : fields) {
                if (!objectMap.containsKey(field.getName())) {
                    continue;
                }
                if (!UIAElement.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                String fieldLocator = objectMap.get(field.getName());
                if (fieldLocator == null) {
                    continue;
                }
                field.setAccessible(true);
                Class<?> dataMemberClass = Class.forName(field.getType().getName());
                Class<?> parameterTypes[] = new Class[] { String.class };
                // As per the current implementation IOS elements accept only locators hence initializing one
                // argument constructors
                Constructor<?> constructor = dataMemberClass.getDeclaredConstructor(parameterTypes);

                Object[] constructorArgList = new Object[] { fieldLocator };
                Object retobj = constructor.newInstance(constructorArgList);
                field.set(whichClass, retobj);
            }
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException exception) {
            throw new RuntimeException(errorDesc, exception);
        }
    }

}
