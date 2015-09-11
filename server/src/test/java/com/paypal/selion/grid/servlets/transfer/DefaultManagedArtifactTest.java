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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.grid.servlets.transfer.DefaultManagedArtifact.DefaultRequestParameters;

import java.io.File;
import java.io.IOException;

public class DefaultManagedArtifactTest extends PowerMockTestCase {

    private String artifactFileOnePath;

    @BeforeSuite(alwaysRun = true)
    public void setUpBeforeSuite() {
        System.setProperty("selionHome",
                new File(DefaultManagedArtifactTest.class.getResource("/").getPath()).getAbsoluteFile().getParent()
                        + "/.selion");
        new File(SeLionConstants.SELION_HOME_DIR).mkdirs();
    }

    @BeforeClass
    public void setUpBeforeClass() throws IOException {
        File f = new File(DefaultManagedArtifactTest.class.getResource("/artifacts")
                .getFile());
        FileUtils.copyDirectory(f, new File(System.getProperty("selionHome") + "/repository"));
        artifactFileOnePath = new File(System.getProperty("selionHome") + "/repository/userOne/DummyArtifact.any")
                .getAbsolutePath();
    }

    @Test(enabled = false)
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
        Assert.assertEquals(managedArtifact.matchesPathInfo("/userOne/DummyArtifact.any"), true,
                "Artifact does not match the expected criteria");
    }

    @Test
    public void testUnEqualFileNameMatches() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertEquals(managedArtifact.matchesPathInfo("/userOne/DummyArtifact4.zip"), false,
                "Artifact matches for different file name");
    }

    @Test
    public void testUnEqualUserIdMatches() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertEquals(managedArtifact.matchesPathInfo("/userTwo/DummyArtifact.any"), false,
                "Artifact matches for different userId");
    }

    @Test
    public void testFileName() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertEquals(managedArtifact.getArtifactName(), "DummyArtifact.any", "Artifact file name does not match");
    }

    @Test
    public void testContentType() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertEquals(managedArtifact.getHttpContentType(), "application/zip",
                "Artifact file name does not match");
    }

    @Test
    public void testPathInfo() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        String actual = managedArtifact.getAbsolutePath();
        String expected = FilenameUtils.separatorsToSystem(SeLionConstants.SELION_HOME_DIR
                + "repository/userOne/DummyArtifact.any");
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testUID() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertEquals(managedArtifact.getUIDFolderName(), "userOne");
    }

    @Test
    public void testSubfolder() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertEquals(managedArtifact.getSubFolderName(), "");
    }

    @Test
    public void testRequestParameters() {
        DefaultManagedArtifact managedArtifact = new DefaultManagedArtifact(artifactFileOnePath);
        Assert.assertTrue(managedArtifact.getRequestParameters() instanceof DefaultRequestParameters);
        Assert.assertTrue(managedArtifact.getRequestParameters().getParameters().containsKey("uid"));
        Assert.assertTrue(managedArtifact.getRequestParameters().getParameters()
                .containsKey(ManagedArtifact.ARTIFACT_FILE_NAME));
        Assert.assertTrue(managedArtifact.getRequestParameters().getParameters()
                .containsKey(ManagedArtifact.ARTIFACT_FOLDER_NAME));
    }

}
