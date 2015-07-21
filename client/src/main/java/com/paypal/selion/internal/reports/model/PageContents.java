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

package com.paypal.selion.internal.reports.model;

import java.util.Arrays;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Object representing web page contents. Internally used. PageContents will contain two aspects of a web page:
 * 
 * 1.- The HTML page source </br>
 * 2.- The rendered screen image of the page</br>
 * 
 * An ID string will be associated with each of the web page aspects. User can retrieved the page contents by using this
 * ID.
 */
public final class PageContents {
    private static SimpleLogger logger = SeLionLogger.getLogger();
    private String id;
    private String pageSource;
    private byte[] screenImage;

    /**
     * Create a new page source object.
     * 
     * @param content
     *            the content of a web page as an array of char
     * @param id
     *            the identifier to associate with this page source
     */
    public PageContents(char[] content, String id) {
        this(String.copyValueOf(content), id);
    }

    /**
     * Creates a new screen shot object
     * 
     * @param content
     *            content of the image as a byte array
     * @param id
     *            identifier associated with this image
     */
    public PageContents(byte[] content, String id) {
        logger.entering(new Object[] { content, id });
        this.screenImage = Arrays.copyOf(content, content.length);
        this.id = id;
        logger.exiting();
    }

    /**
     * Create a new page source object.
     * 
     * @param content
     *            the content as a {@link String}
     * @param id
     *            the identifier to associate with this page source
     */
    public PageContents(String content, String id) {
        this.id = id;
        this.pageSource = content;
    }

    /**
     * Get the id for this page source object
     * 
     * @return the identifier associated as a {@link String}
     */
    public String getId() {
        logger.entering();
        logger.exiting(this.id);
        return this.id;
    }

    /**
     * Set the id for this page source object
     * 
     * @param id
     *            the identifier to associate as a {@link String}
     */
    public void setId(String id) {
        logger.entering(id);
        this.id = id;
        logger.exiting();
    }

    /**
     * Get the content for this source code object
     * 
     * @return the content as a {@link String}
     */
    public String getPageSource() {
        logger.entering();
        logger.exiting("source code omitted from logs");
        return this.pageSource;
    }

    /**
     * Get the image content
     * 
     * @return byte array representing the image content
     */
    public byte[] getScreenImage() {
        logger.entering();
        logger.exiting(this.screenImage);
        return Arrays.copyOf(screenImage, screenImage.length);
    }

    /**
     * Set the content for this source code object
     * 
     * @param content
     *            the content as a {@link String}
     */
    public void setPageSource(String content) {
        logger.entering("source code omitted from logs");
        this.pageSource = content;
        logger.exiting();
    }

    /**
     * Set the image content
     * 
     * @param content
     *            byte array representing the image content
     */
    public void setScreenImage(byte[] content) {
        logger.entering(content);
        this.screenImage = Arrays.copyOf(content, content.length);
        logger.exiting();
    }

}
