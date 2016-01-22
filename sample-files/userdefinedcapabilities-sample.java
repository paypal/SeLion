package com.mycompany.test;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;

public class UserDefinedCapabilities extends DefaultCapabilitiesBuilder {

  @Override
  public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
    capabilities.setCapability("capName", "capValue");
    return capabilities;
  }
}