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

import com.paypal.selion.internal.platform.grid.BrowserFlavors;
import com.paypal.selion.internal.platform.grid.WebTestSession;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

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
            ChromeOptions options = new ChromeOptions();
            options.setBinary(browserPath);
            // To run chrome on virtualized openVZ environments
            options.addArguments("--no-sandbox");
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        }
        return capabilities;
    }
}
