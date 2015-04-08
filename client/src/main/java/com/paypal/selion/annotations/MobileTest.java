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
 * Interface creates annotation MobileTest and specified parameters<br>
 * Annotation @MobileTest will let Grid know to open a session on a mobile device/simulator
 */
@Retention(RUNTIME)
@Target({ CONSTRUCTOR, METHOD, TYPE })
public @interface MobileTest {
    /**
     * Establish application name to use. This is mandatory for all defaults apps. <b>Example</b>
     * 
     * <pre>
     * &#064;Test()
     * &#064;MobileTest(appName = &quot;Safari&quot;)
     * public void webtest2() {
     *     Grid.open(&quot;http://paypal.com&quot;);
     * }
     * </pre>
     * 
     * App version can be specified as part of the appName as:
     * 
     * <pre>
     * appName = &quot;Safari:7.0&quot;
     * </pre>
     */
    String appName() default "";

    /**
     * Establish the type of device to be used(android, iphone, etc). Default's to <code>iphone</code> Platform version
     * can be specified as part of the device as:
     * 
     * <pre>
     * device = &quot;iphone:7.1&quot;
     * </pre>
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
     * Device Serial to be used. Defaults to device/emulator chosen by Selendroid / iosDriver
     */
    String deviceSerial() default "";

    /**
     * Establish the type of device to be used (Nexus5, Iphone5s, etc). Defaults to device/emulator chosen by Selendroid
     * / iosDriver /Appium
     */
    String deviceType() default "";

    /**
     * Provide additional capabilities that you may wish to add as a name value pair. Values of true or false will be
     * treated as Boolean capabilities unless you surround the value with '
     * 
     * <pre>
     * {@literal @}Test
     * {@literal @}MobileTest(additionalCapabilities={"key1:value1","key2:value2"})
     * public void testMethod(){
     *     // flow
     * }
     * </pre>
     */
    String[] additionalCapabilities() default {};

    /**
     * This parameter represents the fully qualified path of the app that is to be spawned. For app exist in the local
     * disk this should be an absolute path, for app exist in the remote location it should be http URL and for app
     * exist in sauce cloud it can be sauce storage "sauce-storage:testApp.apk". This is mandatory for installable apps
     * running on appium. appPath cannot be used along with the appName.
     * 
     * <pre>
     *  for app in local disk it can be like appPath = C:\\test\\testApp.apk;
     * or for app in http location it can be like appPath = http://server/downloads/testApp.apk
     * or for app in sauce cloud it can be like appPath = sauce-storage:testApp.zip
     * </pre>
     */
    String appPath() default "";
    
    /**
     * This parameter specifies to execute mobile test cases using respective mobile driver.
     */
    String mobileNodeType() default "";

}
