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

import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.Map;

public class HeadlessFirefoxCapabilitiesBuilder extends FireFoxCapabilitiesBuilder {
    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        capabilities = super.getCapabilities(capabilities);
        Map<String, List<String>> existing =
            (Map<String, List<String>>) capabilities.asMap().get(FirefoxOptions.FIREFOX_OPTIONS);

        FirefoxOptions updated = new FirefoxOptions();
        if (existing != null) {
            updated.addArguments(existing.get("args"));
        }
        updated.setHeadless(true);
        capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, updated);
        return capabilities;
    }
}
