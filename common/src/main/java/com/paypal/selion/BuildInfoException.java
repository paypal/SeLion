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

package com.paypal.selion;

import java.io.IOException;

/**
 * This exception is supposed to be thrown in case something goes wrong while setting up the configuration values either
 * default or user provided
 */
final class BuildInfoException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 4476910896599875963L;

    public BuildInfoException(String msg, IOException e) {
        super(msg, e);
    }

    public BuildInfoException(String msg) {
        super(msg);
    }

}
