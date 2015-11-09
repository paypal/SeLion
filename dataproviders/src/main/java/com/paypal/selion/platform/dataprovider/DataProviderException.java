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

package com.paypal.selion.platform.dataprovider;

/**
 * This Exception class is specific to data reader.
 */
public class DataProviderException extends RuntimeException {

    private static final long serialVersionUID = 3290312548375984346L;

    public DataProviderException() {
        super();
    }

    public DataProviderException(String message) {
        super(message);
    }

    public DataProviderException(Throwable exception) {
        super(exception);
    }

    public DataProviderException(String message, Throwable exception) {
        super(message, exception);
    }
}
