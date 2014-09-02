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

import java.util.Map;

import com.paypal.selion.internal.utils.InvokedMethodInformation;

/**
 * This class represents a basic test session.
 *
 */
public class BasicTestSession extends AbstractTestSession {
    private static final String DEFAULT_MESSAGE = "This operation is NOT supported.";

    @Override
    public SeLionSession startSesion() {
        throw new UnsupportedOperationException(DEFAULT_MESSAGE);
    }

    @Override
    public void closeAllSessions(Map<String, SeLionSession> sessionMap) {
        throw new UnsupportedOperationException(DEFAULT_MESSAGE);
    }

    @Override
    public WebDriverPlatform getPlatform() {
        return WebDriverPlatform.UNDEFINED;
    }

    @Override
    public SeLionSession startSession(Map<String, SeLionSession> sessions) {
        throw new UnsupportedOperationException(DEFAULT_MESSAGE);
    }

    @Override
    public void initializeTestSession(InvokedMethodInformation method, Map<String, SeLionSession> sessionMap) {
        throw new UnsupportedOperationException(DEFAULT_MESSAGE);        
    }

    @Override
    public void initializeTestSession(InvokedMethodInformation method) {
        this.initTestSession(method);
    }

    @Override
    public void closeCurrentSession(Map<String, SeLionSession> sessionMap, InvokedMethodInformation result) {
        throw new UnsupportedOperationException(DEFAULT_MESSAGE);        
    }

}
