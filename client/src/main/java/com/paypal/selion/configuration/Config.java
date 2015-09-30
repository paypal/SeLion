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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ISuite;
import org.testng.ITestContext;

import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.configuration.LocalConfig;
import com.paypal.selion.internal.platform.grid.MobileNodeType;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.EventListener;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import com.paypal.selion.platform.html.support.events.ElementEventListener;

/**
 * A configuration object that contains properties needed throughout SeLion. These options can be configured via the
 * TestNG formatted testng-suite.xml file. The testng-suite.xml file is used to override the default settings, so only
 * the data you want to modify from default values should be specified in the suite configuration file. Any data not
 * found in the testng-suite.xml file will use the built-in defaults. <br>
 * 
 * Note that some configuration properties are global-only in scope. Any value set for these applies to all suite tests
 * executed (possibly in parallel). Properties that must be specified for global use are specified below with
 * "(GLOBAL)". These properties cannot be used in SeLionLocal configuration instances. The others can be specified for
 * Global or Local (test) scope.
 * 
 * 
 * <pre>
 * &lt;!-- If parameter is empty string or omitted, please see defaults --&gt;
 * 
 * &lt;!-- SELENIUM CONFIGURATION --&gt;
 * 
 * &lt;!-- optional, defaults to "" or localhost when runLocally is true (GLOBAL) --&gt;
 * &lt;parameter name="seleniumhost" value="" /&gt;
 * &lt;!-- optional, defaults to 4444 (GLOBAL) --&gt;
 * &lt;parameter name="seleniumport" value="" /&gt;
 * &lt;!-- optional, defaults to *firefox  --&gt;
 * &lt;parameter name="browser" value="*firefox" /&gt;
 * &lt;!-- optional, defaults to false  (GLOBAL) --&gt;
 * &lt;parameter name="runLocally" value="true" /&gt;
 * &lt;!-- optional, turns automatic screen shots for click handlers on/off, defaults to true (GLOBAL) --&gt;
 * &lt;parameter name="autoScreenShot" value="true" /&gt;
 * &lt;!-- optional, used when runLocally is true, defaults to 'default' --&gt;
 * &lt;parameter name="profileName" value="SeleniumProfile" /&gt;     
 * 
 * &lt;!-- SELION FILES LOCATION --&gt;
 * 
 * &lt;!-- optional, default to selionFiles (GLOBAL) --&gt;
 * &lt;parameter name="basedir" value=""  /&gt;
 * &lt;!-- optional, default to ${selionFiles.basedir}/selionLogs (GLOBAL) --&gt;
 * &lt;parameter name="workDir"  value="" /&gt;
 * </pre>
 * 
 * <strong>All other configuration settings can be set in a similar fashion.</strong><br>
 * 
 * <br>
 * Also, system properties and/or environment variables can also be used to configure SeLion. The values used should
 * always start with "SELION_" and end with the value you would like the set. The variable equals the
 * {@link ConfigProperty} variable name. So, for instance, to set the execution_timeout to "180000", the following
 * system property or environment variable should be set prior to initializing SeLion:
 * 
 * <pre>
 * SELION_EXECUTION_TIMEOUT = 180000
 * </pre>
 * 
 * <h4>The order of initialization for Configuration values is</h4>
 * <ol>
 * <li>System properties (Highest Precedence)
 * <li>Environment variables</li>
 * <li>From a testng-suite.xml file</li>
 * <li>SeLion defaults</li>
 * </ol>
 */
public final class Config {
    private static volatile XMLConfiguration config;

    private Config() {
        // Utility class. So hide the constructor
    }

    static XMLConfiguration getConfig() {
        if (config != null) {
            return config;
        }
        initConfig();
        return config;
    }

    /**
     * Parses suite parameters and generates SeLion Config object
     * 
     * @param suite
     *            list of parameters from configuration file within &lt;suite&gt;&lt;/suite&gt; tag
     */
    public synchronized static void initConfig(ISuite suite) {
        SeLionLogger.getLogger().entering(suite);
        Map<ConfigProperty, String> initialValues = new HashMap<ConfigProperty, String>();
        for (ConfigProperty prop : ConfigProperty.values()) {
            String paramValue = suite.getParameter(prop.getName());
            // empty values may be valid for some properties
            if (paramValue != null) {
                initialValues.put(prop, paramValue);
            }
        }
        initConfig(initialValues);

        SeLionLogger.getLogger().exiting();
    }

    /**
     * Parses configuration file and extracts values for test environment
     * 
     * @param context
     *            list of parameters includes values within &lt;suite&gt;&lt;/suite&gt; and &lt;test&gt;&lt;/test&gt;
     *            tags
     */
    public synchronized static void initConfig(ITestContext context) {
        SeLionLogger.getLogger().entering(context);
        Map<ConfigProperty, String> initialValues = new HashMap<ConfigProperty, String>();
        Map<String, String> testParams = context.getCurrentXmlTest().getLocalParameters();
        if (!testParams.isEmpty()) {
            for (ConfigProperty prop : ConfigProperty.values()) {
                // Check if a selionConfig param resides in the <test>
                String newValue = testParams.get(prop.getName());
                // accept param values that are empty in initialValues.
                if (newValue != null) {
                    initialValues.put(prop, newValue);
                }
            }
        }

        ConfigManager.addConfig(context.getCurrentXmlTest().getName(), new LocalConfig(initialValues));
        SeLionLogger.getLogger().exiting();
    }

    /**
     * Reads and parses configuration file Initializes the configuration, reloading all data
     */
    public synchronized static void initConfig() {
        SeLionLogger.getLogger().entering();
        Map<ConfigProperty, String> initialValues = new HashMap<ConfigProperty, String>();

        initConfig(initialValues);

        SeLionLogger.getLogger().exiting();
    }

    /**
     * Prints SeLion Config Values
     */
    public static void printSeLionConfigValues() {
        SeLionLogger.getLogger().entering();
        StringBuilder builder = new StringBuilder("SeLion configuration :{");
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            builder.append(String.format("(%s , %s),", configProperty,
                    Config.getConfig().getString(configProperty.getName())));
        }
        builder.append("}\n");
        SeLionLogger.getLogger().info(builder.toString());
        SeLionLogger.getLogger().exiting();
    }

    private static void loadInitialValues() {
        for (ConfigProperty configProps : ConfigProperty.values()) {
            config.setProperty(configProps.getName(), configProps.getDefaultValue());
        }
    }

    private static void loadValuesFromUser(Map<ConfigProperty, String> initialValues) {
        if (!initialValues.isEmpty()) {
            for (Entry<ConfigProperty, String> eachConfig : initialValues.entrySet()) {
                config.setProperty(eachConfig.getKey().getName(), eachConfig.getValue());
            }
        }
    }

    private static void loadValuesFromEnvironment() {
        final String PREFIX = "SELION_";
        for (ConfigProperty configProps : ConfigProperty.values()) {
            String envValue = System.getenv(PREFIX + configProps.name());
            if (StringUtils.isNotBlank(envValue)) {
                config.setProperty(configProps.getName(), envValue);
            }
            // Now load system properties variables (if defined).
            String sysValue = System.getProperty(PREFIX + configProps.name());
            if (StringUtils.isNotBlank(sysValue)) {
                config.setProperty(configProps.getName(), sysValue);
            }
        }
    }

    /**
     * Initializes the configuration, reloading all data while adding the supplied <code>initialValues</code> to the
     * configuration.
     * 
     * @param initialValues
     *            The initial set of values used to configure SeLion
     */
    public synchronized static void initConfig(Map<ConfigProperty, String> initialValues) {
        SeLionLogger.getLogger().entering(initialValues);
        /*
         * Internally, HtmlUnit uses Apache commons logging. Each class that uses logging in HtmlUnit creates a Logger
         * by using the LogFactory, and the defaults it generates. So to modify the Logger that is created, we need to
         * set this attribute "org.apache.commons.logging.Log" to the Logger we want it to use.
         * 
         * Note: this has to be the *first* thing done prior to any apache code getting a handle, so we're putting it in
         * here because the next call is to XMLConfiguration (apache code).
         */
        // NoOpLog essentially disables logging of HtmlUnit

        boolean permitClogging = Boolean.valueOf(System.getProperty("SELION_PERMIT_CLOGGING", "false")).booleanValue();

        if (!permitClogging) {
            LogFactory factory = LogFactory.getFactory();
            factory.setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        }

        // only do this if the global config is not already initialized.
        if (config == null) {
            config = new XMLConfiguration();
            // don't auto throw, let each config value decide
            config.setThrowExceptionOnMissing(false);
            // because we can config on the fly, don't auto-save
            config.setAutoSave(false);

            // Set defaults
            loadInitialValues();
        }

        /*
         * otherwise, update the global config
         */

        // Load in our supplied values (if defined)
        loadValuesFromUser(initialValues);

        // Load in environment & system variables (if defined)
        loadValuesFromEnvironment();

        // Init Selenium configuration
        boolean runLocally = config.getBoolean(ConfigProperty.SELENIUM_RUN_LOCALLY.getName());
        if (runLocally) {
            config.setProperty(ConfigProperty.SELENIUM_HOST.getName(), "localhost");
        }

        SeLionLogger.getLogger().exiting();
    }

    /**
     * Returns a configuration property <b>String</b> value based off the {@link ConfigProperty}
     * 
     * @param configProperty
     *            The configuration property value to return
     * @return The configuration property <b>String</b> value
     */
    public static String getConfigProperty(ConfigProperty configProperty) {
        checkArgument(configProperty != null, "Config property cannot be null");
        return Config.getConfig().getString(configProperty.getName());
    }

    /**
     * Returns a configuration property <b>String</b> value based off the {@link ConfigProperty}
     * 
     * @param configProperty
     *            String Property Name
     * @return The configuration property <b>String</b> values
     */
    public static String getConfigProperty(String configProperty) {
        checkArgument(configProperty != null, "Config property cannot be null");
        return Config.getConfig().getString(configProperty);
    }

    /**
     * Returns a configuration property <b>int</b> value based off the {@link ConfigProperty}
     * 
     * @param configProperty
     *            The configuration property value to return
     * @return The configuration property <b>int</b> value
     */
    public static int getIntConfigProperty(ConfigProperty configProperty) {
        checkArgument(configProperty != null, "Config property cannot be null");
        return Config.getConfig().getInt(configProperty.getName());
    }

    /**
     * Returns a configuration property <b>boolean</b> value based off the {@link ConfigProperty}
     * 
     * @param configProperty
     *            The configuration property value to return
     * @return The configuration property <b>boolean</b> value
     */
    public static boolean getBoolConfigProperty(ConfigProperty configProperty) {
        checkArgument(configProperty != null, "Config property cannot be null");
        return Config.getConfig().getBoolean(configProperty.getName());
    }

    /**
     * Checks if property exists in the configuration
     * 
     * @param propertyName
     *            String Property Name
     * @return <b>true</b> or <b>false</b>
     */
    public static boolean checkPropertyExists(String propertyName) {
        return Config.getConfig().containsKey(propertyName);
    }

    /**
     * Sets a SeLion configuration value. This is useful when you want to override or set a setting.
     * 
     * @param configProperty
     *            The configuration element to set
     * @param configPropertyValue
     *            The value of the configuration element
     * @throws IllegalArgumentException
     *             If problems occur during the set
     */
    public static synchronized void setConfigProperty(ConfigProperty configProperty, String configPropertyValue) {
        checkArgument(configProperty != null, "Config property cannot be null.");
        checkArgument(configPropertyValue != null, "Config property value cannot be null.");
        Config.getConfig().setProperty(configProperty.getName(), configPropertyValue);
    }

    /**
     * SeLion config properties
     */
    public static enum ConfigProperty {
        // Settings specific to SeLion
        /**
         * Automatically take screen shots.<br>
         */
        AUTO_SCREEN_SHOT("autoScreenShot", "true", true),

        /**
         * Selenium host might be localhost or another location where a Selenium server is running. Used when
         * {@link ConfigProperty#SELENIUM_RUN_LOCALLY} is <b>false</b><br>
         * Default is set to <b>""</b>
         */
        SELENIUM_HOST("seleniumhost", "", true),

        /**
         * Selenium port, any port where Selenium is running.<br>
         * Default is set to <b>4444</b>
         */
        SELENIUM_PORT("seleniumport", "4444", true),

        /**
         * The firefox profile that is to be used for local/remote runs. <br>
         * Use this when you would like to have firefox work with your custom firefox profile. <br>
         * You can either specify the name of the profile or you can specify the directory location of your firefox
         * profile. <br>
         * The framework internally first assumes it to be a profile name. If that assumption fails we fail-over to
         * assuming it to be the firefox profile directory. If that also fails we will work with an anonymous firefox
         * profile. <br>
         * <br>
         * <b>WARNING:</b><br>
         * Please be aware that if your firefox profile directory is very huge (above ~10 MB) then there are chances
         * that it can crash your local JVM (be it your desktop or your fusion build job).
         */
        SELENIUM_FIREFOX_PROFILE("firefoxProfile", "", false),

        /**
         * The name of the JSON file that contains the configuration customization info needed for saucelabs. By
         * default, the configurations specified at <a href="https://saucelabs.com/docs/additional-config">Sauce labs
         * Configurations</a> are applicable.
         */
        SELENIUM_SAUCELAB_GRID_CONFIG_FILE("saucelabGridConfigFile", "", true),

        /**
         * The path to the chromedriver executable on the local machine. This parameter is taken into consideration for
         * local runs involving the Google Chrome browser.
         */
        SELENIUM_CHROMEDRIVER_PATH("chromeDriverPath", "", true),

        /**
         * The path to the PhantomJS executable on the local machine. This parameter is taken into consideration for
         * local runs involving the PhantomJS browser.
         */
        SELENIUM_PHANTOMJS_PATH("phantomjsPath", "", true),

        /**
         * The path to the IEDriver executable on the local machine. This parameter is taken into consideration for
         * local runs involving the IE browser.
         */
        SELENIUM_IEDRIVER_PATH("ieDriverPath", "", true),

        /**
         * The path to the EdgeDriver executable on the local machine. This parameter is taken into consideration for
         * local runs involving the Edge browser.
         */
        SELENIUM_EDGEDRIVER_PATH("edgeDriverPath", "", true),

        /**
         * Use this parameter to set the user agent for firefox when working with Mobile version. This parameter should
         * be set in conjunction with the parameter {@link ConfigProperty#BROWSER}
         */
        SELENIUM_USERAGENT("userAgent", "", false),

        /**
         * Use this parameter to set the Proxy server name to be used.
         */
        SELENIUM_PROXY_HOST("proxyServerHost", "", true),

        /**
         * Use this parameter to set the Proxy server port to be used.
         */
        SELENIUM_PROXY_PORT("proxyServerPort", "", true),

        /**
         * Use this parameter to indicate if your remote runs are to be run against the sauce lab grid or against your 
         * own grid. This flag is required when running against the Sauce lab grid because we need to disable fetching 
         * of the WebDriver node IP and Port details.
         */
        SELENIUM_USE_SAUCELAB_GRID("useSauceLabGrid", "false", true),

        /**
         * Use this parameter to execute mobile test cases using respective mobile driver. The valid values are
         * {@link MobileNodeType}
         */
        MOBILE_NODE_TYPE("mobileNodeType", "", false),

        /**
         * Use this parameter to provide SeLion with a custom capabilities provider. The value for this parameter
         * would be the fully qualified class name which is a sub-class of {@link DefaultCapabilitiesBuilder}.
         * If more than one custom capabilities providers are required, please separate their fully qualified class 
         * names with commas.
         */
        SELENIUM_CUSTOM_CAPABILITIES_PROVIDER("customCapabilities", "", true),

        /**
         * Use this parameter to provide SeLion with a custom listener which can be plugged into {@link RemoteWebDriver}
         * {@link CommandExecutor}. If the fully qualified class implements {@link EventListener} then SeLion will
         * invoke the custom implementation provided by you as and when the relevant events happen. If more than one
         * custom listeners are required, please separate the fully qualified class names with commas.
         */
        SELENIUM_WEBDRIVER_EVENT_LISTENER("webDriverEventListener", "", true),

        /**
         * Flip this parameter to <code>true</code> if you would like a browser to be spawned locally on your machine
         * and run automation tests there. Default is set to <b>false</b> which means your tests are always going to be
         * run against a Remote Grid environment as pointed to by {@link ConfigProperty#SELENIUM_HOST}.
         */
        SELENIUM_RUN_LOCALLY("runLocally", "false", true),

        /**
         * This parameter represents the folder which would contain the mobile app. This parameter is currently only
         * useful for local runs. This is the folder from which applications would be searched for by SeLion when it
         * comes to mobile automation in local runs. Please ensure that all the built applications reside in this
         * folder. If no value is provided, SeLion assumes that the application is available under a directory named
         * "Applications" in the current working directory.
         */
        MOBILE_APP_FOLDER("mobileAppFolder", System.getProperty("user.dir") + File.separator + "Applications", true),

        /**
         * This parameter represents the name of the app that is to be spawned. Specifying the name of the mobile app
         * should suffice.
         * 
         * <b>Note:</b> Use of this parameter is not recommended for test suites which test different mobile
         * applications or different versions of the same mobile applications.
         */
        MOBILE_APP_NAME("mobileAppName", "", false),

        /**
         * This parameter represents the fully qualified path of the app that is to be spawned. For app exist in the
         * local disk this should be an absolute path, for app exist in the remote location it should be http URL and
         * for app exist in sauce cloud it can be sauce storage like "sauce-storage:testApp.apk".
         */
        MOBILE_APP_PATH("appPath", "", false),

        /**
         * This parameter represents the language to be used. By default it is always <code>English</code> represented
         * as <code>en</code>
         */
        MOBILE_APP_LANGUAGE("mobileAppLanguage", "en", false),

        /**
         * This parameter represents the locale to be used. By default it is always <code>US English</code> represented
         * as <code>en_US</code>
         */
        MOBILE_APP_LOCALE("mobileAppLocale", "en_US", false),

        /**
         * Use this parameter to provide SeLion with a custom element listener that implements
         * {@link ElementEventListener}. SeLion will invoke the custom implementation when the relevant events happen.
         * If more than one custom listeners is needed, please separate the fully qualified class names with commas.
         */
        ELEMENT_EVENT_LISTENER("elementEventListener", "", true),

        /**
         * This parameter is used to pass SauceLabs user name
         */
        SAUCELAB_USER_NAME("sauceUserName", "", true),

        /**
         * This parameter is used to pass SauceLabs user name
         */
        SAUCELAB_API_KEY("sauceApiKey", "", true),

        /**
         * This parameter is used to pass SauceLabs parent tunnel user id
         */
        SAUCELAB_TUNNEL_USER_ID("sauceTunnelUserId", "", true),

        /**
         * Directory with page asset files to read info about GUI controls from.<br>
         * Default is set to <b>GUIData</b> in resources
         */
        GUI_DATA_DIR("GUIDataDir", "GUIData", true),

        /**
         * Site will show country used for tests.<br>
         * Default is set to <b>US</b>
         */
        SITE_LOCALE("siteLocale", "US", false),

        /**
         * Browser specified by user.<br>
         * Default is set to <b>firefox</b>
         */
        BROWSER("browser", "*firefox", false),

        /**
         * version specified by user when working with custom browser needs.<br>
         */
        BROWSER_CAPABILITY_VERSION("version", "", false),

        /**
         * Turn this flag ON to see GUI actions such as loading a URL, click/setting text etc., being logged into the
         * test reports that get generated by SeLion.
         * 
         */
        ENABLE_GUI_LOGGING("enableGUILogging", "false", true),

        /**
         * The base directory for SeLion files<br>
         * Default is set to <b>selionFiles</b>
         */
        BASE_DIR("baseDir", "selionFiles", true),

        /**
         * The work directory of SeLion
         */
        WORK_DIR("workDir", BASE_DIR.defaultValue + "/selionWorkDir", true),

        /**
         * platform is specified by user.<br>
         * Default is set to <b>ANY</b> Supporting values are: ANDROID, ANY, LINUX, MAC, UNIX, VISTA, WINDOWS, XP.
         */
        BROWSER_CAPABILITY_PLATFORM("platform", "ANY", false),

        /**
         * Should javascript capability be enabled on the browser for the AUT. By default javascript will be enabled on
         * the client browser, but this flag can be used to toggle this setting.
         */
        BROWSER_CAPABILITY_SUPPORT_JAVASCRIPT("enableJavaScript", "true", false),

        /**
         * Set the Height of the Browser Window<br>
         * Should be a whole number<br>
         */
        BROWSER_HEIGHT("browserHeight", "", false),

        /**
         * Set the Width of the Browser Window<br>
         * Should be a whole number<br>
         */
        BROWSER_WIDTH("browserWidth", "", false),

        /**
         * Timeout for an execution command, in milliseconds.<br>
         * <br>
         * Used in SeLion to configure Selenium timeouts<br>
         * Default is set to <b>120000</b>
         */
        EXECUTION_TIMEOUT("executionTimeout", "120000", false),

        /**
         * Automatically attempt to download various webdriver dependencies (chromedriver, etc) if they are not
         * available. <br>
         * Default is set to <b>false</b>
         */
        DOWNLOAD_DEPENDENCIES("downloadDependencies", "false", true),

        /**
         * Timeout for downloading dependencies, in milliseconds.<br>
         * <br>
         * Used in SeLion to configure the download timeout<br>
         * Default is set to <b>600000</b>
         */
        DOWNLOAD_TIMEOUT("downloadTimeout", "600000", true),

        /**
         * Automatically log pages source code.<br>
         * Used in conjunction with {@link ConfigProperty#AUTO_SCREEN_SHOT}.<br>
         * Default is set to <b>true</b>
         */
        LOG_PAGES("logPages", "true", true),

        /**
         * Maximum duration of a session duration in milliseconds. Session will be forcefully terminated if it takes
         * longer.<br>
         * The value of this parameter is used as the value of <b>sessionTimeout</b> for ios-driver and selendroid.<br>
         * This configuration affects LOCAL EXECUTION RUNS only i.e., when {@link ConfigProperty#SELENIUM_RUN_LOCALLY}
         * is set to true.<br>
         * The unit is milliseconds.<br>
         * Default is set to <b>1800000</b> milliseconds.
         */
        MOBILE_DRIVER_SESSION_TIMEOUT("sessionTimeout", "1800000", false),

        /**
         * Selendroid Server port to use.<br>
         * Defaults to the value used by Selendroid (<b>8080</b>)
         */
        SELENDROID_SERVER_PORT("selendroidServerPort", "8080", true),

        /**
         * Device Serial to use.<br>
         * Default is selected automatically by Selendroid
         */
        SELENDROID_DEVICE_SERIAL("selendroidDeviceSerial", "", true),

        /**
         * Force Reinstall the Selendroid Server and AUT.<br>
         * Default is set to <b>false</b>
         */
        SELENDROID_SERVER_FORCE_REINSTALL("selendroidServerForceReinstall", "false", true),

        /**
         * Timeout that will be used to start Android emulators.<br>
         * The value for this parameter is used as the value of <b>timeoutEmulatorStart</b> for selendroid.<br>
         * This configuration affects LOCAL EXECUTION RUNS only i.e., when {@link ConfigProperty#SELENIUM_RUN_LOCALLY}
         * is set to true.<br>
         * The unit is milliseconds.<br>
         * Default is set to <b>300000</b> milliseconds.<br>
         */
        SELENDROID_EMULATOR_START_TIMEOUT("timeoutEmulatorStart", "300000", false),

        /**
         * Maximum time in milliseconds to wait for the selendroid-server to come up on the device.<br>
         * The value for this parameter is used as the value of <b>serverStartTimeout</b> for selendroid.<br>
         * This configuration affects LOCAL EXECUTION RUNS only i.e., when {@link ConfigProperty#SELENIUM_RUN_LOCALLY}
         * is set to true.<br>
         * The unit is milliseconds.<br>
         * Default is set to <b>20000</b> milliseconds.<br>
         */
        SELENDROID_SERVER_START_TIMEOUT("serverStartTimeout", "20000", false);

        private String name;
        private String defaultValue;
        private boolean isGlobalScopeOnly;

        private ConfigProperty(String name, String defaultValue, boolean globalScopeOnly) {
            checkArgument(name != null, "Config property name can not be null.");
            checkArgument(defaultValue != null, "Config property default value cannot be null.");
            this.name = name;
            this.defaultValue = defaultValue;
            this.isGlobalScopeOnly = globalScopeOnly;
        }

        /**
         * Returns the name of this configuration property
         * 
         * @return The name of this configuration property
         */
        public String getName() {
            return this.name;
        }

        /**
         * Returns the default value for this configuration property
         * 
         * @return The default <b>String</b> value for this configuration property
         */
        public String getDefaultValue() {
            return this.defaultValue;
        }

        /**
         * Find the Enum for the specified property name.
         * 
         * @return The ConfigProperty Enum for the specified name if found or null if not found.
         */
        public static ConfigProperty find(String name) {
            for (ConfigProperty prop : ConfigProperty.values()) {
                if (prop.getName().equals(name)) {
                    return prop;
                }
            }
            return null;
        }

        /**
         * Answer if the property is a global only property (i.e only Suite scope param / System / Env Property)
         * 
         * @return true if the property is only supported as a global property.
         */
        public boolean isGlobalScopeOnly() {
            return this.isGlobalScopeOnly;
        }

    }

}
