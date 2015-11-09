/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

package com.paypal.selion.testcomponents;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.paypal.selion.platform.html.Button;
import com.paypal.selion.platform.html.Link;
import com.paypal.selion.testcomponents.BasicPageImpl;

public class GUIPageExtensionTest {
    @Test(groups = "unit")
    public void testPageExtension() {
        SampleTestPageExtn SampleTestPageExtn = new SampleTestPageExtn();
        SampleTestPage SampleTestPage = new SampleTestPage();
        assertTrue(
                SampleTestPageExtn.getPersonalLink().getLocator()
                        .equalsIgnoreCase(SampleTestPage.getPersonalLink().getLocator()),
                "The locators should have been same");
    }

    public static class SampleTestPage extends BasicPageImpl {

        private Link personalLink;
        private Button testButton;

        public SampleTestPage() {
            super();
            super.initPage("paypal", "SampleTestPage");
        }
        
        public Button getTestButton() {
            Button element = this.testButton;
            if (element == null) {
                this.testButton = new Button(this.getObjectMap().get("testButton"), "testButton", this);
            }
            return this.testButton;
        }

        @Override
        public SampleTestPage getPage() {
            return this;
        }

        public Link getPersonalLink() {
            Link element = this.personalLink;
            if (element == null) {
                this.personalLink = new Link(this.getObjectMap().get("personalLink"), "personalLink", this);
            }
            return this.personalLink;
        }
    }

    public static class SampleTestPageExtn extends SampleTestPage {

        private Link personalLink;

        public SampleTestPageExtn() {
            super();
        }

        @Override
        public SampleTestPageExtn getPage() {
            return this;
        }

        @Override
        public Link getPersonalLink() {
            Link element = this.personalLink;
            if (element == null) {
                this.personalLink = new Link(this.getObjectMap().get("personalLink"), "personalLink", this);
            }
            return this.personalLink;
        }
    }
}
