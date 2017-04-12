/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2017 PayPal                                                                                     |
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

package com.paypal.selion.plugins;

import java.util.List;

import com.paypal.selion.elements.SeLionElement;
import com.paypal.selion.elements.HtmlSeLionElementSet;
import com.paypal.selion.elements.IOSSeLionElementSet;
import com.paypal.selion.elements.AndroidSeLionElementSet;
import com.paypal.selion.elements.MobileSeLionElementSet;
import com.paypal.selion.elements.HtmlSeLionElementSet.HtmlSeLionElement;

/**
 * A simple POJO class that represents information pertaining to a html object.
 *
 */
public class GUIObjectDetails {
    private static final String DELIMITER = "#";

    private String memberType;
    private String memberName;
    private String parent;
    private String memberPackage;

    public GUIObjectDetails(String memberType, String memberName, String memberPackage) {
        this(memberType, memberName, memberPackage, null);
    }

    public GUIObjectDetails(String memberType, String memberName, String memberPackage, String parent) {
        this.memberType = memberType;
        this.memberName = memberName;
        this.memberPackage = memberPackage;
        this.parent = parent;
    }

    public String getMemberType() {
        return memberType;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberPackage() {
        return memberPackage;
    }

    public String getParent() {
        return parent;
    }

    // This method is used by the velocity template and has reference in Class.vm
    // DO NOT tamper with this method
    public String returnArg(String key) {
       SeLionElement element = HtmlSeLionElementSet.getInstance().findMatch(key);
        if (element == null) {
            return key;
        }
        if (!element.isUIElement()) {
            return key;
        }
        return key.substring(0, key.indexOf(element.getElementClass()));
    }

    // This method is used by the velocity template and has reference in Class.vm
    // DO NOT tamper with this method
    public String firstToUpperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * This method convert each key in the data sheet into corresponding HtmlObjectDetails object and returns list of
     * HtmlObjectDetails
     *
     * @param keys
     *            keys for which {@link GUIObjectDetails} is to be created.
     * @return list of HtmlObjectDetails
     */
    public static List<GUIObjectDetails> transformKeys(List<String> keys, TestPlatform platform) {
        List<GUIObjectDetails> htmlObjectDetailsList = null;

        // Get the HTML object list based on the platform.
        // Note: This part is reached only when there is a valid platform specified. So it's safe to proceed without a
        // default case in switch
        switch (platform) {
        case WEB:
            htmlObjectDetailsList = HtmlSeLionElementSet.getInstance().getGUIObjectList(keys);
            break;
        case IOS:
            htmlObjectDetailsList = IOSSeLionElementSet.getInstance().getGUIObjectList(keys);
            break;
        case ANDROID:
            htmlObjectDetailsList = AndroidSeLionElementSet.getInstance().getGUIObjectList(keys);
            break;
        case MOBILE:
            htmlObjectDetailsList = MobileSeLionElementSet.getInstance().getGUIObjectList(keys);
            break;
        }
        return htmlObjectDetailsList;
    }

    /**
     * A overloaded version of transformKeys method which internally specifies {@link TestPlatform#WEB} as the
     * {@link TestPlatform}
     *
     * @param keys
     *            keys for which {@link GUIObjectDetails} is to be created.
     * @return the {@link List} of {@link GUIObjectDetails}
     */
    public static List<GUIObjectDetails> transformKeys(List<String> keys) {
        return transformKeys(keys, TestPlatform.WEB);
    }

    /**
     * Method to validate the keys against the {@link HtmlSeLionElementSet} or {@link IOSSeLionElementSet} as per the
     * {@link TestPlatform}
     *
     * @param keysToValidate
     *            the keys from the Page Yaml input
     * @param dataFileName
     *            The file name containing the keys
     * @param currentPlatform
     *            the platform specified in the Page Yaml input
     */
    public static void validateKeysInDataFile(List<String> keysToValidate, String dataFileName,
            TestPlatform currentPlatform) {
        for (String currentKey : keysToValidate) {

            // For case: Invalid element inside a container, the key inside a container is split using delimiter.
            // It will be assigned to the currentKey to proceed with the validation.
            if (currentKey.contains(DELIMITER)) {
                String[] keyInContainer = currentKey.split(DELIMITER);
                // assigning the key to the current key to proceed with the validation
                currentKey = keyInContainer[1];
            }
            if (!validForWebPlatform(currentPlatform, currentKey)
                    || !validForMobilePlatforms(currentPlatform, currentKey)) {
                throw new IllegalArgumentException(String.format(
                        "Detected an invalid key [%s] in data file %s for Platform %s", currentKey, dataFileName,
                        currentPlatform.getPlatformName()));
            }

        }
    }

    private static boolean validForWebPlatform(TestPlatform currentPlatform, String currentKey) {
        // Validations for WEB
        if ((currentPlatform == TestPlatform.WEB)) {
            /*
             * For Yaml V1 the non-html elements are added to the List of keys(EG: pageTitle) whereas for V2 it does
             * not. As a result, if a user specifies wrong name for pageTitle we first check it to be valid name and
             * then proceed with the usual check of validating if it's a html element
             */
            // TODO: This is a hack to be removed when V1 is fully deprecated.
            if (!(HtmlSeLionElementSet.getInstance().isValid(currentKey))) {
                return false;
            }

            if (currentKey.equals(HtmlSeLionElement.PAGE_TITLE.getElementClass())) {
                return true;
            }

            if (!(HtmlSeLionElementSet.getInstance().isValidUIElement(currentKey))) {
                return false;
            }
        }
        return true;
    }

    private static boolean validForMobilePlatforms(TestPlatform currentPlatform, String currentKey) {
        // Validations for IOS
        if ((currentPlatform == TestPlatform.IOS && !(IOSSeLionElementSet.getInstance().isValidUIElement(currentKey)))) {
            return false;
        }

        // Validations for Android - If a user provides an element that is not registered as custom element this
        // validation takes care of it
        if ((currentPlatform == TestPlatform.ANDROID && !(AndroidSeLionElementSet.getInstance().isValidUIElement(currentKey)))) {
            return false;
        }

        // Validations for Mobile
        return !(currentPlatform == TestPlatform.MOBILE && !(MobileSeLionElementSet
            .getInstance().isValidUIElement(currentKey)));

    }
}
