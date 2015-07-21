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

package com.paypal.selion.platform.grid;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.ExtendedConfig;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.SeLionReporter;

/**
 * Tests different combinations for @Webtest annotation and it's parameters
 */
public class WebTestTest {

    /**
     * Test support of TestNG's {@link Test} capabilities for multi-threading
     **/
    @Test(groups = { "functional" }, threadPoolSize = 2, invocationCount = 2, timeOut = 10000)
    @WebTest
    public void testUnamedSessionMultipleThreadsAndInvocations() {
        SeLionReporter.log("Browser start page", true, true);
    }

    @Test(groups = "functional")
    @WebTest(additionalCapabilities = { "useBooleanCaps:true","useStringCaps:'true'" })
    public void testCapabilityViaAnnotation() {
        assertEquals(Grid.getWebTestSession().getAdditionalCapabilities().getCapability("useBooleanCaps"), Boolean.TRUE);
        assertEquals(Grid.getWebTestSession().getAdditionalCapabilities().getCapability("useStringCaps"), "true");
    }
    
    @Test(groups = "functional")
    @WebTest(additionalCapabilities = { "name:a:b"})
    public void testCapabilityWithColonInValue() {
        assertEquals(Grid.getWebTestSession().getAdditionalCapabilities().getCapability("name"), "a:b");
    }

    @Test(testName = "testCapabilityViaTestResult", groups = "functional")
    @WebTest
    public void testCapabilityViaTestResult() {
        assertEquals(Grid.getWebTestSession().getAdditionalCapabilities().getCapability("useBooleanDCCaps"), Boolean.TRUE);
        assertEquals(Grid.getWebTestSession().getAdditionalCapabilities().getCapability("useStringDCCaps"), "true");
    }

    @BeforeMethod(alwaysRun = true)
    public void setCapability(ITestResult testResult, Method method) {
        Test test = method.getAnnotation(Test.class);
        if (test != null && test.testName().equalsIgnoreCase("testCapabilityViaTestResult")) {
            DesiredCapabilities dc = new DesiredCapabilities();
            dc.setCapability("useStringDCCaps", "true");
            dc.setCapability("useBooleanDCCaps", Boolean.TRUE);
            testResult.setAttribute(ExtendedConfig.CAPABILITIES.getConfig(), dc);
        }
    }

}
