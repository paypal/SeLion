/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

package com.paypal.selion.internal.platform.grid;

import static com.paypal.selion.platform.asserts.SeLionAsserts.assertEquals;
import static com.paypal.selion.platform.asserts.SeLionAsserts.assertNull;

import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.internal.platform.grid.BasicTestSession;
import com.paypal.selion.internal.platform.grid.MobileTestSession;
import com.paypal.selion.internal.platform.grid.TestSessionFactory;
import com.paypal.selion.internal.platform.grid.WebTestSession;

public class TestSessionFactoryTest {

    @Test(groups = "unit")
    public void testGetSupportedAnnotations() {
        assertEquals(new Class<?>[] { WebTest.class, MobileTest.class }, TestSessionFactory.getSupportedAnnotations());
    }

    @Test(groups = "unit")
    public void testNewInstanceByClass() {
        assertNull(TestSessionFactory.newInstance((Class<?>) null), "verify the new instance is null");
        assertEquals(TestSessionFactory.newInstance(WebTest.class).getClass(), WebTestSession.class,
                "verify a WebTestSession is returned");
        assertEquals(TestSessionFactory.newInstance(MobileTest.class).getClass(), MobileTestSession.class,
                "verify a MobileTestSession instance is returned");
        assertEquals(TestSessionFactory.newInstance(Test.class).getClass(), BasicTestSession.class,
                "verify a BasicTestSession instance is returned");

    }
}
