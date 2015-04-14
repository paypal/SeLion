/*-------------------------------------------------------------------------------------------------------------------*\
   |  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

package com.paypal.selion.platform.dataprovider;

/**
 * This exception is wrapper over exceptions being thrown while reading an XML file or stream.
 */
public class XmlDataProviderException extends DataProviderException {

    private static final long serialVersionUID = 7426560951248746405L;

    /**
     * Sole constructor to provide instance of {@link XmlDataProviderException} class.
     * @param msg
     *  A text message describing the exception.
     * @param e
     *  The {@link Throwable} containing the original exception details.
     */
    public XmlDataProviderException(String msg, Throwable e) {
        super(msg, e);
    }

}
