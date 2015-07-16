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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import com.paypal.selion.internal.utils.RegexUtils;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.AbstractElement;
import com.paypal.selion.platform.html.Container;
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

    private static final String NESTED_CONTAINER_ERR_MSG = "No support for defining a Container within a Container.";

    /**
     * Instantiates a new base page impl.
     */
    protected BasicPageImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.paypal.selion.platform.html.WebPage#initPage(java.lang.String, java.lang.String)
     */

    /**
     * Load object map. This method takes a HashMap<String, String> and uses it to populate the objectMap This is
     * intended to allow for the use of programmatically generated locators in addition to the excel file format IDs and
     * Locators
     * 
     * @param sourceMap
     *            the source map
     */
    // TODO: So what happens if the sourceMap object is null or is empty ? Do we still assume that the page has been
    // initialized ?
    // Come back to this logic.
    protected void loadObjectMap(Map<String, String> sourceMap) {

        if (sourceMap == null) {
            return;
        }
        if (sourceMap.containsKey("pageTitle")) {
            setPageTitle(sourceMap.get("pageTitle"));
        }
        if (objectMap == null) {
            objectMap = new HashMap<String, String>();
        }
        objectMap.putAll(sourceMap);

        setPageInitialized(true);
    }

    /*
     * (non-Javadoc)
     * 
     * Return the actual page title for this page
     */
    public String getActualPageTitle() {
        return Grid.driver().getTitle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.paypal.selion.platform.html.WebPage#getExpectedPageTitle()
     */
    public String getExpectedPageTitle() {
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
        if (getPage().getPageTitle() == null) {
            return false;
        }

        List<String> pageTitles = Arrays.asList(getPage().getPageTitle().split("\\|"));
        for (String title : pageTitles) {
            if (RegexUtils.wildCardMatch(getPage().getActualPageTitle(), title)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Require extended class to provide this implementation.
     * 
     * @return the page
     */
    public abstract BasicPageImpl getPage();

    /**
     * This method is responsible for automatically initializing the PayPal HTML Objects with their corresponding key
     * values obtained from the hash map.
     * 
     * @param whichClass
     *            Indicate for what object you want the initialization to be done for e.g., the GUI Page class name such
     *            as PayPalLoginPage, PayPalAddBankPage, etc
     * @param objectMap
     *            Pass the {@link Map} that contains the key, value pairs read from the yaml file or excel sheet
     */
    public void initializeHtmlObjects(Object whichClass, Map<String, String> objectMap) {

        ArrayList<Field> fields = new ArrayList<Field>();
        Class<?> incomingClass = whichClass.getClass();

        // If the class type is a container then adding the fields related to the container.
        if (incomingClass.getSuperclass().equals(Container.class)) {
            fields.addAll(Arrays.asList(incomingClass.getDeclaredFields()));
        } else {
            // This definitely a page object and so proceeding with loading all the fields
            Class<?> tempIncomingClass = incomingClass;
            do {
                fields.addAll(Arrays.asList(tempIncomingClass.getDeclaredFields()));
                tempIncomingClass = tempIncomingClass.getSuperclass();
            } while (tempIncomingClass != null);

        }

        String errorDesc = " while initializaing HTML fields from the object map. Root cause:";
        try {
            for (Field field : fields) {
                // proceed further only if the data member and the key in the .xls file match with each other
                // below condition checks for this one to one mapping presence
                if (objectMap.containsKey(field.getName())) {
                    field.setAccessible(true);

                    if (isContainerWithinContainer(field, incomingClass)) {
                        throw new UnsupportedOperationException(NESTED_CONTAINER_ERR_MSG);
                    }

                    // We need to perform initialization only for the objects that extend the AbstractElement or Container class
                    // We need to skip for any other objects such as String, custom Classes etc.
                    if (Container.class.isAssignableFrom(field.getType())) {
                        Class<?> dataMemberClass = Class.forName(field.getType().getName());
                        Class<?> parameterTypes[] = new Class[3];

                        parameterTypes[0] = field.getType().getDeclaringClass();
                        parameterTypes[1] = String.class;
                        parameterTypes[2] = String.class;
                        Constructor<?> constructor = dataMemberClass.getDeclaredConstructor(parameterTypes);

                        String locatorValue = objectMap.get(field.getName());
                        if (locatorValue == null) {
                            continue;
                        }
                        Object[] constructorArgList = new Object[3];
                        constructorArgList[0] = whichClass;
                        constructorArgList[1] = new String(locatorValue);
                        constructorArgList[2] = new String(field.getName());
                        Object retobj = constructor.newInstance(constructorArgList);
                        // Associating a parent type here itself! Kind of an hack
                        Container createdContainer = (Container) retobj;
                        createdContainer.setParentForContainer((ParentTraits) whichClass);
                        field.set(whichClass, retobj);

                        // Calling it recursively to load the elements in the container
                        initializeHtmlObjects(retobj, getObjectContainerMap().get(field.getName()));
                    } else if (AbstractElement.class.isAssignableFrom(field.getType())) {
                        // Checking if the superClass/Parent is also a container. If so its not allowed.

                        Class<?> dataMemberClass = Class.forName(field.getType().getName());
                        Class<?> parameterTypes[] = new Class[3];

                        parameterTypes[0] = String.class;
                        parameterTypes[1] = String.class;
                        parameterTypes[2] = ParentTraits.class;
                        Constructor<?> constructor = dataMemberClass.getDeclaredConstructor(parameterTypes);

                        String locatorValue = objectMap.get(field.getName());
                        if (locatorValue == null) {
                            continue;
                        }
                        Object[] constructorArgList = new Object[3];
                        constructorArgList[0] = new String(locatorValue);
                        constructorArgList[1] = new String(field.getName());
                        constructorArgList[2] = whichClass;
                        Object retobj = constructor.newInstance(constructorArgList);
                        field.set(whichClass, retobj);

                    }
                }
            }
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("Class not found" + errorDesc + exception, exception);
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("An illegal argument was encountered" + errorDesc + exception, exception);
        } catch (InstantiationException exception) {
            throw new RuntimeException("Could not instantantiate object" + errorDesc + exception, exception);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException("Could not access data member" + errorDesc + exception, exception);
        } catch (InvocationTargetException exception) {
            throw new RuntimeException("Invocation error occured" + errorDesc + exception, exception);
        } catch (SecurityException exception) {
            throw new RuntimeException("Security error occured" + errorDesc + exception, exception);
        } catch (NoSuchMethodException exception) {
            throw new RuntimeException("Method specified not found" + errorDesc + exception, exception);
        }
    }

    private boolean isContainerWithinContainer(Field field, Class<?> incomingClass) {
        return (field.getType().getSuperclass().equals(Container.class) && incomingClass.getSuperclass().equals(
                Container.class));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.paypal.selion.platform.html.ParentType#locateChildElements(java.lang.String)
     */
    public List<WebElement> locateChildElements(String locator) {
        return HtmlElementUtils.locateElements(locator);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.paypal.selion.platform.html.ParentType#locateChildElement(java.lang.String)
     */
    public RemoteWebElement locateChildElement(String locator) {
        return HtmlElementUtils.locateElement(locator);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.paypal.selion.platform.html.ParentType#getCurrentPage()
     */
    public BasicPageImpl getCurrentPage() {
        return this;
    }

    /**
     * Perform page validations against list of elements defined in the YAML file.
     */
    public void validatePage() {
        // Call getPage to make sure the page is initialized.
        getPage();

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
     * Get the AbstractElement by the key that is defined in the Yaml files.
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

                return (AbstractElement) field.get(this);
            } catch (Exception e) {
                // NOSONAR
            }
        } while ((currentClass = currentClass.getSuperclass()) != null);

        throw new UndefinedElementException("Element with name " + elementName + " doesn't exist.");
    }

    /**
     * Verify if the element is availible based on a certain action
     * 
     * @param elementName
     * @param action
     */
    private void verifyElementByAction(String elementName, String action) {
        AbstractElement element = getAbstractElementThroughReflection(elementName);
        
        boolean present = element.isElementPresent();
        
        switch (action) {
        case "isPresent":
            if (!present) {
                throw new PageValidationException(getClass().getSimpleName() + " isn't loaded in the browser, "
                        + elementName + " isn't present.");
            }
            break;
        case "isVisible":
            if (!present || (present && !element.isVisible())) {
                throw new PageValidationException(getClass().getSimpleName() + " isn't loaded in the browser, "
                        + elementName + " isn't visible.");
            }
            break;
        case "isEnabled":
            if (!present || (present && !element.isEnabled())) {
                throw new PageValidationException(getClass().getSimpleName() + " isn't loaded in the browser, "
                        + elementName + " isn't enabled.");
            }
            break;
        default:
            if (!present) {
                throw new PageValidationException(getClass().getSimpleName() + " isn't loaded in the browser, "
                        + elementName + " isn't present.");
            }
            break;
        }
    }

    /**
     * Verify's if the current page is opened in the browser. It does this based on the pageValidators.
     * 
     * @return boolean if page is opened.
     */
    public boolean isCurrentPageInBrowser() {
        try {
            validatePage();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
