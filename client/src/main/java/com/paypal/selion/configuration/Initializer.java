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

package com.paypal.selion.configuration;

import org.testng.ITestContext;

/**
 * <code>Initializer</code> represents the capabilities that any initializer must possess so as to let SeLion
 * automatically invoke it (even if the implementation resides downstream). This is used within SeLion to drive
 * configuration initializations via the concrete implementations of {@link AbstractConfigInitializer}.
 * 
 */
public interface Initializer {

    /**
     * Denotes the priority value to sequence the initialization. This number is essentially what plays a role when
     * multiple instances of {@link Initializer} are to be sorted in ascending order.
     * 
     * @return a numeric value as priority.
     */
    int getPriority();

    /**
     * Triggers a project specific initialization.
     * 
     * @param context
     *            - a {@link ITestContext} object that represents a &lt;test&gt; configuration.
     */
    void initialize(ITestContext context);
}
