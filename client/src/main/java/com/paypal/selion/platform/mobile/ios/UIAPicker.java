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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * The <code>UIAPicker</code> class allows access to, and control of, wheel elements within a picker
 */
public class UIAPicker extends UIAElement implements UIAutomationPicker {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    private static final String VALUES = "values";
    
    private static final String UIAPICKERWHEEL = "UIAPickerWheel";

    public UIAPicker(String locator) {
        super(locator);
    }

    @Override
    public List<String> getValuesOfWheelAtIndex(int index) {
        logger.entering(index);
        WebElement pickerWheel = findWheelAtIndex(index);
        String allValues = pickerWheel.getAttribute(VALUES);
        List<String> list = asList(allValues);
        logger.exiting(list);
        return list;
    }

    @Override
    public void setValueOfWheelAtIndex(int index, String value) {
        logger.entering(new Object[] { index, value });
        if (value != null) {
            WebElement pickerWheel = findWheelAtIndex(index);
            getBridgeDriver().setPickerWheelValue(pickerWheel, value);
        }
        logger.exiting();
    }

    private WebElement findWheelAtIndex(int index) {
        WebElement pickerView = findElement(getLocator());
        List<WebElement> pickerCells = pickerView.findElements(By.className(UIAPICKERWHEEL));
        if (!pickerCells.isEmpty() && index < pickerCells.size()) {
            return pickerCells.get(index);
        }
        throw new UIOperationFailedException("UIAPicker does not have any picker wheel at index: " + index);
    }

    private List<String> asList(String allValues) {
        List<String> valuesList = Collections.emptyList();
        if (!StringUtils.isBlank(allValues)) {
            String tempAllValues = allValues.trim().substring(1, allValues.length() - 1);
            valuesList = Arrays.asList(tempAllValues.split("\\s*,\\s*"));
        }
        return valuesList;
    }

}
