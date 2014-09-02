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

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.ExtendedConfig;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.WebReporter;

/**
 * Tests different combinations for @Webtest annotation and it's parameters
 */
public class WebTestTest {

    /*
     * Threaded and named session tests
     */

    /**
     * Test un-named session that thinks it wants to use an existing session
     **/
    @Test(groups = { "functional" }, expectedExceptions = IllegalArgumentException.class, enabled = false)
    @WebTest(openNewSession = false)
    public void testUnnamedSessionWithoutDependentMethodOpenNewSession_false() {
        // An IllegalArgumentException should have occurred by now
        // TODO : Fix this test.
        // IllegalArgumentException is being thrown by afterInvocation(). TestNG doesnt seem to consider
        // the exceptions that are being thrown by afterInvocation, when it evaluates expectedExceptions
        // This test would have to be plugged out to prevent false triggers
    }

    /**
     * Test ambiguous dynamically named session - a Test with multiple dependencies where both leave sessions open
     **/
    @Test(groups = { "functional" })
    @WebTest(keepSessionOpen = true)
    public void testDynamicallyNamedSessionWithAmbiguosDependency_part1() {
        // just spawn a session and leave it open for dependent methods
    }

    @Test(groups = { "functional" }, dependsOnMethods = "testDynamicallyNamedSessionWithAmbiguosDependency_part1")
    @WebTest(keepSessionOpen = true)
    public void testDynamicallyNamedSessionWithAmbiguosDependency_part2() {
        // just spawn a session and leave it open for dependent methods
    }

    @Test(groups = { "functional" }, expectedExceptions = IllegalStateException.class, dependsOnMethods = {
            "testDynamicallyNamedSessionWithAmbiguosDependency_part1",
            "testDynamicallyNamedSessionWithAmbiguosDependency_part2" })
    @WebTest(openNewSession = false)
    public void testDynamicallyNamedSessionWithAmbiguosDependency_part3() {
        // An IllegalStateException should have occurred by now
    }

    /**
     * Test support of TestNG's {@link Test} capabilities for multi-threading
     **/
    @Test(groups = { "functional" }, threadPoolSize = 2, invocationCount = 2, timeOut = 10000)
    @WebTest
    public void testUnamedSessionMultipleThreadsAndInvocations() {
        WebReporter.log("Browser start page", true, true);
    }

    @Test(groups = { "functional" })
    @WebTest(sessionName = "errorFlow", keepSessionOpen = true)
    public void dummySessionCreator() {

    }

    @Test(groups = { "functional" }, expectedExceptions = { RuntimeException.class }, dependsOnMethods = "dummySessionCreator")
    @WebTest(sessionName = "errorFlow", keepSessionOpen = true)
    public void testOpenSessionWithExistingSessionName() {
    }

    @Test(groups = "functional")
    @WebTest(additionalCapabilities = { "useCaps:true" })
    public void testCapabilityViaAnnotation() {
        assertEquals(Grid.getWebTestSession().getAdditionalCapabilities().getCapability("useCaps"), "true");
    }

    @Test(testName = "testCapabilityViaTestResult", groups = "functional")
    @WebTest
    public void testCapabilityViaTestResult() {
        assertEquals(Grid.getWebTestSession().getAdditionalCapabilities().getCapability("useCaps"), "true");
    }

    @BeforeMethod(alwaysRun = true)
    public void setCapability(ITestResult testResult, Method method) {
        Test test = method.getAnnotation(Test.class);
        if (test != null && test.testName().equalsIgnoreCase("testCapabilityViaTestResult")) {
            DesiredCapabilities dc = new DesiredCapabilities();
            dc.setCapability("useCaps", "true");
            testResult.setAttribute(ExtendedConfig.CAPABILITIES.getConfig(), dc);
        }
    }

}
