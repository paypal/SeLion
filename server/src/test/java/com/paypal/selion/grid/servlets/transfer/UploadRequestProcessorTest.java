/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

package com.paypal.selion.grid.servlets.transfer;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.reflect.Whitebox;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.grid.servlets.TransferServlet;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.ApplicationUploadRequestProcessor;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.MultipartUploadRequestProcessor;

@PrepareForTest({ HttpServletRequest.class, HttpServletResponse.class, TransferServlet.class })
public class UploadRequestProcessorTest extends PowerMockTestCase {

    @Test
    public void testMultipartRequestProcessor() throws Exception {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        TransferServlet transferServlet = new TransferServlet();
        TransferContext transferContext = new TransferContext(httpServletRequest, httpServletResponse);
        when(httpServletRequest.getContentType()).thenReturn("multipart/form-data");
        UploadRequestProcessor uploadRequestProcessor = Whitebox.<UploadRequestProcessor> invokeMethod(transferServlet,
                "getUploadRequestProcessor", transferContext);
        Assert.assertEquals(uploadRequestProcessor instanceof MultipartUploadRequestProcessor, true,
                "MultipartUploadRequestProcessor not returned for multipart request");
    }

    @Test
    public void testUrlEncodedRequestProcessor() throws Exception {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        TransferServlet transferServlet = new TransferServlet();
        TransferContext transferContext = new TransferContext(httpServletRequest, httpServletResponse);
        when(httpServletRequest.getContentType()).thenReturn("application/x-www-form-urlencoded");
        UploadRequestProcessor uploadRequestProcessor = Whitebox.<UploadRequestProcessor> invokeMethod(transferServlet,
                "getUploadRequestProcessor", transferContext);
        Assert.assertEquals(uploadRequestProcessor instanceof ApplicationUploadRequestProcessor, true,
                "MultipartUploadRequestProcessor not returned for multipart request");
    }

    @Test(expectedExceptions = ArtifactUploadException.class)
    public void testUnknownContentType() throws Exception {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        TransferServlet transferServlet = new TransferServlet();
        TransferContext transferContext = new TransferContext(httpServletRequest, httpServletResponse);
        when(httpServletRequest.getContentType()).thenReturn("unknown/unknown");
        Whitebox.<UploadRequestProcessor> invokeMethod(transferServlet, "getUploadRequestProcessor", transferContext);
    }

}
