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
 * The <code>UIAAlert</code> class allows access to, and control of, alerts within your app.
 */
public class UIAAlert extends UIAElement implements UIAutomationAlert {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    private static final String UIACOLLECTIONVIEW = "UIACollectionView";

    private static final String UIACOLLECTIONCELL = "UIACollectionCell";

    public UIAAlert(String locator) {
        super(locator);
    }

    @Override
    public void clickCancelButton(Object... expected) {
        logger.entering(expected);
        WebElement alertView = findElement(getLocator());
        WebElement collectionView = alertView.findElement(By.className(UIACOLLECTIONVIEW));
        List<WebElement> alertButtons = collectionView.findElements(By.className(UIACOLLECTIONCELL));
        if (!alertButtons.isEmpty()) {
            getBridgeDriver().tap(alertButtons.get(0));
            if (!ArrayUtils.isEmpty(expected)) {
                waitFor(expected);
            }
            logger.exiting();
            return;
        }
        throw new UIOperationFailedException("UIAAlert does not have any button at index: 0");
    }

    @Override
    public void clickButtonAtIndex(int index, Object... expected) {
        logger.entering(new Object[] {index, expected});
        WebElement alertView = findElement(getLocator());
        WebElement collectionView = alertView.findElement(By.className(UIACOLLECTIONVIEW));
        List<WebElement> alertButtons = collectionView.findElements(By.className(UIACOLLECTIONCELL));
        if (!alertButtons.isEmpty() && index < alertButtons.size()) {
            getBridgeDriver().tap(alertButtons.get(index));
            if (!ArrayUtils.isEmpty(expected)) {
                waitFor(expected);
            }
            logger.exiting();
            return;
        }
        throw new UIOperationFailedException("UIAAlert does not have any button at index: " + index
                + ", possible indices range from [ 0 . . " + (alertButtons.size() - 1) + " ]");
    }

}
