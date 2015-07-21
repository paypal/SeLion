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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.paypal.selion.grid.servlets.transfer.ArtifactDownloadException;
import com.paypal.selion.grid.servlets.transfer.Criteria;
import com.paypal.selion.grid.servlets.transfer.ManagedArtifact;
import com.paypal.selion.grid.servlets.transfer.ManagedArtifactRepository;
import com.paypal.selion.utils.ConfigParser;

@PrepareForTest({ManagedArtifactRepository.class, ConfigParser.class })
public class ManagedArtifactRepositoryTest extends PowerMockTestCase {

    @BeforeClass
    public void setUpBeforeClass() {
        System.setProperty("repository", System.getProperty("user.home"));
    }

    @BeforeMethod
    public void setUpBeforeEveryMethod() throws Exception {
        ConfigParser configParser = mock(ConfigParser.class);
        mockStatic(ConfigParser.class);
        when(ConfigParser.parse()).thenReturn(configParser);
        when(configParser.getLong("artifactExpiryInMilliSec")).thenReturn(86400000L);
        when(configParser.getString("managedArtifact")).thenReturn(
                "com.paypal.selion.grid.servlets.transfer.DefaultManagedArtifact");
    }

    @Test()
    public void testIsArtifactPresentFailure() throws Exception {
        ManagedArtifactRepository managedArtifactRespository = spy(ManagedArtifactRepository.getInstance());
        Criteria criteria = mock(Criteria.class);
        doThrow(new ArtifactDownloadException("")).when(managedArtifactRespository, "getMatch", criteria);
        Assert.assertFalse(managedArtifactRespository.isArtifactPresent(criteria),
                "Failed getMatch method within server repository resulted in isArtifactPresent() method returning true");
    }

    @Test()
    public void testIsArtifactPresentFailureTwo() throws Exception {
        ManagedArtifactRepository managedArtifactRespository = spy(ManagedArtifactRepository.getInstance());
        Criteria criteria = mock(Criteria.class);
        ManagedArtifact managedArtifact = mock(ManagedArtifact.class);
        when(managedArtifact.isExpired()).thenReturn(true);
        doReturn(managedArtifact).when(managedArtifactRespository, "getMatch", criteria);
        doReturn(managedArtifact).when(managedArtifactRespository, "getManagedArtifact", Mockito.any(String.class));
        Assert.assertEquals(managedArtifactRespository.isArtifactPresent(criteria), false,
                "Expired Artifact is still fetched by repository");
    }

    @Test()
    public void testIsArtifactPresentSuccess() throws Exception {
        ManagedArtifactRepository managedArtifactRespository = spy(ManagedArtifactRepository.getInstance());
        Criteria criteria = mock(Criteria.class);
        ManagedArtifact managedArtifact = mock(ManagedArtifact.class);
        doReturn(managedArtifact).when(managedArtifactRespository, "getMatch", criteria);
        doReturn(managedArtifact).when(managedArtifactRespository, "getManagedArtifact", Mockito.any(String.class));
        managedArtifactRespository.isArtifactPresent(criteria);
    }

    @Test(expectedExceptions = ArtifactDownloadException.class)
    public void testGetArtifactPresentFailure() throws Exception {
        ManagedArtifactRepository managedArtifactRespository = spy(ManagedArtifactRepository.getInstance());
        Criteria criteria = mock(Criteria.class);
        doThrow(new ArtifactDownloadException("")).when(managedArtifactRespository, "getMatch", criteria);
        managedArtifactRespository.getArtifact(criteria);
    }

    @Test(expectedExceptions = ArtifactDownloadException.class)
    public void testGetArtifactPresentFailureTwo() throws Exception {
        ManagedArtifactRepository managedArtifactRespository = spy(ManagedArtifactRepository.getInstance());
        Criteria criteria = mock(Criteria.class);
        ManagedArtifact managedArtifact = mock(ManagedArtifact.class);
        when(managedArtifact.isExpired()).thenReturn(true);
        doReturn(managedArtifact).when(managedArtifactRespository, "getMatch", criteria);
        doReturn(managedArtifact).when(managedArtifactRespository, "getManagedArtifact", Mockito.any(String.class));
        managedArtifactRespository.getArtifact(criteria);
    }

    @Test()
    public void testGetArtifactSuccess() throws Exception {
        ManagedArtifactRepository managedArtifactRespository = spy(ManagedArtifactRepository.getInstance());
        Criteria criteria = mock(Criteria.class);
        ManagedArtifact managedArtifact = mock(ManagedArtifact.class);
        doReturn(managedArtifact).when(managedArtifactRespository, "getMatch", criteria);
        doReturn(managedArtifact).when(managedArtifactRespository, "getManagedArtifact", Mockito.any(String.class));
        Assert.assertNotNull(managedArtifactRespository.getArtifact(criteria),
                "Matched/Valid proper artifact is returned as null");
    }

}
