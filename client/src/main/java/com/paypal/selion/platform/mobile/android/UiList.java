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

package com.paypal.selion.platform.mobile.android;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.html.support.HtmlElementUtils;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.selion.platform.mobile.elements.MobileList;
import com.paypal.test.utilities.logging.SimpleLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * <code>UiList</code> represents a List widget for Android UI automation framework.
 */
public class UiList extends UiObject implements MobileList {
    private static final SimpleLogger logger = SeLionLogger.getLogger();
    private By childBy;

    public UiList(String locator) {
        super(locator);
    }

    @Override
    public void setChildBy(String childLocator){
        setChildBy(HtmlElementUtils.resolveByType(childLocator));
    }

    @Override
    public void setChildBy(By childBy) {
        this.childBy = childBy;
    }

    @Override
    public void scrollToCellAtIndex(int index) {
        throw new UIOperationFailedException("scrollToCellAtIndex() method is not supported in Android platform.");
    }

    @Override
    public void clickCellAtIndex(int index, Object... expected) {
        logger.entering(index, expected);
        WebElement element = findElementAtIndex(index);
        initBridgeDriver();
        driver.click(element);
        waitFor(expected);
        logger.exiting();
    }

    @Override
    public WebElement findElementAtIndex(int index) {
        List<WebElement> tableCells = getChildren();
        if (!tableCells.isEmpty() && index < tableCells.size()) {
            return tableCells.get(index);
        }
        throw new UIOperationFailedException("UiList does not have any row at index: " + index);
    }

    @Override
    public int childrenCount() {
        return getChildren().size();
    }

    @Override
    public List<WebElement> getChildren() {
        WebElement tableView = findElement(getLocator());
        if (childBy == null) {
            throw new UIOperationFailedException("for Android list, cast list to UiList and set the childBy.");
        }
        return tableView.findElements(childBy);
    }
}
