package com.paypal.selion.platform.grid.browsercapabilities;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;

public class AdditionalSauceCapabilitiesBuilderTest {
    private static String SAUCE_CONFIG_FILE = "sauceConfig.json";

    @Test
    public void test() {
        Config.setConfigProperty(ConfigProperty.SELENIUM_SAUCELAB_GRID_CONFIG_FILE, SAUCE_CONFIG_FILE);
        Config.setConfigProperty(ConfigProperty.SELENIUM_USE_SAUCELAB_GRID, "true");
        AdditionalSauceCapabilitiesBuilder builder = new AdditionalSauceCapabilitiesBuilder();
        DesiredCapabilities capabilities = builder.getCapabilities(new DesiredCapabilities());

        assertTrue(capabilities.getCapability("tunnel-identifier").equals(""));
        assertTrue(capabilities.getCapability("tags") instanceof List<?>);
        assertTrue(capabilities.getCapability("selenium-version") != null);
        assertTrue(capabilities.getCapability("username") != null);
        assertTrue(capabilities.getCapability("accessKey") != null);
        assertTrue(capabilities.getCapability("parent-tunnel") != null);
    }
}
