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

package com.paypal.selion.reports.services;

import com.paypal.selion.reports.runtime.SeLionReporter;

/**
 * This interface facilitates custom actions to be taken place whenever a person invokes
 * {@link SeLionReporter#log(String, boolean, boolean)} method. An instance of this interface needs to be hooked into
 * {@link SeLionReporter} via a TestNG listener using the method {@link SeLionReporter#addLogAction(LogAction)}.
 * 
 */
public interface LogAction {

    /**
     * Implement this method to define the custom action that needs to be done as apart of executing
     * {@link SeLionReporter#log(String, boolean, boolean)}
     */
    void perform();

}
