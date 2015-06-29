package com.paypal.selion.grid;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@Test(singleThreaded = true, groups = { "iosdriver" })
public class IOSDriverJarSpawnerTest {
    private Thread thread;
    private RunnableLauncher spawner;
    private String host;
    private int port;

    @BeforeClass
    public void beforeClass() {
        host = new NetworkUtils().getIpOfLoopBackIp4();
        port = PortProber.findFreePort();
        spawner = new IOSDriverJarSpawner(new String[] { "-noContinuousRestart", "-host", host, "-port",
                String.valueOf(port) });
        thread = new Thread(spawner);
    }

    @Test
    public void testStartServer() throws Exception {
        thread.start();

        // wait for it to start, max 120 seconds
        int attempts = 0;
        while (!spawner.isRunning() && (attempts < 12)) {
            Thread.sleep(10000);
            attempts += 1;
        }

        if (attempts == 3) {
            fail("IOSDriverJarSpawner did not start the server process");
        }
    }

    @Test(dependsOnMethods = { "testStartServer" })
    public void testShutDown() throws Exception {
        spawner.shutdown();
        assertFalse(spawner.isRunning());
    }
}
