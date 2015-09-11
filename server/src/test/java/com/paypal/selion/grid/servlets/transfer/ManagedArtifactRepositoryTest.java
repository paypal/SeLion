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

import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.paypal.selion.utils.ConfigParser;

@PrepareForTest({ ManagedArtifactRepository.class, ConfigParser.class })
public class ManagedArtifactRepositoryTest extends PowerMockTestCase {

    @BeforeMethod
    public void setUpBeforeEveryMethod() {
        ConfigParser configParser = mock(ConfigParser.class);
        mockStatic(ConfigParser.class);
        when(ConfigParser.parse()).thenReturn(configParser);
        when(configParser.getLong("artifactExpiryInMilliSec")).thenReturn(86400000L);
        when(configParser.getString("managedArtifact")).thenReturn(
                "com.paypal.selion.grid.servlets.transfer.DefaultManagedArtifact");
    }

    @Test(expectedExceptions = ArtifactDownloadException.class)
    public void testGetArtifactPresentFailure() throws Exception {
        ManagedArtifactRepository managedArtifactRepository = spy(ManagedArtifactRepository.getInstance());
        doThrow(new ArtifactDownloadException("")).when(managedArtifactRepository, "getMatchedArtifact",
                Mockito.anyString());
        managedArtifactRepository.getArtifact("/userOne/DummyArtifact.any");
    }

    @Test(expectedExceptions = ArtifactDownloadException.class)
    public void testGetArtifactPresentFailureTwo() throws Exception {
        ManagedArtifactRepository managedArtifactRepository = spy(ManagedArtifactRepository.getInstance());
        ManagedArtifact managedArtifact = mock(ManagedArtifact.class);
        when(managedArtifact.isExpired()).thenReturn(true);
        doReturn(managedArtifact).when(managedArtifactRepository, "getMatchedArtifact", Mockito.anyString());
        managedArtifactRepository.getArtifact("/userOne/DummyArtifact.any");
    }

    @Test()
    public void testGetArtifactSuccess() throws Exception {
        ManagedArtifactRepository managedArtifactRepository = spy(ManagedArtifactRepository.getInstance());
        ManagedArtifact managedArtifact = mock(ManagedArtifact.class);
        doReturn(managedArtifact).when(managedArtifactRepository, "getMatchedArtifact", Mockito.anyString());
        Assert.assertNotNull(managedArtifactRepository.getArtifact("/userOne/DummyArtifact.any"),
                "Matched/Valid proper artifact is returned as null");
    }

}
