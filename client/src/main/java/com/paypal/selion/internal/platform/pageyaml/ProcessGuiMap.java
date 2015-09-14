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

package com.paypal.selion.internal.platform.pageyaml;

import java.io.IOException;

import com.paypal.selion.platform.dataprovider.impl.FileSystemResource;

/**
 * This interface represents the abilities of any concrete implementation that deals with parsing GUI Object map for 
 * creating the SeLion Page Objects.
 *
 */
public interface ProcessGuiMap {
    /**
     * @return - <code>true</code> if the processing of {@link FileSystemResource} was successful.
     */
    boolean processed();

    /**
     * Process a file represented by {@link FileSystemResource}
     * @param resource - A {@link FileSystemResource} object.
     * @throws IOException
     */
    void processPage(FileSystemResource resource) throws IOException;
}
