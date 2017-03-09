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
import com.paypal.selion.appium.sample.pages.SamplePage;
import com.paypal.selion.appium.sample.pages.StatePage;
import com.paypal.selion.appium.sample.pages.TapPage;
import com.paypal.selion.appium.sample.pages.TouchPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestStatePage {

    @Test
    @MobileTest
    public void testSlider() throws Exception {
        StatePage statePage = InitializePages();
        statePage.getSeekbarSlider().dragToValue(1);
        Assert.assertEquals(statePage.getSliderTextField().getValue(), "1.000000");
        statePage.getSeekbarSlider().dragToValue(0);
        Assert.assertEquals(statePage.getSliderTextField().getValue(), "0.000000");
    }

    @Test
    @MobileTest
    public void testSwitch() throws Exception {
        StatePage statePage = InitializePages();
        statePage.getStateSwitch().changeValue();
        Assert.assertEquals(statePage.getSwitchTextField().getValue(), "Switch is OFF");
        statePage.getStateSwitch().changeValue();
        Assert.assertEquals(statePage.getSwitchTextField().getValue(), "Switch is ON");
    }

    private StatePage InitializePages() {
        SamplePage samplePage = new SamplePage();
        TapPage tapPage = new TapPage();
        TouchPage touchPage = new TouchPage();
        StatePage statePage = new StatePage();
        samplePage.getNextButton().tap(tapPage.getSingleTapButton());
        tapPage.getNextButton().tap();
//        tapPage.getNextButton().tap(touchPage.getTouchButton());
        touchPage.getNextButton().tap(statePage.getStateSwitch());
        return statePage;
    }
}
