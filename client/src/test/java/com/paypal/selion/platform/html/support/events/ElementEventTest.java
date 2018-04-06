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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.testcomponents.TestPage;

public class ElementEventTest {

    @Test(groups = { "functional" })
    @WebTest
    public void testClickEvents() throws InterruptedException, IOException {
        Grid.open("about:blank");
        String script = getScript();
        Grid.driver().executeScript(script);
        Thread.sleep(4000);

        Grid.getTestSession().getElementEventListeners().add(new ElementListenerTestImpl());

        TestPage page = new TestPage("US");

        page.clickContinueButton(page);
        Assert.assertEquals(page.getLogLabel().getProperty("data-before-click"), "true",
                "beforeClick method was not triggered.");
        Assert.assertEquals(page.getLogLabel().getProperty("data-after-click"), "true",
                "afterClick method was not triggered.");
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testHoverEvents() throws InterruptedException, IOException {
        Grid.open("about:blank");
        String script = getScript();
        Grid.driver().executeScript(script);
        Thread.sleep(4000);

        Grid.getTestSession().getElementEventListeners().add(new ElementListenerTestImpl());

        TestPage page = new TestPage("US");

        page.getContinueButton().hover();
        Assert.assertEquals(page.getLogLabel().getProperty("data-before-hover"), "true",
                "beforeHover method was not triggered.");
        Assert.assertEquals(page.getLogLabel().getProperty("data-after-hover"), "true",
                "afterHover method was not triggered.");
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testTypeEvents() throws InterruptedException, IOException {
        Grid.open("about:blank");
        String script = getScript();
        Grid.driver().executeScript(script);
        Thread.sleep(4000);

        Grid.getTestSession().getElementEventListeners().add(new ElementListenerTestImpl());

        TestPage page = new TestPage("US");

        page.setFieldXTextFieldValue("Test");
        Assert.assertEquals(page.getLogLabel().getProperty("data-before-type"), "true",
                "beforeType method was not triggered.");
        Assert.assertEquals(page.getLogLabel().getProperty("data-after-type"), "true",
                "afterType method was not triggered.");
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testSelectEvents() throws InterruptedException, IOException {
        Grid.open("about:blank");
        String script = getScript();
        Grid.driver().executeScript(script);
        Thread.sleep(4000);

        Grid.getTestSession().getElementEventListeners().add(new ElementListenerTestImpl());

        TestPage page = new TestPage("US");

        page.getXSelectList().selectByIndex(1);
        page.getXSelectList().deselectByIndex(1);
        Assert.assertEquals(page.getLogLabel().getProperty("data-before-select"), "true",
                "beforeSelect method was not triggered.");
        Assert.assertEquals(page.getLogLabel().getProperty("data-after-select"), "true",
                "afterSelect method was not triggered.");
        Assert.assertEquals(page.getLogLabel().getProperty("data-before-deselect"), "true",
                "beforeDeselect method was not triggered.");
        Assert.assertEquals(page.getLogLabel().getProperty("data-after-deselect"), "true",
                "afterDeselect method was not triggered.");
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testCheckEvents() throws InterruptedException, IOException {
        Grid.open("about:blank");
        String script = getScript();
        Grid.driver().executeScript(script);
        Thread.sleep(4000);

        Grid.getTestSession().getElementEventListeners().add(new ElementListenerTestImpl());

        TestPage page = new TestPage("US");

        page.getXCheckBox().check();
        page.getXCheckBox().uncheck();

        Assert.assertEquals(page.getLogLabel().getProperty("data-before-check"), "true",
                "beforeCheck method was not triggered.");
        Assert.assertEquals(page.getLogLabel().getProperty("data-after-check"), "true",
                "afterCheck method was not triggered.");
        Assert.assertEquals(page.getLogLabel().getProperty("data-before-uncheck"), "true",
                "beforeUncheck method was not triggered.");
        Assert.assertEquals(page.getLogLabel().getProperty("data-after-uncheck"), "true",
                "afterUncheck method was not triggered.");

        // Reset the events triggered for RadioButton testing
        page.getLogLabel().setProperty("data-before-check", null);
        page.getLogLabel().setProperty("data-after-check", null);

        page.getXRadioButton().check();

        Assert.assertEquals(page.getLogLabel().getProperty("data-before-check"), "true",
                "beforeCheck method was not triggered.");
        Assert.assertEquals(page.getLogLabel().getProperty("data-after-check"), "true",
                "afterType method was not triggered.");
    }

    private String getScript() throws IOException {
        File scriptFile = new File("src/test/resources/testdata/InsertHtmlElements.js");
        return FileUtils.readFileToString(scriptFile, "UTF-8");
    }
}
