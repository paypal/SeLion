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

package com.paypal.selion.internal.platform.grid;

/**
 * This enum represents the platform against which a test is to be run. For regular browser tests running on
 * MAC/WINDOWS/LINUX the platform value would be WEB.
 * 
 */
public enum WebDriverPlatform {
    /**
     * Represents the iOS Platform.
     */
    IOS,
    /**
     * Represents the Android Platform.
     */
    ANDROID,
    /**
     * Used to indicate that the current platform is not a Mobile based platform.
     */
    WEB,
    
    /**
     * Used to indicate that the current platform is not known to SeLion.
     */
    UNDEFINED;
}
