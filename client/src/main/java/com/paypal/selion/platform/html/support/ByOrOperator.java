/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

package com.paypal.selion.platform.html.support;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

/**
 * Extends {@link By} and provides a mechanism for locating an element from a list of {@link By}s where the element
 * found will be the first match in the list.
 */

public class ByOrOperator extends By {

    private final List<By> bys;

    public ByOrOperator(List<By> bys) {
        super();
        this.bys = bys;
    }

    @Override
    public WebElement findElement(SearchContext context) {
        List<WebElement> elements = findElements(context);
        if (elements.size() == 0) {
            throw new NoSuchElementException("Cannot locate an element using " + toString());
        }
        return elements.get(0);
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        List<WebElement> result = null;
        for (By by : bys) {
            try {
                result = by.findElements(context);
                if (result != null && result.size() != 0) {
                    return result;
                }
            } catch (RuntimeException e) {
                // do nothing, lets check for the next element.
            }
        }
        return new ArrayList<WebElement>();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("By.OrOperator(");
        stringBuilder.append("{");

        boolean first = true;
        for (By by : bys) {
            stringBuilder.append((first ? "" : ",")).append(by);
            first = false;
        }
        stringBuilder.append("})");
        return stringBuilder.toString();
    }
}
