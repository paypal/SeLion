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

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.Test;

@PrepareForTest({ DownloadRequestProcessor.class, ServerRepository.class })
public class DownloadRequestProcessorTest extends PowerMockTestCase {

    @Test(expectedExceptions = ArtifactDownloadException.class)
    public void testFailedGetArtifact() {
        ServerRepository serverRepository = mock(ServerRepository.class);
        when(serverRepository.getArtifact(Mockito.anyString())).thenThrow(new ArtifactDownloadException(""));

        DownloadRequestProcessor downloadRequestProcessor = new DownloadRequestProcessor();
        Whitebox.setInternalState(downloadRequestProcessor, "serverRepository", serverRepository);

        downloadRequestProcessor.getArtifact("/userOne/DummyArtifact.any");
    }

    @Test()
    public void testGetArtifact() {
        ServerRepository serverRepository = mock(ServerRepository.class);
        ManagedArtifact managedArtifact = mock(ManagedArtifact.class);
        when(serverRepository.getArtifact(Mockito.anyString())).thenReturn(managedArtifact);

        DownloadRequestProcessor downloadRequestProcessor = new DownloadRequestProcessor();
        Whitebox.setInternalState(downloadRequestProcessor, "serverRepository", serverRepository);

        downloadRequestProcessor.getArtifact("/userOne/DummyArtifact.any");
    }

}
