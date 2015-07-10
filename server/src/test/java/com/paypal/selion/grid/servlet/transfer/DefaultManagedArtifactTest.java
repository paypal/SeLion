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

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

import java.util.EnumMap;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.paypal.selion.grid.servlets.transfer.Criteria;
import com.paypal.selion.grid.servlets.transfer.DefaultManagedArtifact;
import com.paypal.selion.grid.servlets.transfer.DefaultManagedArtifact.DefaultCriteria;
import com.paypal.selion.grid.servlets.transfer.ManagedArtifact;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.RequestHeaders;
import com.paypal.selion.utils.ConfigParser;

@PrepareForTest({ ConfigParser.class })
public class DefaultManagedArtifactTest extends PowerMockTestCase {

    private String artifactFileOnePath;


    @BeforeClass
    public void setUpBeforeClass() {
        artifactFileOnePath = DefaultManagedArtifactTest.class.getResource(
                "/artifacts/userOne/DummyArtifact.any").getFile();
    }

    @BeforeMethod
    public void setUpBeforeEveryMethod() throws Exception {
        ConfigParser configParser = mock(ConfigParser.class);
        mockStatic(ConfigParser.class);
        when(ConfigParser.parse()).thenReturn(configParser);
        when(configParser.getLong("artifactExpiryInMilliSec")).thenReturn(86400000L);
    }

    @Test(enabled=false)
    // Enable this test case for testing time difference
    public void testIsExpired() {
        ManagedArtifact managedArtifact = new DefaultManagedArtifact(
                "src/test/resources/artifacts/userOne/userFolder/DummyArtifact.any");
        Assert.assertEquals(managedArtifact.isExpired(), true, "Artifact is not expired after a day");
    }

    @Test
    public void testReflexive() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertEquals(managedArtifact.equals(managedArtifact), true,
                "Managed artifact comparison is not reflexive");
    }

    @Test
    public void testSymmetry() {
        DefaultManagedArtifact managedArtifactOne = new DefaultManagedArtifact(artifactFileOnePath);
        DefaultManagedArtifact managedArtifactTwo = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertEquals(managedArtifactOne.equals(managedArtifactTwo), true,
                "Managed artifact comparison is not symmetric");
        Assert.assertEquals(managedArtifactTwo.equals(managedArtifactOne), true,
                "Managed artifact comparison is not symmetric");
    }

    @Test
    public void testTransitive() {
        DefaultManagedArtifact managedArtifactOne = new DefaultManagedArtifact(artifactFileOnePath);
        DefaultManagedArtifact managedArtifactTwo = new DefaultManagedArtifact(artifactFileOnePath);
        DefaultManagedArtifact managedArtifactThree = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertEquals(managedArtifactOne.equals(managedArtifactTwo), true,
                "Managed artifact comparison is not transitive");
        Assert.assertEquals(managedArtifactTwo.equals(managedArtifactThree), true,
                "Managed artifact comparison is not transitive");
        Assert.assertEquals(managedArtifactOne.equals(managedArtifactThree), true,
                "Managed artifact comparison is not transitive");
    }

    @Test
    public void testMatches() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        EnumMap<RequestHeaders, String> firstMap = new EnumMap<>(RequestHeaders.class);
        firstMap.put(RequestHeaders.FILENAME, "DummyArtifact.any");
        firstMap.put(RequestHeaders.USERID, "userOne");
        Criteria criteria = new DefaultCriteria(firstMap);
        Assert.assertEquals(managedArtifact.matches(criteria), true, "Artifact does not match the expected criteria");
    }

    @Test
    public void testUnEqualFileNameMatches() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        EnumMap<RequestHeaders, String> firstMap = new EnumMap<>(RequestHeaders.class);
        firstMap.put(RequestHeaders.FILENAME, "DummyArtifact4.zip");
        firstMap.put(RequestHeaders.USERID, "userOne");
        Criteria criteria = new DefaultCriteria(firstMap);
        Assert.assertEquals(managedArtifact.matches(criteria), false, "Artifact matches for different file name");
    }

    @Test
    public void testUnEqualUserIdMatches() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        EnumMap<RequestHeaders, String> firstMap = new EnumMap<>(RequestHeaders.class);
        firstMap.put(RequestHeaders.FILENAME, "DummyArtifact.any");
        firstMap.put(RequestHeaders.USERID, "userTwo");
        Criteria criteria = new DefaultCriteria(firstMap);
        Assert.assertEquals(managedArtifact.matches(criteria), false, "Artifact matches for different userId");
    }

    @Test
    public void testFileName() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertEquals(managedArtifact.getArtifactName(), "DummyArtifact.any",
                "Artifact file name does not match");
    }

    @Test
    public void testContentType() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertEquals(managedArtifact.getHttpContentType(), "application/zip",
                "Artifact file name does not match");
    }

}
