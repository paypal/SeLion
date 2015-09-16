/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 PayPal                                                                                     |
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

import com.paypal.selion.testcomponents.BasicPageImpl;

/**
 * A generic interface for web Page Objects in SeLion.<br>
 * <br>
 * Note: "web" is used loosely here. This interface can also be implemented to support mobile applications as is the
 * case with {@link BasicPageImpl}
 * 
 */
public interface WebPage {

    /**
     * Initialize the page by it's name and page path
     * 
     * @param pagePath
     *            the path
     * @param pageClassName
     *            the class name
     */
    void initPage(String pagePath, String pageClassName);

    /**
     * Initialize the page by it's name, page path, and site locale
     * 
     * @param pagePath
     *            the path
     * @param pageClassName
     *            the class name
     * @param siteLocale
     *            the locale to use
     */
    void initPage(String pagePath, String pageClassName, String siteLocale);

    /**
     * @return initialization state
     */
    boolean isInitialized();

    /**
     * @return the expected page title for this page
     */
    String getExpectedPageTitle();

    /**
     * @return the current siteLocale setting for this page
     */
    String getSiteLocale();

    /**
     * @return a {@link WebPage} object
     */
    WebPage getPage();

    /**
     * Validates the page against the defined <code>pageValidators</code> defined in the PageYAML for this page.
     * 
     * @throws PageValidationException
     *             when the page does not validate.
     */
    void validatePage();

    /**
     * Returns the outcome of calling {@link WebPage#validatePage()} to validate the loaded page on the WebDriver
     * session.
     * 
     * @return <code>true</code> or <code>false</code>, if the page is validated, meaning all
     *         <code>pageValidators</code> pass
     */
    boolean isPageValidated();
}
