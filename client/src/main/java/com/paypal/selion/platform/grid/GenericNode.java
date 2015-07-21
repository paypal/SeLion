package com.paypal.selion.platform.grid;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;
import org.apache.commons.exec.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Represents a node that will support executions for mobile platform for tests that are running locally.
 */
class GenericNode implements LocalServerComponent {
    private boolean isRunning = false;
    private final SimpleLogger logger = SeLionLogger.getLogger();
    private Executor executor;

    @Override
    public void boot(AbstractTestSession testSession) {
        logger.entering(testSession.getPlatform());
        if (isRunning) {
            logger.exiting();
            return;
        }
        if (!(testSession instanceof MobileTestSession)) {
            logger.exiting();
            return;
        }

        String cmd = Config.getConfigProperty(Config.ConfigProperty.SELENIUM_LOCAL_NODE_SPAWN_COMMAND);
        if (StringUtils.isEmpty(cmd)) {
            throw new IllegalArgumentException("Please provide a valid command to start the node");
        }

        logger.info("Running the command :" + cmd);
        CommandLine cmdLine = null;
        String[] parts = cmd.split(" ");

        if (parts.length > 0) {
            cmdLine = new CommandLine(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                cmdLine.addArgument(parts[i]);
            }
        } else {
            cmdLine = new CommandLine(cmd);
        }
        executor = new DefaultExecutor();
        executor.setStreamHandler(new PumpStreamHandler());
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();

        executor = new DefaultExecutor();
        try {
            executor.execute(cmdLine, handler);
            //TODO : We would need to come back here to figure out how to address this wait duration.
            Thread.sleep(TimeUnit.SECONDS.toMillis(45));
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, e.getMessage(),e);
            throw new NodeBootFailedException(e);
        }
        isRunning = true;
    }

    @Override
    public void shutdown() {
        if (isRunning)
            executor.getWatchdog().destroyProcess();

    }

    public static class NodeBootFailedException extends RuntimeException {
        public NodeBootFailedException(Throwable e) {
            super(e);
        }

    }
}
