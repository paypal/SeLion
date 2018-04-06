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

package com.paypal.selion.grid.servlets;

import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import java.util.ArrayList;
import java.util.List;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.pojos.BrowserStatisticsCollection;
import com.paypal.selion.pojos.BrowserStatisticsCollection.BrowserStatistics;

/**
 * Tests for GridStatistics servlet
 */
@PrepareForTest({ GridStatistics.class })
public class GridStatisticsTest extends PowerMockTestCase {

    @Test
    public void testValidResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept", "*/*");
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<BrowserStatistics> browserStatisticsList = new ArrayList<>();
        BrowserStatisticsCollection t = new BrowserStatisticsCollection();
        BrowserStatistics browserStatistics = t.new BrowserStatistics("chrome");
        browserStatisticsList.add(browserStatistics);
        spy(GridStatistics.class);
        stub(method(GridStatistics.class, "getGridLoadResponse")).toReturn(browserStatisticsList);
        GridStatistics gridStatistics = new GridStatistics();
        gridStatistics.doPost(request, response);
        Assert.assertEquals(response.getContentAsString(),
                "[{\"browserName\":\"chrome\",\"statistics\":{\"waitingRequests\":0,\"maxBrowserInstances\":0}}]",
                "Servlet output not matching");

    }

    @Test
    public void testEmptyResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept", "*/*");
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<BrowserStatistics> browserStatisticsList = new ArrayList<>();
        spy(GridStatistics.class);
        stub(method(GridStatistics.class, "getGridLoadResponse")).toReturn(browserStatisticsList);
        GridStatistics gridStatistics = new GridStatistics();
        gridStatistics.doPost(request, response);
        Assert.assertEquals(response.getContentAsString(), "[]", "Servlet output not matching");

    }

}
