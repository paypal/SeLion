/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-16 PayPal                                                                                       |
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

package com.paypal.selion.platform.grid;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map.Entry;

import com.paypal.selion.platform.mobile.elements.AbstractMobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CommandExecutor;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.selion.platform.mobile.ios.GestureOptions;
import com.paypal.selion.platform.mobile.ios.SeLionIOSBridgeDriver;
import com.paypal.selion.platform.mobile.ios.GestureOptions.TapOffset;
import com.paypal.selion.platform.mobile.ios.GestureOptions.TapOptions;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * <code>SelionRemoteIOSDriver</code> provides facility to add custom {@link CommandExecutor} to {@link RemoteIOSDriver}
 * . This class also implements the {@link SeLionIOSBridgeDriver} interface to expose methods for {@link AbstractMobileElement} and
 * its subclasses.
 *
 */
public class SelionRemoteIOSDriver extends RemoteIOSDriver {
    /**
     * Constant representing one finger
     */
    private static final String ONE_FINGER = "1";

    /**
     * Constant representing single tap
     */
    private static final String SINGLE_TAP = "1";

    /**
     * Constant representing double tap
     */
    private static final String DOUBLE_TAP = "2";

    /**
     * Constant representing tap gesture
     */
    private static final String TAP_DURATION = "0";

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    protected JavascriptExecutor javascriptExecutor; // NOSONAR

    public SelionRemoteIOSDriver(URL url, IOSCapabilities iOSCapabilities) {
        super(url, iOSCapabilities);
        javascriptExecutor = this;
    }

    public SelionRemoteIOSDriver(CommandExecutor command, IOSCapabilities capability) {
        this((URL) null, capability);
        setCommandExecutor(command);
    }

    public WebElement findElementBy(By by) {
        logger.entering(by);
        WebElement webElement = findElement(by);
        logger.exiting(webElement);
        return webElement;
    }

    public void doubleTap(WebElement webElement) {
        logger.entering(webElement);
        TapOptions tapOptions = createTapOptionsForDoubleTap(ONE_FINGER);
        javascriptExecutor.executeScript("arguments[0].tapWithOptions(" + tapOptions + ")", webElement);
        logger.exiting();
    }

    public void scrollToVisible(WebElement webElement) {
        logger.entering(webElement);
        javascriptExecutor.executeScript("arguments[0].scrollToVisible()", webElement);
        logger.exiting();
    }

    public void tap(WebElement webElement) {
        logger.entering(webElement);
        TapOptions tapOptions = createTapOptionsForSingleTap(ONE_FINGER);
        javascriptExecutor.executeScript("arguments[0].tapWithOptions(" + tapOptions + ")", webElement);
        logger.exiting();
    }

    public void tapWithOptions(WebElement webElement, EnumMap<GestureOptions, String> gestureOptions) {
        logger.entering(new Object[] { webElement, gestureOptions });
        try {
            TapOptions tapOptions = new TapOptions();
            for (Entry<GestureOptions, String> entry : gestureOptions.entrySet()) {
                tapOptions.setOption(entry.getKey(), Integer.parseInt(entry.getValue()));
            }
            setTapOffsetFromGestureOptions(tapOptions, gestureOptions);
            javascriptExecutor.executeScript("arguments[0].tapWithOptions(" + tapOptions + ")", webElement);
            logger.exiting();
        } catch (NumberFormatException nfex) {
            throw new UIOperationFailedException(
                    "NumberFormatException in parsing Options map (possibly non integer value received for integer option)",
                    nfex);
        } catch (JsonSyntaxException jsex) {
            throw new UIOperationFailedException("JsonSyntaxException in options: " + gestureOptions, jsex);
        }
    }

    public void twoFingerTap(WebElement webElement) {
        logger.entering(webElement);
        TapOptions tapOptions = new TapOptions();
        tapOptions.setOption(GestureOptions.TAP_COUNT, 1);
        tapOptions.setOption(GestureOptions.TOUCH_COUNT, 2);
        tapOptions.setOption(GestureOptions.DURATION, 0);
        javascriptExecutor.executeScript("arguments[0].tapWithOptions(" + tapOptions + ")", webElement);
        logger.exiting();
    }

    public void dragSliderToValue(WebElement webElement, double value) {
        logger.entering(new Object[] { webElement, value });
        javascriptExecutor.executeScript("arguments[0].dragToValue(" + value + ")", webElement);
        logger.exiting();
    }

    public void setPickerWheelValue(WebElement webElement, String value) {
        logger.entering(new Object[] { webElement, value });
        javascriptExecutor.executeScript("arguments[0].selectValue('" + value + "')", webElement);
        logger.exiting();
    }

    public String getLabel(WebElement webElement) {
        logger.entering(webElement);
        String label = webElement.getAttribute("label");
        logger.exiting(label);
        return label;
    }

    public String getName(WebElement webElement) {
        logger.entering(webElement);
        String name = webElement.getAttribute("name");
        logger.exiting(name);
        return name;
    }

    public String getValue(WebElement webElement) {
        logger.entering(webElement);
        String value = webElement.getAttribute("value");
        logger.exiting(value);
        return value;
    }

    private void setTapOffsetFromGestureOptions(TapOptions tapOptions, EnumMap<GestureOptions, String> gestureOptions) {
        if (gestureOptions.containsKey(GestureOptions.TAP_OFFSET)) {
            Gson gson = new GsonBuilder().create();
            tapOptions.setOffset(gson.fromJson(gestureOptions.get(GestureOptions.TAP_OFFSET), TapOffset.class));
        }
    }

    private TapOptions createTapOptionsForSingleTap(String fingers) {
        TapOptions tapOptions = new TapOptions();
        tapOptions.setOption(GestureOptions.TAP_COUNT, Integer.parseInt(SINGLE_TAP));
        tapOptions.setOption(GestureOptions.TOUCH_COUNT, Integer.parseInt(fingers));
        tapOptions.setOption(GestureOptions.DURATION, Integer.parseInt(TAP_DURATION));
        return tapOptions;
    }

    private TapOptions createTapOptionsForDoubleTap(String fingers) {
        TapOptions tapOptions = new TapOptions();
        tapOptions.setOption(GestureOptions.TAP_COUNT, Integer.parseInt(DOUBLE_TAP));
        tapOptions.setOption(GestureOptions.TOUCH_COUNT, Integer.parseInt(fingers));
        tapOptions.setOption(GestureOptions.DURATION, Integer.parseInt(TAP_DURATION));
        return tapOptions;
    }
}
