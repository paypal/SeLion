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

import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.paypal.selion.grid.servlets.transfer.ArtifactUploadException;
import com.paypal.selion.grid.servlets.transfer.ManagedArtifact;
import com.paypal.selion.grid.servlets.transfer.TransferContext;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.RequestHeaders;
import com.paypal.selion.grid.servlets.transfer.UploadResponder.JsonUploadResponder;
import com.paypal.selion.grid.servlets.transfer.UploadResponder.TextPlainUploadResponder;
import com.paypal.selion.utils.ConfigParser;

@PrepareForTest({ UploadRequestProcessor.class, HttpServletRequest.class, HttpServletResponse.class,
        ManagedArtifact.class, ConfigParser.class })
public class UploadResponderTest extends PowerMockTestCase {

    @BeforeMethod
    public void setUpBeforeEveryMethod() throws Exception {
        ConfigParser configParser = PowerMockito.mock(ConfigParser.class);
        mockStatic(ConfigParser.class);
        when(ConfigParser.getInstance()).thenReturn(configParser);
        when(configParser.getString("managedCriteria")).thenReturn(
                "com.paypal.selion.grid.servlets.transfer.DefaultManagedArtifact$DefaultCriteria");
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testJsonResponder() throws IOException {
        UploadRequestProcessor<ManagedArtifact> requestProcessor = mock(UploadRequestProcessor.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        ManagedArtifact managedArtifact = PowerMockito.mock(ManagedArtifact.class);

        String expected = "{\"files\":[{\"fileName\":\"InternationalMountains_app.zip\",\"url\":\"http://localhost:4444/path/TransferServlet/userOne/InternationalMountains_app.zip\"}]}";
        EnumMap<RequestHeaders, String> map = new EnumMap<>(RequestHeaders.class);
        map.put(RequestHeaders.FILENAME, "InternationalMountains_app.zip");
        map.put(RequestHeaders.USERID, "userOne");
        
        StringBuffer stringBuffer = new StringBuffer("http://localhost:4444/path/TransferServlet");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        List<ManagedArtifact> managedArtifactList = new ArrayList<>();
        managedArtifactList.add(managedArtifact);
        TransferContext transferContext = mock(TransferContext.class);

        when(managedArtifact.getFolderName()).thenReturn("userOne");
        when(managedArtifact.getArtifactName()).thenReturn("InternationalMountains_app.zip");
        when(transferContext.getHttpServletRequest()).thenReturn(httpServletRequest);
        when(transferContext.getHttpServletResponse()).thenReturn(httpServletResponse);
        when(transferContext.getUploadRequestProcessor()).thenReturn(requestProcessor);
        when(transferContext.getHeadersMap()).thenReturn(map);
        
        when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);
        doNothing().when(httpServletResponse).setContentType(Mockito.anyString());
        when(requestProcessor.getUploadedData()).thenReturn(managedArtifactList);
        when(transferContext.getHeadersMap()).thenReturn(map);

        JsonUploadResponder uploadResponder = new JsonUploadResponder(transferContext);
        uploadResponder.respond();
        Assert.assertEquals(stringWriter.toString().trim(), expected,
                "The output to http servlet response is not as expected");
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testTextPlainResponder() throws IOException {
        UploadRequestProcessor<ManagedArtifact> requestProcessor = mock(UploadRequestProcessor.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        ManagedArtifact managedArtifact = PowerMockito.mock(ManagedArtifact.class);

        String expected = "fileName=InternationalMountains_app.zip,url=http://localhost:4444/path/TransferServlet/userOne/InternationalMountains_app.zip;";
        EnumMap<RequestHeaders, String> map = new EnumMap<>(RequestHeaders.class);
        map.put(RequestHeaders.FILENAME, "InternationalMountains_app.zip");
        map.put(RequestHeaders.USERID, "userOne");
        
        StringBuffer stringBuffer = new StringBuffer("http://localhost:4444/path/TransferServlet");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        List<ManagedArtifact> managedArtifactList = new ArrayList<>();
        managedArtifactList.add(managedArtifact);
        TransferContext transferContext = mock(TransferContext.class);

        when(managedArtifact.getFolderName()).thenReturn("userOne");
        when(managedArtifact.getArtifactName()).thenReturn("InternationalMountains_app.zip");
        when(transferContext.getHttpServletRequest()).thenReturn(httpServletRequest);
        when(transferContext.getHttpServletResponse()).thenReturn(httpServletResponse);
        when(transferContext.getUploadRequestProcessor()).thenReturn(requestProcessor);
        when(transferContext.getHeadersMap()).thenReturn(map);
        
        when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);
        doNothing().when(httpServletResponse).setContentType(Mockito.anyString());
        when(requestProcessor.getUploadedData()).thenReturn(managedArtifactList);
        when(transferContext.getHeadersMap()).thenReturn(map);

        TextPlainUploadResponder uploadResponder = new TextPlainUploadResponder(transferContext);
        uploadResponder.respond();
        Assert.assertEquals(stringWriter.toString().trim(), expected,
                "The output to http servlet response is not as expected");
    }

    @Test(expectedExceptions = ArtifactUploadException.class)
    @SuppressWarnings({ "unchecked" })
    public void testResponderIfListIsEmpty() throws IOException {
        UploadRequestProcessor<ManagedArtifact> requestProcessor = mock(UploadRequestProcessor.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        EnumMap<RequestHeaders, String> map = new EnumMap<>(RequestHeaders.class);
        map.put(RequestHeaders.FILENAME, "InternationalMountains_app.zip");
        map.put(RequestHeaders.USERID, "userOne");
        StringBuffer stringBuffer = new StringBuffer("http://localhost:4444/path/TransferServlet");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        List<ManagedArtifact> managedArtifactList = new ArrayList<>();
        TransferContext transferContext = mock(TransferContext.class);

        when(transferContext.getHttpServletRequest()).thenReturn(httpServletRequest);
        when(transferContext.getHttpServletResponse()).thenReturn(httpServletResponse);
        when(transferContext.getUploadRequestProcessor()).thenReturn(requestProcessor);
        when(transferContext.getHeadersMap()).thenReturn(map);
        
        when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);
        doNothing().when(httpServletResponse).setContentType(Mockito.anyString());
        when(requestProcessor.getUploadedData()).thenReturn(managedArtifactList);
        when(transferContext.getHeadersMap()).thenReturn(map);

        TextPlainUploadResponder upooadResponder = new TextPlainUploadResponder(transferContext);

        when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);
        doNothing().when(httpServletResponse).setContentType(Mockito.anyString());
        when(requestProcessor.getUploadedData()).thenReturn(managedArtifactList);

        upooadResponder.respond();
    }

}
