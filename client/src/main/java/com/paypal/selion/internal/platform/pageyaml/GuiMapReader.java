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

package com.paypal.selion.internal.platform.pageyaml;

import com.paypal.selion.internal.platform.grid.WebDriverPlatform;

import java.util.List;
import java.util.Map;

/**
 * Interface which all PageObject "GUI Map" providers must implement.
 */
public interface GuiMapReader {

    String KEY = "Key";
    String CONTAINER = "Container";
    String ELEMENTS = "Elements";
    String ELEMENTSv2 = "containerElements";
    String DEFAULT_LOCALE = "US";

    Map<String, String> getGuiMap(String locale);

    Map<String,String> getGuiMap(String locale, WebDriverPlatform platform);

    Map<String, String> getGuiMapForContainer(String containerKey, String locale);

    List<String> getPageValidators();

    List<String> getPageLoadingValidators();

}
