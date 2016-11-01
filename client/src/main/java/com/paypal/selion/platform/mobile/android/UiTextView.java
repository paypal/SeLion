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

package com.paypal.selion.platform.mobile.android;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import com.paypal.test.utilities.logging.SimpleLogger;
import org.openqa.selenium.WebElement;

/**
 * <code>UiTextView</code> interface allows access to, and control of, text field elements in your Android app.
 */
public class UiTextView extends UiObject implements MobileTextField {
    private static final SimpleLogger logger = SeLionLogger.getLogger();

    public UiTextView(String locator) {
        super(locator);
    }

    @Override
    public void setValue(CharSequence... keysToSend) {
        if (keysToSend != null) {
            StringBuilder sb = new StringBuilder();
            for (CharSequence charSequence : keysToSend) {
                sb.append(charSequence);
            }
            super.setText(sb.toString());
        } else {
            super.setText("");
        }
    }

    @Override
    public void sendKeys(String keys) {
        logger.entering(keys);
        WebElement webElement = findElement(locator);
        webElement.sendKeys(keys);
        logger.exiting();

    }
}
