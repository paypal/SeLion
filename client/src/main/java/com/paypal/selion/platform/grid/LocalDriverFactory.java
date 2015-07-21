package com.paypal.selion.platform.grid;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.grid.BrowserFlavors;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

/**
 * A simple factory class that produces concrete browser driver instances when users need to run tests against a browser
 * at their local desktoops.
 */
class LocalDriverFactory {
    private LocalDriverFactory() {

    }

    public static RemoteWebDriver newWebDriver(BrowserFlavors browser, DesiredCapabilities capabilities) {
        if (!Config.getBoolConfigProperty(Config.ConfigProperty.SELENIUM_RUN_LOCALLY)) {
            throw new UnsupportedOperationException("Cannot instantiate a webdriver for remote executions.");
        }
        RemoteWebDriver webDriver = null;
        switch (browser) {
        case HTMLUNIT:
            throw new UnsupportedOperationException("Local executions on HtmlUnit are not supported.");
        case INTERNET_EXPLORER:
            webDriver = new InternetExplorerDriver(capabilities);
            break;
        case OPERA:
            webDriver = new OperaDriver(capabilities);
            break;
        case CHROME:
            webDriver = new ChromeDriver(capabilities);
            break;
        case SAFARI:
            webDriver = new SafariDriver(capabilities);
            break;
        case PHANTOMJS:
            webDriver = new PhantomJSDriver(capabilities);
            break;
        default:
            webDriver = new FirefoxDriver(capabilities);
            break;
        }
        return webDriver;
    }

}
