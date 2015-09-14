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

package com.paypal.selion.internal.platform.grid;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.grid.common.exception.GridException;
import org.openqa.selenium.net.PortProber;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.grid.RunnableLauncher;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * An abstract base class for starting/stopping Selenium server components.
 */
abstract class AbstractBaseLocalServerComponent implements LocalServerComponent {

    private static final SimpleLogger LOGGER = SeLionLogger.getLogger();

    private ExecutorService executor;

    private RunnableLauncher launcher;

    private String host;

    private int port;

    /**
     * @return the {@link LocalServerComponent} which extends {@link AbstractBaseLocalServerComponent}
     */
    abstract AbstractBaseLocalServerComponent getLocalServerComponent();

    static LocalServerComponent getSingleton() {
        throw new IllegalStateException(String.format("%s does support instantiation.",
                AbstractBaseLocalServerComponent.class.getSimpleName()));
    }

    public synchronized void boot(AbstractTestSession testSession) {
        LOGGER.entering();

        if (getLauncher().isRunning()) {
            LOGGER.exiting();
            return;
        }

        checkPort(getPort(), String.format("for the %s", this.getClass().getSimpleName()));

        setExecutor(Executors.newSingleThreadExecutor());
        Runnable worker = getLauncher();
        try {
            getExecutor().execute(worker);
            waitForInitialization(Long.parseLong(Config.getConfigProperty(ConfigProperty.DOWNLOAD_TIMEOUT)));
            waitForComponentToComeUp();
            LOGGER.info(String.format("%s spawned", this.getClass().getSimpleName()));
        } catch (IllegalStateException e) {
            throw new GridException(String.format("Failed to start a %s", this.getClass().getSimpleName()), e);
        }
        LOGGER.exiting();
    }

    public synchronized void shutdown() {
        LOGGER.entering();

        if (!getLauncher().isRunning()) {
            LOGGER.exiting();
            return;
        }

        try {
            getLauncher().shutdown();
            getExecutor().shutdownNow();
            while (!getExecutor().isTerminated() || getLauncher().isRunning()) {
                getExecutor().awaitTermination(30, TimeUnit.SECONDS);
            }
            LOGGER.info(String.format("%s has been stopped", this.getClass().getSimpleName()));
        } catch (InterruptedException e) {
            String errorMsg = "An error occurred while attempting to shut down the %s.";
            LOGGER.log(Level.SEVERE, String.format(errorMsg, this.getClass().getSimpleName()));
        }
        LOGGER.exiting();
    }

    /**
     * Check the port availability
     * 
     * @param port
     *            the port to check
     * @param msg
     *            the text to append to the end of the error message displayed when the port is not available.
     * @throws IllegalArgumentException
     *             when the port is not available.
     */
    void checkPort(int port, String msg) {
        StringBuilder message = new StringBuilder().append(" ").append(msg);
        String portInUseError = String.format("Port %d is already in use. Please shutdown the service "
                + "listening on this port or configure a different port%s.", port, message);
        boolean free = false;
        try {
            free = PortProber.pollPort(port);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(portInUseError, e);
        } finally {
            if (!free) {
                throw new IllegalArgumentException(portInUseError);
            }
        }
    }

    /*
     * Waits for the launcher to initialize. This code exists purely to give the launcher more time to download
     * dependencies. Otherwise, it is not needed since RunnableLauncher#isRunning() _should_ check for isInitialized()
     * at method entry.
     */
    private void waitForInitialization(long timeout) {
        LOGGER.entering();
        long time = 0;
        while (!getLauncher().isInitialized() && (time < timeout)) {
            try {
                // SeLion Grid could still be downloading dependencies.. Wait for it.
                Thread.sleep(1000);
                time += 1000;
            } catch (InterruptedException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        if (time == timeout) {
            throw new IllegalStateException(String.format("Timed out waiting for %s to initialize.",
                    getLocalServerComponent().getClass().getSimpleName()));
        }
        LOGGER.exiting();
    }

    /**
     * Checks component has come up every 3 seconds. Waits for the component for a maximum of 60 seconds. Throws an
     * {@link IllegalStateException} if the component can not be contacted
     */
    private void waitForComponentToComeUp() {
        LOGGER.entering();
        for (int i = 0; i < 60; i++) {
            try {
                // Sleep for 3 seconds.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            if (getLauncher().isRunning()) {
                LOGGER.exiting();
                return;
            }
        }
        throw new IllegalStateException(String.format("%s can not be contacted.", getLocalServerComponent().getClass()
                .getSimpleName()));
    }

    /**
     * Set the host for the local server component
     * 
     * @param host
     *            the host
     */
    void setHost(String host) {
        getLocalServerComponent().host = host;
    }

    /**
     * Set the port for the local server component
     * 
     * @param port
     *            the port
     */
    void setPort(int port) {
        getLocalServerComponent().port = port;
    }

    /**
     * Set the launcher for the local server component
     * 
     * @param launcher
     *            the {@link RunnableLauncher}
     */
    void setLauncher(RunnableLauncher launcher) {
        getLocalServerComponent().launcher = launcher;
    }

    /**
     * Set the {@link ExecutorService} for the local server component
     * 
     * @param executor
     *            the {@link ExecutorService}
     */
    void setExecutor(ExecutorService executor) {
        getLocalServerComponent().executor = executor;
    }

    /**
     * @return the {@link ExecutorService} for the local server component
     */
    ExecutorService getExecutor() {
        return getLocalServerComponent().executor;
    }

    /**
     * @return the port used for the {@link LocalServerComponent}
     */
    public int getPort() {
        return getLocalServerComponent().port;
    }

    /**
     * @return the host used for the {@link LocalServerComponent}
     */
    public String getHost() {
        return getLocalServerComponent().host;
    }

    /**
     * @return the {@link RunnableLauncher} used for the {@link LocalServerComponent}o
     */
    public RunnableLauncher getLauncher() {
        return getLocalServerComponent().launcher;
    }
}
