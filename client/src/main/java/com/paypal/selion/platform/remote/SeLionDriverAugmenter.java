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
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;

import io.selendroid.SelendroidDriver;
import io.selendroid.SelendroidCapabilities;

import org.openqa.selenium.Beta;

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
     * @param capability
     *            {@link Capabilities} to be used by the {@link SelendroidDriver}, should be of type
     *            {@link SelendroidCapabilities}
     * @return a transformed {@link SelendroidDriver} to be used in the automation
     * @throws Exception 
     */
    public static SelendroidDriver selendroidDriver(RemoteWebDriver driver, Capabilities capability) throws Exception {
        if (!(driver.getCommandExecutor() instanceof HttpCommandExecutor)) {
            throw new WebDriverException("selendroid only supports http communication.");
        }
        if (!(driver instanceof RemoteWebDriver)) {
            throw new WebDriverException(
                    "SelendoidDriver needs a RemoteWebDriver with http command executor and SelendroidCapabilities to function.");
        }
        return new SeLionRemoteSelendroidDriver((RemoteWebDriver) driver, capability);
    }

}
