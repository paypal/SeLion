/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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

import java.util.Map;

import org.openqa.grid.internal.utils.CapabilityMatcher;

import com.paypal.selion.proxy.SeLionSauceProxy;

/**
 * Used in conjunction with a grid that has a {@link SeLionSauceProxy} connected. <br>
 * <br>
 * A simple {@link CapabilityMatcher} which delegates matches against node capabilities to the Sauce Labs cloud when
 * {@link SeLionSauceProxy} forwards the session.
 * 
 */
public class SeLionSauceCapabilityMatcher implements CapabilityMatcher {
    public boolean matches(Map<String, Object> nodeCapability, Map<String, Object> requestedCapabilities) {
        // nothing to check.. delegate everything to sauce labs cloud
        return true;
    }
}
