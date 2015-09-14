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

import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

/**
 * This class represents the capabilities that are specific to Safari.
 * 
 */
class SafariCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        capabilities.setBrowserName(DesiredCapabilities.safari().getBrowserName());
        if (ProxyHelper.isProxyServerRequired()) {
            String msg = String.format(ProxyHelper.WARNING_MSG, "Safari");
            SeLionLogger.getLogger().severe(msg);
            capabilities.setCapability(CapabilityType.PROXY, ProxyHelper.createProxyObject());
        }

        return capabilities;
    }
}
