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

package com.paypal.selion.reports.runtime;

import org.testng.IConfigurationListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * A simple {@link ITestListener} which logs {@link Test} events to {@link Reporter#log(String)}
 */
public class DebugListener implements ITestListener, ISuiteListener, IConfigurationListener {

    @Override
    public void onFinish(ITestContext arg0) {
        System.out.println("<test> " + arg0.getName() + " finished");
    }

    @Override
    public void onStart(ITestContext arg0) {
        System.out.println("about to start <test> " + arg0.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
        Reporter.log(arg0.getTestClass().getName() + "." + arg0.getMethod().getMethodName() + " on thread "
                + Thread.currentThread().getId() + " failed but within success percentage", true);
    }

    @Override
    public void onTestFailure(ITestResult arg0) {
        Reporter.log(arg0.getTestClass().getName() + "." + arg0.getMethod().getMethodName() + " on thread "
                + Thread.currentThread().getId() + " failed", true);
        arg0.getThrowable().printStackTrace();
    }

    @Override
    public void onTestSkipped(ITestResult arg0) {
        Reporter.log(arg0.getTestClass().getName() + "." + arg0.getMethod().getMethodName() + " on thread "
                + Thread.currentThread().getId() + " skipped", true);
    }

    @Override
    public void onTestStart(ITestResult arg0) {
        Reporter.log("about to start test " + arg0.getTestClass().getName() + "." + arg0.getMethod().getMethodName()
                + " on thread " + Thread.currentThread().getId(), true);
    }

    @Override
    public void onTestSuccess(ITestResult arg0) {
        Reporter.log(arg0.getTestClass().getName() + "." + arg0.getMethod().getMethodName() + " on thread "
                + Thread.currentThread().getId() + " passed", true);
    }

    @Override
    public void onStart(ISuite suite) {
        System.out.println("about to start <suite> " + suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("<suite> " + suite.getName() + " finished");
    }

    @Override
    public void onConfigurationSuccess(ITestResult itr) {
        System.out.println(itr.getTestClass().getName() + "." + itr.getMethod().getMethodName() + " on thread "
                        + Thread.currentThread().getId() + " passed");
    }

    @Override
    public void onConfigurationFailure(ITestResult itr) {
        System.out.println(itr.getTestClass().getName() + "." + itr.getMethod().getMethodName() + " on thread "
                        + Thread.currentThread().getId() + " failed");
        itr.getThrowable().printStackTrace();
    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {
        System.out.println(itr.getTestClass().getName() + "." + itr.getMethod().getMethodName() + " on thread "
                        + Thread.currentThread().getId() + " skipped");
    }
}
