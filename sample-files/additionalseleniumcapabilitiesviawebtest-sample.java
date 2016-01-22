package com.mycompany.test;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;

/**
 * This test demonstrates how to add additional Selenium capabilities via
 * the WebTest annotation. MobileTest has the exact same property
 * "additionalCapabilities"
 */
public class AdditionalSeleniumCapabilitiesViaWebTest {

  @Test
  @WebTest(additionalCapabilities = { "useBooleanCaps:true","key:value" })
  public void testCapabilityViaAnnotation() {
    assertEquals(Grid.getWebTestSession().getAdditionalCapabilities()
      .getCapability("useBooleanCaps"), Boolean.TRUE);
    assertEquals(Grid.getWebTestSession().getAdditionalCapabilities()
      .getCapability("key"), "value");
  }
}