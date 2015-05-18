package com.mycompany;

import com.paypal.selion.platform.html.AbstractElement;
import com.paypal.selion.platform.html.ParentTraits;

public class CustomElementOne extends AbstractElement {

  public CustomElementOne(ParentTraits parent, String locator) {
    super(parent, locator);
  }

  public CustomElementOne(String locator, String controlName, 
                          ParentTraits parent) {
    super(locator, controlName, parent);
  }

  public CustomElementOne(String locator, String controlName) {
    super(locator, controlName);
  }

  public CustomElementOne(String locator) {
    super(locator);
  }
}
