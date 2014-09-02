/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.testcomponents;

import com.paypal.selion.platform.html.Button;
import com.paypal.selion.platform.html.Container;
import com.paypal.selion.platform.html.Link;
import com.paypal.selion.testcomponents.BasicPageImpl;

/*TODO: This Page is generated from code generator and placed here. 
 * This test page must be maintained to validate initializing HTML objects 
 * using BasicPageImpl.
 * 
 * A Temporary arrangement till code generator is part of OSS.
 */
public class TestInitializeElementsPage extends BasicPageImpl {

    private HeaderContainer headerContainer;
    private Button preLoginButton;

   /**
    * SeLion PageObject Standard
    *
    * The naming conventions followed in SeLion for Html Elements are like,
    *
    * <alias>corresponding Class Name of the Html Element in SeLion
    * where - <alias> will be the object name with its first letter in lowercase.
    *
    *
    * <alias>Button                For Button html element.
    * <alias>CheckBox              For Check Box html element.
    * <alias>DatePicker            For Date Picker html element.
    * <alias>Form                  For Form html element.
    * <alias>Image                 For Image html element.
    * <alias>Label                 For Label html element.
    * <alias>Link                  For Link html element.
    * <alias>RadioButton           For Radio Button html element.
    * <alias>List                  For Select List html element.
    * <alias>Table                 For Table html element.
    * <alias>TextField             For Text Field html element.
    * <alias>Container             For Container html element.
    *
    */
    

    private static String CLASS_NAME = "TestInitializeElementsPage";
    private static String PAGE_DOMAIN = "paypal";

    /**
     * Creates a new TestInitializeElementsPage object
     */
    public TestInitializeElementsPage() {
        super.initPage(PAGE_DOMAIN, CLASS_NAME);
    }

    /**
     * Creates a new TestInitializeElementsPage object
     * 
     * @param siteLocale
     *            - The Country locale for the site you are accessing
     */
    public TestInitializeElementsPage(String siteLocale) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
    }

    public TestInitializeElementsPage getPage() {
        if (!isInitialized()) {
            loadObjectMap();
            initializeHtmlObjects(this, this.objectMap);
            // Initialize non SeLion Html members here
        }
        return this;
    }

    /**
     * 
     * For Container : headerContainer
     */

    /**
     * Used to get the HeaderContainer.
     * 
     * @return headerContainer
     */
    public HeaderContainer getHeaderContainer() {
        return getPage().headerContainer;
    }

    /**
     * Used to get HeaderContainer at specified index.
     * 
     * @return headerContainer at index
     */
    public HeaderContainer getHeaderContainer(int index) {
        getPage().headerContainer.setIndex(index);
        return headerContainer;
    }

    /**
     * 
     * For Button : preLoginButton
     */

    /**
     * Used to get preLoginButton in the page TestInitializeElementsPage
     * 
     * @return preLoginButton
     */
    public Button getPreLoginButton() {
        return getPage().preLoginButton;
    }

    /**
     * Used to click preLoginButton in the page TestInitializeElementsPage and check that resulting page contains
     * expected item.
     * 
     */
    public void clickPreLoginButton(Object... expected) {
        getPage().preLoginButton.click(expected);
    }

    /**
     * Used to click preLoginButton in the page TestInitializeElementsPage
     * 
     */
    public void clickPreLoginButton() {
        getPage().preLoginButton.click();
    }

    /**
     * Used to get the value of preLoginButton in the page TestInitializeElementsPage.
     * 
     * @return text in preLoginButton
     */

    public String getPreLoginButtonValue() {
        return getPage().preLoginButton.getText();
    }

    public class HeaderContainer extends Container {

        private Link someLink;

        /**
         * HeaderContainer Construction method <br>
         * <br>
         * <b>Usage:</b>
         * 
         * <pre>
         * private TestInitializeElementsPage pageInstance = new TestInitializeElementsPage();
         * private TestInitializeElementsPage.HeaderContainer headerContainer = pageInstance.new HeaderContainer(
         *         &quot;//span[@id='containerLocator']&quot;);
         * </pre>
         * 
         * @param locator
         *            the element locator
         */
        public HeaderContainer(String locator) {
            super(locator);
        }

        /**
         * Use this constructor to override default controlName for logging purposes. Default controlName would be the
         * element locator.
         * 
         * @param locator
         *            the element locator
         * @param controlName
         *            the control name used for logging
         */
        public HeaderContainer(String locator, String controlName) {
            super(locator, controlName);
        }

        private HeaderContainer getContainer() {
            if (!isInitialized()) {
                loadObjectMap();
                initializeHtmlObjects(this, TestInitializeElementsPage.this.objectMap);
            }
            return this;
        }

        /**
         * 
         * For HeaderContainer Link : someLink
         * 
         * Used to get someLink in headerContainer
         * 
         * @return someLink
         */
        public Link getSomeLink() {
            return getContainer().someLink;
        }

    }
}
