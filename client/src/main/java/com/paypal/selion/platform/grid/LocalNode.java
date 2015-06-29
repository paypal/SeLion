/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 eBay Software Foundation                                                                   |
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

import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;

import com.paypal.selion.grid.ThreadedLauncher;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local node.
 */
final class LocalNode extends AbstractBaseLocalServerComponent implements LocalServerComponent {
    private static final SimpleLogger LOGGER = SeLionLogger.getLogger();
    private static volatile LocalNode instance;

    static synchronized final LocalServerComponent getSingleton() {
        if (instance == null) {
            return new LocalNode().getLocalServerComponent();
        }
        return instance;
    }

    synchronized final LocalNode getLocalServerComponent() {
        if (instance == null) {
            instance = new LocalNode();

            instance.setHost(new NetworkUtils().getIpOfLoopBackIp4());
            instance.setPort(PortProber.findFreePort());

            instance.setLauncher(new ThreadedLauncher(new String[] { "-role", "node", "-port",
                    String.valueOf(instance.getPort()), "-proxy", DefaultRemoteProxy.class.getName(), "-host",
                    instance.getHost(), "-hubHost", instance.getHost() }));
        }
        return instance;
    }

    @Override
    public void boot(AbstractTestSession testSession) {
        LOGGER.entering(testSession.getPlatform());
        if (!(testSession instanceof WebTestSession)) {
            return;
        }

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
