/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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


import com.paypal.selion.internal.platform.grid.MobileNodeType;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

/**
 * A service provider interface that mobile driver providers for SeLion client (that use IOSDriver, Selendroid
 * or Appium etc.) must implement.
 * <p>
 * This is a service loaded extension for SeLion client mobile driver providers.
 */
public interface MobileDriverProvider {

    /**
     * @return <code>true</code> If mobile driver provider implementation supports the specified {@link MobileNodeType}.
     */
    boolean supports(MobileNodeType nodeType);

    /**
     * Creates an instance of a RemoteWebDriver using mobile driver implementation
     *
     * @return An instance of a {@link RemoteWebDriver} for the mobile driver implementation.
     */
    RemoteWebDriver createDriver(WebDriverPlatform platform, CommandExecutor commandExecutor, URL url,
                                 Capabilities capabilities);

    /**
     * Creates an instance of a CapabilitiesBuilder for mobile driver implementation
     *
     * @return An instance of a {@link DefaultCapabilitiesBuilder} for the mobile driver implementation.
     */
    DefaultCapabilitiesBuilder capabilityBuilder();

}
