/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

import java.util.Map;
import com.paypal.selion.testcomponents.BasicPageImpl;
import com.paypal.selion.platform.html.Button;
import com.paypal.selion.platform.html.Link;
import com.paypal.selion.platform.html.Container;
import com.paypal.selion.platform.html.ParentTraits;

public class TestInitializeElementsPage extends BasicPageImpl {

    private HeaderContainer headerContainer;
    private Button preLoginButton;

     /*
     * SeLion GUI Html Object Standard
     * 
     * The naming conventions followed in SeLion for html elements are as follows,
     *
     * <alias>{corresponding class name of the html element in SeLion}
     *   where - <alias> will be the object name with its first letter in lowercase.
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
        super();
        super.initPage(PAGE_DOMAIN, CLASS_NAME);
    }
    
    /**
     * Creates a new TestInitializeElementsPage object
     * @param siteLocale
     *         The Country locale for the site you are accessing
     */
    public TestInitializeElementsPage(String siteLocale) {
        super();
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
    }

    public TestInitializeElementsPage getPage() {
        return this;
    }

    /**
     * Used to get the HeaderContainer.
     * 
     * @return headerContainer
     */
    public HeaderContainer getHeaderContainer() {
        HeaderContainer element = this.headerContainer;
        if (element == null) {
            this.headerContainer = new HeaderContainer(this.getObjectMap().get("headerContainer"),
                    "headerContainer", this, this.getObjectContainerMap().get("headerContainer"));
        }
        return this.headerContainer;
    }

    /**
     * Used to get HeaderContainer at specified index.
     * @return headerContainer at index
     */
    public HeaderContainer getHeaderContainer(int index) {
        getHeaderContainer().setIndex(index);
        return headerContainer;
    }

    /**
     * Used to get preLoginButton in the page TestInitializeElementsPage
     * 
     * @return preLoginButton
     */
    public Button getPreLoginButton() {
        Button element = this.preLoginButton;
        if (element == null) {
            this.preLoginButton = new Button(this.getObjectMap().get("preLoginButton"), "preLoginButton", this);
        }
        return this.preLoginButton;
    }

    /**
     * Used to click preLoginButton in the page TestInitializeElementsPage and check that resulting page contains
     * expected item.
     */
    public void clickPreLoginButton(Object... expected) {
        getPreLoginButton().click(expected);
    }

    /**
     * Used to click preLoginButton in the page TestInitializeElementsPage
     */
    public void clickPreLoginButton() {
        getPreLoginButton().click();
    }

    /**
     * Used to get the value of preLoginButton in the page TestInitializeElementsPage.
     * @return text in preLoginButton
     */
    public String getPreLoginButtonValue() {
        return getPreLoginButton().getText();
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
         * private TestInitializeElementsPage.HeaderContainer headerContainer = pageInstance.new HeaderContainer(&quot;//span[@id='containerLocator']&quot;);
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
        
        /**
         * Use this constructor to override default controlName and assign a parent
         * 
         * @param locator
         *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
         * @param controlName
         *            the control name used for logging.
         * @param parent
         *            A {@link ParentTraits} object that represents the parent element for this element.
         * 
         */
        public HeaderContainer(String locator, String controlName, ParentTraits parent) {
            super(locator, controlName, parent);
        }

        public HeaderContainer(String locator, String controlName, ParentTraits parent,
                Map<String, String> containerElements) {
            super(locator, controlName, parent, containerElements);
        }

        private HeaderContainer getContainer() {
            if (!isInitialized()) {
                getObjectMap();
            }
            return this;
        }

        /**
         * 
         * Used to get someLink in headerContainer
         * 
         * @return someLink
         */
        public Link getSomeLink() {
            Link containerElement = getContainer().someLink;
            if (containerElement == null) {
                getContainer().someLink = new Link(this.containerElements.get("someLink"), "someLink", this);
            }
            return getContainer().someLink;
        }

    }
}

