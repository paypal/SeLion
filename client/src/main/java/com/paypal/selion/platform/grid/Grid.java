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

package com.paypal.selion.platform.grid;

import io.selendroid.SelendroidCapabilities;
import io.selendroid.SelendroidDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.client.uiamodels.impl.augmenter.IOSDriverAugmenter;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.BrowserFlavors;
import com.paypal.selion.platform.grid.browsercapabilities.DesiredCapabilitiesFactory;
import com.paypal.selion.platform.remote.SeLionDriverAugmenter;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Utility class making it easy to write tests based on Selenium WebDriver in a multi-thread context.
 * 
 */
public final class Grid {

    private static SimpleLogger logger = SeLionLogger.getLogger();
    private static ThreadLocal<ScreenShotRemoteWebDriver> threadLocalWebDriver = new ThreadLocal<ScreenShotRemoteWebDriver>();
    private static ThreadLocal<AbstractTestSession> threadTestSession = new ThreadLocal<AbstractTestSession>();

    private static boolean isLocalRC = Boolean.parseBoolean(Config
            .getConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY));

    static {
        Logger.getLogger("").setLevel(Level.OFF);
    }

    private Grid() {
        // Utility class. So hide the constructor
    }

    static ThreadLocal<ScreenShotRemoteWebDriver> getThreadLocalWebDriver() {
        return threadLocalWebDriver;
    }

    static ThreadLocal<AbstractTestSession> getThreadLocalTestSession() {
        return threadTestSession;
    }

    public static RemoteIOSDriver iOSDriver() {
        return IOSDriverAugmenter.getIOSDriver((RemoteWebDriver)threadLocalWebDriver.get().getWrappedDriver());
    }
    
    public static SelendroidDriver selendroidDriver() throws Exception {
        RemoteWebDriver driver = (RemoteWebDriver) threadLocalWebDriver.get().getWrappedDriver();
        return SeLionDriverAugmenter.selendroidDriver(driver, DesiredCapabilitiesFactory.getCapabilities(BrowserFlavors.GENERIC));
    }
    
    public static long getNewTimeOut() {
        logger.entering();
        String stringTimeOut = ConfigManager.getConfig(getTestSession().getXmlTestName()).getConfigProperty(
                ConfigProperty.EXECUTION_TIMEOUT);
        long returnValue = Long.parseLong(stringTimeOut.trim());
        logger.exiting(returnValue);
        return returnValue;
    }

    public static ScreenShotRemoteWebDriver driver() {
        return threadLocalWebDriver.get();
    }

    /**
     * @return - A {@link AbstractTestSession} object that represents the basic configurations for the currently running
     *         <code>{@literal @}WebTest</code>/<code>{@literal @}MobileTest</code> annotated method.
     */
    public static AbstractTestSession getTestSession() {
        return threadTestSession.get();
    }

    /**
     * @return - A {@link MobileTestSession} object that represents the App configurations for the currently running
     *         <code>{@literal @}MobileTest</code> annotated method.
     */
    public static MobileTestSession getMobileTestSession() {
        AbstractTestSession testSession = getTestSession();
        if (!(testSession instanceof MobileTestSession)) {
            testSession = null;
        }

        return (MobileTestSession) testSession;
    }

    /**
     * @return - A {@link WebTestSession} object that represents the Web configurations for the currently running
     *         <code>{@literal @}WebTest</code> annotated method.
     */
    public static WebTestSession getWebTestSession() {
        AbstractTestSession testSession = getTestSession();
        if (!(testSession instanceof WebTestSession)) {
            testSession = null;
        }

        return (WebTestSession) testSession;
    }

    /**
     * runOnLocalRC()
     * 
     * @return localRC, Seleniuum run local if localRC=true, Selenium will run remote if localRC=false example:
     *         Grid.runOnLocalRC()
     */
    public static boolean getRunOnLocalRC() {
        return isLocalRC;
    }

    /**
     * Helper method that can be used to load a URL in a browser.
     * 
     * @param url
     *            - The url of the web application that needs to be opened.
     */
    public static void open(String url) {
        Grid.driver().get(url);
    }

    /**
     * Converts a http response into a JSON Object
     * 
     * @param resp
     *            - a http response obtained from executing a rqst against a host
     * @return - An object of type {@link JSONObject}
     * @throws IOException
     * @throws JSONException
     */
    private static JSONObject extractObject(HttpResponse resp) throws IOException, JSONException {
        logger.entering(resp);
        BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
        StringBuilder s = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            s.append(line);
        }
        rd.close();
        JSONObject objToReturn = new JSONObject(s.toString());
        logger.exiting(objToReturn);
        return objToReturn;
    }

    /**
     * For a given Session ID against a host on a particular port, this method returns the remote webdriver node and the
     * port to which the execution was redirected to by the hub.
     * 
     * @param hostName
     *            - The name of the hub machine
     * @param port
     *            - The port on which the hub machine is listening to
     * @param session
     *            - An object of type {@link SessionId} which represents the current session for a user.
     * @return - An array of string wherein the first element represents the remote node's name and the second element
     *         represents its port.
     */
    static RemoteNodeInformation getRemoteNodeInfo(String hostName, int port, SessionId session) {
        logger.entering(new Object[] { hostName, port, session });
        RemoteNodeInformation node = null;
        String errorMsg = "Failed to acquire remote webdriver node and port info. Root cause: ";

        try {
            HttpHost host = new HttpHost(hostName, port);
            CloseableHttpClient client = HttpClientBuilder.create().build();
            URL sessionURL = new URL("http://" + hostName + ":" + port + "/grid/api/testsession?session=" + session);
            BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST", sessionURL.toExternalForm());
            CloseableHttpResponse response = client.execute(host, r);
            JSONObject object = extractObject(response);
            URL myURL = new URL(object.getString("proxyId"));
            if ((myURL.getHost() != null) && (myURL.getPort() != -1)) {
                node = new RemoteNodeInformation(myURL.getHost(), myURL.getPort());
            }
        } catch (Exception e) {
            logger.log(Level.FINE, errorMsg, e);
            // Just log the exception at finer level but dont throw any exceptions
            // because this is just value added information.
        }
        logger.exiting(node);
        return node;
    }
}
