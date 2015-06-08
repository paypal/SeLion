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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;

import static com.paypal.selion.pojos.SeLionGridConstants.*;

import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.utils.ConfigParser;

/**
 * This is a stand alone class which sets up SeLion dependencies and spawns {@link SeLionGridLauncher}. It also
 * continuously restarts {@link SeLionGridLauncher}, if it exits in an unexpected fashion. Heath checks for the
 * {@link SeLionGridLauncher} process are configurable via the property 'restartCycle' in the SeLion Grid JSON config
 * file.
 */
public class JarSpawner {

    private static final String SEPARATOR = "\n----------------------------------\n";
    private static final String HUB = "hub";
    private static final String NODE = "node";
    private static final String STANDALONE = "standalone";
    private static final String SAUCE = "sauce";

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(JarSpawner.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> commands = Arrays.asList(args);
        JarSpawner spawner = new JarSpawner();

        spawner.initialSetup();

        // setup the SeLion config if the user want to override the default
        if (commands.contains(SELION_CONFIG_ARG)) {
            ConfigParser.setConfigFile(commands.get(commands.indexOf(SELION_CONFIG_ARG) + 1));
        }

        long interval = ConfigParser.parse().getLong("restartCycle", 60000L);
        LOGGER.info("Restart cycle will check every " + interval + " ms");
        while (true) {
            FileDownloader.checkForDownloads();
            if (commands.contains(HELP_ARG) || commands.contains("-h")) {
                spawner.continuouslyRestart(commands, 100, true);
                spawner.printUsageInfo();
                return;
            }
            spawner.continuouslyRestart(commands, interval, false);
            LOGGER.info("Application exited. Restarting it.");
        }
    }

    /**
     * Print the usage of SeLion Grid jar
     */
    private void printUsageInfo() {
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

    /**
     * This method spawns a jar, and waits for it to exit [either cleanly or forcibly]
     * 
     * @param commands
     *            command to execute using jarspawner
     * @param interval
     *            How often should the application check if the jar is still running or if it exit.
     * @param squelch
     *            Whether to make calls to logger.info
     * @throws IOException
     * @throws InterruptedException
     */
    private void continuouslyRestart(List<String> commands, long interval, boolean squelch) throws IOException,
            InterruptedException {
        LOGGER.entering(new Object[] { commands.toString(), interval, squelch });

        CommandLine cmdLine = createJavaCommandForChildProcess(commands);

        if (!squelch) {
            LOGGER.info("Executing command " + cmdLine.toString());
        }

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(new PumpStreamHandler());
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
        executor.execute(cmdLine, handler);

        while (!handler.hasResult()) {
            LOGGER.fine("Child process still running. Going back to sleep.");
            Thread.sleep(interval);
        }

        if (handler.hasResult()) {
            ExecuteException e = handler.getException();
            if (e != null) {
                LOGGER.log(Level.SEVERE, handler.getException().getMessage(), handler.getException());
            }
        }
        if (!squelch) {
            LOGGER.info("Child process quit.");
        }

        LOGGER.exiting();
    }

    /**
     * This method load the default arguments required to spawn SeLion Grid/Node
     *
     * @param commands
     *            command line arguments passed from User
     * @return {@link CommandLine}
     * @throws IOException
     */
    private CommandLine createJavaCommandForChildProcess(List<String> commands) throws IOException {
        LOGGER.entering(commands.toString());

        String type = STANDALONE;
        if (commands.contains(ROLE_ARG) && commands.contains(NODE)) {
            type = NODE;
        }
        if (commands.contains(ROLE_ARG) && commands.contains(HUB)) {
            type = HUB;
        }

        // start command with java
        // TODO what if java is not in the PATH?
        CommandLine cmdLine = CommandLine.parse("java");

        // add the -D system properties
        cmdLine = addSystemPropertiesToCommandLine(type, cmdLine);

        // Set the classpath
        cmdLine = addClassPathToCommanLine(cmdLine);

        // add all of the [options] and [selenium options] specified by user
        for (int i = 0; i < commands.size(); i++) {
            cmdLine.addArgument(commands.get(i), false);
        }

        // add the default hub or node config arguments
        if (NODE.equals(type)) {
            cmdLine = addNodeArgumentsToCommandLine(commands, cmdLine);
        }
        if (HUB.equals(type)) {
            cmdLine = addGridArgumentsToCommandLine(commands, cmdLine);
        }
        // else role is standalone

        // pass the -selionConfig arg, if not a standalone
        if ((!STANDALONE.equals(type)) && (!commands.contains(SELION_CONFIG_ARG))) {
            cmdLine.addArgument(SELION_CONFIG_ARG);
            cmdLine.addArgument(SELION_CONFIG_FILE);
        }

        LOGGER.exiting(cmdLine.toString());
        return cmdLine;
    }

    /**
     * Set the classpath for the child process. Adds all jars from CWD and SELION_HOME_DIR. Does not recurse into sub
     * directories. Filters out duplicates.
     * 
     * @param the
     *            cmdLine we are building
     * @return the changed {@link CommandLine}
     */
    private CommandLine addClassPathToCommanLine(CommandLine cmdLine) {
        Set<String> uniqueClassPathEntries = new LinkedHashSet<String>();

        // find all jars in the SELION_HOME_DIR
        Collection<File> homeFiles = FileUtils.listFiles(new File(SELION_HOME_DIR), new String[] { "jar" }, false);
        for (File file : homeFiles) {
            uniqueClassPathEntries.add(file.getAbsolutePath());
        }

        // find all jars in the current working directory
        Collection<File> localFiles = FileUtils.listFiles(new File(System.getProperty("user.dir")),
                new String[] { "jar" }, false);
        for (File file : localFiles) {
            uniqueClassPathEntries.add(file.getName());
        }

        // remove any duplicates that were already in the existing classpath. add the left-overs
        String classpath = System.getProperty("java.class.path");
        uniqueClassPathEntries.addAll(Arrays.asList(classpath.split(SystemUtils.PATH_SEPARATOR)));

        // build the -cp [option]
        StringBuffer buf = new StringBuffer();
        for (String s : uniqueClassPathEntries) {
            buf.append(s + File.pathSeparatorChar);
        }
        buf.deleteCharAt(buf.length() - 1);

        cmdLine.addArgument("-cp");
        cmdLine.addArgument(buf.toString());
        cmdLine.addArgument(SeLionGridLauncher.class.getName());

        return cmdLine;
    }

    /**
     * Configure required system properties to launch SeLion Grid/Node
     * 
     * @param type
     *            hub, node, or standalone -- used to setup logging
     * @param cmdLine
     *            the cmdLine we are building
     * @return the changed {@link CommandLine}
     * @throws IOException
     */
    private CommandLine addSystemPropertiesToCommandLine(String type, CommandLine cmdLine) throws IOException {
        LOGGER.entering(new Object[] { type, cmdLine.toString() });

        // Nexe, FWD all JVM -D args to the child process
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            // add all system properties the user specified except 'java.util.loggin.config.file'
            if (jvmArg.startsWith("-D")) {
                String propName = jvmArg.substring(2, jvmArg.indexOf("="));
                if (!"java.util.logging.config.file".equalsIgnoreCase(propName)) {
                    cmdLine.addArgument(jvmArg);
                }
            }
        }

        // Setup logging for child process
        // TODO for now, we force the process to use our installed logging.properties files
        createLoggingPropertiesFile(type);
        cmdLine.addArgument("-Djava.util.logging.config.file=" + LOGGING_PROPERTIES_FILE + "." + type);

        // Make sure we setup WebDriver binary paths for the child process
        if (SystemUtils.IS_OS_WINDOWS && System.getProperty("webdriver.ie.driver") == null) {
            cmdLine.addArgument("-Dwebdriver.ie.driver=" + SELION_HOME_DIR + IE_DRIVER);
        }
        if (System.getProperty("webdriver.chrome.driver") == null) {
            cmdLine.addArgument("-Dwebdriver.chrome.driver=" + SELION_HOME_DIR + CHROME_DRIVER);
        }
        if (System.getProperty("phantomjs.binary.path") == null) {
            cmdLine.addArgument("-Dphantomjs.binary.path=" + SELION_HOME_DIR + PHANTOMJS_DRIVER);
        }

        LOGGER.exiting(cmdLine.toString());
        return cmdLine;
    }

    /**
     * Configure SeLion Node related arguments to the CommandLine
     *
     * @param commands
     *            command line arguments passed from User
     * @param cmdLine
     *            the {@link CommandLine} we are building
     * @return the changed {@link CommandLine}
     * @throws IOException
     */
    private CommandLine addNodeArgumentsToCommandLine(List<String> commands, CommandLine cmdLine)
            throws IOException {
        LOGGER.entering(new Object[] { commands.toString(), cmdLine.toString() });

        LOGGER.info("This instance is considered a SeLion Grid Node");

        if (!commands.contains(NODE_CONFIG_ARG)) {
            cmdLine.addArgument(NODE_CONFIG_ARG).addArgument(NODE_CONFIG_FILE);
            copyFileFromResources(NODE_CONFIG_FILE_RESOURCE, NODE_CONFIG_FILE);
        }

        LOGGER.exiting(cmdLine.toString());
        return cmdLine;
    }

    /**
     * Configure SeLion Grid related arguments to the CommandLine
     *
     * @param commands
     *            command line arguments passed from User
     * @param cmdLine
     *            the {@link CommandLine} we are building
     * @return the changed {@link CommandLine}
     * @throws IOException
     */
    private CommandLine addGridArgumentsToCommandLine(List<String> commands, CommandLine cmdLine)
            throws IOException {
        LOGGER.entering(new Object[] { commands.toString(), cmdLine.toString() });

        LOGGER.info("This instance is considered a SeLion Grid Hub");

        if (!commands.contains(HUB_CONFIG_ARG)) {
            String hubConfig = HUB_CONFIG_FILE;

            // To verify this is SeLion Sauce Grid or not
            if (commands.contains(TYPE_ARG) && commands.contains(SAUCE)) {
                hubConfig = HUB_SAUCE_CONFIG_FILE;
                copyFileFromResources(HUB_SAUCE_CONFIG_FILE_RESOURCE, HUB_SAUCE_CONFIG_FILE);
                copyFileFromResources(SAUCE_CONFIG_FILE_RESOURCE, SAUCE_CONFIG_FILE);
            } else {
                copyFileFromResources(HUB_CONFIG_FILE_RESOURCE, HUB_CONFIG_FILE);
            }

            cmdLine.addArgument(HUB_CONFIG_ARG).addArgument(hubConfig);
        }

        LOGGER.exiting(cmdLine.toString());
        return cmdLine;
    }

    /**
     * Create mandatory folders and files required to start the Grid / Node
     * 
     * @throws IOException
     */
    private void initialSetup() {
        LOGGER.entering();

        try {
            FileUtils.forceMkdir(new File(DOWNLOADS_DIR));
            FileUtils.forceMkdir(new File(LOGS_DIR));
            copyFileFromResources(DOWNLOAD_JSON_FILE_RESOURCE, DOWNLOAD_JSON_FILE);
            copyFileFromResources(SELION_CONFIG_FILE_RESOURCE, SELION_CONFIG_FILE);
            ConfigParser.setConfigFile(SELION_CONFIG_FILE);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to install required components. Please make sure you have write "
                    + "access to " + SELION_HOME_DIR + " or specify a different home directory with -DselionHome.", e);
        }

        LOGGER.exiting();
    }

    /**
     * Copy file from source path to destination location
     *
     * @param sourcePath
     *            Resource path of the source file
     * @param destPath
     *            Path of the destination file
     * @throws IOException
     */
    private void copyFileFromResources(String sourcePath, String destPath) throws IOException {
        LOGGER.entering(new Object[] { sourcePath, destPath });

        File downloadFile = new File(destPath);

        if (!downloadFile.exists()) {
            InputStream stream = JarSpawner.class.getResourceAsStream(sourcePath);
            FileUtils.copyInputStreamToFile(stream, downloadFile);
            LOGGER.info("File copied to " + destPath);
        }
        LOGGER.exiting();
    }

    /**
     * Create logging.properties file in the {@link SeLionGridConstants#SELION_HOME_DIR}
     *
     * @throws IOException
     */
    private void createLoggingPropertiesFile(String type) throws IOException {
        LOGGER.entering(type);

        String installedFile = LOGGING_PROPERTIES_FILE + "." + type;
        if (!new File(installedFile).exists()) {

            // Need to change the backward slash to forward, so that logger able to locate path in windows
            String logPath = LOGS_DIR.replace("\\", "/");

            String value = IOUtils.toString(
                    JarSpawner.class.getResourceAsStream(LOGGING_PROPERTIES_FILE_RESOURCE), "UTF-8");
            value = value.concat("\njava.util.logging.FileHandler.pattern=" + logPath + "selion-grid-" + type
                    + "-%g.log");
            FileUtils.writeStringToFile(new File(installedFile), value);
            LOGGER.info("Logger file created successfully. Path is " + installedFile);
        }

        LOGGER.exiting();
    }
}
