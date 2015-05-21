/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

package com.paypal.selion.grid;

/**
 * Interface which extends {@link Runnable} and adds a shutdown method
 */
interface RunnableLauncher extends Runnable {
    abstract void shutdown();

    /**
     * Maps to a type of WebDriver instance.
     */
    enum InstanceType {
        SELENIUM_HUB("hub"),
        SELENIUM_NODE("node"),
        SELENIUM_STANDALONE("standalone"),
        SELION_SAUCE_HUB("sauce"),
        IOS_DRIVER("ios-driver"),
        SELENDROID("selendroid"),
        APPIUM("appium");

        private String value;

        InstanceType(String value) {
            this.value = value;
        }

        /**
         * @return the friendly {@link String} representation of this {@link InstanceType}
         */
        String getFriendlyType() {
            return this.value;
        }
    }
}
