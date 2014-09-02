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
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.paypal.selion.platform.grid.BrowserFlavors;
import com.paypal.selion.platform.grid.browsercapabilities.DesiredCapabilitiesFactory;

public class DesiredCapabilitiesFactoryTest {
    volatile boolean init = false;

    @Test(dataProvider = "getBrowserFlavor", groups = "functional")
    public void testGetCapabilities(BrowserFlavors flavor) {

        DesiredCapabilities dc = DesiredCapabilitiesFactory.getCapabilities(flavor);
        String browserToTest = flavor.getBrowser().substring(1);
        // Only for IE we are not in sync with WebDriver because we refer to IE as iexplore but webdriver refers to it
        // as internet explorer
        if (flavor == BrowserFlavors.INTERNET_EXPLORER) {
            browserToTest = DesiredCapabilities.internetExplorer().getBrowserName();
        }
        // We would want to be skipping the iteration that involves the generic browser because its a Mobile based one
        // and not
        // testable for the WebTest annotation.
        if (flavor == BrowserFlavors.GENERIC) {
            return;
        }
        Assert.assertTrue(dc.getBrowserName().toLowerCase().contains(browserToTest));
    }

    @DataProvider
    public Object[][] getBrowserFlavor() {
        BrowserFlavors[] browserNames = BrowserFlavors.values();
        int length = browserNames.length;
        Object[][] browsers = new Object[length][1];
        for (int i = 0; i < length; i++) {
            browsers[i][0] = browserNames[i];
        }
        return browsers;
    }
}
