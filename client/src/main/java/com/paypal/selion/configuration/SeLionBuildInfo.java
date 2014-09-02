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

package com.paypal.selion.configuration;

import java.io.IOException;
import java.util.Properties;

/**
 * A simple object which allows users to extract build time information related to SeLion. This information is
 * auto-populated as part of a SeLion build.
 * 
 */
public final class SeLionBuildInfo {

    private static final String BUILD_INFO_FILE = "/selionbuildinfo.properties";
    private static SeLionBuildInfoProperties buildProperties = null;

    private SeLionBuildInfo() {

    }

    private static SeLionBuildInfoProperties getInfo() {
        if (buildProperties != null) {
            return buildProperties;
        }
        initInfo();
        return buildProperties;
    }

    private synchronized static void initInfo() {
        try {
            buildProperties = new SeLionBuildInfoProperties();
            buildProperties.load(SeLionBuildInfo.class.getResourceAsStream(BUILD_INFO_FILE));
        } catch (IOException e) {
            throw new ConfigException("Unable to load build time properties. Root cause: ", e);
        }
    }

    /**
     * Returns values for build time info
     * 
     * @param property
     *            - The {@link SeLionBuildProperty} of interest
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
    public static enum SeLionBuildProperty {
        /**
         * The version of SeLion
         */
        CORE_VERSION("core.build.version"),
        /**
         * The user name of the person that initiated the SeLion build
         */
        CORE_USER_NAME("core.build.user.name");

        private SeLionBuildProperty(String value) {
            this.propertyValue = value;
            this.fallBackValue = "Undefined " + value;
        }

        private String propertyValue;
        private String fallBackValue = null;

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
