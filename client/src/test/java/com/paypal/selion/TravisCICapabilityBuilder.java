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

package com.paypal.selion;

import com.google.common.collect.ImmutableList;
import com.paypal.selion.internal.platform.grid.BrowserFlavors;
import com.paypal.selion.internal.platform.grid.WebTestSession;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.collections.Lists;

import java.util.List;
import java.util.Map;

/**
 * A custom capabilitiesBuilder used during test executions to establish custom browser paths and options.
 */
public class TravisCICapabilityBuilder extends DefaultCapabilitiesBuilder {

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        String browserPath = System.getProperty("BROWSER_PATH");
        WebTestSession session = Grid.getWebTestSession();
        if (StringUtils.isEmpty(browserPath) || session == null) {
            return capabilities;
        }

        String browser = session.getBrowser();
        if (browser.equals(BrowserFlavors.CHROME.getBrowser())) {
            Map<String, Object> existing =
                (Map<String, Object>) capabilities.getCapability(ChromeOptions.CAPABILITY);

            // update the binary path
            existing.put("binary", browserPath);

            // update the args

            // existing.get("args") is an immutable list.
            // so we need to declare a new one and copy the values in, when present
            List<String> args = Lists.newArrayList();
            if (existing.containsKey("args")) {
                args.addAll((List<String>) existing.get("args"));
            }

            // To run chrome on virtualized openVZ environments
            args.add("--no-sandbox");

            // put the updated args back as immutable.
            existing.put("args", ImmutableList.copyOf(args));
        }
        return capabilities;
    }
}
