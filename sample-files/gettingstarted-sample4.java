package com.mycompany.test;

import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.TextField;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

public class Demo {
  /**
   * This test case launches the google URL in the browser and search for the
   * string “SeLion”
   */
  @Test
  @WebTest
  public void DemoTest1() throws InterruptedException {
    //Launch the google URL in the browser
    Grid.driver().get("http://www.google.com/");

    //Define the text field
    TextField field = new TextField("id=lst-ib");

    //Thread will wait until TextFiled element present in the browser
    WebDriverWaitUtils.waitUntilElementIsPresent(field.getLocator());

    //Search for the string 'SeLion' in the text box
    field.type("SeLion\n");
  }
}