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

package com.paypal.selion.platform.mobile.elements;

import com.paypal.selion.platform.mobile.Implementor;
import com.paypal.selion.platform.mobile.ios.UIAPicker;

import java.util.List;

/**
 * <code>MobilePicker</code> interface allows access to, and control of, wheel elements within a picker.
 * keep that in mind that android does not have a default picker so this element should only be used with IOS
 */
@Implementor(ios = UIAPicker.class)
public interface MobilePicker extends MobileElement {

    /**
     * Returns a {@link List} of String of values held by PickerWheel at the specified index
     *
     * @param index Index of the PickerWheel
     * @return {@link List} of String
     */
    List<String> getValuesOfWheelAtIndex(int index);

    /**
     * Sets the value to the PickerWheel at the specified index.
     *
     * @param index Index of the PickerWheel
     * @param value String value to set
     */
    void setValueOfWheelAtIndex(int index, String value);

}
