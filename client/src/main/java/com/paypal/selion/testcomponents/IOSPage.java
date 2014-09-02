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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.paypal.selion.platform.html.WebPage;

/**
 * A Base class from which all page classes denoting a IOS page should be derived.
 * 
 * It contains the code to initialize pages, load values to the "ObjectMap".
 */
public class IOSPage extends AbstractPage {

    protected Map<String, String> fieldsMap = new HashMap<String, String>();

    protected IOSPage() {
        super();
    }

    @Override
    public WebPage getPage() {
        throw new UnsupportedOperationException("This operation is NOT supported for IOS Page");
    }

    protected void initializeHtmlObjects(Object whichClass, Map<String, String> objectMap) {

        ArrayList<Field> fields = new ArrayList<Field>();
        Class<?> incomingClass = whichClass.getClass();

        do {
            fields.addAll(Arrays.asList(incomingClass.getDeclaredFields()));
            incomingClass = incomingClass.getSuperclass();
        } while (incomingClass != null);

        for (Field field : fields) {
            // proceed further only if the data member and the key in the .xls
            // file match with each other
            // below condition checks for this one to one mapping presence
            if (objectMap.containsKey(field.getName())) {
                fieldsMap.put(field.getName(), objectMap.get(field.getName()));
            }
        }
    }

}
