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

package com.paypal.selion.appium.sample;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.appium.sample.pages.SamplePage;
import com.paypal.selion.appium.sample.pages.TapPage;
import com.paypal.selion.platform.mobile.ios.UIAButton;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestTapPage {
    @Test
    @MobileTest
    public void testSingleTap() throws Exception {
        TapPage tapPage = InitializePages();
        tapPage.getSingleTapButton().tap();
        Assert.assertEquals(tapPage.getSingleTapTextField().getValue(), "Tap Count: 1");
        tapPage.getSingleTapButton().tap();
        Assert.assertEquals(tapPage.getSingleTapTextField().getValue(), "Tap Count: 1");
    }

    @Test
    @MobileTest
    public void testMultiTap() throws Exception {
        TapPage tapPage = InitializePages();
        if (tapPage.getPlatform().equals(WebDriverPlatform.IOS)) {
            UIAButton multiTapButton = (UIAButton) tapPage.getMultiTapButton();
            multiTapButton.doubleTap();
            Assert.assertEquals(tapPage.getMultiTapTextField().getValue(), "Tap Count: 2");
        } else {
            Assert.fail("platform " + tapPage.getPlatform().name() + " does not support double tap.");
        }

    }

    private TapPage InitializePages() {
        SamplePage samplePage = new SamplePage();
        TapPage tapPage = new TapPage();
        samplePage.getNextButton().tap(tapPage.getSingleTapButton());
        return tapPage;
    }
}
