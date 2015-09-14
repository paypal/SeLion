/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

/**
 * <code>ListenerInfo</code> serves as a utility to store information pertaining to a Listener, used by
 * {@link ListenerManager#registerListener(ListenerInfo)} .
 * 
 */
public class ListenerInfo {

    // A system default value.
    private final static boolean ENABLE_LISTENER_BY_DEFAULT = true;

    private String listenerClassName;

    private boolean listenerEnabled = true;

    /**
     * Saves information about listener class which is enabled/disabled per its VM argument. When VM argument for the
     * listener is not defined, the listener is enabled by system default.
     * 
     * @param className
     *            - The Class name.
     * @param jvmArgToParse
     *            - The JVM argument that should be read to decide if the listener is to be enabled/disabled.
     */
    public ListenerInfo(Class<?> className, String jvmArgToParse) {
        this(className, jvmArgToParse, ENABLE_LISTENER_BY_DEFAULT);
    }

    /**
     * Saves information about listener class which is enabled/disabled per its VM argument. When VM argument is not
     * defined, whether it is enabled/disabled is decided by the caller via (@code defaultStateWhenNotDefined}.
     * 
     * @param className
     *            - The Class name.
     * @param jvmArgToParse
     *            - The JVM argument that should be read to decide whether the listener is to be enabled/disabled.
     * @param defaultStateWhenNotDefined
     *            - The default boolean for whether enabled/disabled when the JVM argument is not defined.
     */
    public ListenerInfo(Class<?> className, String jvmArgToParse, boolean defaultStateWhenNotDefined) {
        this.listenerClassName = className.getCanonicalName();
        this.listenerEnabled = getBooleanValFromVMArg(jvmArgToParse, defaultStateWhenNotDefined);
    }

    /**
     * Gets the class name of the listener.
     */
    public String getListenerClassName() {
        return listenerClassName;
    }

    /**
     * Gets a boolean indicating whether or not the listener is enabled.
     * 
     * @return - <code>true</code> if the listener is to be enabled, <code>false</code> otherwise.
     */
    public boolean isEnabled() {
        return this.listenerEnabled;
    }

    /**
     * Returns boolean value of the JVM argument when defined, else returns true (system default behavior).
     * 
     * @param vmArgValue
     *            The VM argument name.
     */
    static boolean getBooleanValFromVMArg(String vmArgValue) {
        return getBooleanValFromVMArg(vmArgValue, ENABLE_LISTENER_BY_DEFAULT);
    }

    /**
     * Returns boolean value of the JVM argument when defined, else returns the {@code defaultStateWhenNotDefined}.
     * 
     * @param vmArgValue
     *            The VM argument name.
     * @param defaultStateWhenNotDefined
     *            A boolean to indicate default state of the listener.
     */
    static boolean getBooleanValFromVMArg(String vmArgValue, boolean defaultStateWhenNotDefined) {
        String sysProperty = System.getProperty(vmArgValue);
        boolean flag = defaultStateWhenNotDefined;
        if ((sysProperty != null) && (!sysProperty.isEmpty())) {
            flag = Boolean.parseBoolean(sysProperty);
        }
        return flag;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Listener Information [");
        if (listenerClassName != null) {
            builder.append("Listener:");
            builder.append(listenerClassName);
            builder.append(", ");
        }
        builder.append("State=");
        if (listenerEnabled) {
            builder.append("Enabled");
        } else {
            builder.append("Disabled");
        }
        builder.append("]");
        return builder.toString();
    }

}
