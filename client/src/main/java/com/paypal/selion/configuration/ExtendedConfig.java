/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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

import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;

/**
 * Commonly referred to as SeLion configurations.
 * @deprecated with SeLion 1.2.0. {@link MobileTest} and {@link WebTest} have native support for wiring custom
 * {@link DesiredCapabilities}
 */
@Deprecated
public enum ExtendedConfig {
    /**
     * This can be set from within the configuration method of a given test-script. Here's an example of how to use
     * this.
     * 
     * <pre>
     * {@literal @}BeforeMethod
     * public void setup(ITestResult result, Method method){
     * DesiredCapabilities dc = new DesiredCapabilities();
     * //customize the capabilities
     * result.setAttribute(ExtendedConfig.CAPABILITIES.getConfig(), dc);
     * </pre>
     */
    CAPABILITIES("capabilities"),

    TEST_NAME("name");

    private String configName;

    ExtendedConfig(String configName) {
        this.configName = configName;
    }

    public String getConfig() {
        return this.configName;
    }
}
