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

package com.paypal.selion.appium.sample.pages;

import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.platform.mobile.MobileImplementationFinder;
import com.paypal.selion.platform.mobile.elements.MobileButton;
import com.paypal.selion.platform.mobile.elements.MobileElement;
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import com.paypal.selion.testcomponents.BasicPageImpl;

public class SampleMobilePage extends BasicPageImpl {

    private MobileButton sampleButton;
    private MobileElement sampleElement;
    private MobileTextField sampleTextField;

    private static String CLASS_NAME = "SampleMobilePage";
    private static String PAGE_DOMAIN = "paypal";

    /**
     * Creates a new SampleMobilePage object
     */
    public SampleMobilePage() {
        super.initPage(PAGE_DOMAIN, CLASS_NAME);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param siteLocale
     *            The Country locale for the site you are accessing
     */
    public SampleMobilePage(String siteLocale) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param platform
     *            The Platform for the site you are accessing
     */
    public SampleMobilePage(WebDriverPlatform platform) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, platform);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param siteLocale
     *            The Country locale for the site you are accessing
     * @param platform
     *            The Platform for the site you are accessing
     */
    public SampleMobilePage(String siteLocale, WebDriverPlatform platform) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale, platform);
    }

    public SampleMobilePage getPage() {
        return this;
    }

    /**
     * Used to get sampleButton in the page SampleMobilePage
     *
     * @return sampleButton
     */
    public MobileButton getSampleButton() {
        MobileButton element = this.sampleButton;
        if (element == null) {
            this.sampleButton = MobileImplementationFinder.instantiate(getPlatform(), MobileButton.class,
                    getObjectMap().get("sampleButton"));
        }
        return this.sampleButton;
    }

    /**
     * Used to get sampleElement in the page SampleMobilePage
     *
     * @return sampleElement
     */
    public MobileElement getSampleElement() {
        MobileElement element = this.sampleElement;
        if (element == null) {
            this.sampleElement = MobileImplementationFinder.instantiate(getPlatform(), MobileElement.class,
                    getObjectMap().get("sampleElement"));
        }
        return this.sampleElement;
    }

    /**
     * Used to get sampleTextField in the page SampleMobilePage
     *
     * @return sampleTextField
     */
    public MobileTextField getSampleTextField() {
        MobileTextField element = this.sampleTextField;
        if (element == null) {
            this.sampleTextField = MobileImplementationFinder.instantiate(getPlatform(), MobileTextField.class,
                    getObjectMap().get("sampleTextField"));
        }
        return this.sampleTextField;
    }

}
