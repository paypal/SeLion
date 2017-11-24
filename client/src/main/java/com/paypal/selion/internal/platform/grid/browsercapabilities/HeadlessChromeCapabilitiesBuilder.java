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

import com.google.common.collect.ImmutableList;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.collections.Lists;

import java.util.List;
import java.util.Map;

/**
 * An optional {@link DefaultCapabilitiesBuilder} which can be used to force a single browser=chrome {@link WebTest}
 * to be run in headless mode.
 */
public final class HeadlessChromeCapabilitiesBuilder extends ChromeCapabilitiesBuilder {
    @Override
    @SuppressWarnings("unchecked")
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        super.getCapabilities(capabilities);
        Map<String, Object> existing =
            (Map<String, Object>) capabilities.getCapability(ChromeOptions.CAPABILITY);

        // existing.get("args") is an immutable list.
        // so we need to declare a new one and copy the values in, when present
        List<String> args = Lists.newArrayList();
        if (existing != null && existing.containsKey("args")) {
            args.addAll((List<String>) existing.get("args"));
        }

        args.remove("--headless");
        args.add("--headless");
        args.add("--disable-gpu");

        // put the updated args back as immutable.
        existing.put("args", ImmutableList.copyOf(args));

        return capabilities;
    }
}
