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

import org.openqa.selenium.UnhandledAlertException;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;

/**
 * This class test the Form class methods
 */
public class FormTest {
	
		
    // Only Chrome Driver is throwing a NPE.
    @Test(groups = { "browser-tests" }, expectedExceptions = { UnhandledAlertException.class,
            NullPointerException.class })
    @WebTest
    public void testSubmitNegativeTest() {
        Form searchForm = new Form(TestObjectRepository.FORM_SEARCH.getValue());
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        try {
            searchForm.submit();
            // lets trigger the exception
            Grid.driver().getTitle();
        } finally {
            // lets ensure that we leave the browser clean by dismissing alerts.
            AlertHandler.flushAllAlerts();
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testSubmitForm() {
        Form searchForm = new Form(TestObjectRepository.FORM_SEARCH.getValue());
        try {
            Grid.driver().get(TestServerUtils.getTestEditableURL());
            searchForm.submit();
        } finally {
            // lets ensure that we leave the browser clean by dismissing alerts.
            AlertHandler.flushAllAlerts();
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testSubmitFormConstructor2() {
        String controlName = "searchForm";
        Form searchForm2 = new Form(TestObjectRepository.FORM_SEARCH.getValue(), controlName);
        try {
            Grid.driver().get(TestServerUtils.getTestEditableURL());
            searchForm2.submit();
        } finally {
            // lets ensure that we leave the browser clean by dismissing alerts.
            AlertHandler.flushAllAlerts();
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testSubmitFormConstructor3() {
        ParentTraits parent = null;
        Form searchForm3 = new Form(parent, TestObjectRepository.FORM_SEARCH.getValue());
        try {
            Grid.driver().get(TestServerUtils.getTestEditableURL());
            searchForm3.submit();
        } finally {
            // lets ensure that we leave the browser clean by dismissing alerts.
            AlertHandler.flushAllAlerts();
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testSubmitFormConstructor4() {
        ParentTraits parent = null;
        String controlName = "searchForm";
        Form searchForm4 = new Form(TestObjectRepository.FORM_SEARCH.getValue(), controlName, parent);
        try {
            Grid.driver().get(TestServerUtils.getTestEditableURL());
            searchForm4.submit();
        } finally {
            // lets ensure that we leave the browser clean by dismissing alerts.
            AlertHandler.flushAllAlerts();
        }
    }
}
