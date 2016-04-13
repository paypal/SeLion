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

package com.paypal.selion.platform.mobile;

import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.platform.mobile.android.UiButton;
import com.paypal.selion.platform.mobile.elements.MobileElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * find the correct implementation for given interface
 */
public class MobileImplementationFinderTest {

    @Test(groups = "unit")
    public void testInstantiateDirectClass() throws Exception {
        final MobileElement button = MobileImplementationFinder.instantiate(WebDriverPlatform.UNDEFINED,
                UiButton.class, "//locator");
        Assert.assertEquals(button.getLocator(), "//locator", "locator is wrong");
        Assert.assertEquals(button.getClass(), UiButton.class, "wrong class associated");
    }
}