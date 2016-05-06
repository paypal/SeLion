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
import com.paypal.selion.platform.mobile.android.UiTextView;
import com.paypal.selion.platform.mobile.ios.UIATextField;

/**
 * <code>MobileTextField</code> interface allows access to, and control of, text field elements in your app.
 */
@Implementor(ios = UIATextField.class, android = UiTextView.class)
public interface MobileTextField extends MobileElement {

    /**
     * clear the current text inside the element
     */
    void clearText();

    /**
     * send keys to element without cleaning its values
     *
     * @param keys keys to send to element
     */
    void sendKeys(String keys);

    /**
     * clear the existing text in text field and sets the text in the text field.
     *
     * @param text text to set in the text field.
     */
    void setText(String text);

    /**
     * deprecated because of wrong name. use setText or sendKeys instead.
     */
    @Deprecated
    void setValue(CharSequence... keysToSend);
}
