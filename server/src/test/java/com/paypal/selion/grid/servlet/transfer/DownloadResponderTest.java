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
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.grid.servlets.transfer.ArtifactDownloadException;
import com.paypal.selion.grid.servlets.transfer.Criteria;
import com.paypal.selion.grid.servlets.transfer.DownloadRequestProcessor;
import com.paypal.selion.grid.servlets.transfer.DownloadResponder;
import com.paypal.selion.grid.servlets.transfer.ManagedArtifact;
import com.paypal.selion.grid.servlets.transfer.TransferContext;
import com.paypal.selion.utils.ConfigParser;

@PrepareForTest({ DownloadResponder.class, ConfigParser.class })
public class DownloadResponderTest extends PowerMockTestCase {

    @Test
    public void testSuccessWritingToServletResponse() throws Exception {
        ConfigParser configParser = mock(ConfigParser.class);
        mockStatic(ConfigParser.class);
        when(ConfigParser.parse()).thenReturn(configParser);
        when(configParser.getString("managedCriteria")).thenReturn(
                "com.paypal.selion.grid.servlets.transfer.DefaultManagedArtifact$DefaultCriteria");

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        DownloadRequestProcessor downloadProcessor = mock(DownloadRequestProcessor.class);
        ManagedArtifact managedArtifact = mock(ManagedArtifact.class);

        byte[] bytes = new byte[] { 1, 2, 3, 4 };
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(4);
        TransferContext transferContext = new TransferContext(httpServletRequest, httpServletResponse);
        transferContext.setDownloadRequestProcessor(downloadProcessor);

        when(httpServletRequest.getPathInfo()).thenReturn("/userOne/DummyArtifact.any");
        when(downloadProcessor.getArtifact(Mockito.any(Criteria.class))).thenReturn(managedArtifact);
        when(managedArtifact.getArtifactContents()).thenReturn(bytes);
        when(httpServletResponse.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                bos.write(b);
            }
        });

        DownloadResponder downloadResponder = spy(new DownloadResponder(transferContext));
        downloadResponder.respond();

        Assert.assertEquals(Arrays.toString(bos.toByteArray()), "[1, 2, 3, 4]",
                "The byte array received in the servlet response is not the one read from the artifact");
    }

    @Test(expectedExceptions = ArtifactDownloadException.class)
    public void testFailedToGetArtifact() throws Exception {
        ConfigParser configParser = mock(ConfigParser.class);
        mockStatic(ConfigParser.class);
        when(ConfigParser.parse()).thenReturn(configParser);
        when(configParser.getString("managedCriteria")).thenReturn("xxxx");

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        DownloadRequestProcessor downloadProcessor = mock(DownloadRequestProcessor.class);

        TransferContext transferContext = new TransferContext(httpServletRequest, httpServletResponse);
        transferContext.setDownloadRequestProcessor(downloadProcessor);

        when(httpServletRequest.getPathInfo()).thenReturn("/userOne/DummyArtifact.any");
        when(downloadProcessor.getArtifact(Mockito.any(Criteria.class))).thenThrow(new ArtifactDownloadException(""));

        DownloadResponder downloadResponder = spy(new DownloadResponder(transferContext));
        downloadResponder.respond();
    }

}
