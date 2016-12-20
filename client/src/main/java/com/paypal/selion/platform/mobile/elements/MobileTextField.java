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

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * <code>MobileTextField</code> interface allows access to, and control of, text field elements in your app.
 */
public class MobileTextField extends AbstractMobileElement {
    private static final SimpleLogger logger = SeLionLogger.getLogger();

    public MobileTextField(String locator) {
        super(locator);
    }

    /**
     * clear the current text inside the element
     */
    public void clear() {
        logger.entering();
        getMobileElement().clear();
        logger.exiting();
    }

    /**
     * send keys to element without cleaning its values
     *
     * @param keys keys to send to element
     */
    public void sendKeys(String keys) {
        logger.entering(keys);
        getMobileElement().click();
        getMobileElement().sendKeys(keys);
        logger.exiting();
    }

    /**
     * clear the existing text in text field and sets the text in the text field.
     *
     * @param keysToSend text to set in the text field.
     */
    public void setText(String keysToSend) {
        getMobileElement().clear();
        sendKeys(keysToSend);
    }
}
