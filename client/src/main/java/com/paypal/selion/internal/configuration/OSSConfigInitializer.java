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

package com.paypal.selion.internal.configuration;

import org.testng.ITestContext;

import com.paypal.selion.configuration.AbstractConfigInitializer;
import com.paypal.selion.configuration.Config;

/**
 * A Simple initializer to prioritize initialization related to the {@link Config} initializations.
 * {@link OSSConfigInitializer} has a default priority of "0" set to it.
 * 
 */
public class OSSConfigInitializer extends AbstractConfigInitializer {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void initialize(ITestContext context) {
        Config.initConfig(context);
    }

}
