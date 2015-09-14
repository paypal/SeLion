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

package com.paypal.selion.internal.platform.asserts;

import java.util.logging.Level;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.paypal.selion.configuration.ListenerInfo;
import com.paypal.selion.configuration.ListenerManager;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.asserts.SeLionSoftAssert;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * <code>SeLionAssertsListener</code> holds all the test level logic for SeLion asserts.
 */
public class SeLionAssertsListener implements IInvokedMethodListener {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    /**
     * This String constant represents the JVM argument that can be enabled/disabled to enable/disable
     * {@link SeLionAssertsListener}
     */
    public static final String ENABLE_ASSERTS_LISTENER = "enable.asserts.listener";

    public SeLionAssertsListener() {
        ListenerManager.registerListener(new ListenerInfo(this.getClass(), ENABLE_ASSERTS_LISTENER));
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        logger.entering(new Object[] { method, testResult });
        try {
            if (ListenerManager.isCurrentMethodSkipped(this)) {
                logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
                return;
            }
            // Initialize soft asserts for this test method instance.
            SeLionSoftAssert softAsserts = new SeLionSoftAssert();
            testResult.setAttribute(SeLionSoftAssert.SOFT_ASSERT_ATTRIBUTE_NAME, softAsserts);
        } catch (Exception e) { // NOSONAR
            logger.log(Level.WARNING, "An error occurred while processing beforeInvocation: " + e.getMessage(), e);
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        logger.entering(new Object[] { method, testResult });
        try {
            if (ListenerManager.isCurrentMethodSkipped(this)) {
                logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
                return;
            }
            // Assert all the soft asserts captured as part of this test method instance.
            if (Reporter.getCurrentTestResult() != null) {
                SeLionSoftAssert sa = (SeLionSoftAssert) Reporter.getCurrentTestResult().getAttribute(
                        SeLionSoftAssert.SOFT_ASSERT_ATTRIBUTE_NAME);
                if (sa != null) {
                    sa.assertAll();
                }
            }
        } catch (Exception e) { // NOSONAR
            logger.log(Level.WARNING, "An error occurred while processing afterInvocation: " + e.getMessage(), e);
        }
    }
}
