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

package com.paypal.selion.internal.platform.grid;

/**
 * Enum to represent the valid mobile node types.
 * 
 */
public enum MobileNodeType {

    APPIUM("appium"), IOS_DRIVER("ios-driver"), SELENDROID("selendroid");

    private String mobileNodeType;

    MobileNodeType(String mobileNodeType) {
        this.mobileNodeType = mobileNodeType;
    }

    public String getAsString() {
        return this.mobileNodeType;
    }

    /**
     * This method returns all the mobile node typesthat are supported by the SeLion framework as a String with each
     * value delimited by a comma.
     * 
     * @return - A comma separated string that represents all supported mobile node types.
     */
    public static String getSupportedMobileNodesAsCSV() {
        StringBuilder buffer = new StringBuilder();
        String delimiter = ",";
        for (MobileNodeType node : MobileNodeType.values()) {
            buffer.append(node.getAsString()).append(delimiter);
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    /**
     * @param mobileNodeType
     *            - The raw mobileNodeType string for which the enum format is sought.
     * @return - A {@link MobileNodeType} enum that represents a SeLion compliant mobile node type.
     */
    public static MobileNodeType getMobileNodeType(String mobileNodeType) {
        for (MobileNodeType node : MobileNodeType.values()) {
            if (node.getAsString().equalsIgnoreCase(mobileNodeType)) {
                return node;
            }
        }
        // No corresponding mobile node was found. Throwing an exception
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("MobileNodeType '");
        errorMsg.append(mobileNodeType).append("\' did not match any mobileNodeTypes supported by SeLion.\n");
        errorMsg.append("Supported mobileNodes are : [").append(MobileNodeType.getSupportedMobileNodesAsCSV());
        errorMsg.append("].");
        IllegalArgumentException e = new IllegalArgumentException(errorMsg.toString());
        throw e;
    }
}
