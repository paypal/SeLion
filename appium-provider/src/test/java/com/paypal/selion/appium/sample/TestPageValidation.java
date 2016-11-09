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
import com.paypal.selion.appium.sample.pages.*;
import com.paypal.selion.platform.html.PageValidationException;
import org.testng.annotations.Test;

public class TestPageValidation {
    @Test
    @MobileTest
    public void testSamplePageTest() throws Exception {
        SamplePage samplePage = new SamplePage();
        samplePage.validatePage();
    }

    @Test(expectedExceptions = PageValidationException.class)
    @MobileTest
    public void testTableValidation() throws Exception {
        SamplePage samplePage = new SamplePage();
        TapPage tapPage = new TapPage();
        TouchPage touchPage = new TouchPage();
        StatePage statePage = new StatePage();
        TablePage tablePage = new TablePage();
        samplePage.getNextButton().tap(tapPage.getSingleTapButton());
        tapPage.getNextButton().tap();
        touchPage.getNextButton().tap(statePage.getStateSwitch());
        statePage.getNextButton().tap(tablePage.getTableList());
        tablePage.validatePage();
    }
}
