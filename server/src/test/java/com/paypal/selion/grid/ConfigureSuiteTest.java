package com.paypal.selion.grid;

import java.io.File;

import org.testng.annotations.BeforeSuite;

import com.paypal.selion.SeLionConstants;

public class ConfigureSuiteTest {
    @BeforeSuite(alwaysRun = true)
    public void setUpBeforeSuite() {
        System.setProperty("selionHome",
                new File(ConfigureSuiteTest.class.getResource("/").getPath()).getAbsoluteFile().getParent()
                        + "/.selion"); // should compute to "{selion-project-dir}/server/target/.selion"
        new File(SeLionConstants.SELION_HOME_DIR).mkdirs();
    }
}
