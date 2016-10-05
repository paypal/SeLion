package com.paypal.selion;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.internal.platform.grid.BrowserFlavors;
import com.paypal.selion.internal.platform.grid.WebTestSession;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

/**
 * A custom capabilitiesBuilder used during test executions to establish custom browser paths and options.
 */
public class TestCapabilityBuilder extends DefaultCapabilitiesBuilder {

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        String browserPath = System.getProperty("BROWSER_PATH");
        WebTestSession session = Grid.getWebTestSession();
        if (StringUtils.isEmpty(browserPath) || session == null) {
            return capabilities;
        }

        String browser = session.getBrowser();
        if (browser.equals(BrowserFlavors.CHROME.getBrowser())) {
            ChromeOptions options = new ChromeOptions();
            options.setBinary(browserPath);
            // To run chrome on virtualized openVZ environments
            options.addArguments("--no-sandbox");
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        } else if (browser.equals(BrowserFlavors.FIREFOX.getBrowser())) {
            Map<String, String> firefoxOptions = new HashMap<>();
            firefoxOptions.put("binary", browserPath);
            String key = Config.getBoolConfigProperty(ConfigProperty.SELENIUM_USE_GECKODRIVER) ?
                "moz:firefoxOptions" : "firefox_binary";
            capabilities.setCapability(key, firefoxOptions);
        }
        return capabilities;
    }
}
