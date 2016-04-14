/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.paypal.selion.logging.SeLionGridLogger;

/**
 * <code>BrowserStatisticsCollection</code> stores the all the supported browsers by the Grid and Nodes collectively. It
 * stores the maximum available instances of a particular browser in the Grid - Nodes farm along with the waiting
 * requests for a particular browser. This class is not thread safe, the clients are expected to create individual
 * instances of this class in separate threads.
 * 
 */
public class BrowserStatisticsCollection {

    private static final SeLionGridLogger logger = SeLionGridLogger.getLogger(BrowserStatisticsCollection.class);

    private final List<BrowserStatistics> browserStatisticsList;

    public BrowserStatisticsCollection() {
        browserStatisticsList = new ArrayList<>();
    }

    /**
     * Returns the list of {@link BrowserStatistics}.
     * 
     * @return {@link List} of {@link BrowserStatistics}.
     */
    public List<BrowserStatistics> getBrowserStatisticsList() {
        return browserStatisticsList;
    }

    /**
     * Sets the maximum instances for a particular browser. This call creates a unique statistics for the provided
     * browser name it does not exists.
     * 
     * @param browserName
     *            Name of the browser.
     * @param maxBrowserInstances
     *            Maximum instances of the browser.
     */
    public void setMaxBrowserInstances(String browserName, int maxBrowserInstances) {
        logger.entering(new Object[] { browserName, maxBrowserInstances });
        validateBrowserName(browserName);
        BrowserStatistics lStatistics = createStatisticsIfNotPresent(browserName);
        lStatistics.setMaxBrowserInstances(maxBrowserInstances);
        logger.exiting();
    }

    /**
     * Increments the waiting request for the provided browser name. This call creates a unique statistics for the
     * provided browser name it does not exists.
     * 
     * @param browserName
     *            Name of the browser.
     */
    public void incrementWaitingRequests(String browserName) {
        logger.entering(browserName);
        validateBrowserName(browserName);
        BrowserStatistics lStatistics = createStatisticsIfNotPresent(browserName);
        lStatistics.incrementWaitingRequests();
        logger.exiting();
    }

    private void validateBrowserName(String browserName) {
        if (StringUtils.isBlank(browserName)) {
            throw new IllegalArgumentException("Browser name cannot be null");
        }
    }

    private synchronized BrowserStatistics createStatisticsIfNotPresent(String browserName) {
        for (BrowserStatistics loadStatistics : browserStatisticsList) {
            if (loadStatistics.browserName.equals(browserName)) {
                return loadStatistics;
            }
        }
        BrowserStatistics loadStatistics = new BrowserStatistics(browserName);
        this.browserStatisticsList.add(loadStatistics);
        return loadStatistics;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(browserStatisticsList);
    }

    /**
     * <code>BrowserStatistics</code> holds the statistics for an individual browser. This class acts as a mere Value
     * Object.
     * 
     */
    public class BrowserStatistics {
        private final String browserName;
        private final Statistics statistics;

        public BrowserStatistics(String browserName) {
            this.browserName = browserName;
            statistics = new Statistics();
        }

        public void setMaxBrowserInstances(int maxBrowserInstances) {
            statistics.setMaxBrowserInstances(maxBrowserInstances);
        }

        public void incrementWaitingRequests() {
            statistics.incrementWaitingRequests();
        }

        private class Statistics {
            @SuppressWarnings("unused")
            int waitingRequests;
            @SuppressWarnings("unused")
            int maxBrowserInstances;

            void setMaxBrowserInstances(int maxBrowserInstances) {
                this.maxBrowserInstances = maxBrowserInstances;
            }

            void incrementWaitingRequests() {
                this.waitingRequests += 1;
            }
        }
    }

}
