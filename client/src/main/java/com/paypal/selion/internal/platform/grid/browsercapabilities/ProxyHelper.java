package com.paypal.selion.internal.platform.grid.browsercapabilities;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Proxy;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;

/**
 * This utility class is internally used by SeLion framework to manage proxy information.
 * 
 */
final class ProxyHelper {

    public static final String WARNING_MSG = "Enabling Proxy server settings on %s is known to change the proxy server settings at the machine Level!%n"
            + "So if your remote host supports concurrent test runs it can cause un-predictable test results!";

    private ProxyHelper() {

    }

    /**
     * @return - <code>true</code> if user requires proxy server is required.
     */
    public static boolean isProxyServerRequired() {
        boolean proxyServerInfoPresent = StringUtils.isNotBlank(getProperty(ConfigProperty.SELENIUM_PROXY_HOST));
        boolean proxyPortInfoPresent = StringUtils.isNotBlank(getProperty(ConfigProperty.SELENIUM_PROXY_PORT));
        return (proxyServerInfoPresent && proxyPortInfoPresent);
    }

    /**
     * @return - A {@link Proxy} object that represents the Proxy server to be used.
     */
    public static Proxy createProxyObject() {
        Proxy proxy = new Proxy();
        String proxyHost = String.format("%s:%s", getProperty(ConfigProperty.SELENIUM_PROXY_HOST),
                getProperty(ConfigProperty.SELENIUM_PROXY_PORT));
        proxy.setHttpProxy(proxyHost);
        proxy.setFtpProxy(proxyHost);
        proxy.setSslProxy(proxyHost);
        return proxy;
    }

    // Note: This method is wired correctly only for ConfigProperty's which are global in nature.
    private static String getProperty(ConfigProperty property) {
        return Config.getConfigProperty(property);
    }

}
