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

import com.paypal.selion.platform.mobile.Implementor;
import com.paypal.selion.platform.mobile.android.UiList;
import com.paypal.selion.platform.mobile.ios.UIAList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;

/**
 * <code>MobileList</code> interface allows access to, and control of, elements within a list in your app.
 */
@Implementor(android = UiList.class, ios = UIAList.class)
public interface MobileList extends MobileElement {
    /**
     * set the element that should be searched inside a list
     * 
     * @param childLocator
     *            the locator String for child element
     */
    void setChildBy(String childLocator);

    /**
     * set the element that should be searched inside a list
     * 
     * @param childBy
     *            the locator for child element
     */
    void setChildBy(By childBy);

    /**
     * Scrolls to the table cell at the specified index. The index in 0 based.
     *
     * @param index
     *            Index of the cell to scroll to.
     */
    void scrollToCellAtIndex(int index);

    /**
     * Clicks the table cell at the specified index. The index in 0 based.
     *
     * @param index
     *            Index of the cell to click.
     * @param expected
     *            Expected entities in the form of objects extending {@link MobileElement} or xpath location in the form
     *            of {@link String} or instances of {@link ExpectedCondition}.
     */
    void clickCellAtIndex(int index, Object... expected);

    /**
     * find and return the element at requested index of list
     * 
     * @param index
     *            Index of the cell to click.
     * @return {@link WebElement} the element at given index
     */
    WebElement findElementAtIndex(int index);

    /**
     * find the children of list and return count of them. for android, it will return only the count on elements in
     * current view, not the whole list.
     * 
     * @return count of children in list.
     */
    int childrenCount();

    /**
     * return list of children that matches the child set (or in ios case default child)
     * 
     * @return list of children
     */
    List<WebElement> getChildren();
}
