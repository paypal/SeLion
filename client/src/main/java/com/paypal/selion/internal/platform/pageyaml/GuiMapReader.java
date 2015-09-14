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

package com.paypal.selion.internal.platform.pageyaml;

import java.util.List;
import java.util.Map;

/**
 * Interface which all PageObject "GUI Map" providers must implement.
 */
public interface GuiMapReader {

    static final String KEY = "Key";
    static final String CONTAINER = "Container";
    static final String ELEMENTS = "Elements";
    static final String ELEMENTSv2 = "containerElements";
    static final String DEFAULT_LOCALE = "US";

    Map<String, String> getGuiMap(String locale);

    Map<String, String> getGuiMapForContainer(String containerKey, String locale);

    List<String> getPageValidators();

}
