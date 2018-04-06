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

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.appium.sample.pages.SampleMobilePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestYamlReaderForMobilePlatform {
    private SampleMobilePage pageAndroidUS;
    private SampleMobilePage pageAndroidFR;
    private SampleMobilePage pageAndroidCH;

    @BeforeClass
    public void before() {
        pageAndroidUS = new SampleMobilePage("US", WebDriverPlatform.ANDROID);
        pageAndroidFR = new SampleMobilePage("FR", WebDriverPlatform.ANDROID);
        pageAndroidCH = new SampleMobilePage("CH", WebDriverPlatform.ANDROID);
    }

    @Test
    @WebTest
    public void testSuccessFileLoad() throws Exception {
        new SampleMobilePage(WebDriverPlatform.IOS);
    }

    @Test
    @WebTest
    public void testSampleButton() throws Exception {
        Assert.assertEquals(pageAndroidUS.getSampleButton().getLocator(), "//SampleButton");
        Assert.assertEquals(pageAndroidFR.getSampleButton().getLocator(), "//SampleButton");
        Assert.assertEquals(pageAndroidCH.getSampleButton().getLocator(), "//SampleButton");
    }

    @Test
    @WebTest
    public void testSampleTextField() throws Exception {
        Assert.assertEquals(pageAndroidUS.getSampleTextField().getLocator(), "//duplicate_specific_define");
        Assert.assertEquals(pageAndroidFR.getSampleTextField().getLocator(), "//androidFRTextField");
        Assert.assertEquals(pageAndroidCH.getSampleTextField().getLocator(), "//[@id='ch_locators']");
    }

    @Test
    @WebTest
    public void testSampleElement() throws Exception {
        Assert.assertEquals(pageAndroidUS.getSampleElement().getLocator(), null);
        Assert.assertEquals(pageAndroidFR.getSampleElement().getLocator(), "//androidSampleElement");
        Assert.assertEquals(pageAndroidCH.getSampleElement().getLocator(), null);
    }

}
