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

package com.paypal.selion.utils.process;


/**
 * A simple enum that differentiates Operating systems for internal usage by
 * SeLion.
 * 
 */
public enum OSPlatform {
    /**
     * Represents Windows operating system [ all the sub flavors of windows such as XP, Vista, windows 7 etc., are
     * collectively represented by this value.
     */
    WINDOWS,
    /**
     * Represents Unix based operating systems such as OSX, Linux, or other.
     */
    UNIX;

}
