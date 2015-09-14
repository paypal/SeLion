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

package com.paypal.selion.internal.reports.html;

/**
 * This exception is to be thrown when something goes wrong while running the flow to report test case execution/result
 */
public class ReporterException extends RuntimeException {

    private static final long serialVersionUID = 8071686053553550147L;

    public ReporterException(Exception e) {
        super(e);
    }

    public ReporterException(String msg) {
        super(msg);
    }

    public ReporterException(String msg, Exception e) {
        super(msg, e);
    }

}
