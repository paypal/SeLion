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

package com.paypal.selion.grid;

import static com.paypal.selion.pojos.SeLionGridConstants.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.paypal.selion.grid.LauncherOptions.LauncherOptionsImpl;
import com.paypal.selion.logging.SeLionGridLogger;

/**
 * An abstract base {@link RunnableLauncher} for SeLion Grid components
 */
abstract class AbstractBaseLauncher implements RunnableLauncher {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(AbstractBaseLauncher.class);
    
    /*
     * Launcher options to consider.
     */
    private LauncherOptions launcherOptions;

    /*
     * Whether this launcher has been initialized.
     */
    private boolean initialized;

    /*
     * The current instance type
     */
    private InstanceType type;

    /*
     * The received launcher commands
     */
    private List<String> commands;

    public final boolean isInitialized() {
        return initialized;
    }

    /**
     * Set this launchers to initialized
     * 
     * @param state
     *            the new state
     */
    final void setInitialized(boolean state) {
        this.initialized = state;
    }

    /**
     * Set this launchers commands
     * 
     * @param commands
     *            the launcher commands
     */
    final void setCommands(List<String> commands) {
        this.commands = commands;
    }

    /**
     * @return the launcher commands
     */
    final List<String> getCommands() {
        return commands;
    }

    /**
     * Get the {@link InstanceType} this launcher represents. If type has not been established by a previous call to
     * {@link #setType(InstanceType)} this method attempt to determine the type by inspecting the {@link #commands}.
     * Inspection does not work for mobile instance types. Therefore, it is best to always call
     * {@link #setType(InstanceType)} beforehand.
     * 
     * @return an {@link InstanceType}
     */
    final InstanceType getType() {
        if (type == null) {
            return determineType(commands);
        }
        return type;
    }

    /**
     * Set the {@link InstanceType} for this launcher.
     * 
     * @param type
     *            the {@link InstanceType}
     */
    final void setType(InstanceType type) {
        this.type = type;
    }

    /**
     * Return the {@link InstanceType}. Defaults to {@link InstanceType#SELENIUM_STANDALONE}. Does not work or mobile
     * instance types.
     * 
     * @param commands
     *            command line arguments passed from User
     * @return an {@link InstanceType}
     */
    private InstanceType determineType(List<String> commands) {
        LOGGER.entering(commands.toString());
        type = InstanceType.SELENIUM_STANDALONE;
        if (commands.contains(ROLE_ARG) && commands.contains(InstanceType.SELENIUM_NODE.getFriendlyName())) {
            type = InstanceType.SELENIUM_NODE;
        }
        if (commands.contains(ROLE_ARG) && commands.contains(InstanceType.SELENIUM_HUB.getFriendlyName())) {
            type = InstanceType.SELENIUM_HUB;
        }
        LOGGER.exiting(type);
        return type;
    }

    /**
     * Get program arguments to pass
     *
     * @return the program arguments to pass represented as an array of {@link String}
     * @throws IOException
     */
    String[] getProgramArguments() throws IOException {
        LOGGER.entering();

        InstanceType type = getType();
        // start with the commands we have
        List<String> args = new LinkedList<String>(commands);

        // add the default hub or node config arguments
        if (InstanceType.SELENIUM_NODE.equals(type)) {
            args.addAll(Arrays.asList(getNodeProgramArguments()));
        }
        if (InstanceType.SELENIUM_HUB.equals(type)) {
            args.addAll(Arrays.asList(getHubProgramArguments()));
        }
        // else role is standalone

        // pass the -selionConfig arg, if not a standalone
        if ((!InstanceType.SELENIUM_STANDALONE.equals(type)) && (!commands.contains(SELION_CONFIG_ARG))) {
            args.add(SELION_CONFIG_ARG);
            args.add(SELION_CONFIG_FILE);
        }

        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }

    /**
     * Get SeLion Node related arguments to pass
     *
     * @return the node arguments to pass as program arguments represented as an array of {@link String}
     * @throws IOException
     */
    private String[] getNodeProgramArguments() throws IOException {
        LOGGER.entering();

        LOGGER.info("This instance is considered a SeLion Grid Node");

        List<String> args = new LinkedList<String>();

        if (!commands.contains(NODE_CONFIG_ARG)) {
            args.add(NODE_CONFIG_ARG);
            args.add(NODE_CONFIG_FILE);
            InstallHelper.copyFileFromResources(NODE_CONFIG_FILE_RESOURCE, NODE_CONFIG_FILE);
        }

        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }

    /**
     * Get SeLion Grid related arguments to pass
     *
     * @return the grid arguments to pass as program arguments represented as an array of {@link String}
     * @throws IOException
     */
    private String[] getHubProgramArguments() throws IOException {
        LOGGER.entering();

        LOGGER.info("This instance is considered a SeLion Grid Hub");

        List<String> args = new LinkedList<String>();

        if (!commands.contains(HUB_CONFIG_ARG)) {
            String hubConfig = HUB_CONFIG_FILE;

            // To verify this is SeLion Sauce Grid or not
            if (commands.contains(TYPE_ARG) && commands.contains(InstanceType.SELION_SAUCE_HUB.getFriendlyName())) {
                hubConfig = HUB_SAUCE_CONFIG_FILE;
                InstallHelper.copyFileFromResources(HUB_SAUCE_CONFIG_FILE_RESOURCE, HUB_SAUCE_CONFIG_FILE);
                InstallHelper.copyFileFromResources(SAUCE_CONFIG_FILE_RESOURCE, SAUCE_CONFIG_FILE);
            } else {
                InstallHelper.copyFileFromResources(HUB_CONFIG_FILE_RESOURCE, HUB_CONFIG_FILE);
            }

            args.add(HUB_CONFIG_ARG);
            args.add(hubConfig);
        }

        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }

    /**
     * Get the host for the instance represented by this launcher
     * 
     * @return the host information
     */
    String getHost() {
        LOGGER.entering();
        String val = "";

        InstanceType type = getType();
        if (commands.contains("-host")) {
            val = commands.get(commands.indexOf("-host") + 1);
            LOGGER.exiting(val);
            return val;
        }

        try {
            if (type.equals(InstanceType.SELENIUM_NODE)) {
                val = getSeleniumConfigAsJsonObject().getAsJsonObject("configuration").get("host").getAsString();
            }
            if (type.equals(InstanceType.SELENIUM_HUB)) {
                val = getSeleniumConfigAsJsonObject().get("host").getAsString();
            }
        } catch (JsonParseException | NullPointerException e) {
            // ignore
        }

        // return the value if it looks okay, otherwise return "localhost" as a last ditch effort
        val = (StringUtils.isNotEmpty(val) && !val.equalsIgnoreCase("ip")) ? val : "localhost";
        LOGGER.exiting(val);
        return val;
    }

    /**
     * Get the port for the instance represented by this launcher
     * 
     * @return the port information.
     */
    int getPort() {
        LOGGER.entering();
        int val = -1;

        InstanceType type = getType();
        if (commands.contains("-port")) {
            val = Integer.parseInt(commands.get(commands.indexOf("-port") + 1));
            LOGGER.exiting(val);
            return val;
        }

        try {
            if (type.equals(InstanceType.SELENIUM_NODE)) {
                val = getSeleniumConfigAsJsonObject().getAsJsonObject("configuration").get("port").getAsInt();
            }
            if (type.equals(InstanceType.SELENIUM_HUB)) {
                val = getSeleniumConfigAsJsonObject().get("port").getAsInt();
            }
        } catch (JsonParseException | NullPointerException e) {
            // ignore
        }

        // last ditch effort
        val = (type.equals(InstanceType.SELENIUM_NODE)) ? 5555 : 4444;
        LOGGER.exiting(val);
        return val;
    }

    private JsonObject getSeleniumConfigAsJsonObject() {
        LOGGER.entering();

        String jsonFile = getSeleniumConfigFilePath();
        JsonObject jsonObject = new JsonObject();

        if (StringUtils.isEmpty(jsonFile)) {
            LOGGER.exiting(jsonObject.toString());
            return jsonObject;
        }

        String json;
        try {
            json = FileUtils.readFileToString(new File(jsonFile));
            jsonObject = new JsonParser().parse(json).getAsJsonObject();
        } catch (IOException e) {
            LOGGER.exiting(jsonObject.toString());
            return jsonObject;
        }
        LOGGER.exiting(jsonObject.toString());
        return jsonObject;
    }

    /**
     * Get the config file path for the instance represented by this launcher.
     * 
     * @return the config file path or <code>null</code> if no config file was specified
     */
    private String getSeleniumConfigFilePath() {
        LOGGER.entering();
        
        String result = null;
        InstanceType type = getType();
        
        if (type.equals(InstanceType.SELENIUM_NODE)) {
            result = NODE_CONFIG_FILE;
            if (commands.contains("-nodeConfig")) {
                result = commands.get(commands.indexOf("-nodeConfig" + 1));
            }
        }
        if (type.equals(InstanceType.SELENIUM_HUB)) {
            result = HUB_CONFIG_FILE;
            if (commands.contains("-hubConfig")) {
                result = commands.get(commands.indexOf("-hubConfig" + 1));
            }
        }
        LOGGER.exiting(result);
        return result;
    }

    public boolean isRunning() {
        LOGGER.entering();
        if (!isInitialized()) {
            return false;
        }

        boolean result = false;
        InstanceType type = getType();
        try {
            if (type.equals(InstanceType.SELENIUM_HUB)) {
                String url = String.format("http://%s:%d/grid/api/hub", getHost(), getPort());
                URLConnection hubConnection = new URL(url).openConnection();

                JsonObject fullResponse = extractObject(hubConnection.getInputStream());
                if (fullResponse != null) {
                    result = fullResponse.get("success").getAsBoolean();
                }
            } else {
                // instance is a node or a standalone -- this should work for both
                String endPoint = String.format("http://%s:%d/wd/hub/status", getHost(), getPort());
                URLConnection hubConnection = new URL(endPoint).openConnection();

                JsonObject fullResponse = extractObject(hubConnection.getInputStream());
                if (fullResponse != null) {
                    result = (fullResponse.get("status").getAsInt() == 0);
                }
            }
        } catch (IOException e) {
            // ignore
        }

        LOGGER.exiting(result);
        return result;
    }

    JsonObject extractObject(InputStream inputStream) throws IOException {
        LOGGER.entering();
        StringBuilder information = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String eachLine;
            while ((eachLine = br.readLine()) != null) {
                information.append(eachLine);
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(br);
        }

        LOGGER.exiting(information.toString());
        return new JsonParser().parse(information.toString()).getAsJsonObject();
    }

    <T extends LauncherOptions> void setLauncherOptions(T launcherOptions) {
        this.launcherOptions = launcherOptions;
    }

    LauncherOptions getLauncherOptions() {
        if (launcherOptions == null) {
            launcherOptions = new LauncherOptionsImpl();
        }
        return launcherOptions;
    }

}
