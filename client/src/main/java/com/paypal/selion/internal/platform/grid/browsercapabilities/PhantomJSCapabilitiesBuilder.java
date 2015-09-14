/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

class PhantomJSCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        if (isLocalRun() && StringUtils.isNotBlank(getBinaryPath())) {
            System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, getBinaryPath());
        }
        capabilities.setBrowserName(DesiredCapabilities.phantomjs().getBrowserName());
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] {
                "--ignore-ssl-errors=true", "--webdriver-loglevel=none" });

        String userAgent = getUserAgent();
        if (StringUtils.isNotBlank(userAgent)) {
            capabilities.setCapability("phantomjs.page.settings.userAgent", userAgent);
        }
        return capabilities;
    }

    /*
     * Returns the location of phantomjs or "" if it can not be determined.
     */
    private String getBinaryPath() {
        String location = System.getProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                Config.getConfigProperty(ConfigProperty.SELENIUM_PHANTOMJS_PATH));
        return location;
    }
}
