/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-16 PayPal                                                                                       |
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

package com.paypal.selion.ios.sample;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.mobile.elements.MobileButton;
import com.paypal.selion.platform.mobile.ios.UIANavigationBar;
import org.testng.annotations.Test;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumIOSButtonTouchTest {

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app", deviceType = "iPhone Simulator")
    public void testTwoFingerTap() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new MobileButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'Touch')]"));
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new MobileButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'State')]"));
        //appium can not inspect multi touch view !
        MobileButton twoFingerButton = new MobileButton("xpath=//UIAApplication[1]/UIAWindow[1]/UIAElement[1]");
        twoFingerButton.getMobileElement().tap(2, 100);
        Thread.sleep(2 * 1000);
    }

}
