/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 PayPal                                                                                     |
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

import com.paypal.selion.grid.RunnableLauncher;

/**
 * This interface represents the common functionality possessed by a hub or node.
 */
interface LocalServerComponent {
    /**
     * Brings up the node/hub based on the platform.
     * 
     * @param testSession
     *            an {@link AbstractTestSession} that represents the test session.
     */
    void boot(AbstractTestSession testSession);

    /**
     * Shuts down the node/hub running locally.
     */
    void shutdown();

    /**
     * @return the port used for the {@link LocalServerComponent}
     */
    int getPort();

    /**
     * @return the host used for the {@link LocalServerComponent}
     */
    String getHost();

    /**
     * @return the {@link RunnableLauncher} used for the {@link LocalServerComponent}o
     */
    RunnableLauncher getLauncher();
}
