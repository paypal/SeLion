/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

package com.paypal.selion.grid.matchers;

import io.selendroid.common.SelendroidCapabilities;
import io.selendroid.grid.SelendroidCapabilityMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openqa.grid.internal.utils.DefaultCapabilityMatcher;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * This capability matches for nodes of type 'selendroid', 'ios-driver', or 'appium' when the capability
 * 'mobileNodeType' is included in the {@link DesiredCapabilities}. Otherwise, this matcher will delegate to
 * {@link DefaultCapabilityMatcher}
 */
public class MobileCapabilityMatcher extends DefaultCapabilityMatcher {
    
    /**
     * Capability key to match against for mobile specific nodes.
     */
    private static final String MOBILE_NODE_TYPE = "mobileNodeType";
    private final List<String> toConsider = new ArrayList<String>();

    public MobileCapabilityMatcher() {
        super();

        // Appium specific considerations
        toConsider.add("platformName");
        toConsider.add("platformVersion");
        toConsider.add("deviceName");
        toConsider.add(MOBILE_NODE_TYPE);
    }

    @Override
    public boolean matches(Map<String, Object> nodeCapability, Map<String, Object> requestedCapability) {
        if (requestedCapability.containsKey(MOBILE_NODE_TYPE)) {
            String mobileNodeType = (String) requestedCapability.get(MOBILE_NODE_TYPE);

            switch (mobileNodeType) {
            case "selendroid": {
                // TODO Hack -- As of Selendroid 0.10.0, the AUT capabilities are not added, so we are removing it from
                // the requested capabilities before sending to the matcher.
                // See io.selendroid.server.grid.SelfRegisteringRemote#getNodeConfig() for more on this problem
                Map<String, Object> augmentedRequestedCapabilities = new HashMap<String, Object>(requestedCapability);
                augmentedRequestedCapabilities.remove(SelendroidCapabilities.AUT);
                return (new SelendroidCapabilityMatcher().matches(nodeCapability, augmentedRequestedCapabilities));
            }
            case "ios-driver": {
                return (new MinimalIOSCapabilityMatcher().matches(nodeCapability, requestedCapability));
            }
            case "appium": {
                return (verifyAppiumCapabilities(nodeCapability, requestedCapability))
                        && (matchAgainstMobileNodeType(nodeCapability, mobileNodeType));
            }
            }
        }
        // TODO what if the user does not care about which mobileNodeType they are routed to and instead they
        // simply want ANY node with android or ios support
        return super.matches(nodeCapability, requestedCapability);
    }

    /**
     * Matches requested mobileNodeType against node capabilities.
     */
    private boolean matchAgainstMobileNodeType(Map<String, Object> nodeCapability, String mobileNodeType) {
        String nodeValue = (String) nodeCapability.get(MOBILE_NODE_TYPE);
        if (StringUtils.isBlank(nodeValue)) {
            return false;
        }
        return nodeValue.equalsIgnoreCase(mobileNodeType);
    }

    /**
     * Requested capabilities compared with node capabilities when both capabilities are not blank. If requested
     * capability have "ANY" or "*" then matcher bypassed to next capability without comparison.
     * 
     * @param nodeCapability
     * @param requestedCapability
     * @return <code>true/false</code>
     */
    private boolean verifyAppiumCapabilities(Map<String, Object> nodeCapability, Map<String, Object> requestedCapability) {
        for (String capabilityName : toConsider) {
            String capabilityValue = (String) requestedCapability.get(capabilityName);

            if (StringUtils.isNotBlank(capabilityValue)
                    && !("ANY".equalsIgnoreCase(capabilityValue) || "*".equals(capabilityValue))) {
                String nodeValue = (String) nodeCapability.get(capabilityName);
                if (StringUtils.isNotBlank(nodeValue) && !nodeValue.equalsIgnoreCase(capabilityValue)) {
                    return false;
                }
            }
        }
        return true;
    }
}
