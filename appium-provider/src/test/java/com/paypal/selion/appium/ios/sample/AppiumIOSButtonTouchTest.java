/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2017 PayPal                                                                                     |
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

package com.paypal.selion.appium.ios.sample;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.mobile.ios.GestureOptions;
import com.paypal.selion.platform.mobile.ios.UIAButton;
import com.paypal.selion.platform.mobile.ios.UIAElement;
import com.paypal.selion.platform.mobile.ios.UIANavigationBar;
import org.testng.annotations.Test;

import java.util.EnumMap;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumIOSButtonTouchTest {

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
    public void testTwoFingerTap() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'Touch')]") });
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'State')]") });
        UIAElement twoFingerButton = new UIAElement("xpath=//UIAApplication[1]/UIAWindow[1]/UIAElement[1]");
        EnumMap<GestureOptions, String> options = new EnumMap<>(GestureOptions.class);
        options.put(GestureOptions.TAP_COUNT, "1");
        options.put(GestureOptions.TOUCH_COUNT, "2"); // transforms to Two Finger Tap
        options.put(GestureOptions.DURATION, "0");
        options.put(GestureOptions.X, "0.5");
        options.put(GestureOptions.Y, "0.3");
        twoFingerButton.tapWithOptions(options);
        Thread.sleep(2 * 1000);
    }

}
