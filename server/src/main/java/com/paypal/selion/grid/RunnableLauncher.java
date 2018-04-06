/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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
 * Interface which extends {@link Runnable} and adds some additional methods
 */
public interface RunnableLauncher extends Runnable {
    /**
     * Shutdown the launcher
     */
    void shutdown();
    /**
     * @return Whether the launcher has completed its initialization phase which includes downloading dependencies.
     */ 
    boolean isInitialized();
    
    /**
     * @return Whether the {@link InstanceType} for this launcher is up and running.
     */
    boolean isRunning();

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
        String getFriendlyName() {
            return this.value;
        }
    }
}
