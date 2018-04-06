/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2017 PayPal                                                                                     |
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

package com.paypal.selion.platform.mobile;

import org.openqa.selenium.WebDriverException;

import com.paypal.selion.SeLionBuildInfo;
import com.paypal.selion.SeLionBuildInfo.SeLionBuildProperty;
import com.paypal.selion.SeLionConstants;

/**
 * <code>UIOperationFailedException</code> represents exceptional cases that occur while interacting with mobile
 * elements.
 */
public class UIOperationFailedException extends WebDriverException {

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -2788930472396889548L;

    public UIOperationFailedException(String message) {
        super(message);
    }

    public UIOperationFailedException(Throwable throwable) {
        super(throwable);
    }

    public UIOperationFailedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    @Override
    public String getMessage() {
        return createMessage(super.getMessage());
    }

    @Override
    public String getSupportUrl() {
        return SeLionConstants.SELION_PROJECT_GITHUB_URL;
    }

    protected String getSeLionBuildInformation() {
        return String
                .format("Build info: version: '%s', timestamp: '%s', user: '%s', selenium version: '%s', ios-driver version: '%s', selendroid version: '%s', appium version: '%s'",
                        SeLionBuildInfo.getBuildValue(SeLionBuildProperty.SELION_VERSION),
                        SeLionBuildInfo.getBuildValue(SeLionBuildProperty.BUILD_TIME),
                        SeLionBuildInfo.getBuildValue(SeLionBuildProperty.USER_NAME),
                        SeLionBuildInfo.getBuildValue(SeLionBuildProperty.BUILD_DEPENDENCY_SELENIUM_VERSION),
                        SeLionBuildInfo.getBuildValue(SeLionBuildProperty.BUILD_DEPENDENCY_IOSDRIVER),
                        SeLionBuildInfo.getBuildValue(SeLionBuildProperty.BUILD_DEPENDENCY_SELENDROID),
                        SeLionBuildInfo.getBuildValue(SeLionBuildProperty.BUILD_DEPENDENCY_APPIUM));
    }

    private String createMessage(String originalMessageString) {
        String supportMessage = getSupportUrl() + "\n";
        return (originalMessageString == null ? "" : originalMessageString + "\n") + supportMessage
                + getSeLionBuildInformation() + "\n" + getSystemInformation() + getAdditionalInformation();
    }

}
