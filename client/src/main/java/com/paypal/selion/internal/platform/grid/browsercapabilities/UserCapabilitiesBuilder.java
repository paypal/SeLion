/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

/**
 * Applies user specified desired capabilities.
 */
public class UserCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {

        // Lets check if the user provided more capabilities via the Configuration parameter and add them
        for (DesiredCapabilities eachCaps : CapabilitiesHelper.retrieveCustomCapsObjects()) {
            capabilities.merge(eachCaps);
        }

        // Lets check if the user provided more capabilities via ServiceLoaders and add them
        for (DesiredCapabilities eachCaps : CapabilitiesHelper.retrieveCustomCapsViaServiceLoaders()) {
            capabilities.merge(eachCaps);
        }

        // Applies user provided capabilities which are specified either via the @WebTest or @MobileTest annotation or
        // via the attributes of ITestResult object (deprecated) for the current test.This info will be available in the
        // AbstractTestSession object for the current test.
        capabilities.merge(Grid.getTestSession().getAdditionalCapabilities());

        return capabilities;
    }

}
