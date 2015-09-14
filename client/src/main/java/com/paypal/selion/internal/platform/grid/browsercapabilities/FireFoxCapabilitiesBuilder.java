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

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

/**
 * This class represents the capabilities that are specific to firefox.
 * 
 */
class FireFoxCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        capabilities.setBrowserName(DesiredCapabilities.firefox().getBrowserName());
        capabilities.setCapability(FirefoxDriver.PROFILE, prepareFireFoxProfile());
        if (ProxyHelper.isProxyServerRequired()) {
            capabilities.setCapability(CapabilityType.PROXY, ProxyHelper.createProxyObject());
        }

        return capabilities;
    }

    private FirefoxProfile getProfile(String dirName) {
        logger.entering(dirName);
        FirefoxProfile profile = null;
        File profileDir = new File(dirName);
        if (profileDir.exists()) {
            // If the user provided us with a dir name that represents a FF profile return a profile using that
            logger.finer(String.format("Working with the firefox profile directory [%s]", profileDir.getAbsolutePath()));
            profile = new FirefoxProfile(profileDir);
        }
        logger.exiting(profile);
        return profile;
    }

    private FirefoxProfile getProfile() {
        logger.entering();
        String profileName = getLocalConfigProperty(ConfigProperty.SELENIUM_FIREFOX_PROFILE);
        // Create a new anonymous profile if the user didn't provide any profiles to be used
        if (StringUtils.isBlank(profileName)) {
            logger.finer("Working with an anonymous firefox profile");
            FirefoxProfile profile = new FirefoxProfile();
            logger.exiting(profile);
            return profile;
        }
        FirefoxProfile profile = new ProfilesIni().getProfile(profileName);
        if (profile != null) {
            // If the user gave a valid profile name, create a profile using the name and return it back
            logger.finer(String.format("Working with the firefox profile [%s]", profileName));
            logger.exiting(profile);
            return profile;
        }
        profile = getProfile(profileName);
        if (profile == null) {
            // User provided us with an invalid value. Inform the user and create an anonymous profile.
            String infoMsg = String.format(
                    "[%s] is NOT a valid profile name/directory. Proceeding with using an anonymous profile.",
                    profileName);
            logger.finer(infoMsg);
            profile = new FirefoxProfile();
        }
        logger.exiting(profile);
        return profile;
    }

    private FirefoxProfile prepareFireFoxProfile() {
        logger.entering();
        FirefoxProfile profile = getProfile();

        String userAgent = getUserAgent();
        if (StringUtils.isNotBlank(userAgent)) {
            profile.setPreference("general.useragent.override", userAgent);
        }
        // To understand why the below preferences are being set please
        // see http://code.google.com/p/selenium/issues/detail?id=2863
        profile.setPreference("capability.policy.default.HTMLDocument.readyState", "allAccess");
        profile.setPreference("capability.policy.default.HTMLDocument.compatMode", "allAccess");
        profile.setPreference("capability.policy.default.Document.compatMode", "allAccess");
        profile.setPreference("capability.policy.default.Location.href", "allAccess");
        profile.setPreference("capability.policy.default.Window.pageXOffset", "allAccess");
        profile.setPreference("capability.policy.default.Window.pageYOffset", "allAccess");
        profile.setPreference("capability.policy.default.Window.frameElement", "allAccess");
        profile.setPreference("capability.policy.default.Window.frameElement.get", "allAccess");
        profile.setPreference("capability.policy.default.Window.QueryInterface", "allAccess");
        profile.setPreference("capability.policy.default.Window.mozInnerScreenY", "allAccess");
        profile.setPreference("capability.policy.default.Window.mozInnerScreenX", "allAccess");
        logger.exiting(profile);
        return profile;
    }
}
