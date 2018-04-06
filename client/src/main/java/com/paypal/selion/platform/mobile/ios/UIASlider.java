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

import com.paypal.selion.platform.mobile.elements.MobileSlider;
import org.openqa.selenium.WebElement;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * The <code>UIASlider</code> class allows access to, and control of, slider elements in your app
 */
public class UIASlider extends UIAElement implements UIAutomationSlider, MobileSlider {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    public UIASlider(String locator) {
        super(locator);
    }

    @Override
    public void dragToValue(double value) {
        logger.entering(value);
        WebElement webElement = findElement(getLocator());
        getBridgeDriver().dragSliderToValue(webElement, value);
        logger.exiting();
    }

}
