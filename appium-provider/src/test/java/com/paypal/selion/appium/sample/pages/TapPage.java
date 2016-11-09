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
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import com.paypal.selion.testcomponents.BasicPageImpl;

public class TapPage extends BasicPageImpl {

    private MobileButton nextButton;
    private MobileTextField topTextField;
    private MobileButton singleTapButton;
    private MobileButton multiTapButton;
    private MobileTextField singleTapTextField;
    private MobileButton preButton;
    private MobileTextField multiTapTextField;

    private static String CLASS_NAME = "TapPage";
    private static String PAGE_DOMAIN = "paypal/mobile";

    /**
     * Creates a new TapPage object
     */
    public TapPage() {
        super.initPage(PAGE_DOMAIN, CLASS_NAME);
    }

    /**
     * Creates a new TapPage object
     *
     * @param siteLocale
     *            The Country locale for the site you are accessing
     */
    public TapPage(String siteLocale) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param platform
     *            The Platform for the site you are accessing
     */
    public TapPage(WebDriverPlatform platform) {
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
    public TapPage(String siteLocale, WebDriverPlatform platform) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale, platform);
    }

    public TapPage getPage() {
        return this;
    }

    /**
     * Used to get nextButton in the page TapPage
     *
     * @return nextButton
     */
    public MobileButton getNextButton() {
        MobileButton element = this.nextButton;
        if (element == null) {
            this.nextButton = MobileImplementationFinder.instantiate(getPlatform(), MobileButton.class,
                    getObjectMap().get("nextButton"));
        }
        return this.nextButton;
    }

    /**
     * Used to get topTextField in the page TapPage
     *
     * @return topTextField
     */
    public MobileTextField getTopTextField() {
        MobileTextField element = this.topTextField;
        if (element == null) {
            this.topTextField = MobileImplementationFinder.instantiate(getPlatform(), MobileTextField.class,
                    getObjectMap().get("topTextField"));
        }
        return this.topTextField;
    }

    /**
     * Used to get singleTapButton in the page TapPage
     *
     * @return singleTapButton
     */
    public MobileButton getSingleTapButton() {
        MobileButton element = this.singleTapButton;
        if (element == null) {
            this.singleTapButton = MobileImplementationFinder.instantiate(getPlatform(), MobileButton.class,
                    getObjectMap().get("singleTapButton"));
        }
        return this.singleTapButton;
    }

    /**
     * Used to get multiTapButton in the page TapPage
     *
     * @return multiTapButton
     */
    public MobileButton getMultiTapButton() {
        MobileButton element = this.multiTapButton;
        if (element == null) {
            this.multiTapButton = MobileImplementationFinder.instantiate(getPlatform(), MobileButton.class,
                    getObjectMap().get("multiTapButton"));
        }
        return this.multiTapButton;
    }

    /**
     * Used to get singleTapTextField in the page TapPage
     *
     * @return singleTapTextField
     */
    public MobileTextField getSingleTapTextField() {
        MobileTextField element = this.singleTapTextField;
        if (element == null) {
            this.singleTapTextField = MobileImplementationFinder.instantiate(getPlatform(), MobileTextField.class,
                    getObjectMap().get("singleTapTextField"));
        }
        return this.singleTapTextField;
    }

    /**
     * Used to get preButton in the page TapPage
     *
     * @return preButton
     */
    public MobileButton getPreButton() {
        MobileButton element = this.preButton;
        if (element == null) {
            this.preButton = MobileImplementationFinder.instantiate(getPlatform(), MobileButton.class,
                    getObjectMap().get("preButton"));
        }
        return this.preButton;
    }

    /**
     * Used to get multiTapTextField in the page TapPage
     *
     * @return multiTapTextField
     */
    public MobileTextField getMultiTapTextField() {
        MobileTextField element = this.multiTapTextField;
        if (element == null) {
            this.multiTapTextField = MobileImplementationFinder.instantiate(getPlatform(), MobileTextField.class,
                    getObjectMap().get("multiTapTextField"));
        }
        return this.multiTapTextField;
    }

}
