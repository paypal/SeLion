/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

import io.appium.java_client.ios.IOSDriver;

import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebElement;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.mobile.ios.GestureOptions;
import com.paypal.selion.platform.mobile.ios.SeLionIOSBridgeDriver;
import com.paypal.selion.platform.mobile.ios.GestureOptions.TapOptions;
import com.paypal.selion.platform.mobile.ios.UIAElement;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * <code>SeLionAppiumIOSDriver</code> provides facility to add custom {@link CommandExecutor} to {@link IOSDriver}. This
 * class also implements the {@link SeLionIOSBridgeDriver} interface to expose methods for {@link UIAElement} and its
 * subclasses.
 */
public class SeLionAppiumIOSDriver extends IOSDriver implements SeLionIOSBridgeDriver {

    private static final SimpleLogger logger = SeLionLogger.getLogger();

    private static final String SCROLLTO_SCRIPT = "mobile: scrollTo";

    private static final String TAP_SCRIPT = "mobile: tap";

    private static final String ELEMENT = "element";

    protected JavascriptExecutor javaScriptExecutor;

    public SeLionAppiumIOSDriver(URL url, Capabilities caps) {
        super(url, caps);
        javaScriptExecutor = this;
    }

    public SeLionAppiumIOSDriver(CommandExecutor commandExecutor, Capabilities caps, URL url) {
        super(url, caps);
        setCommandExecutor(commandExecutor);
        javaScriptExecutor = this;
    }

    @Override
    public WebElement findElementBy(By by) {
        logger.entering(by);
        WebElement webElement = findElement(by);
        logger.exiting(webElement);
        return webElement;
    }

    @Override
    public void doubleTap(WebElement webElement) {
        logger.entering(webElement);
        String elementId = ((RemoteWebElement) webElement).getId();
        Map<String, String> optionsMap = createOptionsForDoubleTap(elementId, ONE_FINGER);
        javaScriptExecutor.executeScript(TAP_SCRIPT, optionsMap);
        logger.exiting();
    }

    @Override
    public void scrollToVisible(WebElement webElement) {
        logger.entering(webElement);
        String elementId = ((RemoteWebElement) webElement).getId();
        Map<String, String> arguments = new HashMap<String, String>();
        arguments.put(ELEMENT, elementId);
        javaScriptExecutor.executeScript(SCROLLTO_SCRIPT, arguments);
        logger.exiting();
    }

    @Override
    public void tap(WebElement webElement) {
        logger.entering(webElement);
        String elementId = ((RemoteWebElement) webElement).getId();
        Map<String, String> optionsMap = createOptionsForSingleTap(elementId, ONE_FINGER);
        javaScriptExecutor.executeScript(TAP_SCRIPT, optionsMap);
        logger.exiting();
    }

    @Override
    public void tapWithOptions(WebElement webElement, EnumMap<GestureOptions, String> gestureOptions) {
        logger.entering(new Object[] { webElement, gestureOptions });
        String elementId = ((RemoteWebElement) webElement).getId();
        Map<String, String> optionsMap = new HashMap<>();
        optionsMap.put(ELEMENT, elementId);
        for (Entry<GestureOptions, String> entry : gestureOptions.entrySet()) {
            optionsMap.put(entry.getKey().getOptionName(), entry.getValue());
        }
        javaScriptExecutor.executeScript(TAP_SCRIPT, optionsMap);
        logger.exiting();
    }

    @Override
    public void twoFingerTap(WebElement webElement) {
        logger.entering(webElement);
        String elementId = ((RemoteWebElement) webElement).getId();
        Map<String, String> optionsMap = createOptionsForSingleTap(elementId, TWO_FINGERS);
        javaScriptExecutor.executeScript(TAP_SCRIPT, optionsMap);
        logger.exiting();
    }

    @Override
    public void dragSliderToValue(WebElement webElement, double value) {
        logger.entering(new Object[] { webElement, value });
        String stringValue = String.valueOf(value);
        webElement.sendKeys(stringValue);
        logger.exiting();
    }

    @Override
    public void setPickerWheelValue(WebElement webElement, String value) {
        logger.entering(new Object[] { webElement, value });
        webElement.sendKeys(value);
        logger.exiting();
    }

    @Override
    public String getLabel(WebElement webElement) {
        logger.entering(webElement);
        String label = webElement.getAttribute("label");
        logger.exiting(label);
        return label;
    }

    @Override
    public String getName(WebElement webElement) {
        logger.entering(webElement);
        String name = webElement.getAttribute("name");
        logger.exiting(name);
        return name;
    }

    @Override
    public String getValue(WebElement webElement) {
        logger.entering(webElement);
        String value = webElement.getAttribute("value");
        logger.exiting(value);
        return value;
    }

    private Map<String, String> createOptionsForSingleTap(String elementId, String fingers) {
        TapOptions tapOptions = new TapOptions();
        tapOptions.setOption(GestureOptions.TAP_COUNT, Integer.parseInt(SINGLE_TAP));
        tapOptions.setOption(GestureOptions.TOUCH_COUNT, Integer.parseInt(fingers));
        tapOptions.setOption(GestureOptions.DURATION, Integer.parseInt(TAP));
        Map<String, String> optionsMap = tapOptions.asMap();
        optionsMap.put(ELEMENT, elementId);
        return optionsMap;
    }

    private Map<String, String> createOptionsForDoubleTap(String elementId, String fingers) {
        TapOptions tapOptions = new TapOptions();
        tapOptions.setOption(GestureOptions.TAP_COUNT, Integer.parseInt(DOUBLE_TAP));
        tapOptions.setOption(GestureOptions.TOUCH_COUNT, Integer.parseInt(fingers));
        tapOptions.setOption(GestureOptions.DURATION, Integer.parseInt(TAP));
        Map<String, String> optionsMap = tapOptions.asMap();
        optionsMap.put(ELEMENT, elementId);
        return optionsMap;
    }
}