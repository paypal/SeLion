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

package com.paypal.selion.platform.grid.browsercapabilities;

import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class will create instance of {@link DesiredCapabilities} which is pre-filled with all the common properties
 * that are considered de-facto for all browsers.
 * 
 */
public abstract class DefaultCapabilitiesBuilder {

    protected static final SimpleLogger logger = SeLionLogger.getLogger();

    public DesiredCapabilities createCapabilities() {
        return  getCapabilities(getDefaultCapabilities());
    }

    public abstract DesiredCapabilities getCapabilities(DesiredCapabilities capabilities);

    public DesiredCapabilities getDefaultCapabilities() {
        logger.entering();
        DesiredCapabilities capability = new DesiredCapabilities();

        // always pass the @Test name as "name", because it is useful meta info
        capability.setCapability("name", Grid.getTestSession().getTestName());

        capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        // refer here : http://code.google.com/p/selenium/wiki/DesiredCapabilities#Read-write_capabilities
        // for understanding the relevance of this capability.
        capability.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, "ignore");

        capability.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
        // if user has explicitly asked for javascript to be turned off, then switch it off
        if (!Boolean.parseBoolean(getLocalConfigProperty(ConfigProperty.BROWSER_CAPABILITY_SUPPORT_JAVASCRIPT))) {
            capability.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, false);
        }

        String browserVersion = getLocalConfigProperty(ConfigProperty.BROWSER_CAPABILITY_VERSION);

        if (getLocalConfigProperty(ConfigProperty.BROWSER_CAPABILITY_VERSION) != null) {
            capability.setVersion(browserVersion);
        }

        String platform = getLocalConfigProperty(ConfigProperty.BROWSER_CAPABILITY_PLATFORM);
        if (!platform.equalsIgnoreCase("ANY")) {
            capability.setCapability(CapabilityType.PLATFORM, platform);
        }

        logger.exiting(capability);
        return capability;
    }

    /**
     * @return <code>true</code> if the user is running locally.
     */
    public boolean isLocalRun() {
        return Config.getBoolConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY);
    }

    /**
     * @return A String that represents the user agent that was set for the current &lt;test&gt;
     */
    public String getUserAgent() {
        return getLocalConfigProperty(ConfigProperty.SELENIUM_USERAGENT);
    }

    /**
     * @param configProperty
     *            The {@link ConfigProperty} that is to be queried from the local &lt;test&gt;
     * @return A string that represents the configuration property.
     */
    public String getLocalConfigProperty(ConfigProperty configProperty) {
        String testName = Grid.getTestSession().getXmlTestName();
        return ConfigManager.getConfig(testName).getConfigProperty(configProperty);
    }
}
