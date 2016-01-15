/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

/**
 * <code>UIAutomationSlider</code> interface allows access to, and control of, slider elements in your app
 */
public interface UIAutomationSlider extends UIAutomationElement {

    /**
     * Drags the slider to the specified value.
     * 
     * @param value
     *            The desired decimal value from 0 to 1, inclusive. A 0 value represents far left and a value of 1
     *            represents far right.
     */
    void dragToValue(double value);

}
