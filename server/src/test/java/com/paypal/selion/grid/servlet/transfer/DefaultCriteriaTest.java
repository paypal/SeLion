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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.grid.servlets.transfer.ArtifactDownloadException;
import com.paypal.selion.grid.servlets.transfer.Criteria;
import com.paypal.selion.grid.servlets.transfer.DefaultManagedArtifact.DefaultCriteria;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.RequestHeaders;

public class DefaultCriteriaTest {

    private Criteria criteriaOne;

    private Criteria criteriaTwo;

    private Criteria criteriaThree;

    private EnumMap<RequestHeaders, String> firstMap;

    private EnumMap<RequestHeaders, String> secondMap;

    private EnumMap<RequestHeaders, String> thirdMap;

    private EnumMap<RequestHeaders, String> unequalMap;

    @BeforeClass
    public void setUp() {
        firstMap = new EnumMap<>(RequestHeaders.class);
        secondMap = new EnumMap<>(RequestHeaders.class);
        thirdMap = new EnumMap<>(RequestHeaders.class);
        unequalMap = new EnumMap<>(RequestHeaders.class);
        firstMap.put(RequestHeaders.FILENAME, "TestFile");
        firstMap.put(RequestHeaders.USERID, "userOne");
        secondMap.put(RequestHeaders.FILENAME, "TestFile");
        secondMap.put(RequestHeaders.USERID, "userOne");
        thirdMap.put(RequestHeaders.FILENAME, "TestFile");
        thirdMap.put(RequestHeaders.USERID, "userOne");
        unequalMap.put(RequestHeaders.FILENAME, "TestFile1");
        unequalMap.put(RequestHeaders.USERID, "userTwo");
        unequalMap.put(RequestHeaders.APPLICATIONFOLDER, "appFolder");
        criteriaOne = new DefaultCriteria(firstMap);
        criteriaTwo = new DefaultCriteria(secondMap);
        criteriaThree = new DefaultCriteria(thirdMap);
    }

    @Test
    public void testReflexive() {
        EnumMap<RequestHeaders, String> firstMap = new EnumMap<>(RequestHeaders.class);
        firstMap.put(RequestHeaders.FILENAME, "TestFile");
        firstMap.put(RequestHeaders.USERID, "userOne");
        DefaultCriteria defaultCriteriaOne = new DefaultCriteria(firstMap);
        Assert.assertEquals(defaultCriteriaOne.equals(defaultCriteriaOne), true, "Criteria equals is not reflexive");
    }

    @Test
    public void testSymmetry() {
        Assert.assertEquals(criteriaOne.equals(criteriaTwo), true, "Criterias relation is not symmetric");
        Assert.assertEquals(criteriaTwo.equals(criteriaOne), true, "Criterias relation is not symmetric");
    }

    @Test
    public void testTransitive() {
        Assert.assertEquals(criteriaOne.equals(criteriaTwo), true, "Criterias relation is not transitive");
        Assert.assertEquals(criteriaTwo.equals(criteriaThree), true, "Criterias relation is not transitive");
        Assert.assertEquals(criteriaOne.equals(criteriaThree), true, "Criterias relation is not transitive");
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(criteriaOne.equals(criteriaTwo), true, "Criterias whos elements are same are not equal");
    }

    @Test
    public void testUnequalFileNames() {
        EnumMap<RequestHeaders, String> anotherFileNameMap = new EnumMap<>(RequestHeaders.class);
        anotherFileNameMap.put(RequestHeaders.FILENAME, "TestFile1");
        anotherFileNameMap.put(RequestHeaders.USERID, "userOne");
        Assert.assertEquals(criteriaOne.equals(new DefaultCriteria(anotherFileNameMap)), false,
                "Criterias whos name elements are not same are equal");
    }

    @Test
    public void testUnequalUserId() {
        EnumMap<RequestHeaders, String> anotherTimeMap = new EnumMap<>(RequestHeaders.class);
        anotherTimeMap.put(RequestHeaders.FILENAME, "TestFile");
        anotherTimeMap.put(RequestHeaders.USERID, "userThree");
        Assert.assertEquals(criteriaOne.equals(new DefaultCriteria(anotherTimeMap)), false,
                "Criterias whos userId elements are not same are equal");
    }
    
    @Test
    public void testUnequalFolderNamesOne() {
        EnumMap<RequestHeaders, String> anotherTimeMap = new EnumMap<>(RequestHeaders.class);
        anotherTimeMap.put(RequestHeaders.FILENAME, "TestFile");
        anotherTimeMap.put(RequestHeaders.USERID, "userOne");
        anotherTimeMap.put(RequestHeaders.APPLICATIONFOLDER, "folder1");
        Assert.assertEquals(criteriaOne.equals(new DefaultCriteria(anotherTimeMap)), false,
                "Criterias whos folderName elements are not same are equal");
    }
    
    @Test
    public void testUnequalFolderNamesTwo() {
        EnumMap<RequestHeaders, String> anotherTimeMap = new EnumMap<>(RequestHeaders.class);
        anotherTimeMap.put(RequestHeaders.FILENAME, "TestFile");
        anotherTimeMap.put(RequestHeaders.USERID, "userOne");
        Assert.assertEquals(unequalMap.equals(anotherTimeMap), false,
                "Criterias whos folderName elements are not same are equal");
    }
    
    @Test
    public void testUnequalToNull() {
        Assert.assertEquals(criteriaOne.equals(null), false, "Criteria equals null reference");
    }

    @Test
    public void testSameCriteriaPresentInCollection() {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(criteriaOne);
        Assert.assertEquals(criteriaList.contains(criteriaOne), true, "Same Object not present in Collection");
    }

    @Test
    public void testEqualCriteriaPresentInCollection() {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(criteriaOne);
        Assert.assertEquals(criteriaList.contains(criteriaTwo), true, "Equal Object not present in Collection");
    }

    @Test
    public void testUnEqualCriteriaNotPresentInCollection() {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(criteriaOne);
        Assert.assertEquals(criteriaList.contains(new DefaultCriteria(unequalMap)), false,
                "Unequal Object present in Collection");
    }

    @Test
    public void testCriteriaAsMap() {
        Map<String, String> criteriaMap = criteriaOne.asMap();
        Assert.assertEquals(criteriaMap.containsKey("fileName"), true,
                "fileName key not present in DefaultCriteria map");
        Assert.assertEquals(criteriaMap.containsKey("userId"), true, "userId key not present in DefaultCriteria map");
        Assert.assertEquals(criteriaMap.get("fileName"), "TestFile",
                "fileName value is different in DefaultCriteria map");
        Assert.assertEquals(criteriaMap.get("userId"), "userOne", "userId value is different in DefaultCriteria map");
    }

    @Test(expectedExceptions = ArtifactDownloadException.class)
    public void testInvalidCriteriaCreationOne() {
        EnumMap<RequestHeaders, String> firstMap = new EnumMap<>(RequestHeaders.class);
        firstMap.put(RequestHeaders.FILENAME, "TestFile");
        firstMap.put(RequestHeaders.APPLICATIONFOLDER, "temp");
        new DefaultCriteria(firstMap);
        Assert.assertTrue(false, "Able to create Default Criteria with invalid time");
    }

    @Test(expectedExceptions = ArtifactDownloadException.class)
    public void testInvalidCriteriaCreationTwo() {
        EnumMap<RequestHeaders, String> firstMap = new EnumMap<>(RequestHeaders.class);
        firstMap.put(RequestHeaders.USERID, "userOne");
        firstMap.put(RequestHeaders.APPLICATIONFOLDER, "temp");
        new DefaultCriteria(firstMap);
        Assert.assertTrue(false, "Able to create Default Criteria with invalid time");
    }

}
