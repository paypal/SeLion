package com.paypal.selion.configuration;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ConfigParallelTest {

    @DataProvider(name = "threadSafe", parallel = true)
    public Object[][] data() {
        return new String[][] {
                {"*firefox"},
                {"*chrome"},
                {"*firefox"},
                {"*chrome"}
        };
    }

    @BeforeMethod(groups = { "unit" })
    public void before(Object... objects) {
        if(objects.length > 0 && objects[0] != null) {
            ConfigManager.getConfig().setConfigProperty(Config.ConfigProperty.BROWSER, (String) objects[0]);
        }
    }

    @Test(groups = { "unit" }, dataProvider = "threadSafe")
    public void testTheadSafeConfig(String browser) {
        Assert.assertEquals(ConfigManager.getConfig().getConfigProperty(Config.ConfigProperty.BROWSER), browser);
    }
}
