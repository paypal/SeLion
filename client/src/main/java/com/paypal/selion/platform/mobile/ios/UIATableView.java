/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * <code>UIATableView</code> class allows access to, and control of, elements within a table view in your app.
 */
public class UIATableView extends UIAElement implements UIAutomationTableView {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    private static final String UIATABLECELL = "UIATableCell";

    public UIATableView(String locator) {
        super(locator);
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
        logger.entering(new Object[] { index, expected });
        WebElement tableCell = findElementAtIndex(index);
        getBridgeDriver().tap(tableCell);
        if (!ArrayUtils.isEmpty(expected)) {
            waitFor(expected);
        }
        logger.exiting();

    }

    private WebElement findElementAtIndex(int index) {
        WebElement tableView = findElement(getLocator());
        List<WebElement> tableCells = tableView.findElements(By.className(UIATABLECELL));
        if (!tableCells.isEmpty() && index < tableCells.size()) {
            return tableCells.get(index);
        }
        throw new UIOperationFailedException("UIATableView does not have any cell at index: " + index);
    }
}
