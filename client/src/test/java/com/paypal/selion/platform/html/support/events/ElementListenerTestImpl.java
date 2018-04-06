/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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

package com.paypal.selion.platform.html.support.events;

import com.paypal.selion.platform.html.AbstractElement;
import com.paypal.selion.testcomponents.TestPage;

public class ElementListenerTestImpl implements ElementEventListener {

    @Override
    public void beforeClick(Clickable target, Object... expected) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-click", "true");
    }

    @Override
    public void afterClick(Clickable target, Object... expected) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-click", "true");
    }

    @Override
    public void beforeScreenshot(Clickable target) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-screenshot", "true");
    }

    @Override
    public void afterScreenshot(Clickable target) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-screenshot", "true");
    }

    @Override
    public void beforeType(Typeable target, String value) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-type", "true");
    }

    @Override
    public void afterType(Typeable target, String value) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-type", "true");
    }

    @Override
    public void beforeCheck(Checkable target, String expected) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-check", "true");
    }

    @Override
    public void afterCheck(Checkable target, String expected) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-check", "true");
    }

    @Override
    public void beforeUncheck(Uncheckable target, String expected) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-uncheck", "true");
    }

    @Override
    public void afterUncheck(Uncheckable target, String expected) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-uncheck", "true");
    }

    @Override
    public void beforeSubmit(Submitable target) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-submit", "true");
    }

    @Override
    public void afterSubmit(Submitable target) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-submit", "true");
    }

    @Override
    public void beforeCheck(Checkable target) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-check", "true");
    }

    @Override
    public void afterCheck(Checkable target) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-check", "true");
    }

    @Override
    public void beforeUncheck(Uncheckable target) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-uncheck", "true");
    }

    @Override
    public void afterUncheck(Uncheckable target) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-uncheck", "true");
    }

    @Override
    public void beforeSelect(Selectable target, int index) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-select", "true");
    }

    @Override
    public void afterSelect(Selectable target, int index) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-select", "true");
    }

    @Override
    public void beforeSelect(Selectable target, String value) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-select", "true");
    }

    @Override
    public void afterSelect(Selectable target, String value) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-select", "true");
    }

    @Override
    public void beforeDeselect(Deselectable target, int index) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-deselect", "true");
    }

    @Override
    public void afterDeselect(Deselectable target, int index) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-deselect", "true");
    }

    @Override
    public void beforeDeselect(Deselectable target, String value) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-deselect", "true");
    }

    @Override
    public void afterDeselect(Deselectable target, String value) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-deselect", "true");
    }

    @Override
    public void beforeDeselect(Deselectable target) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-deselect", "true");
    }

    @Override
    public void afterDeselect(Deselectable target) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-deselect", "true");
    }

    @Override
    public void beforeHover(Hoverable target, Object... expected) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-before-hover", "true");
    }

    @Override
    public void afterHover(Hoverable target, Object... expected) {
        AbstractElement element = (AbstractElement) target;
        TestPage page = (TestPage) element.getParent().getCurrentPage();

        page.getLogLabel().setProperty("data-after-hover", "true");
    }

}
