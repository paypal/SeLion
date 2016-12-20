/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

package com.paypal.selion.platform.mobile.elements;

import com.paypal.selion.platform.grid.MobileGrid;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.google.common.base.Function;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.AbstractElement;
import com.paypal.selion.platform.html.support.HtmlElementUtils;
import com.paypal.test.utilities.logging.SimpleLogger;

import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import io.appium.java_client.TouchAction;

/**
 * <code> MobileElement </code> is the super interface for all user interface
 * elements in the context of the Automation instrument for automating user
 * interface testing. This interface defines more general methods that can be
 * used on any type of user interface elements.
 */
public class AbstractMobileElement extends AbstractElement {

	private static final int SWIPE_DURATION_MILLI_SECONDS = 2000;

	private static final SimpleLogger logger = SeLionLogger.getLogger();

	public AbstractMobileElement(String locator) {
		super(locator);
	}

	public boolean isCheckable() {
		logger.entering();
		boolean result = Boolean.parseBoolean(getMobileElement().getAttribute("checkable"));
		logger.exiting(result);
		return result;
	}

	public boolean isChecked() {
		logger.entering();
		boolean result = Boolean.parseBoolean(getMobileElement().getAttribute("checked"));
		logger.exiting(result);
		return result;
	}

	public boolean isClickable() {
		logger.entering();
		boolean result = Boolean.parseBoolean(getMobileElement().getAttribute("clickable"));
		logger.exiting(result);
		return result;
	}

	public boolean isFocusable() {
		logger.entering();
		boolean result = Boolean.parseBoolean(getMobileElement().getAttribute("focusable"));
		logger.exiting(result);
		return result;
	}

	public boolean isFocused() {
		logger.entering();
		boolean result = Boolean.parseBoolean(getMobileElement().getAttribute("focused"));
		logger.exiting(result);
		return result;
	}

	public boolean isLongClickable() {
		logger.entering();
		boolean result = Boolean.parseBoolean(getMobileElement().getAttribute("longClickable"));
		logger.exiting(result);
		return result;
	}

	public boolean isScrollable() {
		logger.entering();
		boolean result = Boolean.parseBoolean(getMobileElement().getAttribute("scrollable"));
		logger.exiting(result);
		return result;
	}

	public boolean isEnabled() {
		logger.entering();
		boolean result = getMobileElement().isEnabled();
		logger.exiting(result);
		return result;
	}

	public boolean isSelected() {
		logger.entering();
		boolean result = Boolean.parseBoolean(getMobileElement().getAttribute("selected"));
		logger.exiting(result);
		return result;
	}

	public void longPress(Object... expected) {
		logger.entering();
		Function<?, ?> tapFunc = new Function<MobileElement, MobileElement>() {

			@Override
			public MobileElement apply(MobileElement input) {
				getMobileElement().tap(1, 2000);
				return input;
			}
		};
		click(tapFunc, expected);
		logger.exiting();
	}

	public void scrollToVisible() {
		scrollToVisible(SwipeElementDirection.UP);
	}

	public void scrollToVisible(SwipeElementDirection direction) {
		boolean android = Grid.driver() instanceof AndroidDriver;
		AbstractMobileElement page = new AbstractMobileElement(android?"//*":"//UIAWindow[1]");
		for (int i = 0; i < 9; i++) {
			if ((android && isElementPresent()) ||
					(!android && getMobileElement().isDisplayed())) {
				return;
			}
			page.swipe(direction);
		}
		addInfoForNoSuchElementException(new NoSuchElementException(
				"Cannot locate an element using " + HtmlElementUtils.resolveByType(getLocator())));
	}

	public MobileElement getMobileElement() {
		return MobileElement.class.cast(getElement());
	}

	public MobileElement getMobileElement(WebElement element) {
		return MobileElement.class.cast(element);
	}

	public void swipeLeft() {
		swipe(SwipeElementDirection.LEFT);
	}

	public void swipeRight() {
		swipe(SwipeElementDirection.RIGHT);
	}

	public void swipeUp() {
		swipe(SwipeElementDirection.UP);
	}

	public void swipeDown() {
		swipe(SwipeElementDirection.DOWN);
	}

	public void swipe(SwipeElementDirection direction){
		getMobileElement().swipe(direction, 200, 200, SWIPE_DURATION_MILLI_SECONDS);
	}

	public void tap(Object... expected) {
		tap(getMobileElement(), expected);
	}

	protected void tap(final MobileElement element, Object... expected) {
		Function<?, ?> tapFunc = new Function<MobileElement, MobileElement>() {

			@Override
			public MobileElement apply(MobileElement input) {
				element.tap(1, 100);
				return input;
			}
		};
		click(tapFunc, expected);
	}

	public void tapInMiddle(Object... expected) {
		Function<?, ?> tapFunc = new Function<MobileElement, MobileElement>() {

			@Override
			public MobileElement apply(MobileElement input) {
				Point p = getMobileElement().getCenter();
                MobileGrid.mobileDriver().tap(1, p.getX(), p.getY(), 100);
				return input;
			}
		};
		click(tapFunc, expected);
	}

	public void tapBottomRight(Object... expected) {
		logger.entering();
		Function<?, ?> tapFunc = new Function<MobileElement, MobileElement>() {

			@Override
			public MobileElement apply(MobileElement input) {
				Point currentPoint = getMobileElement().getLocation();
				Dimension dimension = getMobileElement().getSize();
				TouchAction clickBottomRight = new TouchAction(MobileGrid.mobileDriver());
				int newX = currentPoint.getX() + dimension.getWidth();
				int newY = currentPoint.getY() + dimension.getHeight();
				clickBottomRight.longPress(newX - 1, newY - 1).release().perform();
				return input;
			}
		};
		click(tapFunc, expected);
		logger.exiting();
	}

	public void tapTopLeft(Object... expected) {
		logger.entering();
		Function<?, ?> tapFunc = new Function<MobileElement, MobileElement>() {

			@Override
			public MobileElement apply(MobileElement input) {
				Point currentPoint = getMobileElement().getLocation();
				TouchAction clickTopLeft = new TouchAction(MobileGrid.mobileDriver());
				clickTopLeft.longPress(currentPoint.getX(), currentPoint.getY()).release().perform();
				return input;
			}
		};
		click(tapFunc, expected);
		logger.exiting();
	}

	public void zoom() {
		getMobileElement().zoom();
	}

	public void pinch() {
		getMobileElement().pinch();
	}

}
