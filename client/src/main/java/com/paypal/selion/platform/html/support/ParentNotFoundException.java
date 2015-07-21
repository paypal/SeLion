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

package com.paypal.selion.platform.html.support;

import org.openqa.selenium.NoSuchElementException;

/**
 * User defined exception to be thrown when a parent type is not found
 * 
 */
public class ParentNotFoundException extends NoSuchElementException {

    private static final long serialVersionUID = -9113615926728828034L;

    public ParentNotFoundException(String reason) {
        super(reason);
    }

    public ParentNotFoundException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
