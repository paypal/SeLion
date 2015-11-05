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

package com.paypal.selion;

import java.io.IOException;
import java.util.Properties;

/**
 * <code>SeLionBuildInfo</code> allows users to extract build time information related to SeLion. This information is
 * auto-populated as part of a SeLion build.
 */
public final class SeLionBuildInfo {

    private static final String BUILD_INFO_FILE = "/selionbuildinfo.properties";

    private static SeLionBuildInfoProperties buildProperties;

    private SeLionBuildInfo() {

    }

    private static SeLionBuildInfoProperties getInfo() {
        if (buildProperties == null) {
            initInfo();
        }
        return buildProperties;
    }

    private static synchronized void initInfo() {
        try {
            buildProperties = new SeLionBuildInfoProperties();
            buildProperties.load(SeLionBuildInfo.class.getResourceAsStream(BUILD_INFO_FILE));
        } catch (IOException e) {
            throw new BuildInfoException("Unable to load build time properties. Root cause: ", e);
        }
    }

    /**
     * Returns values for build time info
     * 
     * @param property
     *           The {@link SeLionBuildProperty} of interest
     * @return The build time value.</br></br> The fall back value which can be obtained via
     *         {@link SeLionBuildProperty#getFallBackValue()} if the build time property is not defined.
     */
    public static final String getBuildValue(SeLionBuildProperty property) {
        return getInfo().getProperty(property.getPropertyValue(), property.getFallBackValue());
    }

    private static class SeLionBuildInfoProperties extends Properties {

        private static final long serialVersionUID = -4808947170980686563L;

        public SeLionBuildInfoProperties() {
            super();
        }

        public String getProperty(String name, String fallBackValue) {
            String returnValue = super.getProperty(name, fallBackValue);
            if (returnValue.contains("${")) {
                return fallBackValue;
            }
            return returnValue;
        }

    }

    /**
     * SeLion build time properties
     */
    public enum SeLionBuildProperty {

        /**
         * The version of SeLion
         */
        SELION_VERSION("selion.build.version"),

        /**
         * The build time
         */
        BUILD_TIME("selion.build.time"),

        /**
         * The user name of the person that initiated the SeLion build
         */
        USER_NAME("selion.build.user.name"),

        /**
         * The version of Java used for the SeLion build
         */
        JAVA_VERSION("selion.build.java.version"),

        /**
         * The Java vendor used for the SeLion build
         */
        JAVA_VENDOR("selion.build.java.vendor"),

        /**
         * The Java compiler compatibility version used for the SeLion build
         */
        JAVA_COMPILE_VERSION("selion.build.java.compile.version"),

        /**
         * The OS architecture used to build SeLion
         */
        OS_ARCH("selion.build.os.arch"),

        /**
         * The OS name used to build SeLion
         */
        OS_NAME("selion.build.os.name"),

        /**
         * The OS version used to build SeLion
         */
        OS_VERSION("selion.build.os.version"),

        /**
         * Whether tests were executed as part of the SeLion build
         */
        SKIP_TESTS("selion.build.skip.tests"),

        /**
         * The TestNG dependency version at the time of compilation
         */
        BUILD_DEPENDENCY_TESTNG("selion.build.dependency.testng.version"),

        /**
         * The Selenium dependency version at the time of compilation
         */
        BUILD_DEPENDENCY_SELENIUM_VERSION("selion.build.dependency.selenium.version"),

        /**
         * The ios-driver dependency version at the time of compilation
         */
        BUILD_DEPENDENCY_IOSDRIVER("selion.build.dependency.iosdriver.version"),

        /**
         * The Selendroid dependency version at the time of compilation
         */
        BUILD_DEPENDENCY_SELENDROID("selion.build.dependency.selendroid.version"),

        /**
         * The Appium dependency version at the time of compilation
         */
        BUILD_DEPENDENCY_APPIUM("selion.build.dependency.appium.version");

        SeLionBuildProperty(String value) {
            this.propertyValue = value;
            this.fallBackValue = "Undefined " + value;
        }

        private String propertyValue;
        private String fallBackValue;

        /**
         * Returns the build property value
         * 
         * @return the property value
         */
        public String getPropertyValue() {
            return this.propertyValue;
        }

        /**
         * Returns the fall back value for this build property
         * 
         * @return The fall back value
         */
        public String getFallBackValue() {
            return this.fallBackValue;
        }

        public String toString() {
            return this.propertyValue;
        }

    }
}
