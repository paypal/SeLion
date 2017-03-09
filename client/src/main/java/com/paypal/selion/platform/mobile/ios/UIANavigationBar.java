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

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.selion.platform.mobile.elements.AbstractMobileElement;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import com.paypal.test.utilities.logging.SimpleLogger;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.List;
import java.util.ListIterator;

/**
 * The <code>UIANavigationBar</code> class allows access to, and control of, buttons in your app’s navigation bar.
 */
public class UIANavigationBar extends AbstractMobileElement {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    private static final String BACK = "Back";

    private static final String LABEL = "label";

    private static final String NAME = "name";

    private static final String UIABUTTON = "UIAButton";

    public UIANavigationBar(String locator) {
        super(locator);
    }

    public void clickLeftButton(Object... expected) {
        logger.entering();
        List<MobileElement> buttonElements = getMobileElement().findElements(By.className(UIABUTTON));
        MobileElement leftButton = findLeftButton(buttonElements);
        leftButton.click();
        WebDriverWaitUtils.waitFor(expected);
        logger.exiting();
    }

    public void clickRightButton(Object... expected) {
        logger.entering();
        List<MobileElement> buttonElements = getMobileElement().findElements(By.className(UIABUTTON));
        MobileElement rightButton = findRightButton(buttonElements);
        rightButton.click();
        WebDriverWaitUtils.waitFor(expected);
        logger.exiting();
    }

    private MobileElement findLeftButton(List<MobileElement> buttonElements) {
        if (buttonElements.size() >= 1) {
            for (MobileElement button : buttonElements) {
                if (BACK.equals(button.getAttribute(LABEL)) || BACK.equals(button.getAttribute(NAME))) {
                    return button;
                }
            }

            /*
             * Assumption that 'if the default left button with the label "BACK" is not present in the iOS app, consider
             * the button in the first index to be back button'. Tests on a normal iOS app shows that a
             * WebDriverException is thrown if the button at the first index of a navigation bar in the first screen is
             * operated.
             */
            return buttonElements.get(0);
        }
        throw new UIOperationFailedException("UIANavigationBar does not have any left button");
    }

    private MobileElement findRightButton(List<MobileElement> buttonElements) {
        if (buttonElements.size() >= 1) {

            /*
             * Assumption that 'right navigation button is seated on an index greater than 0'
             */
            int index = buttonElements.size() - 1;
            ListIterator<MobileElement> listIterator = buttonElements.listIterator(buttonElements.size());
            while (listIterator.hasPrevious()) {
                MobileElement button = listIterator.previous();
                if (!BACK.equals(button.getAttribute(LABEL)) && !BACK.equals(button.getAttribute(NAME)) && index > 0) {
                    return button;
                }
                --index;
            }
        }
        throw new UIOperationFailedException("UIANavigationBar does not have any right button");
    }

    public String getName() {
        return getAttribute("name");
    }
}
