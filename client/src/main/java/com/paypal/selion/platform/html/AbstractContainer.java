/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import com.paypal.selion.platform.html.support.HtmlElementUtils;
import com.paypal.selion.platform.html.support.ParentNotFoundException;
import com.paypal.selion.testcomponents.BasicPageImpl;

/**
 * This abstract class is meant to be extended to create a web element Container wrapper<br>
 * <br>
 * <p>
 * <table border=1>
 * <thead>
 * <th>Use case</th>
 * <th>Usage</th>
 * </thead> <tbody>
 * <tr>
 * <td><b><i> A stand alone container</b></i>
 * 
 * <pre>
 * class MyContainer extends AbstractContainer {
 * 
 *     public MyContainer(String locator) {
 *         super(locator);
 *     }
 * 
 *     public MyContainer(String locator, String controlName) {
 *         super(locator, controlName);
 *     }
 * 
 *     private TextField myTextField = new TextField(this, &quot;id=nameTextField&quot;);
 * 
 *     public TextField getMyTextField() {
 *         return myTextField;
 *     }
 * }
 * </pre>
 * 
 * </td>
 * <td>
 * 
 * <pre>
 * MyContainer sampleContainer = new MyContainer("id=sampleLocator");
 *    
 *    <u>Locate an element at index 0 inside the container</u>
 *    
 *    sampleContainer.locateElement(0, "A locator to be searched in a container") ;
 *    
 *    <u>To Retrieve the no. of containers</u> 
 *    
 *    sampleContainer.size();
 *    
 *    <u>To perform an operation with an element inside the container</u>
 *    
 *    sampleContainer.getMyTextField().type("Hi! Welcome");
 *    
 *     <b><h2>Note:</b> A stand alone constructor does not have a parent by default.
 *      Hence access to method getCurrentPage() will resolve to null.
 * </pre>
 * 
 * </td>
 * </tr>
 * <tr>
 * <td>
 * <b><i>A container inside a page</i></b>
 * 
 * <pre>
 * public class MyPage extends BasePageImpl {
 *     private MyContainer myContainer;
 * 
 *     public MyPage() {
 *         super.initPage(PAGE_DOMAIN, CLASS_NAME);
 *     }
 * 
 *     public MyPage(String siteLocale) {
 *         super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
 *     }
 * 
 *     public MyPage getPage() {
 *         if (!isInitialized()) {
 *             loadObjectMap();
 *             initializeHtmlObjects(this, this.objectMap);
 *         }
 *         return this;
 *     }
 * 
 *     public MyContainer getMyContainer() {
 *        return getPage().myContainer;
 *     }
 * 
 *     public MyContainer getMyContainer(int index) {
 *         getPage().myContainer.setIndex(index);
 *         return myContainer;
 *     }
 * 
 *   <b> Container creation </b>
 *     class MyContainer extends AbstractContainer {
 *         public MyContainer(String locator) {
 *             super(locator);
 *         }
 * 
 *         public MyContainer(String locator, String controlName) {
 *             super(locator, controlName);
 *         }
 * 
 *         private TextField myTextField;
 * 
 *         public TextField getMyTextField() {
 *             return myTextField;
 *         }
 *     }
 * }
 * </pre>
 * 
 * </td>
 * <td>
 * 
 * <pre>
 * <u>To retrieve a child element from a container at a specified index:</u>
 * 
 * <pre>
 * MyPage myPage = new MyPage();
 * myPage.getMyContainer(1).getMyTextField();
 * </pre>
 * 
 * <u>To retrieve a child element from a container at the last specified index or 0 if no index has ever been
 * specified:</u>
 * 
 * <pre>
 * MyPage myPage = new MyPage();
 * myPage.getMyContainer().getMyTextField();
 * </pre>
 * 
 * <u>To retrieve the number of containers found on the page:</u>
 * 
 * <pre>
 * MyPage myPage = new MyPage();
 * myPage.getMyContainer().size();
 * 
 * <h2><b>Note:</b> In this use case, access to getCurrentPage will resolve to the page object where the container is present.
 * In this case it is the MyPage.
 * 
 * </pre>
 * 
 * </td>
 * </tr>
 * </table>
 * </p>
 */
public abstract class AbstractContainer extends AbstractElement implements ParentTraits {

    private int index;
    protected Map<String, String> containerElements;

    /**
     * Constructs a Container with locator.
     * 
     * @param locator
     *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * 
     */
    public AbstractContainer(String locator) {
        this(locator, null);
    }

    /**
     * Constructs a Container with locator and controlName.
     * 
     * @param locator
     *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            The control name used for logging.
     */
    public AbstractContainer(String locator, String controlName) {
        this(locator, controlName, null);
    }

    /**
     * Constructs a {@link Container} with locator, controlName and a parent
     * 
     * @param locator
     *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            The control name used for logging.
     * @param parent
     *            A {@link ParentTraits} object that represents the parent element for this element.
     */
    public AbstractContainer(String locator, String controlName, ParentTraits parent) {
        super(locator, controlName, parent);
    }
    
    /**
     * Constructs a {@link Container} with locator, controlName, parent and containerElements
     * 
     * @param locator
     *            A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            The control name used for logging.
     * @param parent
     *            A {@link ParentTraits} object that represents the parent element for this element.
     * @param containerElements
     *            A {@link Map} containing the locators for elements inside this container.
     */
    public AbstractContainer(String locator, String controlName, ParentTraits parent,Map<String,String> containerElements){
        super(locator,controlName,parent);
        this.containerElements = containerElements; 
    }

    /**
     * Sets the index at which the element will be returned when {@link #getElement()} is called.
     * 
     * @param index
     *            The index at which the element will be returned when getElement() is called.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Used to call {@link HtmlElementUtils#locateElements(String) locateElements} and returns the element at current
     * index (which was set via {@link #setIndex(int)})
     * 
     * @return The web element found by locator at current index
     */
    @Override
    public RemoteWebElement getElement() {
        /*
         * Note: Rationale behind throwing ParentNotFoundException here.
         * 
         * Container's being a parent type is searched using a locator. When the locator is invalid it will be better to
         * throw ParentNotFoundException to the user so that it clearly indicates its the container.
         */
        List<WebElement> elements = null;
        try {
            if (getParent() != null) {
                elements = getParent().locateChildElements(getLocator());
            } else {
                // Its a case where there is a stand alone container and no parent
                elements = HtmlElementUtils.locateElements(getLocator());
            }
        } catch (NoSuchElementException n) {
            throw new ParentNotFoundException("Could not find any parent with the locator " + getLocator(), n);
        }
        if (index <= elements.size()) {
            return (RemoteWebElement) elements.get(index);
        }
        throw new NoSuchElementException("Cannot find Element With index :{" + index + "} in Container"
                + this.getClass().getSimpleName());
    }

    /**
     * Returns the number of containers found on the page.
     * 
     * @return the number of containers found on the page
     */
    public int size() {
        int size = 0;
        try {
            if (getParent() != null) {
                size = getParent().locateChildElements(getLocator()).size();
            } else {
                size = HtmlElementUtils.locateElements(getLocator()).size();
            }
        } catch (NoSuchElementException e) { // NOSONAR
            // do nothing, let size be returned as 0
        }

        return size;
    }

    /**
     * Sets the container index and searches for the descendant element using the child locator.
     * 
     * @param index
     *            index of the container element to search on
     * @param childLocator
     *            locator of the child element within the container
     * @return child WebElement found using child locator at the indexed container
     */
    public WebElement locateElement(int index, String childLocator) {
        if (index < 0) {
            throw new IllegalArgumentException("index cannot be a negative value");
        }
        setIndex(index);
        WebElement locatedElement = null;
        if (getParent() != null) {
            locatedElement = getParent().locateChildElement(childLocator);
        } else {
            locatedElement = HtmlElementUtils.locateElement(childLocator, this);
        }
        return locatedElement;
    }

    public List<WebElement> locateChildElements(String locator) {
        HtmlElementUtils.isValidXpath(locator);
        By locatorBy = HtmlElementUtils.resolveByType(locator);
        return this.getElement().findElements(locatorBy);
    }

    public RemoteWebElement locateChildElement(String locator) {
        HtmlElementUtils.isValidXpath(locator);
        By locatorBy = HtmlElementUtils.resolveByType(locator);
        return (RemoteWebElement) this.getElement().findElement(locatorBy);
    }

    public BasicPageImpl getCurrentPage() {
        return (BasicPageImpl) this.getParent();
    }

}
