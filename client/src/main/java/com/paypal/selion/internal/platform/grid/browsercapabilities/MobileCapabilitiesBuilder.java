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

package com.paypal.selion.internal.platform.grid.browsercapabilities;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.internal.platform.grid.MobileTestSession;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

class MobileCapabilitiesBuilder extends DefaultCapabilitiesBuilder {
    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {

        if (Grid.getMobileTestSession() == null) {
            return capabilities;
        }
        MobileTestSession mobileSession = Grid.getMobileTestSession();
        DefaultCapabilitiesBuilder capabilitiesBuilder = null;
        switch (mobileSession.getMobileNodeType()) {

        case IOS_DRIVER:
            capabilitiesBuilder = new IOSDriverCapabilitiesBuilder();
            break;
        case SELENDROID:
            capabilitiesBuilder = new SelendroidCapabilitiesBuilder();
            break;
        case APPIUM:
            capabilitiesBuilder = new AppiumCapabilitiesBuilder();
            break;
        default:
            break;
        }

        return capabilitiesBuilder.getCapabilities(capabilities); // NOSONAR
    }

}
