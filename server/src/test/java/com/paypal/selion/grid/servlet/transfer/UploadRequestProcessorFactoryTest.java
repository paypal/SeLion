/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

package com.paypal.selion.grid.servlet.transfer;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.reflect.Whitebox;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.grid.servlets.TransferServlet;
import com.paypal.selion.grid.servlets.transfer.ArtifactUploadException;
import com.paypal.selion.grid.servlets.transfer.TransferContext;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.ApplicationUploadRequestProcessor;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.MultipartUploadRequestProcessor;
import com.paypal.selion.utils.ConfigParser;

@PrepareForTest({ TransferServlet.class, HttpServletRequest.class, ConfigParser.class })
public class UploadRequestProcessorFactoryTest extends PowerMockTestCase {

    @BeforeClass
    public void setUpBeforeClass() {
        System.setProperty("repository", System.getProperty("user.home"));
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testMultipartRequestProcessor() throws Exception {
        ConfigParser configParser = mock(ConfigParser.class);
        mockStatic(ConfigParser.class);
        when(ConfigParser.parse()).thenReturn(configParser);
        when(configParser.getLong("artifactMaxFileSize")).thenReturn(86400000L);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        TransferServlet transferServet = new TransferServlet();
        TransferContext transferContext = new TransferContext(httpServletRequest, httpServletResponse);
        when(httpServletRequest.getContentType()).thenReturn("multipart/form-data");
        UploadRequestProcessor uploadRequestProcessor = Whitebox.<UploadRequestProcessor> invokeMethod(transferServet,
                "getUploadRequestProcessor", transferContext);
        Assert.assertEquals(uploadRequestProcessor instanceof MultipartUploadRequestProcessor, true,
                "MultipartUploadRequestProcessor not returned for multipart request");
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testUrlEncodedRequestProcessor() throws Exception {
        ConfigParser configParser = mock(ConfigParser.class);
        mockStatic(ConfigParser.class);
        when(ConfigParser.parse()).thenReturn(configParser);
        when(configParser.getLong("artifactMaxFileSize")).thenReturn(86400000L);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        TransferServlet transferServet = new TransferServlet();
        TransferContext transferContext = new TransferContext(httpServletRequest, httpServletResponse);
        when(httpServletRequest.getContentType()).thenReturn("application/x-www-form-urlencoded");
        UploadRequestProcessor uploadRequestProcessor = Whitebox.<UploadRequestProcessor> invokeMethod(transferServet,
                "getUploadRequestProcessor", transferContext);
        Assert.assertEquals(uploadRequestProcessor instanceof ApplicationUploadRequestProcessor, true,
                "MultipartUploadRequestProcessor not returned for multipart request");
    }

    @Test(expectedExceptions = ArtifactUploadException.class)
    @SuppressWarnings({ "rawtypes" })
    public void testUnknownContentType() throws Exception {
        ConfigParser configParser = mock(ConfigParser.class);
        mockStatic(ConfigParser.class);
        when(ConfigParser.parse()).thenReturn(configParser);
        when(configParser.getLong("artifactMaxFileSize")).thenReturn(86400000L);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        TransferServlet transferServet = new TransferServlet();
        TransferContext transferContext = new TransferContext(httpServletRequest, httpServletResponse);
        when(httpServletRequest.getContentType()).thenReturn("unknown/unknown");
        Whitebox.<UploadRequestProcessor> invokeMethod(transferServet, "getUploadRequestProcessor", transferContext);
    }

}
