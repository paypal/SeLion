/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

package com.paypal.selion.platform.html;

/**
 * This class return the HTML object location and the HTML page URL.
 */
public enum TestObjectRepository {
    TEXTFIELD_LOCATOR("name=normal_text"), 
    TEXTFIELD_DISABLED_LOCATOR("name=disabled_text"), 
    SELECTLIST_LOCATOR("name=normal_select"), 
    BUTTON_SUBMIT_LOCATOR("id=submitButton"), 
    CHROME_BUTTON_SUBMIT_LOCATOR("id=navigateButton"), 
    LINK_LOCATOR("confirmAndLeave"), 
    ALERT_SPAWNING_LINK_LOCATOR("confirmAndLeave"),
    NEW_PAGE_LINK_LOCATOR("openNewPage"), 
    CHECKBOX_BEANS_LOCATOR("option-beans"), 
    CHECKBOX_CHILLI_LOCATOR("option-chilli"), 
    RADIOBUTTON_SPUD_LOCATOR("base-spud"), 
    RADIOBUTTON_RICE_LOCATOR("base-rice"), 
    FORM_SEARCH("searchForm"), 
    LABEL_EDITABLE("//th[contains(text(),'Editable text-field')]"), 
    IMAGE_TEST("Earth"), 
    CHROME_IMAGE_TEST("id=ChangePage"), 
    SUCCESS_PAGE_TEXT("//h1[contains(text(),'Success Page')]"), 
    TEXT_AREA_LOCATOR("myTextarea"), 
    TABLE_LOCATOR("id=TestTable"), 
    TABLE_MULTIPLEHEADERS_LOCATOR("id=TestMultipleHeadersTable"), 
    TABLE_THEAD_LOCATOR("id=TestColumnInTHEADTable"), 
    TABLE_EMPTYTABLE_LOCATOR("id=TestEmptyTable"), 
    COMPLETED_LINK_LOCATOR("id=Complted|id=completed1"), 
    COMPLETED_LINK_LOCATOR_NEG("id=Complted|id=CompletedTest"), 
    CHECKBOX_LOCATOR("selection1"), 
    
    EMPTY_PAGE_URL("about:blank");
    
    private String value;

    private TestObjectRepository(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
