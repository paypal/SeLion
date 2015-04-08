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
 * Interface creates annotation WebTest and specified parameters<br>
 * Annotation @Webtest will let Grid know to open browser instance and start a session
 */
@Retention(RUNTIME)
@Target({ CONSTRUCTOR, METHOD, TYPE })
public @interface WebTest {

    /**
     * Establish browser profile to use. Will default to "firefox". <b>Example</b>
     * 
     * <pre>
     * &#064;Test()
     * &#064;WebTest(browser = &quot;*iexplore&quot;)
     * public void webtest2() {
     *     Grid.open(&quot;http://paypal.com&quot;);
     * }
     * </pre>
     */
    String browser() default "";

    /**
     * Keep the session open or not <code>true/false</code> <b>default</b>: false</br> <b>Example</b>
     * 
     * <pre>
     * &#064;Test()
     * &#064;WebTest(keepSessionOpen = false)
     * public void webtest2() {
     *     Grid.selenium().open(&quot;http://paypal.com&quot;);
     * }
     * </pre>
     */
    boolean keepSessionOpen() default false;

    /**
     * Force a new session to open or not <code>true/false</code> <b>default</b>: true</br>
     * 
     * <pre>
     * &#064;Test()
     * &#064;WebTest(openNewSession = false)
     * public void webtest2() {
     *     Grid.selenium().open(&quot;http://paypal.com&quot;);
     * }
     * </pre>
     */
    boolean openNewSession() default true;

    /**
     * Establish a name for this browser session or switch to a session left open with the same name. By default, a
     * dynamic name will be assigned. Use this in conjunction with {@link #keepSessionOpen()} and
     * {@link #openNewSession()}. Session names are class aware. In other words, you can not share sessions across
     * different classes.
     * 
     * <pre>
     * &#064;Test()
     * &#064;WebTest(sessionName = &quot;buyerSession&quot;, keepSessionOpen = true)
     * public void testNavigateToPayPal() {
     *     Grid.driver().get(&quot;https://paypal.com&quot;);
     * }
     * 
     * &#064;Test()
     * &#064;WebTest(sessionName = &quot;buyerSession&quot;, openNewSession = false)
     * public void testBuyerLoginToPayPal() {
     *     // Do PayPal login steps
     * }
     * </pre>
     */
    String sessionName() default "";

    /**
     * Provide additional capabilities that you may wish to add as a name value pair. Values of true or false will be
     * treated as Boolean capabilities unless you surround the value with '
     * 
     * <pre>
     * {@literal @}Test
     * {@literal @}WebTest(additionalCapabilities={"key1:value1","key2:value2"})
     * public void testMethod(){
     *     //UI navigation steps 
     * }
     * </pre>
     */
    String[] additionalCapabilities() default {};

    /**
     * Define the height of the browser window that will be spawned
     * 
     * The width also has to be provided else the height is ignored
     * 
     * <pre>
     * {@literal @}Test
     * {@literal @}WebTest(browserHeight="height")
     * public void testMethod(){
     *     //UI navigation steps 
     * }
     * </pre>
     */
    int browserHeight() default 0;

    /**
     * Define the Width of the browser window that will be spawned
     * 
     * The height also has to be provided else the width is ignored
     * 
     * <pre>
     * {@literal @}Test
     * {@literal @}WebTest(browserWidth="width")
     * public void testMethod(){
     *     //UI navigation steps 
     * }
     * </pre>
     */
    int browserWidth() default 0;
}
