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

import com.paypal.selion.platform.html.Button;
import com.paypal.selion.platform.html.CheckBox;
import com.paypal.selion.platform.html.Container;
import com.paypal.selion.platform.html.Label;
import com.paypal.selion.platform.html.ParentTraits;
import com.paypal.selion.platform.html.RadioButton;
import com.paypal.selion.platform.html.SelectList;
import com.paypal.selion.platform.html.TextField;
import com.paypal.selion.testcomponents.BasicPageImpl;

public class TestPage extends BasicPageImpl {

    private RadioButton xRadioButton;
    private Label logLabel;
    private SelectList xSelectList;
    private Button hiddenButton;
    private SelionContainer selionContainer;
    private Button continueButton;
    private TextField fieldXTextField;
    private CheckBox xCheckBox;

    private static String CLASS_NAME = "TestPage";
    private static String PAGE_DOMAIN = "paypal";

    /**
     * Creates a new TestPage object
     */
    public TestPage() {
        super();
        super.initPage(PAGE_DOMAIN, CLASS_NAME);
    }

    /**
     * Creates a new TestPage object
     * 
     * @param siteLocale
     *            The Country locale for the site you are accessing
     */
    public TestPage(String siteLocale) {
        super();
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
    }

    public TestPage(String siteLocale, String className) {
        super();
        super.initPage(PAGE_DOMAIN, className, siteLocale);
    }

    public TestPage getPage() {
        return this;
    }

    /**
     * Used to get xRadioButton in the page TestPage
     * 
     * @return xRadioButton
     */
    public RadioButton getXRadioButton() {
        RadioButton element = this.xRadioButton;
        if (element == null) {
            this.xRadioButton = new RadioButton(this.getObjectMap().get("xRadioButton"), "xRadioButton", this);
        }
        return this.xRadioButton;
    }

    /**
     * Used to check the control xRadioButton in the page TestPage
     */
    public void checkXRadioButton() {
        getXRadioButton().check();
    }

    /**
     * Used to click the control xRadioButton in the page TestPage
     */
    public void clickXRadioButton() {
        getXRadioButton().click();
    }

    /**
     * Used to get logLabel in the page TestPage
     * 
     * @return logLabel
     */
    public Label getLogLabel() {
        Label element = this.logLabel;
        if (element == null) {
            this.logLabel = new Label(this.getObjectMap().get("logLabel"), "logLabel", this);
        }
        return this.logLabel;
    }

    /**
     * Used to check for the specific text available in the control logLabel
     */
    public boolean isTextPresentForLogLabel(String pattern) {
        return getLogLabel().isTextPresent(pattern);
    }

    /**
     * Used to get xSelectList in the page TestPage
     * 
     * @return xSelectList
     */
    public SelectList getXSelectList() {
        SelectList element = this.xSelectList;
        if (element == null) {
            this.xSelectList = new SelectList(this.getObjectMap().get("xSelectList"), "xSelectList", this);
        }
        return this.xSelectList;
    }

    /**
     * Used to select element in the control xSelectList based on the value.
     */
    public void selectXSelectListByValue(String value) {
        getXSelectList().selectByValue(value);
    }

    /**
     * Used to select element in the control xSelectList based on the label.
     */
    public void selectXSelectListByLabel(String label) {
        getXSelectList().selectByLabel(label);
    }

    /**
     * Used to select element in the control xSelectList based on the index
     */
    public void selectXSelectListByIndex(int index) {
        getXSelectList().selectByIndex(index);
    }

    /**
     * Used to get hiddenButton in the page TestPage
     * 
     * @return hiddenButton
     */
    public Button getHiddenButton() {
        Button element = this.hiddenButton;
        if (element == null) {
            this.hiddenButton = new Button(this.getObjectMap().get("hiddenButton"), "hiddenButton", this);
        }
        return this.hiddenButton;
    }

    /**
     * Used to click hiddenButton in the page TestPage and check that resulting page contains expected item.
     */
    public void clickHiddenButton(Object... expected) {
        getHiddenButton().click(expected);
    }

    /**
     * Used to click hiddenButton in the page TestPage
     */
    public void clickHiddenButton() {
        getHiddenButton().click();
    }

    /**
     * Used to get the value of hiddenButton in the page TestPage.
     * 
     * @return text in hiddenButton
     */
    public String getHiddenButtonValue() {
        return getHiddenButton().getText();
    }

    /**
     * Used to get the SelionContainer.
     * 
     * @return selionContainer
     */
    public SelionContainer getSelionContainer() {
        SelionContainer element = this.selionContainer;
        if (element == null) {
            this.selionContainer = new SelionContainer(this.getObjectMap().get("selionContainer"),
                    "selionContainer", this, this.getObjectContainerMap().get("selionContainer"));
        }
        return this.selionContainer;
    }

    /**
     * Used to get SelionContainer at specified index.
     * 
     * @return selionContainer at index
     */
    public SelionContainer getSelionContainer(int index) {
        getSelionContainer().setIndex(index);
        return selionContainer;
    }

    /**
     * Used to get continueButton in the page TestPage
     * 
     * @return continueButton
     */
    public Button getContinueButton() {
        Button element = this.continueButton;
        if (element == null) {
            this.continueButton = new Button(this.getObjectMap().get("continueButton"), "continueButton", this);
        }
        return this.continueButton;
    }

    /**
     * Used to click continueButton in the page TestPage and check that resulting page contains expected item.
     */
    public void clickContinueButton(Object... expected) {
        getContinueButton().click(expected);
    }

    /**
     * Used to click continueButton in the page TestPage
     */
    public void clickContinueButton() {
        getContinueButton().click();
    }

    /**
     * Used to get the value of continueButton in the page TestPage.
     * 
     * @return text in continueButton
     */
    public String getContinueButtonValue() {
        return getContinueButton().getText();
    }

    /**
     * Used to get fieldXTextField in the page TestPage
     * 
     * @return fieldXTextField
     */
    public TextField getFieldXTextField() {
        TextField element = this.fieldXTextField;
        if (element == null) {
            this.fieldXTextField = new TextField(this.getObjectMap().get("fieldXTextField"), "fieldXTextField",
                    this);
        }
        return this.fieldXTextField;
    }

    /**
     * Used to set the value of fieldXTextField in the page TestPage.
     */
    public void setFieldXTextFieldValue(String fieldX) {
        getFieldXTextField().type(fieldX);
    }

    /**
     * Used to get the value of fieldXTextField in the page TestPage.
     * 
     * @return text in fieldXTextField
     */
    public String getFieldXTextFieldValue() {
        return getFieldXTextField().getText();
    }

    /**
     * Used to get xCheckBox in the page TestPage
     * 
     * @return xCheckBox
     */
    public CheckBox getXCheckBox() {
        CheckBox element = this.xCheckBox;
        if (element == null) {
            this.xCheckBox = new CheckBox(this.getObjectMap().get("xCheckBox"), "xCheckBox", this);
        }
        return this.xCheckBox;
    }

    /**
     * Used to check the control xCheckBox in the page TestPage
     */
    public void checkXCheckBox() {
        getXCheckBox().check();
    }

    /**
     * Used to uncheck the control xCheckBox in the page TestPage
     */
    public void uncheckXCheckBox() {
        getXCheckBox().uncheck();
    }

    /**
     * Used to click the control xCheckBox in the page TestPage
     */
    public void clickXCheckBox() {
        getXCheckBox().click();
    }

    public class SelionContainer extends Container {

        private Button containerButton;

        /**
         * SelionContainer Construction method <br>
         * <br>
         * <b>Usage:</b>
         * 
         * <pre>
         * private TestPage pageInstance = new TestPage();
         * private TestPage.SelionContainer selionContainer = pageInstance.new SelionContainer(&quot;//span[@id='containerLocator']&quot;);
         * </pre>
         * 
         * @param locator
         *            the element locator
         */
        public SelionContainer(String locator) {
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
        public SelionContainer(String locator, String controlName) {
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
        public SelionContainer(String locator, String controlName, ParentTraits parent) {
            super(locator, controlName, parent);
        }

        public SelionContainer(String locator, String controlName, ParentTraits parent,
                java.util.Map<String, String> containerElements) {
            super(locator, controlName, parent, containerElements);
        }

        private SelionContainer getContainer() {
            if (!isInitialized()) {
                getObjectMap();
            }
            return this;
        }

        /**
         * 
         * For SelionContainer Button : containerButton
         * 
         * Used to get containerButton in selionContainer
         * 
         * @return containerButton
         */
        public Button getContainerButton() {
            Button containerElement = getContainer().containerButton;
            if (containerElement == null) {
                getContainer().containerButton = new Button(this.containerElements.get("containerButton"),
                        "containerButton", this);
            }
            return getContainer().containerButton;
        }
    }
}