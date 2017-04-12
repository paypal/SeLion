/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2017 PayPal                                                                                     |
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

package com.paypal.selion.elements;

import com.google.common.base.Preconditions;

/**
 * Utility methods to do code generation for supported elements.
 */
public class HtmlElementUtils {
    /**
     * Extracts the package from a qualified class.
     *
     * @param element string of the qualified class
     * @return package
     */
    public static String getPackage(String element) {
        Preconditions.checkNotNull(element,"argument 'element' can not be null");
        return element.substring(0, element.lastIndexOf('.'));
    }

    /**
     * Extracts the class name from a qualified class.
     *
     * @param element string of the qualified class
     * @return class name
     */
    public static String getClass(String element) {
        Preconditions.checkNotNull(element,"argument 'element' can not be null");
        return element.substring(element.lastIndexOf('.') + 1);
    }
}
