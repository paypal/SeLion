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

package com.paypal.selion.testcomponents.paypal;

import com.paypal.selion.platform.html.Button;
import com.paypal.selion.platform.html.CheckBox;
import com.paypal.selion.platform.html.Container;
import com.paypal.selion.platform.html.Label;
import com.paypal.selion.platform.html.RadioButton;
import com.paypal.selion.platform.html.SelectList;
import com.paypal.selion.platform.html.TextField;
import com.paypal.selion.testcomponents.BasicPageImpl;

public class TestPage extends BasicPageImpl {

    private TextField fieldXTextField;
    private Button continueButton;
    private Button hiddenButton;
    private Label logLabel;
    private RadioButton xRadioButton;
    private CheckBox xCheckBox;
    private SelectList xSelectList;
    private SeLionContainer selionContainer;

    private String CLASS_NAME = "TestPage";

    private String PAGE_DOMAIN = "paypal";

    public TestPage() {
        super.initPage(PAGE_DOMAIN, CLASS_NAME);
    }

    public TestPage(String siteLocale) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
    }

    public TestPage(String siteLocale, String className) {
        super.initPage(PAGE_DOMAIN, className, siteLocale);
    }

    public TestPage getPage() {
        if (!isInitialized()) {
            loadObjectMap();
            initializeHtmlObjects(this, this.objectMap);
        }
        return this;
    }

    public Button getContinueButton() {
        return getPage().continueButton;
    }

    public void clickContinueButton(Object... expected) {
        getPage().continueButton.click(expected);
    }

    public void clickContinueButton() {
        getPage().continueButton.click();
    }

    public String getContinueButtonValue() {
        return getPage().continueButton.getText();
    }

    public Button getHiddenButton() {
        return getPage().hiddenButton;
    }

    public void clickHiddenButton(Object... expected) {
        getPage().hiddenButton.click(expected);
    }

    public void clickHiddenButton() {
        getPage().hiddenButton.click();
    }

    public String getHiddenButtonValue() {
        return getPage().hiddenButton.getText();
    }

    public TextField getFieldXTextField() {
        return getPage().fieldXTextField;
    }

    public void setFieldXTextFieldValue(String value) {
        getPage().fieldXTextField.type(value);
    }

    public String getFieldXTextFieldValue() {
        return getPage().fieldXTextField.getText();
    }

    public Label getLogLabel() {
        return getPage().logLabel;
    }
    
    public RadioButton getXRadioButton() {
        return getPage().xRadioButton;
    }
    
    public CheckBox getXCheckBox() {
        return getPage().xCheckBox;
    }
    
    public SelectList getXSelectList() {
        return getPage().xSelectList;
    }

    public SeLionContainer getSelionContainer() {
        return getPage().selionContainer;
    }

    public SeLionContainer getSelionContainer(int index) {
        getPage().selionContainer.setIndex(index);
        return selionContainer;
    }

    public class SeLionContainer extends Container {

        private Button containerButton;

        public SeLionContainer(String locator) {
            super(locator);
        }

        public SeLionContainer(String locator, String controlName) {
            super(locator, controlName);
        }

        private SeLionContainer getContainer() {
            if (!isInitialized()) {
                loadObjectMap();
                initializeHtmlObjects(this, TestPage.this.objectMap);
            }
            return this;
        }

        public Button getContainerButton() {
            return getContainer().containerButton;
        }
    }

    public void setPageTitle(String pageTitle) {
        getPage().pageTitle = pageTitle;
    }
}