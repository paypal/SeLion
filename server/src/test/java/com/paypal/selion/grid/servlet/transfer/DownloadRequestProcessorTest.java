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

package com.paypal.selion.grid.servlet.transfer;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.reflect.Whitebox;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.grid.servlets.transfer.ArtifactDownloadException;
import com.paypal.selion.grid.servlets.transfer.DownloadRequestProcessor;
import com.paypal.selion.grid.servlets.transfer.ManagedArtifact;
import com.paypal.selion.grid.servlets.transfer.ServerRepository;

@PrepareForTest({ DownloadRequestProcessor.class, ServerRepository.class })
public class DownloadRequestProcessorTest extends PowerMockTestCase {

    @BeforeClass
    public void setUpBeforClass() {
        System.setProperty("repository", System.getProperty("user.home"));
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testSuccessIsArtifactPresent() {
        ServerRepository serverRepository = mock(ServerRepository.class);
        when(serverRepository.isArtifactPresent(Mockito.anyString())).thenReturn(true);

        DownloadRequestProcessor downloadRequestProcessor = new DownloadRequestProcessor();
        Whitebox.setInternalState(downloadRequestProcessor, "serverRepository", serverRepository);

        Assert.assertEquals(downloadRequestProcessor.isArtifactPresent("/artifacts/userOne/DummyArtifact.any"), true,
                "False returned for isArtifactPresent when the internal artifact present is true");
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testFailedIsArtifactPresent() {
        ServerRepository serverRepository = mock(ServerRepository.class);
        when(serverRepository.isArtifactPresent(Mockito.anyString())).thenReturn(false);

        DownloadRequestProcessor downloadRequestProcessor = new DownloadRequestProcessor();
        Whitebox.setInternalState(downloadRequestProcessor, "serverRepository", serverRepository);

        Assert.assertEquals(downloadRequestProcessor.isArtifactPresent("/artifacts/userOne/DummyArtifact.any"), false,
                "True returned for isArtifactPresent when the internal artifact present is false");
    }

    @Test(expectedExceptions = ArtifactDownloadException.class)
    @SuppressWarnings({ "rawtypes" })
    public void testFailedGetArtifact() {
        ServerRepository serverRepository = mock(ServerRepository.class);
        when(serverRepository.getArtifact(Mockito.anyString())).thenThrow(new ArtifactDownloadException(""));

        DownloadRequestProcessor downloadRequestProcessor = new DownloadRequestProcessor();
        Whitebox.setInternalState(downloadRequestProcessor, "serverRepository", serverRepository);

        downloadRequestProcessor.getArtifact("/artifacts/userOne/DummyArtifact.any");
    }

    @Test()
    @SuppressWarnings({ "rawtypes" })
    public void testGetArtifact() {
        ServerRepository serverRepository = mock(ServerRepository.class);
        ManagedArtifact managedArtifact = mock(ManagedArtifact.class);
        when(serverRepository.isArtifactPresent(Mockito.anyString())).thenReturn(true);
        when(serverRepository.getArtifact(Mockito.anyString())).thenReturn(managedArtifact);

        DownloadRequestProcessor downloadRequestProcessor = new DownloadRequestProcessor();
        Whitebox.setInternalState(downloadRequestProcessor, "serverRepository", serverRepository);

        downloadRequestProcessor.getArtifact("/artifacts/userOne/DummyArtifact.any");
    }

}
