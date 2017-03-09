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

package com.paypal.selion.platform.mobile.android;

import com.paypal.selion.internal.platform.grid.MobileNodeType;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
//import com.paypal.selion.platform.grid.SeLionAppiumAndroidDriver;
import com.paypal.selion.platform.mobile.elements.MobileSlider;
import com.paypal.test.utilities.logging.SimpleLogger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * <code>UiSlider</code> represents a Slider widget for Android UI automation framework.
 */
public class UiSlider extends UiObject implements MobileSlider {
    private static final SimpleLogger logger = SeLionLogger.getLogger();
    private static final int MIN_END_X = 1;
    private static final int VALUE_UPPER_LIMIT = 1;

    public UiSlider(String locator) {
        super(locator);
    }

    /**
     * it is not accurate and is best to used only for setting value to 0 or 1, otherwise the result is close to parameter
     * @param value The desired decimal value from 0 to 1, inclusive. 0 represents far left and 1 represent far right.
     */
    @Override
    public void dragToValue(double value) {
        logger.entering(value);
        WebElement webElement = findElement(locator);
        Point currentLocation = webElement.getLocation();
        Dimension elementSize = webElement.getSize();
        int x = currentLocation.getX();
        int y = currentLocation.getY() + (elementSize.getHeight() / 2);
        int pos = Double.valueOf(value * (elementSize.getWidth())).intValue();
        int endX = x + pos;
        // full width will throw exception
        if (value >= VALUE_UPPER_LIMIT) {
            endX--;
        }
        // endX == 0 move to the middle
        if (endX < MIN_END_X) {
            endX = MIN_END_X;
        }

        initBridgeDriver();
        MobileNodeType mobileType = Grid.getMobileTestSession().getMobileNodeType();
        if (mobileType.equals(MobileNodeType.APPIUM)) {
            // On Appium we use tap (TouchAction) instead
            driver.swipe(1, endX, y, 100);
        } else {
            driver.swipe(x, y,endX, y);
        }
        logger.exiting();
    }
}
