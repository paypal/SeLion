/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016-2017 PayPal                                                                                     |
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

package com.paypal.selion.pojos;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.RemoteProxy;

import com.paypal.selion.logging.SeLionGridLogger;

/**
 * <code>BrowserInformationCache</code> acts as a cache holding the browser name and the maximum instances available in
 * a particular node. The key to the cache is the {@link URL} object of the node that uniquely identifies the node.
 */
public class BrowserInformationCache {

    public static final String[] SUPPORTED_BROWSERS;

    private static final int INITIAL_CAPACITY = 25;

    private static final SeLionGridLogger logger = SeLionGridLogger.getLogger(BrowserInformationCache.class);

    private static final BrowserInformationCache instance = new BrowserInformationCache();

    private final Map<URL, TestSlotInformation> nodeMap;

    static {
        List<String> browserList = new ArrayList<>();
        try {
            Class<?> clazz = Class.forName("org.openqa.selenium.remote.BrowserType");
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getAnnotation(Deprecated.class) == null) {
                    browserList.add(field.get(null).toString());
                }
            }
        } catch (Exception e) {
            browserList.clear();
            browserList.addAll(Arrays.asList("android", "chrome", "firefox", "htmlunit",
                "internet explorer", "iPhone", "iPad", "opera", "safari", "MicrosoftEdge"));
        } finally {
            SUPPORTED_BROWSERS = browserList.toArray(new String[0]);
        }
    }

    private BrowserInformationCache() {
        nodeMap = new ConcurrentHashMap<>(INITIAL_CAPACITY);
    }

    public static BrowserInformationCache getInstance() {
        return instance;
    }

    /**
     * Updates the Cache for the provide Node represented by the {@link URL} instance. This methods creates or updates
     * information for the Node/Browser combination.
     *
     * @param url
     *            {@link URL} of the Node.
     * @param browserName
     *            Browser name as {@link String}.
     * @param maxInstances
     *            Maximum instances of the browser.
     */
    public synchronized void updateBrowserInfo(URL url, String browserName, int maxInstances) {
        logger.entering(new Object[] { url, browserName, maxInstances });
        BrowserInformation browserInformation = BrowserInformation.createBrowserInfo(browserName, maxInstances);
        TestSlotInformation testSlotInformation = (nodeMap.get(url) == null) ? new TestSlotInformation() : nodeMap
                .get(url);
        testSlotInformation.addBrowserInfo(browserInformation);
        if (nodeMap.get(url) == null) {
            logger.log(Level.FINE, "Creating new entry -> " + url + " : [" + browserName + ":" + maxInstances + "]");
            nodeMap.put(url, testSlotInformation);
        } else {
            logger.log(Level.FINE, "Added entry -> " + url + " : " + " : [" + browserName + ":" + maxInstances + "]");
        }
    }

    /**
     * Returns the total instances of a particular browser, available through all nodes. This methods takes an instance
     * of {@link GridRegistry} to clean the cache before returning the results.
     *
     * @param browserName
     *            Browser name as {@link String}
     * @param registry
     *            {@link GridRegistry} instance.
     * @return Total instances of a particular browser across nodes.
     */
    public synchronized int getTotalBrowserCapacity(String browserName, GridRegistry registry) {
        logger.entering(new Object[] { browserName, registry });
        cleanCacheUsingRegistry(registry);
        int totalBrowserCounts = 0;
        for (Map.Entry<URL, TestSlotInformation> entry : nodeMap.entrySet()) {
            BrowserInformation browserInfo = entry.getValue().getBrowserInfo(browserName);
            totalBrowserCounts += (browserInfo == null) ? 0 : browserInfo.getMaxInstances();
        }
        logger.exiting(totalBrowserCounts);
        return totalBrowserCounts;
    }

    private void cleanCacheUsingRegistry(GridRegistry registry) {
        List<URL> relevantURLs = getRegistryURLs(registry);
        removeIrrelevantURLs(relevantURLs);
    }

    private List<URL> getRegistryURLs(GridRegistry registry) {
        Iterator<RemoteProxy> remoteProxyIterator = registry.getAllProxies().iterator();
        List<URL> urlList = new ArrayList<>();
        while (remoteProxyIterator.hasNext()) {
            RemoteProxy remoteProxy = remoteProxyIterator.next();
            urlList.add(remoteProxy.getRemoteHost());
        }
        return urlList;
    }

    private void removeIrrelevantURLs(List<URL> hotURLList) {
        Iterator<URL> urlIterator = nodeMap.keySet().iterator();
        while (urlIterator.hasNext()) {
            URL cacheURL = urlIterator.next();
            if (!hotURLList.contains(cacheURL)) {
                nodeMap.remove(cacheURL);
            }
        }
    }

    /**
     * <code>TestSlotInformation</code> holds the {@link BrowserInformation} corresponding to a individual unique test
     * slot.
     */
    private static class TestSlotInformation {

        private static final SeLionGridLogger logger = SeLionGridLogger.getLogger(TestSlotInformation.class);

        // Browser information set.
        private final Set<BrowserInformation> browserInformationSet;

        private TestSlotInformation() {
            browserInformationSet = new HashSet<>();
        }

        /**
         * Updates the BrowserInformation. This method removes the existing entry and updates with the new entry.
         *
         * @param browserInformation
         *            Instance of {@link BrowserInformation}
         * @return True if addition is successful, false otherwise.
         */
        private boolean addBrowserInfo(BrowserInformation browserInformation) {
            logger.entering(browserInformation);
            if (!browserInformationSet.contains(browserInformation)) {
                logger.log(Level.INFO, "Adding BrowserInfo " + browserInformation + " to set...");
                return browserInformationSet.add(browserInformation);
            } else {
                logger.log(Level.INFO, "BrowserInfo " + browserInformation + " already present in set, replacing...");
                browserInformationSet.remove(browserInformation);
                return browserInformationSet.add(browserInformation);
            }
        }

        private BrowserInformation getBrowserInfo(String browserName) {
            logger.entering(browserName);
            for (BrowserInformation browserInformation : browserInformationSet) {
                if (browserInformation.getBrowserName().equals(browserName)) {
                    logger.exiting(browserInformation);
                    return browserInformation;
                }
            }
            logger.log(Level.FINE, "requested browser name " + browserName + " unavailable in set... returning null");
            return null;
        }

    }

    /**
     * Pojo for holding browser information
     */
    private static class BrowserInformation {

        // Name of the browser
        private final String browserName;

        // Maximum instances
        private final int maxInstances;

        private volatile int calculatedHashCode;

        private BrowserInformation(String browserName, int maxInstances) {
            this.browserName = browserName;
            this.maxInstances = maxInstances;
        }

        private static BrowserInformation createBrowserInfo(String browserName, int maxInstances) {
            BrowserInformation browserInfo = new BrowserInformation(browserName, maxInstances);
            return browserInfo;
        }

        public String getBrowserName() {
            return browserName;
        }

        public int getMaxInstances() {
            return maxInstances;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BrowserInformation)) {
                return false;
            }
            return this.getBrowserName().equals(((BrowserInformation) o).getBrowserName());
        }

        @Override
        public int hashCode() {
            int result = calculatedHashCode;
            if (result == 0) {
                result = 17;
                result = 31 * result + getBrowserName().hashCode();
                calculatedHashCode = result;
            }
            return result;
        }

        @Override
        public String toString() {
            return "[" + browserName + ":" + maxInstances + "]";
        }
    }
}
