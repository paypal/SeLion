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
import com.paypal.selion.platform.mobile.elements.MobileList;
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import com.paypal.selion.testcomponents.BasicPageImpl;

public class TablePage extends BasicPageImpl {

    private MobileButton nextButton;
    private MobileList tableList;
    private MobileElement alertElement;
    private MobileTextField topTextField;
    private MobileButton preButton;
    private MobileElement notVisibleElement;

    private static String CLASS_NAME = "TablePage";
    private static String PAGE_DOMAIN = "paypal/mobile";

    /**
     * Creates a new TablePage object
     */
    public TablePage() {
        super.initPage(PAGE_DOMAIN, CLASS_NAME);
    }

    /**
     * Creates a new TablePage object
     *
     * @param siteLocale
     *            The Country locale for the site you are accessing
     */
    public TablePage(String siteLocale) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param platform
     *            The Platform for the site you are accessing
     */
    public TablePage(WebDriverPlatform platform) {
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
    public TablePage(String siteLocale, WebDriverPlatform platform) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale, platform);
    }

    public TablePage getPage() {
        return this;
    }

    /**
     * Used to get nextButton in the page TablePage
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
     * Used to get tableList in the page TablePage
     *
     * @return tableList
     */
    public MobileList getTableList() {
        MobileList element = this.tableList;
        if (element == null) {
            this.tableList = MobileImplementationFinder.instantiate(getPlatform(), MobileList.class, getObjectMap()
                    .get("tableList"));
        }
        return this.tableList;
    }

    /**
     * Used to get alertElement in the page TablePage
     *
     * @return alertElement
     */
    public MobileElement getAlertElement() {
        MobileElement element = this.alertElement;
        if (element == null) {
            this.alertElement = MobileImplementationFinder.instantiate(getPlatform(), MobileElement.class,
                    getObjectMap().get("alertElement"));
        }
        return this.alertElement;
    }

    /**
     * Used to get topTextField in the page TablePage
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
     * Used to get preButton in the page TablePage
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

    public MobileElement getNotVisibleElement() {
        MobileElement element = this.notVisibleElement;
        if (element == null) {
            this.notVisibleElement = MobileImplementationFinder.instantiate(getPlatform(), MobileElement.class,
                    getObjectMap().get("notVisibleElement"));
        }
        return this.notVisibleElement;
    }
}
