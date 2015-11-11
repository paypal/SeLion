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

package com.paypal.selion.configuration;

import java.util.HashMap;
import java.util.Map;

import org.testng.ITestNGListener;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * <code>ListenerManager</code> provides facilities to register and use Listeners.
 * 
 * TestNG 6.1 and above provides a capability wherein you can hook in Listeners via ServiceLoading. To leverage this
 * capability, and also be able to dynamically enable/disable it from within a project , we need to first register the
 * listener with {@link ListenerManager}. <br>
 * <ul>
 * <li>Use {@link ListenerManager#registerListener(ListenerInfo)} to register your listener and then use
 * <li>{@link ListenerManager#executeCurrentMethod(ITestNGListener)} - to find if a current method within your listener
 * should be invoked or skipped.
 * </ul>
 * 
 * For more details about ServiceLoader capabilities in TestNG refer <a href=https
 * ://groups.google.com/forum/?fromgroups#!topic/testng-users/ZVloM26gEoI >here</a>
 */
public final class ListenerManager {

    public static final String THREAD_EXCLUSION_MSG = "Encountered either a duplicate or an Extended instance for SeLion Mandatory Listener. Skipping execution...";
    /**
     * This String constant represents the JVM argument that can be enabled/disabled to enable/disable
     * {@link ListenerManager} to manage the listeners.
     */
    public static final String ENABLE_LISTENER_MANAGER = "enable.listener.manager";

    private static Map<String, ListenerInfo> listenerMap = new HashMap<String, ListenerInfo>();

    private static volatile boolean serviceLoaderEnabled = true;

    private static SimpleLogger logger = SeLionLogger.getLogger();

    /**
     * Register your Listener using this method.
     * 
     * @param information
     *            - A {@link ListenerInfo} object that contains information pertaining to your listener.
     */
    public static void registerListener(ListenerInfo information) {
        if (isServiceLoaderDisabled()) {
            // Donot even attempt register any listeners if the user doesnt want them to be managed.
            return;
        }
        logger.entering(information);
        listenerMap.put(information.getListenerClassName(), information);
    }

    /**
     * This method decides if a TestNG interface implementation should be executed or skipped. The decision is made
     * after checking if the current Listener enabled/disabled as determined by {@link ListenerInfo#isEnabled()}.
     * 
     * @param listener
     *            - A {@link ITestNGListener} object which represents your listener.
     * 
     * @return - A flag which states if the current method is to be skipped or executed.
     */
    public static boolean executeCurrentMethod(ITestNGListener listener) {
        String className = listener.getClass().getName();
        logger.entering("Listener Class : " + className);
        if (isServiceLoaderDisabled()) {
            logger.info("Listener Management was disabled. None of the managed listeners will be invoked.");
            logger.exiting(false);
            return false;
        }

        boolean runCurrentMethod = listenerMap.get(className).isEnabled();

        // below lines of code are only intended to pretty print the exiting message.
        String instanceName = listener.toString();
        if (instanceName.indexOf('@') >= 0) {
            instanceName = ((instanceName.split("@").length >= 2) ? (instanceName.split("@")[1]) : instanceName);
        }

        logger.exiting(String.format("Execute listener methods of class %s on instance %s ? %s", className,
                instanceName, runCurrentMethod));
        return runCurrentMethod;
    }

    static {
        serviceLoaderEnabled = ListenerInfo.getBooleanValFromVMArg(ENABLE_LISTENER_MANAGER);
    }

    private ListenerManager() {
    }

    /**
     * Check if {@link ITestNGListener} is to be skipped
     * 
     * @param listener
     *            the {@link ITestNGListener}
     * @return true if method is to be skipped.
     */
    public static boolean isCurrentMethodSkipped(ITestNGListener listener) {
        return (executeCurrentMethod(listener) == false);
    }

    private static boolean isServiceLoaderDisabled() {
        return !serviceLoaderEnabled;
    }

}
