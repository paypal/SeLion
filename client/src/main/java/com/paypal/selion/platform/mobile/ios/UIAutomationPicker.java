/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

package com.paypal.selion.platform.mobile.ios;

import java.util.List;

/**
 * <code>UIAutomationPicker</code> interface allows access to, and control of, wheel elements within a picker
 */
public interface UIAutomationPicker extends UIAutomationElement {

    /**
     * Returns a {@link List} of String of values held by PickerWheel at the specified index
     * 
     * @param index
     *            Index of the PickerWheel
     * @return {@link List} of String
     */
    List<String> getValuesOfWheelAtIndex(int index);

    /**
     * Sets the value to the PickerWheel at the specified index.
     * 
     * @param index
     *            Index of the PickerWheel
     * @param value
     *            String value to set
     */
    void setValueOfWheelAtIndex(int index, String value);

}
