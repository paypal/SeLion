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

package com.paypal.selion.platform.remote;

import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.WebDriverException;

import io.selendroid.client.SelendroidDriver;
import io.selendroid.common.SelendroidCapabilities;

import org.openqa.selenium.Beta;

import com.paypal.selion.platform.grid.BrowserFlavors;
import com.paypal.selion.platform.grid.browsercapabilities.DesiredCapabilitiesFactory;

/**
 * This class transforms a normal {@link RemoteWebDriver} with Selendroid capabilities to a {@link SelendroidDriver}
 */
@Beta
public final class SeLionDriverAugmenter {
    private SeLionDriverAugmenter() {
        // Utility class. So hide the constructor
    }

    /**
     * This function will return the transformed {@link SelendroidDriver}
     * 
     * @param driver
     *            {@link RemoteWebDriver} with {@link SelendroidCapabilities} to be transformed
     * @return a transformed {@link SelendroidDriver} to be used in the automation
     */
    public static SelendroidDriver getSelendroidDriver(RemoteWebDriver driver) {
        if (!(driver.getCommandExecutor() instanceof HttpCommandExecutor)) {
            throw new WebDriverException("selendroid only supports http communication.");
        }
        if (!(driver instanceof RemoteWebDriver)) {
            throw new WebDriverException(
                    "SelendoidDriver needs a RemoteWebDriver with http command executor and SelendroidCapabilities to function.");
        }
        try {
            return new SeLionRemoteSelendroidDriver((RemoteWebDriver) driver, 
                    DesiredCapabilitiesFactory.getCapabilities(BrowserFlavors.GENERIC));
        } catch (Exception e) {
            throw new WebDriverException(e);
        }
    }

}
