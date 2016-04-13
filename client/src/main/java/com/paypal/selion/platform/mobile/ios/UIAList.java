/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                     |
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

package com.paypal.selion.platform.mobile.ios;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.html.support.ByOrOperator;
import com.paypal.selion.platform.html.support.HtmlElementUtils;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.selion.platform.mobile.elements.MobileList;
import com.paypal.test.utilities.logging.SimpleLogger;
import org.apache.commons.lang.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * <code>UIATableView</code> class allows access to, and control of, elements within a table view in your app.
 */
public class UIAList extends UIAElement implements UIAutomationTableView, MobileList {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    private By childBy = new ByOrOperator(
            By.className("UIACollectionCell"),
            By.className("UIATableCell"));

    public UIAList(String locator) {
        super(locator);
    }

    @Override
    public void setChildBy(String childLocator) {
        setChildBy(HtmlElementUtils.resolveByType(childLocator));
    }

    @Override
    public void setChildBy(By childBy) {
        this.childBy = childBy;
    }

    @Override
    public void scrollToCellAtIndex(int index) {
        logger.entering(index);
        WebElement tableCell = findElementAtIndex(index);
        getBridgeDriver().scrollToVisible(tableCell);
        logger.exiting();
    }

    @Override
    public void clickCellAtIndex(int index, Object... expected) {
        logger.entering(index, expected);
        WebElement tableCell = findElementAtIndex(index);
        getBridgeDriver().tap(tableCell);
        if (!ArrayUtils.isEmpty(expected)) {
            waitFor(expected);
        }
        logger.exiting();

    }

    @Override
    public WebElement findElementAtIndex(int index) {
        List<WebElement> tableCells = getChildren();
        if (!tableCells.isEmpty() && index < tableCells.size()) {
            return tableCells.get(index);
        }
        throw new UIOperationFailedException("List does not have any cell at index: " + index);
    }

    @Override
    public int childrenCount() {
        return getChildren().size();
    }

    @Override
    public List<WebElement> getChildren() {
        WebElement tableView = findElement(getLocator());
        return tableView.findElements(childBy);
    }
}
