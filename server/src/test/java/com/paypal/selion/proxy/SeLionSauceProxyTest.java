package com.paypal.selion.proxy;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.pojos.SeLionGridConstants;
import org.apache.commons.io.FileUtils;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.utils.GridHubConfiguration;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.openqa.grid.web.Hub;
import org.openqa.selenium.remote.internal.HttpClientFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;
import java.io.InputStream;

import static org.testng.Assert.assertTrue;

public class SeLionSauceProxyTest {

    private HttpClientFactory httpClientFactory;
    private Hub hub;
    private RegistrationRequest req;
    private File tempFile;

    @BeforeClass
    public void setup() throws Exception {
        httpClientFactory = new HttpClientFactory();
        String[] args = new String[] {
                "-role", "hub", "-type", "sauce",
                "-host", "localhost",
                "-servlets", "com.paypal.selion.grid.servlets.LoginServlet,com.paypal.selion.grid.servlets.SauceServlet",
        };
        GridHubConfiguration ghc = GridHubConfiguration.build(args);
        hub = new Hub(ghc);
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
        RemoteProxy p = DefaultRemoteProxy.getNewInstance(req, hub.getRegistry());
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
