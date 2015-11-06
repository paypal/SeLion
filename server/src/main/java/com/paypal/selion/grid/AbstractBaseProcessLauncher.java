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
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.os.ProcessUtils;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.grid.ProcessLauncherOptions.ProcessLauncherOptionsImpl;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;
import com.paypal.selion.utils.process.ProcessHandlerFactory;

/**
 * An abstract {@link RunnableLauncher} for spinning nodes or hubs as a sub process. Supports continuously restarts of
 * the sub process. Provides utility methods for working with Java CLASSPATH's and System Properties.
 */
abstract class AbstractBaseProcessLauncher extends AbstractBaseLauncher {
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(AbstractBaseProcessLauncher.class);

    private DefaultExecuteResultHandler handler;
    private ProcessLauncherOptions launcherOptions;
    private boolean shutdownCalled;
    private final SeLionExecuteWatchDog watchdog = new SeLionExecuteWatchDog(ExecuteWatchdog.INFINITE_TIMEOUT);

    /*
     * The command line to run
     */
    private CommandLine cmdLine;

    /**
     * Get the sub-process pid as an integer
     */
    int getProcessPID() {
        return watchdog.getProcessId();
    }

    class SeLionExecuteWatchDog extends ExecuteWatchdog {
        boolean starting;
        Process process;

        SeLionExecuteWatchDog(long timeout) {
            super(timeout);
        }

        public int getProcessId() {
            if (SystemUtils.IS_OS_WINDOWS) {
                //TODO implement me 
                throw new IllegalStateException("Implementation missing.. No means to detect sub process pid on Windows");
            }
            try {
                Field f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                Integer pid = (Integer) f.get(process);
                return pid;
            } catch (Exception e) { //NOSONAR
                throw new RuntimeException("Couldn't detect sub process pid", e);
            }
        }

        @Override
        public synchronized void start(Process process) {
            this.process = process;
            starting = false;
            super.start(process);
        }

        public void reset() {
            starting = true;
        }

        private void waitForProcessStarted() {
            while (starting) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new WebDriverException(e);
                }
            }
        }

        private void waitForTerminationAfterDestroy(int duration, TimeUnit unit) {
            long end = System.currentTimeMillis() + unit.toMillis(duration);
            while (isRunning() && System.currentTimeMillis() < end) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new WebDriverException(e);
                }
            }
        }

        public void destroyProcessForcefully() {
            ProcessUtils.killProcess(process);
        }
    }

    Thread shutDownHook = new Thread() {
        @Override
        public void run() {
            shutdown();
        }
    };

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
        init(args, null);
    }

    /**
     * Init with the supplied dash arguments and the supplied {@link ProcessLauncherOptions}
     * 
     * @param args
     * @param options
     */
    void init(String[] args, ProcessLauncherOptions options) {
        setLauncherOptions(options);

        InstallHelper.firstTimeSetup();

        List<String> commands = new LinkedList<String>(Arrays.asList(args));
        setCommands(commands);

        if (Arrays.asList(args).contains(SELION_NOCONTINUOUS_ARG)) {
            getLauncherOptions().setContinuouslyRestart(false);
        }

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
     * @throws IOException
     * @throws InterruptedException
     */
    final void continuouslyRestart(long interval) throws IOException, InterruptedException {
        LOGGER.entering(new Object[]{interval});

        while (true) {
            if (!isInitialized()) {
                FileDownloader.checkForDownloads(getType(), getLauncherOptions()
                        .isFileDownloadCheckTimeStampOnInvocation(), getLauncherOptions()
                        .isFileDownladCleanupOnInvocation());
            }

            startProcess(false);

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
            LOGGER.info("Child process quit. Restarting it.");
            setInitialized(false);
        }
    }

    /**
     * Start a process based on the commands provided.
     *
     * @param squelch
     *            Whether to show command executed as a logger.info message
     * @throws IOException
     */
    void startProcess(boolean squelch) throws IOException {
        LOGGER.entering(squelch);

        if (!squelch) {
            LOGGER.info("Executing command " + cmdLine.toString());
        }

        watchdog.reset();
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWatchdog(watchdog);
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
                FileDownloader.checkForDownloads(getType(), getLauncherOptions()
                        .isFileDownloadCheckTimeStampOnInvocation(), getLauncherOptions()
                        .isFileDownladCleanupOnInvocation());
                setInitialized(true);
            }

            if (getCommands().contains(HELP_ARG) || getCommands().contains("-h")) {
                startProcess(true);
                handler.waitFor();
                printUsageInfo();
                return;
            }

            if (getLauncherOptions().isContinuouslyRestart()) {
                long interval = ConfigParser.parse().getLong("restartCycle", 60000L);
                LOGGER.info("Restart cycle will check every " + interval + " ms");
                continuouslyRestart(interval);
            }

            // non-continuous process.
            startProcess(false);
            handler.waitFor();
        } catch (InterruptedException | IOException e) {
            //log the exception and exit, if shutdown was not called
            if (!shutdownCalled) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                System.exit(1);
            }
        }
    }

    abstract void printUsageInfo();

    /**
     * Shuts down the instance represented by this launcher. Uses the {@link ProcessHandlerFactory} to find sub
     * processes.
     */
    public void shutdown() {
        shutdownCalled = true;
        if (isRunning()) {
            watchdog.waitForProcessStarted();
            watchdog.destroyProcess();
            watchdog.waitForTerminationAfterDestroy(2, SECONDS);
            if (isRunning()) {
                watchdog.destroyProcessForcefully();
                watchdog.waitForTerminationAfterDestroy(1, SECONDS);
                if (isRunning()) {
                    LOGGER.severe(String.format("Unable to kill process with PID %s", watchdog.getProcessId()));
                }
            }
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
        if (getLauncherOptions().isIncludeJarsInSeLionHomeDir()) {
            Collection<File> homeFiles = FileUtils.listFiles(new File(SeLionConstants.SELION_HOME_DIR),
                    new String[] { "jar" }, false);
            for (File file : homeFiles) {
                if (file.getName().startsWith(jarNamePrefix) || StringUtils.isEmpty(jarNamePrefix)) {
                    uniqueClassPathEntries.add(file.getAbsolutePath());
                }
            }
        }

        // find all jars in the current working directory
        if (getLauncherOptions().isIncludeJarsInPresentWorkingDir()) {
            Collection<File> localFiles = FileUtils.listFiles(SystemUtils.getUserDir(), new String[] { "jar" }, false);
            for (File file : localFiles) {
                uniqueClassPathEntries.add(file.getName());
            }
        }

        // remove any duplicates that were already in the existing classpath. add the left-overs
        if (getLauncherOptions().isIncludeParentProcessClassPath()) {
            String classpath = SystemUtils.JAVA_CLASS_PATH;
            uniqueClassPathEntries.addAll(Arrays.asList(classpath.split(SystemUtils.PATH_SEPARATOR)));
        }

        // build the -cp [option]
        StringBuilder buf = new StringBuilder();
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
        args.addAll(Arrays.asList(getPresentJavaSystemPropertiesArguments()));

        // Setup logging for child process
        args.addAll(Arrays.asList(getLoggingSystemPropertiesArguments()));

        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }

    private String[] getLoggingSystemPropertiesArguments() throws IOException {
        LOGGER.entering();
        List<String> args = new LinkedList<String>();

        if (getLauncherOptions().isSetupLoggingForJavaSubProcess()) {
            InstallHelper.createLoggingPropertiesFile(getType());
            args.add("-Djava.util.logging.config.file=" + LOGGING_PROPERTIES_FILE + "." + getType().getFriendlyName());
        }
        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }

    private String[] getPresentJavaSystemPropertiesArguments() {
        LOGGER.entering();
        List<String> args = new LinkedList<String>();

        if (getLauncherOptions().isIncludeJavaSystemProperties()) {
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
        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }

    <T extends ProcessLauncherOptions> void setLauncherOptions(T launcherOptions) {
        this.launcherOptions = launcherOptions;
    }

    @Override
    ProcessLauncherOptions getLauncherOptions() {
        if (launcherOptions == null) {
            launcherOptions = new ProcessLauncherOptionsImpl();
        }
        return launcherOptions;
    }

    final void addJVMShutDownHook() {
        shutDownHook.setName("SeLionJarSpawnerShutdownHook");
        Runtime.getRuntime().addShutdownHook(shutDownHook);
    }

}
