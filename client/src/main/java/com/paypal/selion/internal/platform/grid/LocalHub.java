/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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

import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.grid.LauncherConfiguration;
import com.paypal.selion.grid.LauncherOptions;
import com.paypal.selion.grid.ThreadedLauncher;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local Hub.
 */
final class LocalHub extends AbstractBaseLocalServerComponent {
    private static final SimpleLogger LOGGER = SeLionLogger.getLogger();
    private static volatile LocalHub instance;

    static synchronized LocalServerComponent getSingleton() {
        if (instance == null) {
            instance = new LocalHub().getLocalServerComponent();
        }
        return instance;
    }

    synchronized LocalHub getLocalServerComponent() {
        if (instance == null) {
            instance = new LocalHub();

            instance.setHost(new NetworkUtils().getIpOfLoopBackIp4());

            // Choose a random port for local hub.
            int hubPort = PortProber.findFreePort();
            instance.setPort(hubPort);
            // Set ConfigProperty.SELENIUM_PORT so that the local nodes can register to it.
            Config.setConfigProperty(ConfigProperty.SELENIUM_PORT, Integer.toString(hubPort));

            LauncherOptions launcherOptions = new LauncherConfiguration()
                    .setFileDownloadCheckTimeStampOnInvocation(false).setFileDownloadCleanupOnInvocation(false);

            instance.setLauncher(new ThreadedLauncher(new String[] { "-role", "hub", "-port",
                    String.valueOf(instance.getPort()), "-host", instance.getHost() }, launcherOptions));
        }
        return instance;
    }

    @Override
    public void boot(AbstractTestSession testSession) {
        LOGGER.entering();
        if (instance == null) {
            getLocalServerComponent();
        }
        super.boot(testSession);
        LOGGER.exiting();
    }

    @Override
    public void shutdown() {
        LOGGER.entering();
        if (instance == null) {
            LOGGER.exiting();
            return;
        }
        super.shutdown();
        LOGGER.exiting();
    }

}
