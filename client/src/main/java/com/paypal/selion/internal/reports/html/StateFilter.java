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

package com.paypal.selion.internal.reports.html;

import org.testng.ITestResult;

/**
 * This filter will be created with any of the three states ({@link ITestResult#FAILURE},{@link ITestResult#SUCCESS} AND
 * {@link ITestResult#SKIP}). It will filter those {@link ITestResult} that matches the state of the filter
 * 
 */
public class StateFilter implements Filter {

    private final int state;

    public StateFilter(int state) {
        this.state = state;
    }

    @Override
    public boolean isValid(ITestResult result) {

        return result.getStatus() == this.state;
    }

}
