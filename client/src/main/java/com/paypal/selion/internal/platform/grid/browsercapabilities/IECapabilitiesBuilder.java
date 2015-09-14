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

package com.paypal.selion.internal.platform.grid.browsercapabilities;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

/**
 * This class represents the capabilities that are specific to IE.
 */
class IECapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        if (isLocalRun() && StringUtils.isNotBlank(getBinaryPath())) {
            System.setProperty(SeLionConstants.WEBDRIVER_IE_DRIVER_PROPERTY, getBinaryPath());
        }
        capabilities.setBrowserName(DesiredCapabilities.internetExplorer().getBrowserName());
        if (ProxyHelper.isProxyServerRequired()) {
            capabilities.setCapability(InternetExplorerDriver.IE_USE_PRE_PROCESS_PROXY, true);
            capabilities.setCapability(CapabilityType.PROXY, ProxyHelper.createProxyObject());
        }
        return capabilities;
    }

    /*
     * Returns the location of iedriverserver or "" if it can not be determined.
     */
    private String getBinaryPath() {
        String location = System.getProperty(SeLionConstants.WEBDRIVER_IE_DRIVER_PROPERTY,
                Config.getConfigProperty(ConfigProperty.SELENIUM_IEDRIVER_PATH));
        return location;
    }
}
