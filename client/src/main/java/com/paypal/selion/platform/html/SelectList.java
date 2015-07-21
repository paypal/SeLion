/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
|                                                                                                                     |
|  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     |
|  with the License.                                                                                                  |
|                                                                                                                     |
|  You may obtain a copy of the License at                                                                            |
|                                                                                                                     |
|       http://www.apache.org/licenses/LICENSE-2.0                                                                    |
|                                                                                                                     |
|  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   |
|  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  |
|  the specific language governing permissions and limitations under the License.                                     |
\*-------------------------------------------------------------------------------------------------------------------*/

package com.paypal.selion.platform.html;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.platform.html.support.events.Deselectable;
import com.paypal.selion.platform.html.support.events.Selectable;

/**
 * This class is the web element Select wrapper.
 * <p>
 * In this class, the method 'select' is encapsulated to select option against the specified element.
 * </p>
 * 
 */
public class SelectList extends AbstractElement implements Selectable, Deselectable {

    /**
     * SelectList Construction method <br>
     * <br>
     * <b>Usage:</b>
     * 
     * <pre>
     * private SelectList selShippingService = new SelectList("//select[@id='shipping']")
     * </pre>
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     */
    public SelectList(String locator) {
        super(locator);
    }

    /**
     * Use this constructor to override default controlName for logging purposes. Default controlName would be the
     * element locator.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging
     */
    public SelectList(String locator, String controlName) {
        super(locator, controlName);
    }

    /**
     * Use this constructor to create a SelectList contained within a parent.
     * 
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     */
    public SelectList(ParentTraits parent, String locator) {
        super(parent, locator);
    }

    /**
     * Use this constructor to create a SelectList contained within a parent. Use this constructor to override default
     * controlName for logging purposes. Default controlName would be the element locator.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * 
     */
    public SelectList(String locator, String controlName, ParentTraits parent) {
        super(locator, controlName, parent);
    }

    /**
     * Selects an option using optionLocator. Locator must be prefixed with one of the following: <li>label= <li>value=
     * <li>index= <li>id=
     * 
     * @param optionLocator
     *            the select list option locator
     */
    public void select(String optionLocator) {
        getDispatcher().beforeSelect(this, optionLocator);
        
        if (StringUtils.isBlank(optionLocator)) {
            throw new IllegalArgumentException("Locator cannot be null or empty.");
        }
        if (optionLocator.split("=").length != 2) {
            StringBuilder errMsg = new StringBuilder("Invalid locator specified :");
            errMsg.append(optionLocator);
            errMsg.append(". Locator should be of the form label=<value> (or) ");
            errMsg.append("value=<value> (or) ");
            errMsg.append("index=<value> (or) ");
            errMsg.append("id=<value>.");
            throw new IllegalArgumentException(errMsg.toString());
        }
        String locatorToUse = optionLocator.split("=")[1].trim();
        String tLocator = optionLocator.toLowerCase().split("=")[0].trim();
        if (tLocator.indexOf("label") >= 0) {
            // label was given
            new Select(getElement()).selectByVisibleText(locatorToUse);
        } else if (tLocator.indexOf("value") >= 0) {
            // value was given
            new Select(getElement()).selectByValue(locatorToUse);
        } else if (tLocator.indexOf("index") >= 0) {
            // index was given
            new Select(getElement()).selectByIndex(Integer.parseInt(locatorToUse));
        } else if (tLocator.indexOf("id") >= 0) {
            // id was given
            getElement().findElementById(locatorToUse).click();
        } else {
            throw new NoSuchElementException("Unable to find " + optionLocator);
        }
        
        getDispatcher().afterSelect(this, optionLocator);
    }

    /**
     * Selects an option using a String array of optionLocators. Each locator must be prefixed with one of the
     * following: <li>label= <li>value= <li>index= <li>id=
     * 
     * @param optionLocators
     *            the select list option locators
     */
    public void select(String[] optionLocators) {
        for (int i = 0; i < optionLocators.length; i++) {
            select(optionLocators[i]);
        }
    }

    /**
     * Does the same thing as {@link #select(String)}
     * 
     * @param optionLocator
     *            the option locator to select
     */
    public void addSelection(String optionLocator) {
        select(optionLocator);
    }

    /**
     * Select all options that have a value matching the argument.
     * 
     * @param value
     *            the value to select
     */
    public void selectByValue(String value) {
        getDispatcher().beforeSelect(this, value);
        
        new Select(getElement()).selectByValue(value);
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIActions(UIActions.SELECTED, value);
        }
        
        getDispatcher().afterSelect(this, value);
    }

    /**
     * Select all options that display text matching the argument.
     * 
     * @param label
     *            the label to select
     */
    public void selectByLabel(String label) {
        getDispatcher().beforeSelect(this, label);
        
        new Select(getElement()).selectByVisibleText(label);
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIActions(UIActions.SELECTED, label);
        }
        
        getDispatcher().afterSelect(this, label);
    }

    /**
     * Select the option at the given index. This is done by examing the "index" attribute of an element, and not merely
     * by counting.
     * 
     * @param index
     *            the index to select
     */
    public void selectByIndex(int index) {
        getDispatcher().beforeSelect(this, index);
        
        new Select(getElement()).selectByIndex(index);
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIActions(UIActions.SELECTED, Integer.toString(index));
        }
        
        getDispatcher().afterSelect(this, index);
    }

    /**
     * Select all options that have a value matching any arguments.
     * 
     * @param values
     *            the values to select
     */
    public void selectByValue(String[] values) {
        for (int i = 0; i < values.length; i++) {
            selectByValue(values[i]);
        }
    }

    /**
     * Select all options that display text matching any arguments.
     * 
     * @param labels
     *            the labels to select
     */
    public void selectByLabel(String[] labels) {
        for (int i = 0; i < labels.length; i++) {
            selectByLabel(labels[i]);
        }
    }

    /**
     * Select the option at the given indexes. This is done by examing the "index" attribute of an element, and not
     * merely by counting.
     * 
     * @param indexes
     *            the indexes to select
     */
    public void selectByIndex(String[] indexes) {
        for (int i = 0; i < indexes.length; i++) {
            selectByIndex(Integer.parseInt(indexes[i]));
        }
    }

    /**
     * Does the same thing as {@link #selectByValue(String)}
     * 
     * @param value
     *            the value to select
     */
    public void addSelectionByValue(String value) {
        selectByValue(value);
    }

    /**
     * Does the same thing as {@link #selectByLabel(String)}
     * 
     * @param label
     *            the label to select
     */
    public void addSelectionByLabel(String label) {
        selectByLabel(label);
    }

    /**
     * Does the same thing as {@link #selectByIndex(int)}
     * 
     * @param index
     *            the index to select
     */
    public void addSelectionByIndex(String index) {
        selectByIndex(Integer.parseInt(index));
    }

    /**
     * Returns all options currently selected.
     * 
     * @return All options currently selected.
     */
    public String[] getSelectOptions() {
        List<WebElement> optionList = getElement().findElements(By.tagName("option"));
        String[] optionArray = new String[optionList.size()];
        for (int i = 0; i < optionList.size(); i++) {
            optionArray[i] = optionList.get(i).getText();
        }
        return optionArray;
    }

    /**
     * Get a single selected label. If multiple options are selected, then the first one is returned.
     * 
     * @return A single selected label.
     */
    public String getSelectedLabel() {
        List<WebElement> options = getElement().findElements(By.tagName("option"));
        for (WebElement option : options) {
            if (option.isSelected()) {
                return option.getText();
            }
        }
        return null;
    }

    /**
     * Get a single selected value. If multiple options are selected, then the first one is returned.
     * 
     * @return A single selected value.
     */
    public String getSelectedValue() {
        List<WebElement> options = getElement().findElements(By.tagName("option"));
        for (WebElement option : options) {
            if (option.isSelected()) {
                return option.getAttribute("value");
            }
        }
        return null;
    }

    /**
     * Gets multiple selected labels.
     * 
     * @return All selected labels.
     */
    public String[] getSelectedLabels() {
        List<WebElement> options = getElement().findElements(By.tagName("option"));
        List<String> selected = new ArrayList<String>();
        for (WebElement option : options) {
            if (option.isSelected()) {
                selected.add(option.getText());
            }
        }
        return (String[]) selected.toArray(new String[selected.size()]);
    }

    /**
     * Gets multiple selected values.
     * 
     * @return All selected values.
     */
    public String[] getSelectedValues() {
        List<WebElement> options = getElement().findElements(By.tagName("option"));
        List<String> selected = new ArrayList<String>();
        for (WebElement option : options) {
            if (option.isSelected()) {
                selected.add(option.getAttribute("value"));
            }
        }
        return (String[]) selected.toArray(new String[selected.size()]);
    }

    /**
     * Get all labels, whether they are selected or not.
     * 
     * @return All labels, selected or not.
     */
    public String[] getContentLabel() {
        List<WebElement> options = getElement().findElements(By.tagName("option"));
        List<String> contents = new ArrayList<String>();

        for (WebElement option : options) {
            contents.add(option.getText());
        }

        return (String[]) contents.toArray(new String[contents.size()]);
    }

    /**
     * Get all values, whether they are selected or not.
     * 
     * @return All values, selected or not.
     */
    public String[] getContentValue() {
        List<WebElement> options = getElement().findElements(By.tagName("option"));
        List<String> contents = new ArrayList<String>();

        for (WebElement option : options) {
            contents.add(option.getAttribute("value"));
        }

        return (String[]) contents.toArray(new String[contents.size()]);
    }

    /**
     * Clear all selected entries. This is only valid when the SELECT supports multiple selections.
     * 
     * @throws UnsupportedOperationException
     *             If the SELECT does not support multiple selections
     */
    public void deselectAll() {
        getDispatcher().beforeDeselect(this);
        
        new Select(getElement()).deselectAll();
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIActions(UIActions.CLEARED, "all");
        }
        
        getDispatcher().afterDeselect(this);
    }

    /**
     * Deselect all options that have a value matching the argument.
     * 
     * @param value
     *            the value to deselect
     */
    public void deselectByValue(String value) {
        getDispatcher().beforeDeselect(this, value);
        
        new Select(getElement()).deselectByValue(value);
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIActions(UIActions.CLEARED, value);
        }
        
        getDispatcher().afterDeselect(this, value);
    }

    /**
     * Deselect the option at the given index. This is done by examing the "index" attribute of an element, and not
     * merely by counting.
     * 
     * @param index
     *            the index to deselect
     */
    public void deselectByIndex(int index) {
        getDispatcher().beforeDeselect(this, index);
        
        new Select(getElement()).deselectByIndex(index);
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIActions(UIActions.CLEARED, Integer.toString(index));
        }
        
        getDispatcher().afterDeselect(this, index);
    }

    /**
     * Deselect all options that display text matching the argument.
     * 
     * @param label
     *            the label to deselect
     */
    public void deselectByLabel(String label) {
        getDispatcher().beforeDeselect(this, label);
        
        new Select(getElement()).deselectByVisibleText(label);
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIActions(UIActions.CLEARED, label);
        }
        
        getDispatcher().afterDeselect(this, label);
    }
}
