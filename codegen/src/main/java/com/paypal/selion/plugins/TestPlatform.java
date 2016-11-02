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

package com.paypal.selion.plugins;

/**
 * Represents the platform types code generation can be performed for. Also maps each platform type to a velocity
 * template.
 */
public enum TestPlatform {

    /**
     * Maps to the iOS velocity template
     */
    IOS("ios", "MobileTemplate.vm"),
    /**
     * Maps to the Web velocity template
     */
    WEB("web", "Class.vm"),
    /**
     * Maps to the Android velocity template
     */
    ANDROID("android", "MobileTemplate.vm"),
    /**
     * Maps to the common mobile velocity template
     */
    MOBILE("mobile", "CommonMobileTemplate.vm");

    private String platformName;
    private String defFileName;

    TestPlatform(String platformName, String defFileName) {
        this.platformName = platformName;
        this.defFileName = defFileName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getVelocityTemplateToUse() {
        return defFileName;
    }

    /**
     * Utility to map the platform from Page Yaml to {@link TestPlatform}
     * 
     * @param platformFromFile
     *            - The value for platform read from the Yaml file
     * @return - {@link TestPlatform} if a valid match is found otherwise null
     */
    public static TestPlatform identifyPlatform(String platformFromFile) {
        for (TestPlatform platform : TestPlatform.values()) {
            if (platform.getPlatformName().equals(platformFromFile)) {
                return platform;
            }
        }
        return null;
    }

}
