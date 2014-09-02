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

package com.paypal.selion.configuration;

/**
 * A utility class that stores some information pertaining to a Listener. This object is referred used by
 * {@link ListenerManager#registerListener(ListenerInfo)}.
 * 
 */
public class ListenerInfo {

    private String listenerClassName;
    private boolean enabled = true;

    /**
     * 
     * @param className
     *            - The Class name.
     * @param jvmArgToParse
     *            - The JVM argument that should be read to decide if the listener is to be enabled/disabled.
     */
    public ListenerInfo(Class<?> className, String jvmArgToParse) {
        this.listenerClassName = className.getCanonicalName();
        this.enabled = getBooleanValFromVMArg(jvmArgToParse);
    }

    public String getListenerClassName() {
        return listenerClassName;
    }

    /**
     * @return - <code>true</code> if the listener is to be enabled, <code>false</code> otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    static boolean getBooleanValFromVMArg(String vmArgValue) {
        boolean flag = true;
        String sysProperty = System.getProperty(vmArgValue);
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
        if (enabled) {
            builder.append("Enabled");
        } else {
            builder.append("Disabled");
        }
        builder.append("]");
        return builder.toString();
    }

}
