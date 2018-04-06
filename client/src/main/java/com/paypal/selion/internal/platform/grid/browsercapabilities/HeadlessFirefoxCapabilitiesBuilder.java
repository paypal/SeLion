/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2017 PayPal                                                                                          |
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

package com.paypal.selion.internal.platform.grid.browsercapabilities;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * An optional {@link DefaultCapabilitiesBuilder} which can be used to force a single browser=firefox {@link WebTest}
 * to be run in headless mode.
 */
public final class HeadlessFirefoxCapabilitiesBuilder extends FireFoxCapabilitiesBuilder {
    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        super.getCapabilities(capabilities);
        FirefoxOptions updated = new FirefoxOptions(capabilities);
        updated.setHeadless(true);
        capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, updated);
        return capabilities;
    }
}
