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
import com.paypal.selion.platform.mobile.elements.MobileSlider;
import com.paypal.selion.platform.mobile.elements.MobileSwitch;
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import com.paypal.selion.testcomponents.BasicPageImpl;

public class StatePage extends BasicPageImpl {

    private MobileButton nextButton;
    private MobileSlider seekbarSlider;
    private MobileTextField topTextField;
    private MobileButton preButton;
    private MobileTextField switchTextField;
    private MobileSwitch stateSwitch;
    private MobileTextField sliderTextField;

    private static String CLASS_NAME = "StatePage";
    private static String PAGE_DOMAIN = "paypal/mobile";

    /**
     * Creates a new StatePage object
     */
    public StatePage() {
        super.initPage(PAGE_DOMAIN, CLASS_NAME);
    }

    /**
     * Creates a new StatePage object
     *
     * @param siteLocale
     *            The Country locale for the site you are accessing
     */
    public StatePage(String siteLocale) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param platform
     *            The Platform for the site you are accessing
     */
    public StatePage(WebDriverPlatform platform) {
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
    public StatePage(String siteLocale, WebDriverPlatform platform) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale, platform);
    }

    public StatePage getPage() {
        return this;
    }

    /**
     * Used to get nextButton in the page StatePage
     *
     * @return nextButton
     */
    public MobileButton getNextButton() {
        MobileButton element = this.nextButton;
        if (element == null) {
            this.nextButton = MobileImplementationFinder.instantiate(getPlatform(), MobileButton.class, getObjectMap()
                    .get("nextButton"));
        }
        return this.nextButton;
    }

    /**
     * Used to get seekbarSlider in the page StatePage
     *
     * @return seekbarSlider
     */
    public MobileSlider getSeekbarSlider() {
        MobileSlider element = this.seekbarSlider;
        if (element == null) {
            this.seekbarSlider = MobileImplementationFinder.instantiate(getPlatform(), MobileSlider.class,
                    getObjectMap().get("seekbarSlider"));
        }
        return this.seekbarSlider;
    }

    /**
     * Used to get topTextField in the page StatePage
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
     * Used to get preButton in the page StatePage
     *
     * @return preButton
     */
    public MobileButton getPreButton() {
        MobileButton element = this.preButton;
        if (element == null) {
            this.preButton = MobileImplementationFinder.instantiate(getPlatform(), MobileButton.class, getObjectMap()
                    .get("preButton"));
        }
        return this.preButton;
    }

    /**
     * Used to get switchTextField in the page StatePage
     *
     * @return switchTextField
     */
    public MobileTextField getSwitchTextField() {
        MobileTextField element = this.switchTextField;
        if (element == null) {
            this.switchTextField = MobileImplementationFinder.instantiate(getPlatform(), MobileTextField.class,
                    getObjectMap().get("switchTextField"));
        }
        return this.switchTextField;
    }

    /**
     * Used to get stateSwitch in the page StatePage
     *
     * @return stateSwitch
     */
    public MobileSwitch getStateSwitch() {
        MobileSwitch element = this.stateSwitch;
        if (element == null) {
            this.stateSwitch = MobileImplementationFinder.instantiate(getPlatform(), MobileSwitch.class, getObjectMap()
                    .get("stateSwitch"));
        }
        return this.stateSwitch;
    }

    /**
     * Used to get sliderTextField in the page StatePage
     *
     * @return sliderTextField
     */
    public MobileTextField getSliderTextField() {
        MobileTextField element = this.sliderTextField;
        if (element == null) {
            this.sliderTextField = MobileImplementationFinder.instantiate(getPlatform(), MobileTextField.class,
                    getObjectMap().get("sliderTextField"));
        }
        return this.sliderTextField;
    }

}
