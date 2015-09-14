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

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A utility class which is internally used by SeLion to load capabilities via {@link ServiceLoader} and via
 * the {@literal @}WebTest annotation.
 * 
 */
final class CapabilitiesHelper {
    private static SimpleLogger logger = SeLionLogger.getLogger();

    private CapabilitiesHelper() {
        // Utility class. So hide the constructor
    }

    /**
     * @return - A list of {@link DesiredCapabilities} found via {@link ServiceLoader}.
     */
    public static List<DesiredCapabilities> retrieveCustomCapsViaServiceLoaders() {
        logger.entering();
        ServiceLoader<DefaultCapabilitiesBuilder> allCapsBuilderInstances = ServiceLoader
                .load(DefaultCapabilitiesBuilder.class);
        List<DesiredCapabilities> allCaps = new ArrayList<DesiredCapabilities>();
        for (DefaultCapabilitiesBuilder eachCapsBuilderInstance : allCapsBuilderInstances) {
            allCaps.add(eachCapsBuilderInstance.createCapabilities());
        }
        logger.exiting(allCaps);
        return allCaps;
    }

    /**
     * @return - A list of {@link DesiredCapabilities} found via the
     *         {@link ConfigProperty#SELENIUM_CUSTOM_CAPABILITIES_PROVIDER}
     */
    public static List<DesiredCapabilities> retrieveCustomCapsObjects() {
        logger.entering();
        List<DesiredCapabilities> capsObjects = new ArrayList<DesiredCapabilities>();
        String customCapsProv = Config.getConfigProperty(ConfigProperty.SELENIUM_CUSTOM_CAPABILITIES_PROVIDER);
        if (StringUtils.isBlank(customCapsProv)) {
            return capsObjects;
        }
        for (String eachProvider : customCapsProv.split(",")) {
            try {
                if (DefaultCapabilitiesBuilder.class.isAssignableFrom(Class.forName(eachProvider))) {
                    capsObjects.add(((DefaultCapabilitiesBuilder) Class.forName(eachProvider).newInstance())
                            .createCapabilities());
                } else {
                    logger.info("Skipping " + eachProvider + " because it is not a subclass of "
                            + DefaultCapabilitiesBuilder.class.getCanonicalName());
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                // Throw an Un-checked exception and let the user know that the custom capabilities that they provided
                // us with has problems.
                // Doing this will prevent their tests from running under the assumption that the capabilities they
                // provided was fine.
                throw new IllegalStateException(e);
            }
        }
        logger.exiting(capsObjects);
        return capsObjects;
    }

}
