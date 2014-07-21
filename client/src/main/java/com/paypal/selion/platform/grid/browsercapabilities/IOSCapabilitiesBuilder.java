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

import org.openqa.selenium.remote.DesiredCapabilities;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;

/**
 * @deprecated dead code. todo - refactor GenericCapabilitiesBuilder so this is used
 */
class IOSCapabilitiesBuilder extends DefaultCapabilitiesBuilder {
    private DeviceType deviceType;

    public IOSCapabilitiesBuilder(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        if (deviceType == DeviceType.ipad) {
            return IOSCapabilities.ipad("Safari");
        } else if (deviceType == DeviceType.iphone) {
            return IOSCapabilities.iphone("Safari");
        } else {
            throw new IllegalArgumentException("Safari is supported either on an iPhone or an iPad.");
        }
    }
}
