package com.paypal.selion.platform.grid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
//import java.util.logging.Handler;
import java.util.logging.Level;
//import java.util.logging.Logger;


import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.common.exception.GridException;

import io.selendroid.SelendroidConfiguration;
import io.selendroid.SelendroidLauncher;
import io.selendroid.grid.SelendroidSessionProxy;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local android driver
 * node.
 * 
 */
public class LocalSelendroidNode implements LocalServerComponent {

    protected boolean isRunning = false;
    private SelendroidLauncher server = null;
    private SelendroidConfiguration sconfig = new SelendroidConfiguration();
    private SimpleLogger logger = SeLionLogger.getLogger();

    @Override
    public void startUp(WebDriverPlatform platform) {
        logger.entering(platform);
        if (isRunning) {
            logger.exiting();
            return;
        }
        if (platform != WebDriverPlatform.ANDROID) {
            logger.exiting();
            return;
        }
        String host = "localhost";
        String hubPort = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
        String registrationUrl = String.format("http://%s:%s/grid/register", host, hubPort);

        try {
            int port = new LocalGridConfigFileParser().getPort() + 2;
            startSelendroidDriverNode(port);
            waitForNodeToComeUp(port);
            isRunning = true;
            logger.log(Level.INFO, "Attached SelendroidDriver node to local hub " + registrationUrl);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new GridException("Failed to start a local Selendroid Node", e);
        }
    }

    @Override
    public void shutdown() {
        if (server != null) {
            try {
                server.stopSelendroid();
                logger.log(Level.INFO, "Local Selendroid Node has been stopped");
            } catch (Exception e) {
                String errorMsg = "An error occured while attempting to shut down the local Selendroid Node. Root cause: ";
                logger.log(Level.SEVERE, errorMsg, e);
            }
        }

    }

    private JSONObject extractObject(HttpResponse resp) throws IOException, JSONException {
        logger.entering(resp);
        BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
        StringBuilder s = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            s.append(line);
        }
        rd.close();
        logger.exiting(s.toString());
        return new JSONObject(s.toString());
    }

    public boolean wasNodeSpawned(int port) {
        logger.entering(port);
        String endPoint = String.format("http://localhost:%d/wd/hub/status", port);

        CloseableHttpClient client = HttpClientBuilder.create().build();
        try {
            URL url = new URL(endPoint);
            URL api = new URL("http://" + url.getHost() + ":" + url.getPort() + "/wd/hub/status");
            HttpHost host = new HttpHost(api.getHost(), api.getPort());

            BasicHttpRequest r = new BasicHttpRequest("GET", api.toExternalForm());

            HttpResponse response = client.execute(host, r);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new GridException("hub down or not responding. Reason : "
                        + response.getStatusLine().getReasonPhrase());
            }
            JSONObject o = extractObject(response);
            boolean status = (o.getInt("status") == 0);
            logger.exiting(status);
            return status;
        } catch (Exception e) {
            throw new GridException("Problem querying the status", e);
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    public void waitForNodeToComeUp(int port) { // this is fine
        logger.entering(port);
        for (int i = 0; i < 5; i++) {
            try {
                // Sleep for 10 seconds.
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                throw new GridException(e.getMessage(), e);
            }
            if (wasNodeSpawned(port)) {
                logger.exiting();
                return;
            }
        }
        throw new GridException(
                "Encountered problems when attempting to register the Selendroid Node to the local Grid");
    }

    private void startSelendroidDriverNode(int port) throws Exception {
        logger.entering(port);
        List<String> args = new ArrayList<String>();
        args.add("-deviceScreenshot");
        args.add("-logLevel");
        args.add("VERBOSE");
        args.add("-hub");
        args.add("http://127.0.0.1:" + Config.getIntConfigProperty(ConfigProperty.SELENIUM_PORT) + "/grid/register");
        args.add("-port");
        args.add(Integer.toString(port));

        String autFolder = Config.getConfigProperty(ConfigProperty.SELENIUM_NATIVE_APP_FOLDER);
        if (autFolder != null && !autFolder.trim().isEmpty()) {
            args.add("-aut");
            args.add(autFolder + "/paypal-here-2-0-0-2037-qa-qa.apk");
        }

        args.add("-proxy");
        args.add(SelendroidSessionProxy.class.getCanonicalName());
        args.add("-host");
        args.add("127.0.0.1");

        sconfig = SelendroidConfiguration.create(args.toArray(new String[args.size()]));
        server = new SelendroidLauncher(sconfig);

        //HACK :: put the RootLogger back into the original state
         // remove all handlers first
        Handler[] handlers = Logger.getLogger("").getHandlers();
        Level level = Logger.getLogger("").getLevel();

        for (Handler handler : Logger.getLogger("").getHandlers()) {
         Logger.getLogger("").removeHandler(handler);
         }
         // put the original ones back
         for (Handler handler : handlers) {
         Logger.getLogger("").addHandler(handler);
         }
         // reset the log level
         Logger.getLogger("").setLevel(level);

        server.launchSelendroid();
        logger.exiting();
    }

}