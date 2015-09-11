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

import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.grid.servlets.transfer.DefaultManagedArtifact.DefaultRequestParameters;
import com.paypal.selion.grid.servlets.transfer.UploadResponder.JsonUploadResponder;
import com.paypal.selion.grid.servlets.transfer.UploadResponder.TextPlainUploadResponder;

@PrepareForTest({ UploadRequestProcessor.class, HttpServletRequest.class, HttpServletResponse.class,
        ManagedArtifact.class })
public class UploadResponderTest extends PowerMockTestCase {

    @Test
    public void testJsonResponder() throws IOException {
        UploadRequestProcessor requestProcessor = mock(UploadRequestProcessor.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        ManagedArtifact managedArtifact = mock(ManagedArtifact.class);

        String expected = "{\"files\":[{\"fileName\":\"DummyArtifact.any\",\"url\":\"http://localhost:4444/path/TransferServlet/userOne/DummyArtifact.any\"}]}";
        Map<String, String> map = new HashMap<>();
        map.put(ManagedArtifact.ARTIFACT_FILE_NAME, "DummyArtifact.any");
        map.put(DefaultRequestParameters.UID, "userOne");

        StringBuffer stringBuffer = new StringBuffer("http://localhost:4444/path/TransferServlet");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        List<ManagedArtifact> managedArtifactList = new ArrayList<>();
        managedArtifactList.add(managedArtifact);
        TransferContext transferContext = mock(TransferContext.class);

        when(managedArtifact.getArtifactName()).thenReturn("DummyArtifact.any");
        when(managedArtifact.getAbsolutePath()).thenReturn("/userOne/DummyArtifact.any");
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
    public void testTextPlainResponder() throws IOException {
        UploadRequestProcessor requestProcessor = mock(UploadRequestProcessor.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        ManagedArtifact managedArtifact = mock(ManagedArtifact.class);

        String expected = "fileName=DummyArtifact.any,url=http://localhost:4444/path/TransferServlet/userOne/DummyArtifact.any";
        Map<String, String> map = new HashMap<>();
        map.put(ManagedArtifact.ARTIFACT_FILE_NAME, "DummyArtifact.any");
        map.put(DefaultRequestParameters.UID, "userOne");

        StringBuffer stringBuffer = new StringBuffer("http://localhost:4444/path/TransferServlet");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        List<ManagedArtifact> managedArtifactList = new ArrayList<>();
        managedArtifactList.add(managedArtifact);
        TransferContext transferContext = mock(TransferContext.class);

        when(managedArtifact.getArtifactName()).thenReturn("DummyArtifact.any");
        when(managedArtifact.getAbsolutePath()).thenReturn("/userOne/DummyArtifact.any");
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
    public void testResponderIfListIsEmpty() throws IOException {
        UploadRequestProcessor requestProcessor = mock(UploadRequestProcessor.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        Map<String, String> map = new HashMap<>();

        map.put(ManagedArtifact.ARTIFACT_FILE_NAME, "DummyArtifact.any");
        map.put(DefaultRequestParameters.UID, "userOne");
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
