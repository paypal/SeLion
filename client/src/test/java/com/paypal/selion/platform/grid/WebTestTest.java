/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2017 PayPal                                                                                     |
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
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import com.paypal.selion.reports.runtime.SeLionReporter;

/**
 * Tests different combinations for @Webtest annotation and it's parameters
 */
public class WebTestTest {

    public static class ChromeOptionsOverrideCapabilities extends DefaultCapabilitiesBuilder {
        @Override
        public DesiredCapabilities createCapabilities() {
            return getCapabilities(DesiredCapabilities.chrome());
        }

        @Override
        public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--bogus-arg");

            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
            return capabilities;
        }
    }

    /**
     * Test support of TestNG's {@link Test} capabilities for multi-threading
     **/
    @Test(groups = { "functional" }, threadPoolSize = 2, invocationCount = 2, timeOut = 10000)
    @WebTest
    public void testUnamedSessionMultipleThreadsAndInvocations() {
        SeLionReporter.log("Browser start page", true, true);
    }

    @Test(groups = "functional")
    @WebTest(additionalCapabilities = { "useBooleanCaps:true", "useStringCaps:'true'" })
    public void testCapabilityViaAnnotation() {
        // @deprecated assertions
        assertTrue((boolean) Grid.getWebTestSession().getAdditionalCapabilities().getCapability("useBooleanCaps"));
        assertEquals(Grid.getWebTestSession().getAdditionalCapabilities().getCapability("useStringCaps"), "true");
    }

    @Test(groups = "functional")
    @WebTest(additionalCapabilities = { "name:a:b" })
    public void testCapabilityWithColonInValue() {
        assertEquals(Grid.getWebTestSession().getAdditionalCapabilities().getCapability("name"), "a:b");
    }

    @SuppressWarnings("unchecked")
    @Test(groups = "functional")
    @WebTest(additionalCapabilitiesBuilders = ChromeOptionsOverrideCapabilities.class)
    public void testCapabilityBuilderViaAnnotation() throws Exception {
        DesiredCapabilities caps = Grid.getWebTestSession().getAdditionalCapabilities();

        assertEquals(caps.getCapability(CapabilityType.BROWSER_NAME), "chrome");

        Map<String, List<String>> chromeOptions = (Map<String, List<String>>) caps.asMap().get(ChromeOptions.CAPABILITY);
        List<String> args = chromeOptions.get("args");
        assertEquals(args.size(), 1);
        assertEquals(args.get(0), "--bogus-arg");
    }

    @SuppressWarnings("unchecked")
    @Test(groups = "functional")
    @WebTest
    public void testCapabilityBuilderInline() throws Exception {
        DesiredCapabilities caps = Grid.getWebTestSession().getAdditionalCapabilities();

        caps.merge(new ChromeOptionsOverrideCapabilities().createCapabilities());
        assertEquals(caps.getCapability(CapabilityType.BROWSER_NAME), "chrome");

        Map<String, List<String>> chromeOptions = (Map<String, List<String>>) 
                Grid.getWebTestSession().getAdditionalCapabilities().getCapability(ChromeOptions.CAPABILITY);
        List<String> args = (List<String>) chromeOptions.get("args");
        assertEquals(args.size(), 1);
        assertEquals(args.get(0), "--bogus-arg");
    }

    @Test(testName = "testCapabilityViaTestResult", groups = "functional")
    @WebTest
    @Deprecated
    public void testCapabilityViaTestResult() {
        // @deprecated assertions
        assertTrue((boolean) Grid.getWebTestSession().getAdditionalCapabilities().getCapability("useBooleanDCCaps"));
        assertEquals(Grid.getWebTestSession().getAdditionalCapabilities().getCapability("useStringDCCaps"), "true");
    }

    @BeforeMethod(alwaysRun = true)
    @Deprecated
    public void setCapability(ITestResult testResult, Method method) {
        Test test = method.getAnnotation(Test.class);
        if (test != null && test.testName().equalsIgnoreCase("testCapabilityViaTestResult")) {
            DesiredCapabilities dc = new DesiredCapabilities();
            dc.setCapability("useStringDCCaps", "true");
            dc.setCapability("useBooleanDCCaps", Boolean.TRUE);
            testResult.setAttribute(com.paypal.selion.configuration.ExtendedConfig.CAPABILITIES.getConfig(), dc);
        }
    }

}
