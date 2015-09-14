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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class contains methods to start and shutdown local grid.
 */
final class LocalGridManager {

    private LocalGridManager() {
        // Utility class. So hide the constructor
    }

    private static final SimpleLogger LOGGER = SeLionLogger.getLogger();
    private static List<LocalServerComponent> toBoot = new ArrayList<>();

    private static void setupToBootList() {
        if (!toBoot.isEmpty()) {
            return;
        }

        toBoot.add(new LocalHub());
        toBoot.add(new LocalNode());
        toBoot.add(new LocalIOSNode());
        toBoot.add(new LocalSelendroidNode());
    }

    private static void clearToBootList() {
        toBoot.clear();
    }

    private static boolean isRunLocally() {
        return (Config.getBoolConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY));
    }

    /**
     * This method is responsible for spawning a local hub for supporting local executions
     * 
     * @param testSession
     *            - A {@link AbstractTestSession} that represents the type of test session to start (mobile or web).
     * 
     */
    public static synchronized void spawnLocalHub(AbstractTestSession testSession) {
        LOGGER.entering(testSession.getPlatform());
        if (!isRunLocally()) {
            LOGGER.exiting();
            return;
        }

        setupToBootList();
        for (LocalServerComponent eachItem : toBoot) {
            try {
                eachItem.boot(testSession);
            } catch (Exception e) { //NOSONAR
                // If either the Grid or the Node failed to start at the first attempt then there is NO point in trying
                // to keep restarting it for every iteration. So lets log a severe message and exit the JVM.
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                System.exit(1);
            }
        }
        LOGGER.exiting();
    }

    /**
     * This method helps shut down the already spawned hub for local runs
     */
    final static synchronized void shutDownHub() {
        LOGGER.entering();
        if (!isRunLocally()) {
            LOGGER.exiting();
            return;
        }

        // shutdown in reverse order
        Collections.reverse(toBoot);
        for (LocalServerComponent eachItem : toBoot) {
            eachItem.shutdown();
        }
        clearToBootList();
        LOGGER.exiting();
    }

}
