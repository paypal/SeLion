/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.platform.html.support;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ByIdOrName;

import com.google.common.base.Preconditions;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.ParentTraits;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A Simple class that houses the utilities related to a HTML element locators
 * 
 */
public class HtmlElementUtils {

    private static SimpleLogger logger = SeLionLogger.getLogger();
    private static final String INVALID_LOCATOR_ERR_MSG = "Locator cannot be null (or) empty.";
    private static final String INVALID_PARENT_ERR_MSG = "Parent element cannot be null.";

    private HtmlElementUtils() {
        //Utility class. Hide the constructor to prevent instantiation
    }

    /**
     * Parses locator string to identify the proper By subclass before calling Selenium
     * {@link WebElement#findElement(By)} to locate the web element.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @return - A {@link RemoteWebElement} that represents the html element that was located using the locator
     *         provided.
     */
    public static RemoteWebElement locateElement(String locator) {
        logger.entering(locator);
        Preconditions.checkArgument(StringUtils.isNotBlank(locator), INVALID_LOCATOR_ERR_MSG);
        By locatorBy = resolveByType(locator);
        RemoteWebDriver rwd = (RemoteWebDriver) Grid.driver().getWrappedDriver();

        RemoteWebElement element = (RemoteWebElement) rwd.findElement(locatorBy);
        logger.exiting(element);

        return element;
    }

    /**
     * Parses locator string to identify the proper By subclass before calling Selenium
     * {@link WebElement#findElement(By)} to locate the web element nested within the parent web element.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * @return - A {@link RemoteWebElement} that represents the html element that was located using the locator
     *         provided.
     */
    public static RemoteWebElement locateElement(String locator, ParentTraits parent) {
        logger.entering(new Object[] { locator, parent });
        Preconditions.checkArgument(StringUtils.isNotBlank(locator), INVALID_LOCATOR_ERR_MSG);
        Preconditions.checkArgument(parent != null, INVALID_PARENT_ERR_MSG);
        RemoteWebElement element = parent.locateChildElement(locator);
        logger.exiting(element);
        return element;
    }

    /**
     * Parses locator string to identify the proper By subclass before calling Selenium
     * {@link WebElement#findElements(By)} to locate the web elements.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * 
     * @return A {@link WebElement} list that represents the html elements that was located using the locator provided.
     */
    public static List<WebElement> locateElements(String locator) {
        logger.entering(locator);
        Preconditions.checkArgument(StringUtils.isNotBlank(locator), INVALID_LOCATOR_ERR_MSG);
        By locatorBy = resolveByType(locator);

        RemoteWebDriver rwd = (RemoteWebDriver) Grid.driver().getWrappedDriver();
        List<WebElement> webElementsFound = rwd.findElements(locatorBy);
        // if element is empty list then throw exception since unlike
        // findElement() findElements() always returns a list
        // irrespective of whether an element was found or not
        if (webElementsFound.isEmpty()) {
            throw new NoSuchElementException(generateUnsupportedLocatorMsg(locator));
        }
        logger.exiting(webElementsFound);
        return webElementsFound;
    }

    /**
     * Parses locator string to identify the proper By subclass before calling Selenium
     * {@link WebElement#findElements(By)} to locate the web elements nested within the parent web .
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * @return A {@link WebElement} list that represents the html elements that was located using the locator provided.
     */
    public static List<WebElement> locateElements(String locator, ParentTraits parent) {
        logger.entering(new Object[] { locator, parent });
        Preconditions.checkArgument(StringUtils.isNotBlank(locator), INVALID_LOCATOR_ERR_MSG);
        Preconditions.checkArgument(parent != null, INVALID_PARENT_ERR_MSG);
        List<WebElement> webElementsFound = parent.locateChildElements(locator);
        // if element is empty list then throw exception since unlike
        // findElement() findElements() always returns a list
        // irrespective of whether an element was found or not
        if (webElementsFound.isEmpty()) {
            throw new NoSuchElementException(generateUnsupportedLocatorMsg(locator));
        }
        logger.exiting(webElementsFound);
        return webElementsFound;
    }

    /**
     * Validates a child locator to have the xpath dot notation.
     * 
     * @param locator
     *            - A String that represents the xpath locator for an element.
     */
    public static void isValidXpath(String locator) {
        logger.entering(locator);
        Preconditions.checkArgument(StringUtils.isNotBlank(locator), INVALID_LOCATOR_ERR_MSG);
        if (locator.startsWith("xpath=/") || locator.startsWith("/")) {
            throw new UnsupportedOperationException(
                    "Use xpath dot notation to search for Container descendant elements. Example: \".//myLocator\". ");
        }
        logger.exiting();
    }

    /**
     * Method to split the locator string with delimiter '|' to return a valid {@link By } type.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @return A {@link By} object that represents the actual locating strategy that would be employed.
     */
    public static By resolveByType(String locator) {
        logger.entering(locator);
        Preconditions.checkArgument(StringUtils.isNotBlank(locator), INVALID_LOCATOR_ERR_MSG);
        By locatorBy = null;
        locator = locator.trim();

        if (locator.indexOf("|") == -1) {
            locatorBy = getFindElementType(locator);
        } else {
            String[] locators = locator.split("\\Q|\\E");
            List<By> result = new ArrayList<By>();
            for (String temp : locators) {
                result.add(getFindElementType(temp));
            }
            locatorBy = new ByOrOperator(result);
        }

        logger.exiting(locatorBy);
        return locatorBy;
    }

    /**
     * Detects Selenium {@link org.openqa.selenium.By By} type depending on what the locator string starts with.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @return The {@link By} sub-class that represents the actual location strategy that will be used.
     */
    public static By getFindElementType(String locator) {
        logger.entering(locator);
        Preconditions.checkArgument(StringUtils.isNotBlank(locator), INVALID_LOCATOR_ERR_MSG);
        By valueToReturn = null;
        locator = locator.trim();
        if (locator.startsWith("id=")) {
            valueToReturn = By.id(locator.substring("id=".length()));
        } else if (locator.startsWith("name=")) {
            valueToReturn = By.name(locator.substring("name=".length()));
        } else if (locator.startsWith("link=")) {
            valueToReturn = By.linkText(locator.substring("link=".length()));
        } else if (locator.startsWith("xpath=")) {
            valueToReturn = By.xpath(locator.substring("xpath=".length()));
        } else if (locator.startsWith("/") || locator.startsWith("./")) {
            valueToReturn = By.xpath(locator);
        } else if (locator.startsWith("css=")) {
            valueToReturn = By.cssSelector(locator.substring("css=".length()));
        } else {
            valueToReturn = new ByIdOrName(locator);
        }
        if (logger.isLoggable(Level.FINE)) {
            String msg = valueToReturn.getClass().getSimpleName()
                    + " will be the location strategy that will be used for locating " + locator;
            logger.log(Level.FINE, msg);
        }
        logger.exiting(valueToReturn);
        return valueToReturn;
    }

    private static String generateUnsupportedLocatorMsg(String locator) {
        return "Unsupported locator {" + locator
                + "}. Locator has to be either a name, id, link text, xpath, or css selector.";
    }

    /**
     * Checks if the provided element is present on the page based on the locator provided
     *
     * @param locator
     *         A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @return
     *         A boolean indicating if the element was found.
     */
    public static boolean isElementPresent(String locator) {
        logger.entering(locator);
        boolean flag = false;
        try {
            flag = HtmlElementUtils.locateElement(locator) != null;
        } catch (NoSuchElementException e) { //NOSONAR
        }
        logger.exiting(flag);
        return flag;
    }

}
