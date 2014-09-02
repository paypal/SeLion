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

package com.paypal.selion.platform.grid;

import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.html.TestObjectRepository;
import com.paypal.selion.platform.html.TextField;
import com.paypal.selion.reports.runtime.WebReporter;
import com.paypal.test.utilities.server.TestServerUtils;

public class SessionSharingTest {
	
    /**
     * Test dynamically named session across three dependent test methods
     **/
    @Test(groups = { "sessionWebTests" })
    @WebTest(keepSessionOpen = true)
    public void testDynamicallyNamedSessionAcrossThreeTestMethods_part1() {
        Grid.open(TestServerUtils.getTestEditableURL());
        
        WebReporter.log("Editable Test Page (" + getSessionId() + ")", true, true);
    }
    
    private SessionId getSessionId() {
        return ((RemoteWebDriver) Grid.driver().getWrappedDriver()).getSessionId();
    }

    @Test(groups = { "sessionWebTests" }, dependsOnMethods = "testDynamicallyNamedSessionAcrossThreeTestMethods_part1")
    @WebTest(openNewSession = false, keepSessionOpen = true)
    public void testDynamicallyNamedSessionAcrossThreeTestMethods_part2() throws IOException {
        // should already be on test Page
        WebReporter.log("Editable Test Page (" + getSessionId() + ")", true, true);
        assertTrue(Grid.driver().getTitle().contains("Sample Unit Test Page"), "shuold be on Sample Unit Test Page already with this session");
        Grid.open(TestServerUtils.getTestEditableURL());
        WebReporter.log("Sample Unit Test Page", true, true);
    }

    @Test(groups = { "sessionWebTests" }, dependsOnMethods = {
            "testDynamicallyNamedSessionAcrossThreeTestMethods_part1",
            "testDynamicallyNamedSessionAcrossThreeTestMethods_part2" })
    @WebTest(openNewSession = false)
    public void testDynamicallyNamedSessionAcrossThreeTestMethods_part3() {
        // should already be on apple.com
        WebReporter.log("Sample Unit Test Page (" + getSessionId() + ")", true, true);
        assertTrue(Grid.driver().getTitle().contains("Sample Unit Test Page"),
                "should be on editable Test page already with this session");
        TextField normalTextField = new TextField(TestObjectRepository.TEXTFIELD_LOCATOR.getValue());
        normalTextField.type("Test");
    }

    /**
     * Test named session across two dependent test methods
     * @throws IOException 
     **/
    @Test(groups = { "sessionWebTests" }, dependsOnMethods = "testDynamicallyNamedSessionAcrossThreeTestMethods_part3")
    @WebTest(sessionName = "paypal-help-flow", keepSessionOpen = true)
    public void testNamedSessionAcrossTwoDependentTestMethods_part1() throws IOException {
        Grid.open(TestServerUtils.getTestEditableURL());
        WebReporter.log("Sample Unit Test Page (" + getSessionId() + ")", true, true);
    }

    @Test(groups = { "sessionWebTests" }, dependsOnMethods = "testNamedSessionAcrossTwoDependentTestMethods_part1")
    @WebTest(sessionName = "paypal-help-flow", openNewSession = false)
    public void testNamedSessionAcrossTwoDependentTestMethods_part2() {
        WebReporter.log("Sample Unit Test Page (" + getSessionId() + ")", true, true);
        assertTrue(Grid.driver().getTitle().contains("Sample Unit Test Page"),
                "should be on Sample Unit Test already with this session");
        TextField normalTextField = new TextField(TestObjectRepository.TEXTFIELD_LOCATOR.getValue());
        normalTextField.type("Test");
    }
}
