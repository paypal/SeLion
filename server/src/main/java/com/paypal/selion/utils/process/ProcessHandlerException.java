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

package com.paypal.selion.utils.process;

import com.paypal.selion.pojos.ProcessInfo;

/**
 * A custom exception that represents any problems that may arise while in the process of 
 * ascertaining a set of {@link ProcessInfo} to be recycled.
 *
 */
public class ProcessHandlerException extends Exception {

    private static final long serialVersionUID = 5445790498525782457L;

    public ProcessHandlerException() {
        super();
    }

    public ProcessHandlerException(String message) {
        super(message);
    }

    public ProcessHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessHandlerException(Throwable cause) {
        super(cause);
    }

}
