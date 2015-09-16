/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 eBay Software Foundation                                                                     |
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

package com.paypal.selion.testcomponents;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import com.paypal.selion.internal.utils.RegexUtils;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.AbstractElement;
import com.paypal.selion.platform.html.PageValidationException;
import com.paypal.selion.platform.html.ParentTraits;
import com.paypal.selion.platform.html.UndefinedElementException;
import com.paypal.selion.platform.html.support.HtmlElementUtils;

/**
 * A Base class from which all page classes should be derived.
 * 
 * It contains the code to initialize pages, load values to the "ObjectMap", and interact in various ways with the
 * page(s).
 */
public abstract class BasicPageImpl extends AbstractPage implements ParentTraits {

    /**
     * Instantiates a new base page impl.
     */
    protected BasicPageImpl() {
        super();
    }

    /**
     * @return the actual title for this page
     */
    public String getActualPageTitle() {
        return Grid.driver().getTitle();
    }

    @Override
    public String getExpectedPageTitle() {
        this.getObjectMap();
        return getPage().getPageTitle();
    }

    /**
     * Validates whether the actual current page title equals to expected page title.
     * 
     * @return true if the actual page title is equal to any of the titles represented by this page object otherwise
     *         returns false
     */
    public boolean hasExpectedPageTitle() {
        // If there are no page titles defined we should return false
        if (getExpectedPageTitle() == null) {
            return false;
        }

        List<String> pageTitles = Arrays.asList(getExpectedPageTitle().split("\\|"));
        for (String title : pageTitles) {
            if (RegexUtils.wildCardMatch(this.getActualPageTitle(), title)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the page object
     */
    public abstract BasicPageImpl getPage();

    public List<WebElement> locateChildElements(String locator) {
        return HtmlElementUtils.locateElements(locator);
    }

    public RemoteWebElement locateChildElement(String locator) {
        return HtmlElementUtils.locateElement(locator);
    }

    public BasicPageImpl getCurrentPage() {
        return this;
    }

    @Override
    public void validatePage() {
        getObjectMap();

        if (getPageValidators().size() == 0) {
            if (!hasExpectedPageTitle()) {
                throw new PageValidationException(getClass().getSimpleName() + " isn't loaded in the browser, "
                        + getExpectedPageTitle() + " didn't match.");
            }
        } else {
            for (String elementName : getPageValidators()) {
                // We can set the action we want to check for, by putting a dot at the end of the elementName.
                // Following by isPresent, isVisible or isEnabled, default behaviour is isPresent
                String action = "";
                int indexOf = elementName.indexOf(".");
                if (indexOf != -1) {
                    action = elementName.substring(indexOf + 1, elementName.length());
                    elementName = elementName.substring(0, indexOf);
                }

                verifyElementByAction(elementName, action);
            }
        }
    }

    /**
     * Get the AbstractElement by the key that is defined in the PageYAML files.
     * 
     * @param elementName
     *            The element name
     * @return instance of {@link AbstractElement}
     */
    private AbstractElement getAbstractElementThroughReflection(String elementName) {
        Field field = null;
        Class<?> currentClass = getClass();

        do {
            try {
                field = currentClass.getDeclaredField(elementName);
                field.setAccessible(true);
                return (AbstractElement) currentClass.getMethod("get" + StringUtils.capitalize(field.getName()))
                        .invoke(this);
            } catch (Exception e) {
                // NOSONAR
            }
        } while ((currentClass = currentClass.getSuperclass()) != null);

        throw new UndefinedElementException("Element with name " + elementName + " doesn't exist.");
    }

    /**
     * Verify if the element is available based on a certain action
     * 
     * @param elementName
     *            element to perform verification action on
     * @param action
     *            verification action to perform
     */
    private void verifyElementByAction(String elementName, String action) {
        AbstractElement element = getAbstractElementThroughReflection(elementName);

        boolean present = element.isElementPresent();

        switch (action) {
        case "isPresent":
            if (!present) {
                throw new PageValidationException(getClass().getSimpleName() + " isn't loaded in the browser, "
                        + elementName + " with locator " + element.getLocator() + " isn't present.");
            }
            break;
        case "isVisible":
            if (!present || (present && !element.isVisible())) {
                throw new PageValidationException(getClass().getSimpleName() + " isn't loaded in the browser, "
                        + elementName + " with locator " + element.getLocator() + " isn't visible.");
            }
            break;
        case "isEnabled":
            if (!present || (present && !element.isEnabled())) {
                throw new PageValidationException(getClass().getSimpleName() + " isn't loaded in the browser, "
                        + elementName + " with locator " + element.getLocator() + " isn't enabled.");
            }
            break;
        default:
            if (!present) {
                throw new PageValidationException(getClass().getSimpleName() + " isn't loaded in the browser, "
                        + elementName + " with locator " + element.getLocator() + " isn't present.");
            }
            break;
        }
    }

    @Override
    public boolean isPageValidated() {
        try {
            validatePage();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
