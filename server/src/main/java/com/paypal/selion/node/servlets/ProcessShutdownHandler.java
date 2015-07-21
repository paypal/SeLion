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

package com.paypal.selion.node.servlets;

import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.ProcessInfo;
import com.paypal.selion.utils.process.ProcessHandler;
import com.paypal.selion.utils.process.ProcessHandlerException;
import com.paypal.selion.utils.process.ProcessHandlerFactory;

import java.util.List;
import java.util.logging.Logger;

/**
 * A helper class for shutting down of processes started on SeLion Grid node.
 */
public class ProcessShutdownHandler {

    private static final Logger LOGGER = SeLionGridLogger.getLogger(ProcessShutdownHandler.class);

    /**
     * This method terminates all Node processes that we started.
     *
     */
    public void shutdownProcesses() throws ProcessHandlerException {
        LOGGER.info("Shutting down all our node processes.");

        ProcessHandler handler = ProcessHandlerFactory.createInstance();
        List<ProcessInfo> processes = handler.potentialProcessToBeKilled();
        handler.killProcess(processes);

        LOGGER.info("Successfully shutdown all processes");
    }

}
