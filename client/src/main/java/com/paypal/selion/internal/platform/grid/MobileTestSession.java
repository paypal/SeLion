/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.internal.platform.grid.browsercapabilities.MobileDriverFactory;
import com.paypal.selion.internal.utils.InvokedMethodInformation;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A class for loading and representing the {@link MobileTest} annotation parameters. Also performs sanity checks.
 *
 */
public class MobileTestSession extends AbstractTestSession {
    private static SimpleLogger logger = SeLionLogger.getLogger();
    private String appName;
    private final String appLocation;
    private String device = "iphone";
    private static final String ANDROID = "android";
    private static final String IPHONE = "iphone";
    private static final String IPAD = "ipad";
    private String appLanguage;
    private String appLocale;
    private String deviceSerial;
    private String deviceType;
    private String platformVersion;
    private String appPath;
    private String appVersion;
    private String androidAppMainActivity;
    private String androidAppPackage;

    private WebDriverPlatform platform;

    private MobileNodeType mobileNodeType;

    private static final String SAUCE_URL = "sauce-storage:";
    private static final String SELION_HUB_STORAGE = "selion-hub-storage";

    MobileTestSession() {
        super();
        // go ahead and init global-only config properties
        appLocation = Config.getConfigProperty(ConfigProperty.MOBILE_APP_FOLDER);
    }

    public String getAppLocale() {
        return appLocale;
    }

    public String getAppLanguage() {
        return appLanguage;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public String getAppLocation() {
        return appLocation;
    }

    public String getAppPath() {
        return appPath;
    }

    public String getAndroidAppPackage() {
        return androidAppPackage;
    }

    public String getAndroidAppMainActivity() {
        return androidAppMainActivity;
    }

    public WebDriverPlatform getPlatform() {
        return platform;
    }

    public MobileNodeType getMobileNodeType() {
        return mobileNodeType;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public String getDevice() {
        return device;
    }

    public String getDeviceType() {
        return deviceType;
    }

    @Override
    public void startSession() {
        logger.entering();
        Grid.getThreadLocalWebDriver().set(MobileDriverFactory.createInstance());
        setStarted(true);
        logger.exiting();
    }

    @Override
    public void initializeTestSession(InvokedMethodInformation method) {
        logger.entering(method);
        initTestSession(method);
        MobileTest deviceTestAnnotation = method.getAnnotation(MobileTest.class);
        // check class has MobileTest annotation (i.e. using shared sessions)
        if (deviceTestAnnotation == null) {
            deviceTestAnnotation = method.getActualMethod().getDeclaringClass().getAnnotation(MobileTest.class);
        }

        // load global config
        String mobileNode = Config.getConfigProperty(ConfigProperty.MOBILE_NODE_TYPE);

        // First load these from the <test> local config
        appName = getLocalConfigProperty(ConfigProperty.MOBILE_APP_NAME);
        appPath = getLocalConfigProperty(ConfigProperty.MOBILE_APP_PATH);
        deviceSerial = getLocalConfigProperty(ConfigProperty.SELENDROID_DEVICE_SERIAL);

        if (StringUtils.isNotBlank(getLocalConfigProperty(ConfigProperty.MOBILE_NODE_TYPE))) {
            mobileNode = getLocalConfigProperty(ConfigProperty.MOBILE_NODE_TYPE);
        }

        if (StringUtils.isNotBlank(getLocalConfigProperty(ConfigProperty.MOBILE_APP_LOCALE))) {
            appLocale = getLocalConfigProperty(ConfigProperty.MOBILE_APP_LOCALE);
        }

        if (StringUtils.isNotBlank(getLocalConfigProperty(ConfigProperty.MOBILE_APP_LANGUAGE))) {
            appLanguage = getLocalConfigProperty(ConfigProperty.MOBILE_APP_LANGUAGE);
        }

        if (StringUtils.isNotBlank(getLocalConfigProperty(ConfigProperty.MOBILE_DEVICE))) {
            String device = getLocalConfigProperty(ConfigProperty.MOBILE_DEVICE);
            setDeviceParameters(device);
        }

        if (StringUtils.isNotBlank(getLocalConfigProperty(ConfigProperty.MOBILE_DEVICE_TYPE))) {
            deviceType = getLocalConfigProperty(ConfigProperty.MOBILE_DEVICE_TYPE);
        }

        if (StringUtils.isNotBlank(getLocalConfigProperty(ConfigProperty.ANDROID_APP_MAIN_ACTIVITY))) {
            androidAppMainActivity = getLocalConfigProperty(ConfigProperty.ANDROID_APP_MAIN_ACTIVITY);
        }

        if (StringUtils.isNotBlank(getLocalConfigProperty(ConfigProperty.ANDROID_APP_PACKAGE))) {
            androidAppPackage = getLocalConfigProperty(ConfigProperty.ANDROID_APP_PACKAGE);
        }

        // Override values when supplied via the annotation
        if (deviceTestAnnotation != null) {
            if (StringUtils.isNotBlank(deviceTestAnnotation.appName())) {
                this.appName = deviceTestAnnotation.appName();
            }

            if (StringUtils.isNotBlank(deviceTestAnnotation.language())) {
                this.appLanguage = deviceTestAnnotation.language();
            }
            if (StringUtils.isNotBlank(deviceTestAnnotation.locale())) {
                this.appLocale = deviceTestAnnotation.locale();
            }
            if (StringUtils.isNotBlank(deviceTestAnnotation.device())) {
                setDeviceParameters(deviceTestAnnotation.device());
            }
            if (StringUtils.isNotBlank(deviceTestAnnotation.deviceSerial())) {
                this.deviceSerial = deviceTestAnnotation.deviceSerial();
            }
            if (StringUtils.isNotBlank(deviceTestAnnotation.deviceType())) {
                this.deviceType = deviceTestAnnotation.deviceType();
            }
            if (StringUtils.isNotBlank(deviceTestAnnotation.appPath())) {
                this.appPath = deviceTestAnnotation.appPath();
            }
            if (StringUtils.isNotBlank(deviceTestAnnotation.mobileNodeType())) {
                mobileNode = deviceTestAnnotation.mobileNodeType();
            }
            this.mobileNodeType = MobileNodeType.getMobileNodeType(mobileNode);

            initializeAdditionalCapabilities(method);
            initializeAdditionalCapabilities(deviceTestAnnotation.additionalCapabilities());
            initializeAdditionalCapabilities(deviceTestAnnotation.additionalCapabilitiesBuilders());
        }

        boolean appPathProvided = StringUtils.isNotBlank(appPath);

        checkArgument(!(mobileNodeType != MobileNodeType.APPIUM && appPathProvided),
                "appPath can be specified for appium only, Please specify appName instead of appPath");

        checkArgument(
                StringUtils.isNotBlank(appName) ^ StringUtils.isNotBlank(appPath),
                "Either you have provided both appPath and appName or you have specified nothing. Please specify either "
                + "appPath or appName");

        checkArgument(isDeviceDefined(),
                "The device should either be provided as 'device name:version' or 'device' or 'device:version'." +
                        " like 'iphone', 'ipad', 'iphone:7.1', 'android', 'nexus 5:17', 'android:18', etc.");

        // appName can be passed via the annotation or as a config var. It may contain precision info.
        if (StringUtils.contains(this.appName, ":")) {
            String[] appNames = StringUtils.split(this.appName, ":");
            appVersion = appNames[1];
            appName = appNames[0];
        }

        // appPath can be passed via the annotation or as a config var. It may need formatting.
        if (this.appPath.startsWith(SELION_HUB_STORAGE)) {
            // parse and construct the absolute url for selion hub storage
            this.appPath = getSelionHubStorageUrl(this.appPath);
        } else if (!this.appPath.startsWith(SAUCE_URL) && !StringUtils.startsWithIgnoreCase(appPath, "http")) {
            // construct the absolute url for apps exist in resource folder.
            Path p = Paths.get(appPath);
            if (!p.isAbsolute()) {
                this.appPath = String.format("%s/%s", System.getProperty("user.dir"), appPath);
            }
        }

        this.platform = WebDriverPlatform.ANDROID;
        if (IPHONE.equalsIgnoreCase(getDevice()) || IPAD.equalsIgnoreCase(getDevice())) {
            this.platform = WebDriverPlatform.IOS;
        }

        logger.exiting();
    }

    private void setDeviceParameters(String device) {
        String deviceName;
        String[] devices = StringUtils.split(device, ":");
        boolean mobileDeviceContainsbothNameAndVersion = devices.length > 1;
        if (mobileDeviceContainsbothNameAndVersion) {
            this.platformVersion = devices[1];
            deviceName = devices[0];
        } else if (device.indexOf(':') == 0) {
            this.platformVersion = devices[0];
            deviceName = "";
        } else {
            deviceName = devices[0];
        }
        deviceName = deviceName.trim();
        this.device =
                deviceName.toLowerCase().startsWith(IPHONE) ? IPHONE :
                        deviceName.toLowerCase().startsWith(IPAD) ? IPAD :
                                StringUtils.isNotBlank(deviceName) ? ANDROID : "";
        if (!deviceName.equalsIgnoreCase(ANDROID) &&
                !deviceName.equalsIgnoreCase(IPHONE) &&
                !deviceName.equalsIgnoreCase(IPAD)) {
            this.deviceType = deviceName;
        }
    }

    private String getSelionHubStorageUrl(String selionHubAppPath) {
        String COLON = ":";
        String SLASH = "/";
        String appPathTokens[] = StringUtils.split(selionHubAppPath, ":");
        String hostName = Config.getConfigProperty(ConfigProperty.SELENIUM_HOST);
        int port = Integer.parseInt(Config.getConfigProperty(ConfigProperty.SELENIUM_PORT));
        StringBuilder url = new StringBuilder("http://");
        url.append(hostName);
        url.append(COLON);
        url.append(port);
        url.append("/grid/admin/TransferServlet");

        for (int i = 1; i < appPathTokens.length; i++) {
            url.append(SLASH);
            url.append(appPathTokens[i]);
        }

        return url.toString();
    }

    private boolean isDeviceDefined() {
        return (device.contains("android") || device.contains("iphone") || device.contains("ipad"));
    }


}
