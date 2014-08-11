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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.internal.utils.GridHubConfiguration;
import org.openqa.grid.web.Hub;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.internal.grid.capabilities.SeLionCapabilitiesMatcher;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local Hub.
 * 
 */
class LocalGrid implements LocalServerComponent {
    private boolean isRunning = false;
    private Hub localHub = null;
    private SimpleLogger logger = SeLionLogger.getLogger();

    public void shutdown() {
        if (localHub != null) {
            try {
                localHub.stop();
                logger.log(Level.INFO, "Local hub has been stopped");
            } catch (Exception e) {
                String errorMsg = "An error occured while attempting to shut down the local Hub. Root cause: ";
                logger.log(Level.SEVERE, errorMsg, e);
            }
        }

    }

    @Override
    public synchronized void startUp(WebDriverPlatform platform) {
        if (isRunning) {
            return;
        }
        GridHubConfiguration config = new GridHubConfiguration();
        config.loadDefault();
        config.setCapabilityMatcher(new SeLionCapabilitiesMatcher());
        config.setHost("localhost");
        config.setPort(Config.getIntConfigProperty(ConfigProperty.SELENIUM_PORT));
        // the below logic will make sure that hub or node or link node actions
        // will not be done several times when executing multiple test-cases
        // locally.
        try {
            // HACK :: Hub() blindly adds another ConsoleHandler to the RootLogger and changes the log Level!!!..
            // We'll want to undo all Hub()'s behavior...
            Handler[] handlers = Logger.getLogger("").getHandlers();
            Level level = Logger.getLogger("").getLevel();

            localHub = new Hub(config);

            // HACK :: put the RootLogger back into the original state
            // remove all handlers first
            for (Handler handler : Logger.getLogger("").getHandlers()) {
                Logger.getLogger("").removeHandler(handler);
            }
            // put the original ones back
            for (Handler handler : handlers) {
                Logger.getLogger("").addHandler(handler);
            }
            // reset the log level
            Logger.getLogger("").setLevel(level);
            localHub.start();
            isRunning = true;
            logger.log(Level.INFO, "Local Hub spawned");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new GridException("Failed to start a local Grid", e);
        }

    }

}
