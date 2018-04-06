/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;

/**
 * SessionSharingMethodInterceptor controls order of execution of tests that share the same selenium session.
 * 
 */
public class SessionSharingMethodInterceptor implements IMethodInterceptor {

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {

        List<IMethodInstance> result = new ArrayList<IMethodInstance>();
        Map<Object, List<IMethodInstance>> sharingResult = new HashMap<Object, List<IMethodInstance>>();

        for (IMethodInstance temp : methods) {

            if (temp.getInstance().getClass().getAnnotation(WebTest.class) == null
                    && temp.getInstance().getClass().getAnnotation(MobileTest.class) == null) {
                result.add(temp);
            } else {

                if (sharingResult.containsKey(temp.getInstance())) {
                    sharingResult.get(temp.getInstance()).add(temp);
                } else {
                    List<IMethodInstance> temp1 = new ArrayList<IMethodInstance>();
                    temp1.add(temp);
                    sharingResult.put(temp.getInstance(), temp1);
                }
            }
        }

        for (List<IMethodInstance> temp : sharingResult.values()) {
            result.addAll(temp);
        }

        return result;
    }

}
