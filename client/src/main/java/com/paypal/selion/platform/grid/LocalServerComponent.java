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

package com.paypal.selion.platform.grid;

/**
 * This interface represents the common functionality possessed by a hub or node.
 */
interface LocalServerComponent {
    /**
     * Brings up the node/hub based on the platform.
     * 
     * @param platform
     *            A {@link WebDriverPlatform} that represents the platform.
     */
    public void startUp(WebDriverPlatform platform);

    /**
     * Shuts down the node/hub running locally.
     */
    public void shutdown();
}
