package com.paypal.selion.grid;

import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

public class ThreadedLauncherTest {
    private Thread thread;
    private RunnableLauncher launcher;
    private String host;
    private int port;

    @BeforeClass
    public void beforeClass() {
        host = new NetworkUtils().getIpOfLoopBackIp4();
        port = PortProber.findFreePort();
        launcher = new ThreadedLauncher(new String[] { "-host", host, "-port", String.valueOf(port) });

        thread = new Thread(launcher);
    }

    @Test
    public void testStartServer() throws Exception {
        thread.start();

        // wait for it to start, max 120 seconds
        int attempts = 0;
        while (!launcher.isRunning() && (attempts < 12)) {
            Thread.sleep(10000);
            attempts += 1;
        }

        if (attempts == 12) {
            fail("ThreadedLauncher did not start the server process");
        }
    }

    @Test(dependsOnMethods = { "testStartServer" })
    public void testShutDown() throws Exception {
        launcher.shutdown();
        assertFalse(launcher.isRunning());
    }}
