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

package com.paypal.selion.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang.StringUtils;

import com.paypal.selion.logging.SeLionGridLogger;

/**
 * Handles opening a SSH Tunnel using the Sauce Connect logic. The class maintains a cache of {@link Process } instances
 * mapped against the corresponding plan key.
 * 
 */
public class SauceConnectManager {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SauceConnectManager.class);
    private final boolean quietMode;
    private Map<String, Process> tunnelMap = new HashMap<String, Process>();
    int port = 5000;

    /**
     * Lock which ensures thread safety for opening and closing tunnels.
     */
    private Lock accessLock = new ReentrantLock();

    public SauceConnectManager() {
        this(false);
    }

    public SauceConnectManager(boolean quietMode) {
        this.quietMode = quietMode;
    }

    private int getNextPort() {
        port++;
        if (port == 5051) {
            port = 5000;
        }
        return port;
    }

    public Map<String, Process> getTunnelMap() {
        try {
            accessLock.lock();
            return tunnelMap;
        } finally {
            accessLock.unlock();
        }
    }

    public List<String> getUsers() {
        try {
            accessLock.lock();
            List<String> result = new ArrayList<String>();
            result.addAll(tunnelMap.keySet());
            return result;
        } finally {
            accessLock.unlock();
        }
    }

    public void removeUserFromTunnelMap(String user) {
        try {
            accessLock.lock();
            tunnelMap.remove(user);
        } finally {
            accessLock.unlock();
        }
    }

    public void closeTunnelsForPlan(String userName, PrintStream printStream) {
        try {
            accessLock.lock();
            if (tunnelMap.containsKey(userName)) {
                // we can now close the process
                final Process sauceConnect = tunnelMap.get(userName);
                closeTunnel(sauceConnect);
                logMessage(printStream, "Closed Sauce Connect process");
                tunnelMap.remove(userName);
            }
        } finally {
            accessLock.unlock();
        }
    }

    private void closeTunnel(final Process sauceConnect) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    IOUtils.copy(sauceConnect.getInputStream(), new NullOutputStream());
                } catch (IOException e) {
                    // ignore
                }
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                try {
                    IOUtils.copy(sauceConnect.getErrorStream(), new NullOutputStream());
                } catch (IOException e) {
                    // ignore
                }
            }
        }).start();

        sauceConnect.destroy();
    }

    private void logMessage(PrintStream printStream, String message) {
        if (printStream != null) {
            printStream.println(message);
        }
        LOGGER.log(Level.INFO, message);
    }

    private void addTunnelToMap(String userName, Object tunnel) {
        if (!tunnelMap.containsKey(userName)) {
            tunnelMap.put(userName, (Process) tunnel);
        }
    }

    /**
     * Creates a new Java process to run the Sauce Connect library.
     * 
     * @param username
     *            - the name of the Sauce OnDemand user
     * @param apiKey
     *            - the API Key for the Sauce OnDemand user
     * @param sauceConnectJar
     *            - the Jar file containing Sauce Connect. If null, then we attempt to find Sauce Connect from the
     *            classpath
     * @param options
     *            - The options to be passed on to the sauce connect jar [ it should be space separated values ]
     * @param httpsProtocol
     *            - The protocol to be made use of
     * @param printStream
     *            - A {@link PrintStream} object to redirect the output from Sauce Connect to. Can be null
     * @return - A {@link Process} instance which represents the Sauce Connect instance
     * @throws IOException
     */
    public Process openConnection(String username, String apiKey, File sauceConnectJar, String options,
            String httpsProtocol, PrintStream printStream) throws IOException {

        // ensure that only a single thread attempts to open a connection
        try {
            accessLock.lock();
            // do we have an instance for the user?
            if (checkUserExists(username)) {
                Process p = tunnelMap.get(username);
                try {
                    // logic to check the process is still running
                    p.exitValue();
                    removeUserFromTunnelMap(username);
                } catch (Exception e) {
                    // if it throws exception then sauce connect is already running
                    logMessage(printStream, "Sauce Connect already running for " + username);
                    return tunnelMap.get(username);
                }
                // if so, increment counter and return
            }
            // if not, start the process
            File workingDirectory = null;
            StringBuilder builder = new StringBuilder();
            if (sauceConnectJar != null && sauceConnectJar.exists()) {
                builder.append(sauceConnectJar.getPath());
                workingDirectory = sauceConnectJar.getParentFile();
            } else {
                throw new RuntimeException("Sauce Connect jar file not set");
            }

            String fileSeparator = File.separator;
            String path = System.getProperty("java.home") + fileSeparator + "bin" + fileSeparator + "java";
            String[] args;
            if (StringUtils.isBlank(httpsProtocol)) {
                args = new String[] { path, "-jar", builder.toString(), username, apiKey, "-P",
                        String.valueOf(getNextPort()), };
            } else {
                args = new String[] { path, "-Dhttps.protocols=" + httpsProtocol, builder.toString(), username, apiKey,
                        "-P", String.valueOf(getNextPort()) };
            }

            if (StringUtils.isNotBlank(options)) {
                args = addElement(args, options);
            }

            ProcessBuilder processBuilder = new ProcessBuilder(args);
            if (workingDirectory == null) {
                workingDirectory = new File(getSauceConnectWorkingDirectory());
            }
            processBuilder.directory(workingDirectory);
            LOGGER.log(Level.INFO, "Launching Sauce Connect " + Arrays.toString(args));

            final Process process = processBuilder.start();
            try {
                Semaphore semaphore = new Semaphore(1);
                semaphore.acquire();
                StreamGobbler errorGobbler = new SystemErrorGobbler("ErrorGobbler", process.getErrorStream());
                errorGobbler.start();
                StreamGobbler outputGobbler = new SystemOutGobbler("OutputGobbler", process.getInputStream(), semaphore);
                outputGobbler.start();

                boolean sauceConnectStarted = semaphore.tryAcquire(2, TimeUnit.MINUTES);
                if (!sauceConnectStarted) {
                    // log an error message
                    logMessage(printStream, "Time out while waiting for Sauce Connect to start, attempting to continue");
                    closeTunnel(process);
                    return null;

                }
            } catch (InterruptedException e) {
                // continue;
            }
            logMessage(printStream, "Sauce Connect now launched");
            addTunnelToMap(username, process);
            return process;

        } finally {
            // release the access lock
            accessLock.unlock();
        }
    }

    private String[] addElement(String[] original, String added) {
        // split added on space
        String[] split = added.split(" ");
        String[] result = original;
        for (String arg : split) {
            String[] newResult = Arrays.copyOf(result, result.length + 1);
            newResult[result.length] = arg;
            result = newResult;
        }
        return result;
    }

    private String getSauceConnectWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    public boolean checkUserExists(String username) {
        try {
            accessLock.lock();
            if (tunnelMap.get(username) != null) {
                return true;
            } else {
                return false;
            }
        } finally {
            accessLock.unlock();
        }
    }

    private abstract class StreamGobbler extends Thread {
        private InputStream is;

        private StreamGobbler(String name, InputStream is) {
            super(name);
            this.is = is;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    processLine(line);
                }
            } catch (IOException ioe) {
                // ignore stream closed errors
                if (!(ioe.getMessage().equalsIgnoreCase("stream closed"))) {
                    ioe.printStackTrace();
                }
            }
        }

        protected void processLine(String line) {
            if (!quietMode) {
                getPrintStream().println(line);
                LOGGER.info(line);
            }
        }

        public abstract PrintStream getPrintStream();
    }

    private class SystemOutGobbler extends StreamGobbler {

        private final Semaphore semaphore;

        SystemOutGobbler(String name, InputStream is, final Semaphore semaphore) {
            super(name, is);
            this.semaphore = semaphore;
        }

        @Override
        public PrintStream getPrintStream() {
            return System.out;
        }

        @Override
        protected void processLine(String line) {
            super.processLine(line);
            if (StringUtils.containsIgnoreCase(line, "Connected! You may start your tests")) {
                // unlock processMonitor
                semaphore.release();
            }
        }
    }

    private class SystemErrorGobbler extends StreamGobbler {

        SystemErrorGobbler(String name, InputStream is) {
            super(name, is);
        }

        @Override
        public PrintStream getPrintStream() {
            return System.err;
        }
    }
}
