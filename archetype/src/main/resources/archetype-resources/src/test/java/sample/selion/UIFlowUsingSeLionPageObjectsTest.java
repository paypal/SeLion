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

package ${package}.sample.selion;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;
import ${package}.sample.MyAppHomePage;

import ${package}.utilities.server.TestServerUtils;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * This sample demonstrates the Page Object Model that SeLion supports for interacting with web pages.
 * It leverages on the page classes that were created from selion code generator plugin.
 *
 */
public class UIFlowUsingSeLionPageObjectsTest {

    @BeforeClass
    public void startLocalServer () throws Exception {
        TestServerUtils.startServer();
    }

    @Test
    @WebTest
    public void myTest () {
        Grid.open(TestServerUtils.getAppURL());
        //We are now creating a page object that represents the actual test page.
        //In our yaml file which resides under src/main/resources/GUIData our localization value is being
        //given as "english". The default value that SeLion assumes would be "US". So we have two options
        //1. We instantiate the page by passing in the value of 'english' which is how we are going to be
        // dealing with our localizations.
        //2. We can set this at the entire JVM level by passing in the value via the JVM argument
        //-DSELION_SITE_LOCALE=english
        //3. We can set this at a specific <test> level by setting the parameter
        //<parameter name="siteLocale" value="english"/> in the suite xml file.
        MyAppHomePage page = new MyAppHomePage("US");
        page.getFirstNameTextField().type("Joe");
        page.getLastNameTextField().type("User");
        page.getEmailTextField().type("joeuser@foo.bar");
        page.getSubmitButton().clickAndExpect(ExpectedConditions.visibilityOf(page.getRetryAgainButton().getElement()));
        assertEquals(page.getFirstNameLabel().getAttribute("innerHTML"), "Joe");
        assertEquals(page.getLastNameLabel().getAttribute("innerHTML"), "User");
        assertEquals(page.getEmailLabel().getAttribute("innerHTML"), "joeuser@foo.bar");
    }

    @AfterClass
    public void shutdownLocalServer () throws Exception {
        TestServerUtils.stopServer();
    }

}
