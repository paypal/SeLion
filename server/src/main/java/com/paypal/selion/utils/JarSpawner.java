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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;

import com.paypal.selion.grid.SeLionGridLauncher;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.node.servlets.LogServlet;
import com.paypal.selion.node.servlets.NodeAutoUpgradeServlet;
import com.paypal.selion.node.servlets.NodeForceRestartServlet;
import com.paypal.selion.pojos.SeLionGridConstants;

/**
 * This is a standalone class that essentially helps spawn a jar given via the commandline along with the arguments that
 * are required by the jar.
 * 
 */
public class JarSpawner {

    private static final String SEPARATOR = "\n----------------------------------\n";
    private static final String HELP = "-help";
    private static final int _100 = 100;
    private static final String HUB = "hub";
    private static final String _60 = "60";
    private static final String BROWSER_TIMEOUT = "-browserTimeout";
    private static final String SERVLETS = "-servlets";
    private static final String NODE_CONFIG = "-nodeConfig";
    private static final String SAUCE = "sauce";
    private static final String HUB_CONFIG = "-hubConfig";
    private static final String TYPE = "-type";
    private static final String ROLE = "-role";
    private static final Logger logger = SeLionGridLogger.getLogger();

    public static void main(String[] args) throws ExecuteException, IOException, InterruptedException {

        List<String> commands = Arrays.asList(args);

        if (commands.contains(SeLionGridConstants.CONFIG_NAME)) {
            ConfigParser.setConfigFile(commands.get(commands.indexOf(SeLionGridConstants.CONFIG_NAME) + 1));
        }

        initialSetup();
        long interval = ConfigParser.getInstance().getLong("defaultInterval");
        logger.info("Default interval check will be every " + interval + " ms");
        while (true) {
            FileDownloader.checkForDownloads();
            if(commands.contains(HELP)) {
                continuouslyRestart(commands, _100);
                printUsageInfo();
                return;
            }
            continuouslyRestart(commands, interval);
            Thread.sleep(interval);
            logger.info("Application exited. Respawning the application again");
        }
    }

    /**
     * Print the usage of SeLion Grid jar
     */
    private static void printUsageInfo() {

        StringBuffer usage = new StringBuffer();
        usage.append(SEPARATOR);
        usage.append("To use SeLion Grid jar");
        usage.append(SEPARATOR);
        usage.append("\n");
        usage.append("Usage: java [system property] -jar SeLion-Grid.jar [-interactive] [options]\n");
        usage.append("\n");
        usage.append("\t-role <hub|node>: To specify SeLion Grid or Node\n");
        usage.append("\t-type <sauce>: To specify whether it is sauce grid or not\n");
        usage.append("\t-config <configFileName>: SeLion configuration JSON file\n\n");
        usage.append("\tSystem Properties:\n");
        usage.append("\t\t selionHome=<folderPath> :Path of SeLion home directory. default value is <user.home>/.selion/\n");
        usage.append("\t\t java.util.logging.config.file=<logging.properties> :Path of logging.properties file\n");
        logger.info(usage.toString());
    }

    /**
     * This method spawns a jar, and waits for it to exit [either cleanly or forcibly]
     * 
     * @param commands
     *            - command to execute using jarspawner
     * @param interval
     *            - How often should the application check if the jar is still running or if it exit.
     * @throws ExecuteException
     * @throws IOException
     * @throws InterruptedException
     */
    public static void continuouslyRestart(List<String> commands, long interval) throws ExecuteException, IOException,
            InterruptedException {

        CommandLine cmdLine = loadDefaultArguments(commands);

        for (int i = 0; i < commands.size(); i++) {
            cmdLine.addArgument(commands.get(i), false);
        }

        logger.info("Executing command " + cmdLine.toString());

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(new PumpStreamHandler());
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
        executor.execute(cmdLine, handler);
        while (!handler.hasResult()) {
            Thread.sleep(interval);
        }
        logger.info("Waking up");

        if (handler.hasResult()) {
            ExecuteException e = handler.getException();
            if (e != null) {
                logger.log(Level.SEVERE, handler.getException().getMessage(), handler.getException());
            }
        }
    }

    /**
     * This method load the default arguments required to spawn SeLion Grid/Node
     *
     * @param commands - command line arguments passed from User
     * @return {@link CommandLine}
     * @throws IOException
     */
    private static CommandLine loadDefaultArguments(List<String> commands) throws IOException {

        CommandLine cmdLine = CommandLine.parse("java");

        configureSystemProperties(cmdLine);

        cmdLine.addArgument("-cp");
        cmdLine.addArgument(SeLionGridConstants.SELION_HOME + "*" + File.pathSeparatorChar + "."
                + File.pathSeparatorChar + "*" + File.pathSeparatorChar + "." );
        cmdLine.addArgument(SeLionGridLauncher.class.getName());

        // To verify this is SeLion Node or Grid
        if (commands.contains(ROLE) && commands.contains("node")) {
            configureNodeArguments(commands, cmdLine);
        } else {
            configureGridArguments(commands, cmdLine);
        }
        return cmdLine;
    }

    /**
     * Configure required system properties to launch SeLion Grid/Node
     * @param cmdLine
     * @throws IOException
     */
    private static void configureSystemProperties(CommandLine cmdLine) throws IOException {

        if (System.getProperty("selionHome") != null) {
            cmdLine.addArgument("-DselionHome=" + System.getProperty("selionHome"));
        }

        if (System.getProperty("java.util.logging.config.file") == null) {
            createLoggerFile();
            cmdLine.addArgument("-Djava.util.logging.config.file=" + SeLionGridConstants.LOGGER);
        }

        if (SystemUtils.IS_OS_WINDOWS && System.getProperty("webdriver.ie.driver") == null) {
            cmdLine.addArgument("-Dwebdriver.ie.driver=" + SeLionGridConstants.SELION_HOME
                    + SeLionGridConstants.IE_DRIVER);
        }

        if (System.getProperty("webdriver.chrome.driver") == null) {
            cmdLine.addArgument("-Dwebdriver.chrome.driver=" + SeLionGridConstants.SELION_HOME
                    + SeLionGridConstants.CHROME_DRIVER);
        }

        if (System.getProperty("phantomjs.binary.path") == null) {
            cmdLine.addArgument("-Dphantomjs.binary.path=" + SeLionGridConstants.SELION_HOME
                    + SeLionGridConstants.PHANTOMJS_DRIVER);
        }
    }

    /**
     * Configure SeLion Node related arguments to the CommandLine
     *
     * @param commands - command line arguments passed from User
     * @param cmdLine
     * @throws IOException
     */
    private static void configureNodeArguments(List<String> commands, CommandLine cmdLine) throws IOException {

        logger.info("This instance considered as a SeLion Node");

        if (!commands.contains(NODE_CONFIG)) {
            cmdLine.addArgument(NODE_CONFIG).addArgument(SeLionGridConstants.NODE_CONFIG);
            copyFileToUserHome(SeLionGridConstants.JAR_NODE_CONFIG, SeLionGridConstants.NODE_CONFIG);
        }

        if (!commands.contains(SERVLETS)) {
            cmdLine.addArgument(SERVLETS).addArgument(
                    NodeAutoUpgradeServlet.class.getName() + "," + NodeForceRestartServlet.class.getName() + ","
                            + LogServlet.class.getName());
        }

        if (!commands.contains(BROWSER_TIMEOUT)) {
            cmdLine.addArgument(BROWSER_TIMEOUT).addArgument(_60);
        }
    }

    /**
     * Configure SeLion Grid related arguments to the CommandLine
     *
     * @param commands - command line arguments passed from User
     * @param cmdLine
     * @throws IOException
     */
    private static void configureGridArguments(List<String> commands, CommandLine cmdLine) throws IOException {

        logger.info("This instance considered as a SeLion Grid");

        if (!commands.contains(ROLE)) {
            cmdLine.addArgument(ROLE).addArgument(HUB);
        }

        if (!commands.contains(HUB_CONFIG)) {
            String hubConfig = SeLionGridConstants.HUB_CONFIG;

            // To verify this is SeLion Sauce Grid or not
            if (commands.contains(TYPE) && commands.contains(SAUCE)) {
                hubConfig = SeLionGridConstants.HUB_SAUCE_CONFIG;
                copyFileToUserHome(SeLionGridConstants.JAR_HUB_SAUCE_CONFIG, SeLionGridConstants.HUB_SAUCE_CONFIG);
                copyFileToUserHome(SeLionGridConstants.JAR_SAUCE_CONFIG, SeLionGridConstants.SAUCE_CONFIG);
            } else {
                copyFileToUserHome(SeLionGridConstants.JAR_HUB_CONFIG, SeLionGridConstants.HUB_CONFIG);
            }

            cmdLine.addArgument(HUB_CONFIG).addArgument(hubConfig);
        }
    }

    /**
     * Create mandatory folders and files required to start the Grid / Node
     */
    private static void initialSetup() throws IOException {

        createDirs(SeLionGridConstants.DOWNLOAD_DIR_PATH);
        copyFileToUserHome(SeLionGridConstants.JAR_DOWNLOAD_FILE_PATH, SeLionGridConstants.DOWNLOAD_FILE_PATH);
    }

    /**
     * Create the directory from the path if it is not exist
     *
     * @param path - path of of the directory
     */
    private static void createDirs(String path) {
        File downloadDir = new File(path);

        if (!downloadDir.exists()) {
            boolean isSuccess = downloadDir.mkdirs();
            if (!isSuccess) {
                logger.severe("Unable to create directory, path is " + path);
            }
        }
    }

    /**
     * Copy file from source path to destination location
     *
     * @param sourcePath - Path of the source file
     * @param destPath - Path of the destination file
     * @throws IOException
     */
    private static void copyFileToUserHome(String sourcePath, String destPath) throws IOException {
        File downloadFile = new File(destPath);

        if (!downloadFile.exists()) {
            InputStream stream = JarSpawner.class.getResourceAsStream(sourcePath);
            FileUtils.copyInputStreamToFile(stream, downloadFile);
            logger.info("File copied to " + destPath);
        }
    }

    /**
     * Create logger.properties file in the SELION_HOME directory
     *
     * @throws IOException
     */
    private static void createLoggerFile() throws IOException {

        if (!new File(SeLionGridConstants.LOGGER).exists()) {

            // Need to change the backward slash to forward, so that logger able to locate path in windows
            String logPath = SeLionGridConstants.LOGS_FOLDER_PATH.replace("\\", "/");

            String value = IOUtils.toString(JarSpawner.class.getResourceAsStream(SeLionGridConstants.JAR_LOGGER),
                    "UTF-8");
            value = value.concat("\njava.util.logging.FileHandler.pattern=" + logPath + "selion-grid-%g.log");
            FileUtils.writeStringToFile(new File(SeLionGridConstants.LOGGER), value);
            logger.info("Logger file created successfully. Path is " + SeLionGridConstants.LOGGER);
        }
    }
}
