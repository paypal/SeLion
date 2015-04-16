package com.paypal.test.listeners;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.Button;
import com.paypal.selion.platform.html.CheckBox;
import com.paypal.selion.platform.html.DatePicker;
import com.paypal.selion.platform.html.Form;
import com.paypal.selion.platform.html.Image;
import com.paypal.selion.platform.html.Label;
import com.paypal.selion.platform.html.Link;
import com.paypal.selion.platform.html.RadioButton;
import com.paypal.selion.platform.html.SelectList;
import com.paypal.selion.platform.html.Table;
import com.paypal.selion.platform.html.TextField;
import com.paypal.selion.platform.html.support.events.AbstractElementEventListener;
import com.paypal.selion.platform.html.support.events.Checkable;
import com.paypal.selion.platform.html.support.events.Clickable;
import com.paypal.selion.platform.html.support.events.Deselectable;
import com.paypal.selion.platform.html.support.events.Selectable;
import com.paypal.selion.platform.html.support.events.Submitable;
import com.paypal.selion.platform.html.support.events.Typeable;
import com.paypal.selion.platform.html.support.events.Uncheckable;
import com.paypal.selion.reports.runtime.WebReporter;

public class DebugScreenshotListener extends AbstractElementEventListener {
  private static ThreadLocal<Boolean> interactedWithPage = new ThreadLocal<Boolean>() {
    @Override
    protected Boolean initialValue() {
      return false;
    }
  };

  /**
   * If we register this ElementEventListener we should enable AUTO_SCREEN_SHOT functionality from SeLion.
   */
  public DebugScreenshotListener() {
    Config.setConfigProperty(ConfigProperty.AUTO_SCREEN_SHOT, "true");
  }

  @Override
  public void beforeClick(Clickable target, Object... expected) {
    // If an expected object is provided to the click method we take a screenshot.
    if (expected != null && expected.length != 0 && interactedWithPage.get()) {
      WebReporter.log(Grid.driver().getTitle(), true, true);
    }
  }

  @Override
  public void afterClick(Clickable target, Object... expected) {
    if (expected != null && expected.length != 0) {
      reset();
    }
  }

  @Override
  public void afterType(Typeable target, String value) {
    interactedWithPage.set(true);
  }

  @Override
  public void afterCheck(Checkable target, String expected) {
    interactedWithPage.set(true);
  }

  @Override
  public void afterCheck(Checkable target) {
    interactedWithPage.set(true);
  }

  @Override
  public void afterUncheck(Uncheckable target, String expected) {
    interactedWithPage.set(true);
  }

  @Override
  public void afterUncheck(Uncheckable target) {
    interactedWithPage.set(true);
  }

  @Override
  public void afterSubmit(Submitable target) {
    interactedWithPage.set(true);
  }

  @Override
  public void afterSelect(Selectable target, int index) {
    interactedWithPage.set(true);
  }

  @Override
  public void afterSelect(Selectable target, String value) {
    interactedWithPage.set(true);
  }

  @Override
  public void afterDeselect(Deselectable target, int index) {
    interactedWithPage.set(true);
  }

  @Override
  public void afterDeselect(Deselectable target, String value) {
    interactedWithPage.set(true);
  }

  @Override
  public void afterDeselect(Deselectable target) {
    interactedWithPage.set(true);
  }

  public static void reset() {
    interactedWithPage.set(false);
  }

}