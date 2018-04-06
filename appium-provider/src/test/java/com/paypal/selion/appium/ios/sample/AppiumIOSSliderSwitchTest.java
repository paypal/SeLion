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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.mobile.ios.UIAButton;
import com.paypal.selion.platform.mobile.ios.UIANavigationBar;
import com.paypal.selion.platform.mobile.ios.UIASlider;
import com.paypal.selion.platform.mobile.ios.UIASwitch;
import com.paypal.selion.platform.mobile.ios.UIATextField;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumIOSSliderSwitchTest {

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
    public void testSliderDrag() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'Touch')]") });
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'State')]") });
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIASlider(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIAScrollView[1]/UIASlider[1]") });
        UIASlider slider = new UIASlider("xpath=//UIAApplication[1]/UIAWindow[1]/UIAScrollView[1]/UIASlider[1]");
        slider.dragToValue(0.75);
        UIATextField sliderText = new UIATextField(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIAScrollView[1]/UIATextField[1]");
        Assert.assertEquals(sliderText.getValue(), "0.762712", "Slider value does not match");
    }

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
    public void testSwitchStateChange() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'Touch')]") });
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'State')]") });
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'Table')]") });
        UIASwitch uiaswitch = new UIASwitch("xpath=//UIAApplication[1]/UIAWindow[1]/UIAScrollView[1]/UIASwitch[1]");
        uiaswitch.changeValue();
        UIATextField switchText = new UIATextField(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIAScrollView[1]/UIATextField[2]");
        Assert.assertEquals(switchText.getValue(), "Switch is OFF", "Switch state does not match");
    }

}
