/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.platform.grid;

import org.openqa.selenium.WebDriverException;

/**
 * This exception is the wrapper for all the exceptions that might originate from a websession object. The object might
 * be malformed with some of its values not properly populated. Alternatively some of its methods might throw some
 * exceptions that we do not want to expose to the end user.
 * 
 */
public class WebSessionException extends WebDriverException {

    /**
     * 
     */
    private static final long serialVersionUID = -4287555166590550375L;

    public WebSessionException(String msg) {
        super(msg);
    }

    public WebSessionException(String errorMsg, Throwable e) {
        super(errorMsg, e);
    }

}
