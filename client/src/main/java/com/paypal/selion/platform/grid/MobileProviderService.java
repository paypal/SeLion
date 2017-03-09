/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

package com.paypal.selion.platform.grid;

import com.paypal.selion.internal.platform.grid.MobileNodeType;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import com.paypal.test.utilities.logging.SimpleLogger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;

/**
 * This singleton class is responsible for access to all service loaded mobileProvider implementations.
 */
public class MobileProviderService {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    private static final MobileProviderService INSTANCE = new MobileProviderService();

    private final Map<MobileNodeType, MobileDriverProvider> mobileProviders = new HashMap<>();

    private MobileProviderService() {
        ServiceLoader<MobileDriverProvider> providerLoader = ServiceLoader.load(MobileDriverProvider.class);
        for (MobileNodeType nodeType : MobileNodeType.values()) {
            for (MobileDriverProvider provider : providerLoader) {
                if (provider.supports(nodeType)) {
                    logger.log(Level.FINE, "Loading mobile driver provider that supports " + nodeType);
                    mobileProviders.put(nodeType, provider);
                }
            }
        }
    }

    public static MobileProviderService getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new RemoteWebDriver instance from the first MobileProvider that supports the specified
     * nodeType.
     *
     * @param nodeType - The {@link MobileNodeType} to use for creating this mobile remote driver.
     * @param platform - The {@link WebDriverPlatform}.
     * @param command  - The commandExecutor.
     * @param url      - The URL to use for the remote grid.
     * @param caps     - The desired capabilities for this new web driver instance.
     * @return A new RemoteWebDriver instance.
     */
    public RemoteWebDriver createDriver(MobileNodeType nodeType, WebDriverPlatform platform, CommandExecutor command,
                                        URL url, Capabilities caps) {

        if (mobileProviders.containsKey(nodeType)) {
            logger.log(Level.FINE, "Found mobile driver provider that supports " + nodeType);
            return mobileProviders.get(nodeType).createDriver(platform, command, url, caps);
        }
        logger.severe("Did not found a mobile driver provider that supports " + nodeType);
        return null;
    }

    public DefaultCapabilitiesBuilder capabilityBuilder(MobileNodeType nodeType) {
        if (mobileProviders.containsKey(nodeType)) {
            logger.log(Level.FINE, "Found mobile driver provider that supports " + nodeType);
            return mobileProviders.get(nodeType).capabilityBuilder();
        }
        logger.severe("Did not find a mobile capabilities builder that supports " + nodeType);
        return null;
    }

}
