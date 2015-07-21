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

package com.paypal.selion.internal.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A Simple Pojo that is used to represent a TestNG's method and its test result.
 * 
 */
public final class InvokedMethodInformation {
    private String currentTestName;
    private Throwable exception;
    private Method actualMethod;
    private String currentMethodName;
    private Object[] methodParameters;
    private Map<String, Object> testMethodAttributes;
    private boolean testResultSuccess;
    private String[] dependsOnMethods = new String[] {};

    /**
     * @return <code>true</code> if the test method ran successfully.
     */
    public boolean isTestResultSuccess() {
        return testResultSuccess;
    }

    /**
     * @param testResultSuccess
     *            - Sets a status that represents if a test method passed or not.
     * 
     */
    public void setTestResultSuccess(boolean testResultSuccess) {
        this.testResultSuccess = testResultSuccess;
    }

    /**
     * @return A String that represent's a &lt;test&gt; name.
     */
    public String getCurrentTestName() {
        return currentTestName;
    }

    /**
     * @param currentTestName
     *            - A String that represents a &lt;test&gt; name.
     * 
     */
    public void setCurrentTestName(String currentTestName) {
        this.currentTestName = currentTestName;
    }

    /**
     * @return the exception
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * @param exception
     *            - The {@link Throwable} that was raised by a test method.
     */
    public void setException(Throwable exception) {
        this.exception = exception;
    }

    /**
     * @return A {@link Method} that represents the actual method.
     */
    public Method getActualMethod() {
        return actualMethod;
    }

    /**
     * @param actualMethod
     *            - A {@link Method} that represents the actual method.
     * 
     */
    public void setActualMethod(Method actualMethod) {
        this.actualMethod = actualMethod;
    }

    /**
     * @param annotation
     *            - The annotation which needs to be fetched.
     * @return - An {@link Annotation} object that represents the annotation in question.
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        if (this.actualMethod == null) {
            return null;
        }
        return this.actualMethod.getAnnotation(annotation);
    }

    /**
     * @param dependsOnMethods
     *            - An Array of String that represents the list of methods on which the current method depends on.
     */
    public void setMethodsDependedUpon(String[] dependsOnMethods) {
        this.dependsOnMethods = Arrays.copyOf(dependsOnMethods, dependsOnMethods.length);
    }

    /**
     * @return A String array that represents the set of method names upon which the current method depends on.
     */
    public String[] getMethodsDependedUpon() {
        return Arrays.copyOf(this.dependsOnMethods, dependsOnMethods.length);
    }

    /**
     * @return the current name of the method.
     */
    public String getCurrentMethodName() {
        return currentMethodName;
    }

    /**
     * @param currentMethodName
     *            - The name of the current method being executed.
     * 
     */
    public void setCurrentMethodName(String currentMethodName) {
        this.currentMethodName = currentMethodName;
    }

    /**
     * @return An array of Object that represents the current method's parameters.
     */
    public Object[] getMethodParameters() {
        return Arrays.copyOf(methodParameters, methodParameters.length);
    }

    /**
     * @param methodParameters
     *            - An {@link Object} array that represents the current method's parameters.
     */
    public void setMethodParameters(Object[] methodParameters) {
        this.methodParameters = Arrays.copyOf(methodParameters, methodParameters.length);
    }

    /**
     * @param attributeName
     *            - The name of the attribute whose value is to be fetched.
     * @return - An Object that represents the value of the attribute. <code>null</code> otherwise.
     */
    public Object getTestAttribute(String attributeName) {
        if (this.testMethodAttributes == null) {
            return null;
        }
        return testMethodAttributes.get(attributeName);
    }

    /**
     * @param testMethodAttributes
     *            - A {@link Map} that represents the attributes for the current test method.
     */
    public void setTestMethodAttributes(Map<String, Object> testMethodAttributes) {
        this.testMethodAttributes = new HashMap<String, Object>();
        this.testMethodAttributes.putAll(testMethodAttributes);
    }

    /**
     * @return - A String that represents the class name to which the current method belongs to. If the current method
     *         was set to <code>null</code> the class name is also returned as <code>null</code>.
     */
    public String getCurrentClassName() {
        if (this.actualMethod == null) {
            return null;
        }
        return this.actualMethod.getDeclaringClass().getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("InvokedMethodInformation [");
        if (currentTestName != null) {
            builder.append("currentTestName=");
            builder.append(currentTestName);
            builder.append(", ");
        }
        if (exception != null) {
            builder.append("exception=");
            builder.append(exception);
            builder.append(", ");
        }
        if (actualMethod != null) {
            builder.append("actualMethod=");
            builder.append(actualMethod.getName());
            builder.append(", ");
        }
        if (currentMethodName != null) {
            builder.append("currentMethodName=");
            builder.append(currentMethodName);
            builder.append(", ");
        }
        if (methodParameters != null) {
            builder.append("methodParameters=");
            builder.append(Arrays.toString(methodParameters));
            builder.append(", ");
        }
        if (testMethodAttributes != null) {
            builder.append("testMethodAttributes=");
            builder.append(testMethodAttributes);
            builder.append(", ");
        }
        builder.append("testResultSuccess=");
        builder.append(testResultSuccess);
        builder.append(", ");
        if (dependsOnMethods != null) {
            builder.append("dependsOnMethods=");
            builder.append(Arrays.toString(dependsOnMethods));
        }
        builder.append("]");
        return builder.toString();
    }
}
