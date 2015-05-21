package com.paypal.selion.grid;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Test(singleThreaded = true, groups = { "selendroid" })
public class SelendroidJarSpawnerTest {
    private Thread thread;
    private RunnableLauncher spawner;
    private String host;
    private int port;

    @BeforeClass
    public void beforeClass() {
        host = new NetworkUtils().getIpOfLoopBackIp4();
        port = PortProber.findFreePort();
        spawner = new SelendroidJarSpawner(new String[] { "-noContinuousRestart", "-host", host, "-port",
                String.valueOf(port) });
        thread = new Thread(spawner);
    }

    @Test
    public void testStartServer() throws Exception {
        thread.start();

        // wait for it to start, max 30 seconds
        int attempts = 0;
        while (!getServerStatus() && (attempts < 3)) {
            Thread.sleep(10000);
            attempts += 1;
        }

        if (attempts == 3) {
            fail("SelendroidJarSpawner did not start the server process");
        }
    }

    @Test(dependsOnMethods = { "testStartServer" })
    public void testShutDown() throws Exception {
        spawner.shutdown();
        assertFalse(getServerStatus());
    }

    private boolean getServerStatus() throws MalformedURLException, IOException {
        boolean hubStatus = false;
        String url = String.format("http://%s:%d/wd/hub/status", host, port);
        URLConnection hubConnection = new URL(url).openConnection();
        InputStream isr = null;
        BufferedReader br = null;
        try {
            isr = hubConnection.getInputStream();
            br = new BufferedReader(new InputStreamReader(isr));
            StringBuffer information = new StringBuffer();
            String eachLine = null;
            while ((eachLine = br.readLine()) != null) {
                information.append(eachLine);
            }
            JsonObject fullResponse = new JsonParser().parse(information.toString()).getAsJsonObject();
            if (fullResponse != null) {
                hubStatus = (fullResponse.get("status").getAsInt() == 0) ? true : false;
            }
        } catch (ConnectException e) {
            hubStatus = false;
        } finally {
            IOUtils.closeQuietly(isr);
            IOUtils.closeQuietly(br);
        }

        return hubStatus;
    }
}
