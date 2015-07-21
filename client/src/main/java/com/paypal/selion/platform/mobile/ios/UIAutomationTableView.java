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

import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * <code>UIAutomationTableView</code> interface allows access to, and control of, elements within a table view in your
 * app.
 */
public interface UIAutomationTableView extends UIAutomationElement {

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
     *            Expected entities in the form of objects extending {@link UIAElement} or xpath location in the form of
     *            {@link String} or instances of {@link ExpectedCondition}.
     */
    void clickCellAtIndex(int index, Object... expected);

}
