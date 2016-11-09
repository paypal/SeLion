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
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class TestTablePage {

    @MobileTest
    @Test
    public void testTableClick() throws Exception {
        TablePage tablePage = initializePages();
        if (tablePage.getPlatform().equals(WebDriverPlatform.ANDROID)) {
            tablePage.getTableList().setChildBy(By.id("android:id/text1"));
        }
        tablePage.getTableList().clickCellAtIndex(1, tablePage.getAlertElement());
    }

    @MobileTest
    @Test(groups = "android", expectedExceptions = UIOperationFailedException.class,
            expectedExceptionsMessageRegExp = ".*scrollToCellAtIndex\\(\\) method is not supported in Android platform.*")
    public void testScrollElementAndroid() throws Exception {
        TablePage tablePage = initializePages();
        tablePage.getTableList().setChildBy(By.id("android:id/text1"));
        tablePage.getTableList().scrollToCellAtIndex(15);
    }

    @MobileTest
    @Test(groups = "ios")
    public void testScrollElementIos() throws Exception {
        TablePage tablePage = initializePages();
        tablePage.getTableList().scrollToCellAtIndex(15);
        tablePage.getTableList().clickCellAtIndex(15, tablePage.getAlertElement());
    }

    @MobileTest
    @Test(groups = "android", expectedExceptions = UIOperationFailedException.class,
            expectedExceptionsMessageRegExp = ".*for Android list, cast list to UiList and set the childBy.*")
    public void testChildByNotDefined() throws Exception {
        TablePage tablePage = initializePages();
        tablePage.getTableList().clickCellAtIndex(0);
    }

    private TablePage initializePages() {
        SamplePage samplePage = new SamplePage();
        TapPage tapPage = new TapPage();
        TouchPage touchPage = new TouchPage();
        StatePage statePage = new StatePage();
        TablePage tablePage = new TablePage();
        samplePage.getNextButton().tap(tapPage.getSingleTapButton());
        tapPage.getNextButton().tap();
        touchPage.getNextButton().tap(statePage.getStateSwitch());
        statePage.getNextButton().tap(tablePage.getTableList());
        return tablePage;
    }

}
