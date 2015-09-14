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

package com.paypal.selion.configuration;

import java.util.ServiceLoader;

import org.testng.ITestContext;

/**
 * An abstract implementation that guarantees configurations to be initialized via {@link ServiceLoader}. Any downstream
 * consumer of SeLion would need to follow the below mentioned steps to leverage this capability.
 * <ul>
 * <li>Create a customized configuration initializer by extending {@link AbstractConfigInitializer}
 * <li>Incorporate the logic of initializing the configuration based on the current {@link ITestContext} by implementing
 * {@link Initializer#initialize(ITestContext)}
 * <li>Create a folder named <code>META-INF/services/</code> under your project (src/main/resources if it's a maven
 * project).
 * <li>Create a text file with its name as <code>com.paypal.selion.configuration.Initializer</code>.
 * <li>Add the fully qualified name of your custom configuration initializer (the one that extends
 * {@link AbstractConfigInitializer} ) into the file.
 * </ul>
 * 
 * Once the above steps have been followed, SeLion now takes care of invoking your project specific configuration along
 * with invoking its own configuration as well. Now your project does not have to worry about TestNG listener order
 * being maintained etc.,
 * 
 */
public abstract class AbstractConfigInitializer implements Initializer, Comparable<Initializer> {

    @Override
    public int compareTo(Initializer listener) {
        return (this.getPriority() - listener.getPriority());
    }

}
