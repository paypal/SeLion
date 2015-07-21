/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.platform.grid.browsercapabilities;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * This class represents the capabilities that are specific to Chrome browser.
 * 
 */
class ChromeCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {

        if (isLocalRun()) {
            String binaryPath = getBinaryPath();
            if (StringUtils.isNotBlank(binaryPath)) {
                //Set the property ONLY if the user provided us a value. Else things will still work because the user
                //perhaps has already set this in his/her PATH variable.
                System.setProperty("webdriver.chrome.driver", binaryPath);

            }
        }
        capabilities.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
        String userAgent = getUserAgent();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--test-type");
        options.addArguments("--ignore-certificate-errors");
        if ((userAgent != null) && (!userAgent.trim().isEmpty())) {
            options.addArguments("--user-agent=" + userAgent);
        }
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        if (ProxyHelper.isProxyServerRequired()) {
            capabilities.setCapability(CapabilityType.PROXY, ProxyHelper.createProxyObject());
        }
        return capabilities;
    }

    private String getBinaryPath() {
        return Config.getConfigProperty(ConfigProperty.SELENIUM_CHROMEDRIVER_PATH);
    }
}
