/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

package com.paypal.selion.plugins;

/**
 * Thrown when an error occurs generating the .java page objects.
 */
public class CodeGeneratorException extends RuntimeException {
    private static final long serialVersionUID = -2887053135314509648L;

    public CodeGeneratorException() {
        super();
    }

    public CodeGeneratorException(String message) {
        super(message);
    }

    public CodeGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
