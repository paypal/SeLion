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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openqa.grid.common.exception.GridException;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.LocalSelendroidNode;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class contains methods to start and shutdown local grid.
 */
final class LocalGridManager {

    private LocalGridManager() {
        // Utility class. So hide the constructor
    }

    private static SimpleLogger logger = SeLionLogger.getLogger();
    private static List<LocalServerComponent> toBoot = new ArrayList<>();

    private static void initializeServerList() {
        if (!toBoot.isEmpty()) {
            return;
        }

        toBoot.add(new LocalGrid());
        toBoot.add(new LocalNode());
        toBoot.add(new LocalIOSNode());
        toBoot.add(new LocalSelendroidNode());
    }

    private static void resetServerList() {
        toBoot.clear();
    }

    private static boolean isRunLocally() {
        return (Config.getBoolConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY));
    }

    /**
     * This method is responsible for spawning a local hub for supporting local executions
     * 
     * @param platform
     *            - A {@link WebDriverPlatform} that represents the platform [ This is internally used to decide if an
     *            iOS node or an android node is to be additionally spawned and hooked to the Grid.]
     * 
     */
    public static synchronized void spawnLocalHub(WebDriverPlatform platform) {
        logger.entering(platform);
        if (!isRunLocally()) {
            logger.exiting();
            return;
        }
        initializeServerList();
        for (LocalServerComponent eachItem : toBoot) {
            try {
                eachItem.startUp(platform);
            } catch (GridException e) {
                // If either the Grid or the Node or the IOS-Node for that matter failed to start at the first attempt
                // then there is NO point in trying to keep restarting it for every iteration. So lets log a severe
                // message
                // and exit the JVM.
                logger.log(Level.SEVERE, e.getMessage(), e);
                System.exit(1);
            }
        }
        logger.exiting();
    }

    /**
     * This method helps shut down the already spawned hub for local runs
     */
    final static synchronized void shutDownHub() {
        logger.entering();
        if (!isRunLocally()) {
            logger.exiting();
            return;
        }

        for (LocalServerComponent eachItem : toBoot) {
            eachItem.shutdown();
        }
        resetServerList();
        logger.exiting();
    }

}
