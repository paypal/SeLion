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

package com.paypal.selion.internal.reports.excelreport;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Contains Error to Possible cause mapping
 * 
 */
public final class TestCaseErrors {

    private static final String WAIT_EXCEPTION = "Wait Timed Out.  Load issue?";
    private static final String ELEMENT_NOT_FOUND_EXCEPTION = "Element not found. Locator issue? Page fully loaded? bug?";
    private static final String XHR_ERROR_EXCEPTION = "Cert Error. Certificate added to profile?";
    private static final String BIND_EXCEPTION = "Bind Exception. Kill all javaw.exe and retry.";

    private static TestCaseErrors tcErrors;
    private Map<String, String> mpErrorsInfo = new HashMap<>();

    private TestCaseErrors() {
        mpErrorsInfo.put("(?s).*WaitTimedOutException(?s).*", WAIT_EXCEPTION);
        mpErrorsInfo.put("(?s).*SeleniumException: ERROR: Element.*not found(?s).*", ELEMENT_NOT_FOUND_EXCEPTION);
        mpErrorsInfo.put("(?s).*XHR ERROR: URL(?s).*", XHR_ERROR_EXCEPTION);
        mpErrorsInfo.put("(?s).*java.net.BindException(?s).*Address already in use(?s).*", BIND_EXCEPTION);
        mpErrorsInfo.put("(?s).*NoSuchElementException(?s).*", ELEMENT_NOT_FOUND_EXCEPTION);
    }

    public static TestCaseErrors getInstance() {
        if (tcErrors == null) {
            tcErrors = new TestCaseErrors();
        }
        return tcErrors;
    }

    String debugError(Throwable defect) {
        for (String errPattern : mpErrorsInfo.keySet()) {
            if (defect.toString().matches(errPattern)) {
                return mpErrorsInfo.get(errPattern);
            }
        }
        return null;
    }

    public void addError(String sErrorPattern, String sMsgToDisplay) {
        this.mpErrorsInfo.put(sErrorPattern, sMsgToDisplay);
    }
}
