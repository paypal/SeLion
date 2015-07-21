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

import java.util.List;

import com.paypal.selion.pojos.ProcessInfo;

/**
 * This interface represents the capabilities that any custom process handler would need to possess if it is to be used
 * for the auto healing logic of SeLion.
 *
 */
public interface ProcessHandler {
    /**
     * @return A List of {@link ProcessInfo} that represents the bunch of processes that need to be forcibly killed
     *         before a given node can be recycled.
     * @throws ProcessHandlerException
     */
    List<ProcessInfo> potentialProcessToBeKilled() throws ProcessHandlerException;

    /**
     * @param processes
     *            A List of {@link ProcessInfo} that are to be forcibly killed.
     * @throws ProcessHandlerException
     */
    void killProcess(List<ProcessInfo> processes) throws ProcessHandlerException;
}
