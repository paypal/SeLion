/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 eBay Software Foundation                                                                     |
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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.openqa.selenium.remote.RemoteWebDriver;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * <P>
 * By default the WebDriver Session will get timed out based on the value of "timeout" configuration variable in
 * localnode.json file. In some cases, testcase need more time to do their non ui task before coming back to UI related
 * operation. If the non UI task takes more time than the configuration timeout value, we will get RuntimeException with
 * message "Session timed out".
 * <P>
 * 
 * 
 * {@link WebDriverSessionHandler} is used to hold WebDriver session while performing non UI operations.
 * 
 * <P>
 * Code Sample :
 * </P>
 * 
 * <pre>
 *       Grid.driver().get(https://www.paypal.com);
 *       
 *       Grid.driver().findElementById("login_email").sendKeys("exampleUser@paypal.com");
 *       Grid.driver().findElementById("login_password").sendKeys("123Abcdef");
 *       Grid.driver().findElementByName("submit.x").click();
 *       
 *       WebDriverSessionHandler m = new WebDriverSessionHandler(Grid.driver());
 *       m.start();
 *       
 *       //<<include non-ui operation here>>
 *       
 *       m.stop();
 *       Grid.driver().findElementByLinkText("Send Money").click();
 *        
 *       assertTrue(Grid.driver().getTitle().equals("Send Money - PayPal"));
 * </pre>
 * 
 */
public class WebDriverSessionHandler {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private RemoteWebDriver driver;
    private Future<String> result;
    private WebDriverCaller webDriverCaller;

    private volatile boolean bStartSession = false;

    private class WebDriverCaller implements Callable<String> {

        @Override
        public String call() throws Exception {
            logger.entering();
            try {
                while (bStartSession) {
                    driver.findElementByTagName("*");
                    Thread.sleep(1000 * 10);
                }
            } catch (InterruptedException e) {
                logger.exiting(null);
                return null;
            }
            logger.exiting(null);
            return null;
        }
    }

    /**
     * This constructor creates WebDriverSessionHandler instance.
     * 
     * @param driver
     *            need to pass RemoteWebDriver instance [Grid.driver()]
     */
    public WebDriverSessionHandler(RemoteWebDriver driver) {

        if (driver == null) {
            throw new IllegalArgumentException("RemoteWebDriver instance is null");
        }
        this.driver = driver;
        webDriverCaller = new WebDriverCaller();

    }

    /**
     * {@link WebDriverSessionHandler#start()} will start a child thread that will keep pooling the title of the page so
     * the the web session will not get timeout.
     */
    public void start() {

        logger.entering();

        if (bStartSession) {
            throw new IllegalStateException("WebDriverSessionHandler is already started");
        }

        bStartSession = true;

        result = Executors.newSingleThreadExecutor().submit(webDriverCaller);
        logger.exiting();

    }

    /**
     * {@link WebDriverSessionHandler#stop()} will stop the polling child thread.
     * 
     * @throws ExecutionException
     *             thrown when exceptions occur in the child thread while polling.
     */
    public void stop() throws ExecutionException {

        logger.exiting();

        if (!bStartSession) {
            throw new IllegalStateException("Please call startSession() before calling endSession()");
        }
        bStartSession = false;

        try {
            result.get();
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "InterruptedException occured while pinging the WebDriver", e);
        }

        logger.exiting();
    }
}
