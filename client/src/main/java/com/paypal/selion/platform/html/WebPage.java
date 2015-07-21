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

package com.paypal.selion.platform.html;

/**
 * A generic interface for Web Page Objects in SeLion.
 * 
 */
public interface WebPage {

    /**
     * Initialize the page by it's name and page path
     * 
     * @param pagePath
     * @param pageClassName
     */
    void initPage(String pagePath, String pageClassName);

    /**
     * Initialize the page by it's name, page path, and site locale
     * 
     * @param pagePath
     * @param pageClassName
     * @param siteLocale
     */
    void initPage(String pagePath, String pageClassName, String siteLocale);

    /**
     * Return initialization state
     */
    boolean isInitialized();

    /**
     * Return the expected page title for this page
     */
    String getExpectedPageTitle();

    /**
     * Return the current siteLocale setting for this page
     */
    String getSiteLocale();

    /**
     * Return the WebPage object
     * 
     * @return a {@link WebPage}
     */
    WebPage getPage();

    /**
     * Validates if the page is loaded in the browser
     */
    void validatePage();

    /**
     * Returns if page is opened in the browser. Use {@link WebPage#validatePage()} if you want to validate if a page is
     * loaded.
     * 
     * @return if page is opened
     */
    boolean isCurrentPageInBrowser();
}
