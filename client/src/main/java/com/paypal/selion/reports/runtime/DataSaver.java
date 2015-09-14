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

package com.paypal.selion.reports.runtime;

import com.paypal.selion.internal.reports.model.PageContents;

/**
 * Simple interface for data persistence of web page parts.
 */
interface DataSaver {

    /**
     * Initialize the saver.
     */
    void init();

    /**
     * Save a screenshot to the data store.
     * 
     * @param s
     *            a {@link PageContents} object
     * @return a {@link String} which represents a means for retrieving the screen shot, such as a file path or url.
     * @throws Exception
     */
    String saveScreenshot(PageContents s) throws Exception;

    /**
     * Save sources to the data store
     * 
     * @param s
     *            the {@link PageContents} object
     * @return a {@link String} which represent a means for retrieving the source code, such as a file path or url.
     */
    String saveSources(PageContents s);

    /**
     * Get a {@link PageContents} by name
     * 
     * @param name
     *            the {@link String} for retrieving the {@link PageContents} from the data store.
     * @return the retrieved {@link PageContents}
     * @throws Exception
     */
    PageContents getScreenshotByName(String name) throws Exception;
}
