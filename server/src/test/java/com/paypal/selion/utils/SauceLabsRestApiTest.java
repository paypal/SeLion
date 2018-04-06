/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

package com.paypal.selion.utils;

import org.apache.http.HttpStatus;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.Test;

import com.paypal.selion.utils.SauceLabsRestApi.SauceLabsHttpResponse;

import java.util.HashMap;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@PrepareForTest({ SauceLabsRestApi.class, SauceLabsHttpResponse.class })
public class SauceLabsRestApiTest extends PowerMockTestCase {
    private static final String mockApiResult = 
            "{\"subaccounts\": {\"foobar\": {\"all\": 1}},\"totals\": {\"all\": 2},\"concurrency\": 5}";

    @Test
    public void getMaxConcurrency() throws Exception {
        SauceLabsHttpResponse mockHttpResponse = mock(SauceLabsHttpResponse.class);
        doReturn(mockApiResult).when(mockHttpResponse, "getEntity");
        when(mockHttpResponse.getEntityAsJsonObject()).thenCallRealMethod();

        SauceLabsRestApi apiMock = mock(SauceLabsRestApi.class);
        Whitebox.setInternalState(apiMock, "maxTestCase", -1);
        doReturn(mockHttpResponse).when(apiMock, "doSauceRequest", Mockito.anyString());

        when(apiMock.getMaxConcurrency()).thenCallRealMethod();
        assertEquals(apiMock.getMaxConcurrency(), 5);
    }

    @Test
    public void getNumberOfTCRunningForSubAccount() throws Exception {
        SauceLabsHttpResponse mockHttpResponse = mock(SauceLabsHttpResponse.class);
        doReturn(mockApiResult).when(mockHttpResponse, "getEntity");
        when(mockHttpResponse.getEntityAsJsonObject()).thenCallRealMethod();

        SauceLabsRestApi apiMock = mock(SauceLabsRestApi.class);
        doReturn(mockHttpResponse).when(apiMock, "doSauceRequest", Mockito.anyString());

        when(apiMock.getNumberOfTCRunningForSubAccount(Mockito.anyString())).thenCallRealMethod();
        assertEquals(apiMock.getNumberOfTCRunningForSubAccount("foobar"), 1);
    }

    @Test
    public void getNumberOfTCRunning() throws Exception {
        SauceLabsHttpResponse mockHttpResponse = mock(SauceLabsHttpResponse.class);
        doReturn(mockApiResult).when(mockHttpResponse, "getEntity");
        when(mockHttpResponse.getEntityAsJsonObject()).thenCallRealMethod();

        SauceLabsRestApi apiMock = mock(SauceLabsRestApi.class);
        doReturn(mockHttpResponse).when(apiMock, "doSauceRequest", Mockito.anyString());

        when(apiMock.getNumberOfTCRunning()).thenCallRealMethod();
        assertEquals(apiMock.getNumberOfTCRunning(), 2);
    }

    @Test
    public void isAuthenticated() throws Exception {
        SauceLabsHttpResponse mockHttpResponse = mock(SauceLabsHttpResponse.class);
        doReturn(HttpStatus.SC_OK).when(mockHttpResponse, "getStatus");

        SauceLabsRestApi apiMock = mock(SauceLabsRestApi.class);
        Whitebox.setInternalState(apiMock, "accountCache", new HashMap<String, Boolean>());
        doReturn(mockHttpResponse).when(apiMock, "doSauceRequest", Mockito.any(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.anyInt());

        when(apiMock.isAuthenticated(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        assertTrue(apiMock.isAuthenticated("foo", "bar"));
    }
}
