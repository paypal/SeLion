/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

package com.paypal.selion.platform.mobile.elements;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.MobileGrid;
import com.paypal.selion.platform.html.support.HtmlElementUtils;
import com.paypal.selion.platform.mobile.Implementor;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.selion.platform.mobile.android.UiList;
import com.paypal.selion.platform.mobile.ios.UIAList;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import com.paypal.test.utilities.logging.SimpleLogger;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;

/**
 * <code>MobileList</code> interface allows access to, and control of, elements within a list in your app.
 */
@Implementor(android = UiList.class, ios = UIAList.class)
public abstract class MobileList extends AbstractMobileElement {
    protected static final SimpleLogger logger = SeLionLogger.getLogger();
    protected By childBy;

    public MobileList(String locator) {
        super(locator);
    }

    /**
     * set the element that should be searched inside a list
     *
     * @param childLocator the locator String for child element
     */
    public void setChildBy(String childLocator) {
        setChildBy(HtmlElementUtils.resolveByType(childLocator));
    }

    /**
     * set the element that should be searched inside a list
     *
     * @param childBy the locator for child element
     */
    public void setChildBy(By childBy) {
        this.childBy = childBy;
    }
    /**
     * Scrolls to the table cell at the specified index. The index in 0 based.
     *
     * @param index Index of the cell to scroll to.
     */
    public abstract void scrollToCellAtIndex(int index);

    /**
     * Clicks the table cell at the specified index. The index in 0 based.
     *
     * @param index    Index of the cell to click.
     * @param expected Expected entities in the form of objects extending {@link AbstractMobileElement} or xpath location in the form
     *                 of {@link String} or instances of {@link ExpectedCondition}.
     */
    public void clickCellAtIndex(int index, Object... expected) {
        logger.entering(index, expected);
        MobileElement element = findElementAtIndex(index);
        element.click();
        WebDriverWaitUtils.waitFor(expected);
        logger.exiting();
    }

    /**
     * find and return the element at requested index of list
     *
     * @param index Index of the cell to click.
     * @return {@link MobileElement} the element at given index
     */
    public MobileElement findElementAtIndex(int index) {
        List<MobileElement> tableCells = getChildren();
        if (!tableCells.isEmpty() && index < tableCells.size()) {
            return tableCells.get(index);
        }
        throw new UIOperationFailedException("List does not have any cell at index: " + index);
    }

    /**
     * find the children of list and return count of them. for android, it will return only the count on elements in
     * current view, not the whole list.
     *
     * @return count of children in list.
     */
    public int childrenCount() {
        return getChildren().size();
    }

    /**
     * return list of children that matches the child set (or in ios case default child)
     *
     * @return list of children
     */
    public List<MobileElement> getChildren() {
        MobileElement tableView = MobileGrid.mobileDriver().findElement(HtmlElementUtils.resolveByType(getLocator()));
        if (childBy == null) {
            throw new UIOperationFailedException("for Android list, cast list to UiList and set the childBy.");
        }
        return tableView.findElements(childBy);
    }
}
