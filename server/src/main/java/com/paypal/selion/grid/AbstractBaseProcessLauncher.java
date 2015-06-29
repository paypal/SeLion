/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.node.servlets.NodeForceRestartServlet;
import com.paypal.selion.pojos.ProcessInfo;
import com.paypal.selion.utils.ConfigParser;
import com.paypal.selion.utils.process.ProcessHandler;
import com.paypal.selion.utils.process.ProcessHandlerException;
import com.paypal.selion.utils.process.ProcessHandlerFactory;

/**
 * An abstract {@link RunnableLauncher} for spinning nodes or hubs as a sub process. Supports continuously restarts of
 * the sub process. Provides utility methods for working with Java CLASSPATH's and System Properties.
 */
abstract class AbstractBaseProcessLauncher extends AbstractBaseLauncher {
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(AbstractBaseProcessLauncher.class);

    private DefaultExecuteResultHandler handler;
    private ProcessLauncherOptions processLauncherOptions;

    /*
     * The command line to run
     */
    private CommandLine cmdLine;

    /**
     * @return the command line to invoke as a {@link CommandLine}
     */
    CommandLine getCommandLine() {
        return cmdLine;
    }

    /**
     * Set the command line to invoke
     * 
     * @param commandLine
     *            a {@link CommandLine} to invoke
     */
    void setCommandLine(CommandLine commandLine) {
        this.cmdLine = commandLine;
    }

    /**
     * Init with the supplied dash arguments and the default {@link ProcessLauncherOptions}
     * 
     * @param args
     */
    void init(String[] args) {
        init(args, new ProcessLauncherOptions());
    }

    /**
     * Init with the supplied dash arguments and the supplied additional {@link ProcessLauncherOptions}
     * 
     * @param args
     * @param options
     */
    void init(String[] args, ProcessLauncherOptions options) {
        processLauncherOptions = options;

        InstallHelper.firstTimeSetup();

        List<String> commands = new LinkedList<String>(Arrays.asList(args));
        setCommands(commands);

        commands = new LinkedList<String>(Arrays.asList(args));
        processLauncherOptions.setContinuouslyRestart(!Arrays.asList(args).contains(SELION_NOCONTINUOS_ARG));
        // setup the SeLion config if the user want to override the default
        if (commands.contains(SELION_CONFIG_ARG)) {
            ConfigParser.setConfigFile(commands.get(commands.indexOf(SELION_CONFIG_ARG) + 1));
        }
    }

    /**
     * This method spawns a jar, and waits for it to exit [either cleanly or forcibly]
     *
     * @param interval
     *            How often should the application check if the command is still running or if it exit.
     * @param squelch
     *            Whether to make calls to logger.info
     * @throws IOException
     * @throws InterruptedException
     */
    final void continuouslyRestart(long interval, boolean squelch) throws IOException, InterruptedException {
        LOGGER.entering(new Object[] { interval, squelch });

        startProcess(squelch);

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
        LOGGER.info("Child process quit.");

        LOGGER.exiting();
    }

    /**
     * Start a process based on the commands provided.
     *
     * @param squelch
     *            Whether to show command executed as a logger.info message
     * @param cmdLine
     *            the {@link CommandLine} to run
     */
    void startProcess(boolean squelch) throws IOException {
        LOGGER.entering(squelch);

        if (!squelch) {
            LOGGER.info("Executing command " + cmdLine.toString());
        }

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(new PumpStreamHandler());
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        handler = new DefaultExecuteResultHandler();
        executor.execute(cmdLine, handler);

        LOGGER.exiting();
    }

    @Override
    public void run() {
        try {
            if (!isInitialized()) {
                addJVMShutDownHook();
                FileDownloader.checkForDownloads(getType());
                setInitialized(true);
            }

            List<String> commands = getCommands();

            if (commands.contains(HELP_ARG) || commands.contains("-h")) {
                startProcess(true);
                handler.waitFor();
                printUsageInfo();
                return;
            }

            if (processLauncherOptions.isContinuouslyRestart()) {
                long interval = ConfigParser.parse().getLong("restartCycle", 60000L);
                LOGGER.info("Restart cycle will check every " + interval + " ms");
                while (true) {
                    if (!isInitialized()) {
                        FileDownloader.checkForDownloads(getType(),
                                processLauncherOptions.isFileDownloadCheckTimeStampOnInvocation(),
                                processLauncherOptions.isFileDownladCleanupOnInvocation());
                    }
                    continuouslyRestart(interval, false);
                    LOGGER.info("Application exited. Restarting it.");
                    setInitialized(false);
                }
            }

            // non-continuous process.
            startProcess(false);
            handler.waitFor();
        } catch (InterruptedException | IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            System.exit(1);
        }
    }

    abstract void printUsageInfo();

    /**
     * Shuts down the instance represented by this launcher. First, attempts to call the {@link NodeForceRestartServlet}
     * which must be available on the instance. Otherwise, the {@link ProcessHandlerFactory} is used as a fallback
     * mechanism.
     */
    public void shutdown() {
        if (!isRunning()) {
            return;
        }
        // TODO This might not be needed anymore
        // clean up any stale processes
        try {
            ProcessHandler ph = ProcessHandlerFactory.createInstance();
            List<ProcessInfo> processes = ph.potentialProcessToBeKilled();
            ph.killProcess(processes);
        } catch (ProcessHandlerException e) {
            // ignore
        }

        // if shutdown() was called by something other than the shutdown hook, we don't need the shutdown hook anymore
        try {
            if (shutDownHook != null) {
                Runtime.getRuntime().removeShutdownHook(shutDownHook);
            }
        } catch (IllegalStateException e) {
            // ignore.. happens when the shutdown hook is in use, that's okay
        }

    }

    /**
     * Get the classpath for the child process. Determines all jars from CWD and SELION_HOME_DIR. Does not recurse into
     * sub directories. Filters out duplicates.
     *
     * @param jarNamePrefix
     *            when adding jars from the {@link SeLionConstants#SELION_HOME_DIR}, only consider jars whose names
     *            start with this prefix. If <code>null</code>, or <code>""</code> consider all jars.
     * @param mainClass
     *            the main() Class to invoke
     * @return an array of {@link String} which represents the CLASSPATH to pass
     */
    String[] getJavaClassPathArguments(String jarNamePrefix, String mainClass) {
        LOGGER.entering();
        Set<String> uniqueClassPathEntries = new LinkedHashSet<String>();

        // find all jars in the SELION_HOME_DIR
        if (processLauncherOptions.isIncludeJarsInSeLionHomeDir()) {
            Collection<File> homeFiles = FileUtils.listFiles(new File(SeLionConstants.SELION_HOME_DIR),
                    new String[] { "jar" }, false);
            for (File file : homeFiles) {
                if (file.getName().startsWith(jarNamePrefix) || StringUtils.isEmpty(jarNamePrefix)) {
                    uniqueClassPathEntries.add(file.getAbsolutePath());
                }
            }
        }

        // find all jars in the current working directory
        if (processLauncherOptions.isIncludeJarsInPresentWorkingDir()) {
            Collection<File> localFiles = FileUtils.listFiles(SystemUtils.getUserDir(), new String[] { "jar" }, false);
            for (File file : localFiles) {
                uniqueClassPathEntries.add(file.getName());
            }
        }

        // remove any duplicates that were already in the existing classpath. add the left-overs
        if (processLauncherOptions.isIncludeParentProcessClassPath()) {
            String classpath = SystemUtils.JAVA_CLASS_PATH;
            uniqueClassPathEntries.addAll(Arrays.asList(classpath.split(SystemUtils.PATH_SEPARATOR)));
        }

        // build the -cp [option]
        StringBuffer buf = new StringBuffer();
        for (String s : uniqueClassPathEntries) {
            buf.append(s + SystemUtils.PATH_SEPARATOR);
        }
        buf.deleteCharAt(buf.length() - 1);

        String[] args = new String[3];
        args[0] = ("-cp");
        args[1] = buf.toString();
        args[2] = mainClass;

        LOGGER.exiting(args);
        return args;
    }

    /**
     * Get required system properties to launch the sub process
     *
     * @return an array of {@link String} which represents the System properties to pass
     * @throws IOException
     */
    String[] getJavaSystemPropertiesArguments() throws IOException {
        LOGGER.entering();
        List<String> args = new LinkedList<String>();

        // Next, FWD all JVM -D args to the child process
        if (processLauncherOptions.isIncludeJavaSystemProperties()) {
            for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                // add all system properties the user specified except 'java.util.loggin.config.file'
                if (jvmArg.startsWith("-D")) {
                    String propName = jvmArg.substring(2, jvmArg.indexOf("="));
                    if (!"java.util.logging.config.file".equalsIgnoreCase(propName)) {
                        args.add(jvmArg);
                    }
                }
            }
        }

        // Setup logging for child process
        if (processLauncherOptions.isSetupLoggingForJavaSubProcess()) {
            InstallHelper.createLoggingPropertiesFile(getType());
            args.add("-Djava.util.logging.config.file=" + LOGGING_PROPERTIES_FILE + "." + getType().getFriendlyType());
        }

        // include the WebDriver binary paths for Chromedriver, IEDriver, and PhantomJs
        if (processLauncherOptions.isIncludeWebDriverBinaryPaths()
                && (getType().equals(InstanceType.SELENIUM_NODE) || getType().equals(InstanceType.SELENIUM_STANDALONE))) {
            // Make sure we setup WebDriver binary paths for the child process
            if (SystemUtils.IS_OS_WINDOWS && System.getProperty("webdriver.ie.driver") == null) {
                args.add("-Dwebdriver.ie.driver=" + SeLionConstants.SELION_HOME_DIR + SeLionConstants.IE_DRIVER);
            }
            if (System.getProperty("webdriver.chrome.driver") == null) {
                args.add("-Dwebdriver.chrome.driver=" + SeLionConstants.SELION_HOME_DIR + SeLionConstants.CHROME_DRIVER);
            }
            if (System.getProperty("phantomjs.binary.path") == null) {
                args.add("-Dphantomjs.binary.path=" + SeLionConstants.SELION_HOME_DIR
                        + SeLionConstants.PHANTOMJS_DRIVER);
            }
        }

        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }

    Thread shutDownHook = new Thread() {
        @Override
        public void run() {
            shutdown();
        }
    };

    final void addJVMShutDownHook() {
        shutDownHook.setName("SeLionJarSpawnerShutdownHook");
        Runtime.getRuntime().addShutdownHook(shutDownHook);
    }

}
