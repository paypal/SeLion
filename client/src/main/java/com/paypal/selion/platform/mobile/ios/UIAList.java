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

import com.paypal.selion.platform.grid.MobileGrid;
import com.paypal.selion.platform.html.support.ByOrOperator;
import com.paypal.selion.platform.mobile.elements.MobileList;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

/**
 * <code>UIATableView</code> class allows access to, and control of, elements within a table view in your app.
 */
public class UIAList extends MobileList {

    public UIAList(String locator) {
        super(locator);
        childBy = new ByOrOperator(
            By.className("UIACollectionCell"),
            By.className("UIATableCell"));
    }
    @Override
    public void scrollToCellAtIndex(int index) {
        logger.entering(index);
        MobileElement tableCell = findElementAtIndex(index);
        MobileGrid.iOSDriver().scrollToVisible(tableCell);
        logger.exiting();
    }
}
