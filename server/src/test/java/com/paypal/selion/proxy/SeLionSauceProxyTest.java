/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

package com.paypal.selion.proxy;

import com.beust.jcommander.JCommander;
import com.paypal.selion.pojos.SeLionGridConstants;

import org.apache.commons.io.FileUtils;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.BaseRemoteProxy;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.web.Hub;
import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.remote.internal.HttpClientFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.InputStream;

import static org.testng.Assert.assertTrue;

public class SeLionSauceProxyTest {

    private HttpClientFactory httpClientFactory;
    private Hub hub;
    private int port;
    private RegistrationRequest req;
    private File tempFile;

    @BeforeClass
    public void setup() throws Exception {
        httpClientFactory = new HttpClientFactory();
        port = PortProber.findFreePort();

        String[] args = new String[] {
                "-role", "hub",
                "-host", "localhost",
                "-port", String.valueOf(port),
                "-servlet", "com.paypal.selion.grid.servlets.LoginServlet",
                "-servlet", "com.paypal.selion.grid.servlets.SauceServlet"
        };
        GridHubConfiguration ghc = new GridHubConfiguration();
        new JCommander(ghc, args);

        hub = new Hub(ghc);
        hub.start();
    }

    @Test()
    public void testSauceProxyConfig() throws Exception {
        InputStream stream = this.getClass().getResourceAsStream(SeLionGridConstants.NODE_SAUCE_CONFIG_FILE_RESOURCE);
        tempFile = File.createTempFile("selion-test", null);
        FileUtils.copyInputStreamToFile(stream, tempFile);
        req = new RegistrationRequest();
        req.loadFromJSON(tempFile.toString());
        assertTrue(req.getCapabilities().size() > 0);
    }

    @Test(dependsOnMethods = "testSauceProxyConfig")
    public void testSauceProxy() throws Exception {
        RemoteProxy p = BaseRemoteProxy.getNewInstance(req, hub.getRegistry());
        assertTrue(p instanceof SeLionSauceProxy);
        assertTrue(p.getStatus() != null);
    }

    @AfterClass(alwaysRun = true)
    public void teardown() throws Exception {
        tempFile.delete();
        hub.stop();
        httpClientFactory.close();
    }
}
