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

import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * <code>UIAutomationAlert</code> interface allows access to, and control of, alerts within your app.
 */
public interface UIAutomationAlert extends UIAutomationElement {

    /**
     * Click the cancel button of the alert view. The index of the cancel button is 0.
     * 
     * @param expected
     *            Expected entities in the form of objects extending {@link UIAElement} or xpath location in the form of
     *            {@link String} or instances of {@link ExpectedCondition}.
     */
    void clickCancelButton(Object... expected);

    /**
     * Click the button in the corresponding index. The index in 0 based.
     * 
     * @param index
     *            Index of the button to be clicked
     * @param expected
     *            Expected entities in the form of objects extending {@link UIAElement} or xpath location in the form of
     *            {@link String} or instances of {@link ExpectedCondition}.
     */
    void clickButtonAtIndex(int index, Object... expected);

}
