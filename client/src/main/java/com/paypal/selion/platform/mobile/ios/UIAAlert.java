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

import com.paypal.selion.platform.mobile.elements.AbstractMobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.test.utilities.logging.SimpleLogger;

import io.appium.java_client.MobileElement;

/**
 * The <code>UIAAlert</code> class allows access to, and control of, alerts within your app.
 */
public class UIAAlert extends AbstractMobileElement {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    private static final String UIACOLLECTIONVIEW = "UIACollectionView";

    private static final String UIACOLLECTIONCELL = "UIACollectionCell";

    public UIAAlert(String locator) {
        super(locator);
    }

    public void clickCancelButton(Object... expected) {
        logger.entering(expected);
        MobileElement collectionView = getMobileElement().findElement(By.className(UIACOLLECTIONVIEW));
        List<MobileElement> alertButtons = collectionView.findElements(By.className(UIACOLLECTIONCELL));
        if (!alertButtons.isEmpty()) {
        	alertButtons.get(0).click();
            logger.exiting();
            return;
        }
        throw new UIOperationFailedException("UIAAlert does not have any button at index: 0");
    }

    public void clickButtonAtIndex(int index, Object... expected) {
        logger.entering(index, expected);
        WebElement collectionView = getMobileElement().findElement(By.className(UIACOLLECTIONVIEW));
        List<WebElement> alertButtons = collectionView.findElements(By.className(UIACOLLECTIONCELL));
        if (!alertButtons.isEmpty() && index < alertButtons.size()) {
            if (!alertButtons.isEmpty()) {
            	alertButtons.get(0).click();
                logger.exiting();
                return;
            }
            logger.exiting();
            return;
        }
        throw new UIOperationFailedException("UIAAlert does not have any button at index: " + index
                + ", possible indices range from [ 0 . . " + (alertButtons.size() - 1) + " ]");
    }

}
