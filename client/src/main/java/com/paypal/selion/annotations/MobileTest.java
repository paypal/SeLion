/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * interface creates annotation AppTest and specified parameters<br>
 * Annotation @MobileTest will let Grid know to open a session on a mobile device/simulator, start session
 */
@Retention(RUNTIME)
@Target({ CONSTRUCTOR, METHOD, TYPE })
public @interface MobileTest {
    /**
     * Establish application name to use. <b>Example</b>
     * 
     * <pre>
     * &#064;Test()
     * &#064;MobileTest(appName = &quot;Safari&quot;)
     * public void webtest2() {
     *     Grid.open(&quot;http://paypal.com&quot;);
     * }
     * </pre>
     */
    String appName() default "";

    /**
     * Establish the type of device to be used. Default's to <code>iphone</code>
     */
    String device() default "iphone";

    /**
     * Establish the application's language that is to be used. Default's to <code>English</code> specified as
     * <code>en</code>
     */
    String language() default "en";

    /**
     * Establish the application's locale that is to be used. Default's to <code>English</code> specified as
     * <code>en_US</code>
     */
    String locale() default "en_US";

    /**
     * Device Serial to be used. Defaults to device/emulator chosen by Selendroid
     */
    String deviceSerial() default "";

}
