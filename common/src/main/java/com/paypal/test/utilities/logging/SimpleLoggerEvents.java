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

package com.paypal.test.utilities.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Hooks into {@link SimpleLogger} which can be implemented in your logger class
 */
public interface SimpleLoggerEvents {
    /**
     * Called before all log {@link Handler}s are initialized in {@link SimpleLogger}
     * 
     * @param logger
     *            - the {@link Logger} at the point of triggering this hook
     */
    void onPreInitialization(SimpleLogger logger);

    /**
     * Called after all log {@link Handler}s are initialized in {@link SimpleLogger}
     * 
     * @param logger
     *            - the {@link Logger} at the point of triggering this hook
     */
    void onPostInitialization(SimpleLogger logger);

    /**
     * Called when events are logged by {@link SimpleLogger}. This method exists as a means of convenience.
     * 
     * @param record
     *            - the {@link LogRecord} associated with the event
     */
    void onLog(LogRecord record);
}
