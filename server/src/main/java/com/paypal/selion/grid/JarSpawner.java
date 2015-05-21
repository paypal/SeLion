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

package com.paypal.selion.grid;

import static com.paypal.selion.pojos.SeLionGridConstants.*;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.common.base.Preconditions;
import com.paypal.selion.grid.RunnableLauncher.InstanceType;
import com.paypal.selion.logging.SeLionGridLogger;

/**
 * This is a stand alone class which sets up SeLion dependencies and spawns {@link SeLionGridLauncher}. This class
 * functions much like {@link ThreadedLauncher} but with a few exceptions. Mainly, it starts {@link SeLionGridLauncher}
 * in a separate process. It also continuously restarts {@link SeLionGridLauncher}, if it exits in an unexpected
 * fashion. Health checks for the {@link SeLionGridLauncher} process are configurable via the property 'restartCycle' in
 * the SeLion Grid JSON config file.
 */
public class JarSpawner extends AbstractBaseProcessLauncher {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(JarSpawner.class);
    private static final String SEPARATOR = "\n----------------------------------\n";

    public JarSpawner(String[] args) {
        init(args);
    }

    public static void main(String[] args) {
        new JarSpawner(args).run();
    }

    /**
     * Print the usage of SeLion Grid jar
     */
    void printUsageInfo() {
        StringBuffer usage = new StringBuffer();
        usage.append(SEPARATOR);
        usage.append("To use SeLion Grid");
        usage.append(SEPARATOR);
        usage.append("\n");
        usage.append("Usage: java [system properties] -jar SeLion-Grid.jar [options] \n");
        usage.append("            [selenium options] \n");
        usage.append("\n");
        usage.append("  Options:\n");
        usage.append("    " + TYPE_ARG + " <sauce>: \n");
        usage.append("       Used with '-role hub' to start a hub with the on demand \n");
        usage.append("       sauce proxy \n");
        usage.append("    " + SELION_CONFIG_ARG + " <config file name>: \n");
        usage.append("       A SeLion Grid configuration JSON file \n");
        usage.append("    " + SELION_NOCONTINUOS_ARG + "\n");
        usage.append("       Disable continuous restarting of node/hub sub-process \n");
        usage.append("\n");
        usage.append("  Selenium Options: \n");
        usage.append("    Any valid Selenium standalone or grid dash option(s). \n");
        usage.append("       E.g. '-role hub -port 2054' -- To start a hub on port 2054. \n");
        usage.append("\n");
        usage.append("  System Properties: \n");
        usage.append("    -DselionHome=<folderPath>: \n");
        usage.append("       Path of SeLion home directory. Defaults to \n");
        usage.append("       <user.home>/.selion/ \n");
        usage.append("    -D[property]=[value]: \n");
        usage.append("       Any other System Property you wish to pass to the JVM \n");

        System.out.print(usage.toString());
    }

    @Override
    void startProcess(boolean squelch) throws IOException {
        cmdLine = createJavaCommandForChildProcess();
        super.startProcess(squelch);
    }

    /**
     * This method load the default arguments required to spawn SeLion Grid/Node
     *
     * @return {@link CommandLine}
     * @throws IOException
     */
    private CommandLine createJavaCommandForChildProcess() throws IOException {
        LOGGER.entering();

        // start command with java
        CommandLine cmdLine = CommandLine.parse("java");

        // add the -D system properties
        cmdLine.addArguments(getJavaSystemPropertiesArguments());

        // Set the classpath
        cmdLine.addArguments(getJavaClassPathArguments("selenium-", SeLionGridLauncher.class.getName()));

        // add the program argument / dash options
        cmdLine.addArguments(getProgramArguments());

        LOGGER.exiting(cmdLine.toString());
        return cmdLine;
    }

    /**
     * Returns the <strong>{host}:{port}/{servletpath}</strong> for an {@link InstanceType#SELENIUM_HUB},
     * {@link InstanceType#SELION_SAUCE_HUB}, and {@link InstanceType#SELENIUM_NODE}. Returns
     * <code>null<code> for other {@link InstanceType}s
     * 
     * @param servlet
     *            the HttpServlet simple name.
     * @throws IOException
     */
    private String getHostPortAndServletPath(String servlet) throws IOException {
        LOGGER.entering(servlet);
        Preconditions.checkArgument(StringUtils.isNotEmpty(servlet), "servlet name must not be empty or null");
        InstanceType type = getType();

        String result = null;
        if (type.equals(InstanceType.SELENIUM_NODE)) {
            result = getHost() + ":" + getPort() + "/extra/" + servlet;
        }
        if (type.equals(InstanceType.SELENIUM_HUB)) {
            result = getHost() + ":" + getPort() + "/grid/" + servlet;
        }
        // standalone
        LOGGER.exiting(result);
        return result;
    }

    @Override
    public void shutdown() {
        // TODO NodeForceRestartServlet isn't a good option for Hubs
        // TODO Make process handler consider PID files
        CloseableHttpClient client = HttpClientBuilder.create().build();
        StringBuilder url = new StringBuilder();
        try {
            url.append("http://");
            url.append(getHostPortAndServletPath("NodeForceRestartServlet"));
            HttpPost post = new HttpPost(url.toString());
            client.execute(post);
        } catch (IOException e) {
            // ignore
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                // ignore
            }
        }
        super.shutdown();
    }
}
