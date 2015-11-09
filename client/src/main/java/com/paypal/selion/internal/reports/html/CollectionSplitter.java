/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

package com.paypal.selion.internal.reports.html;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

public abstract class CollectionSplitter {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private ISuite suite;
    private Filter filter;
    private final Map<String, Line> lineById = new HashMap<>();
    private int totalInstancePassed;
    private int totalInstancePassedEnvt;
    private int totalInstanceFailed;
    private int totalInstanceFailedEnvt;
    private int totalInstanceSkipped;
    private int totalInstanceSkippedEnvt;

    public final void incrementRotalInstancePassedEnvt() {
        this.totalInstancePassedEnvt++;
    }

    public final void incrementTotalInstancePassed() {
        this.totalInstancePassed++;
    }

    public final void incrementTotalInstanceFailedEnvt() {
        this.totalInstanceFailedEnvt++;
    }

    public final void incrementTotalInstanceFailed() {
        this.totalInstanceFailed++;
    }

    public final void incrementTotalInstanceSkippedEnvt() {
        this.totalInstanceSkippedEnvt++;
    }

    public final void incrementTotalInstanceSkipped() {
        this.totalInstanceSkipped++;
    }

    /**
     * Return the keys the result should be associated with. For instance, for a view where the result should be ordered
     * by package, it should return the package name. It returns null is the result do not belong to any group, for
     * instance if you want a failedByPackage view, and the test is not failed. It returns a list and not unique value
     * in case the splitting is not unique. For package, class etc, the test will only have 1 key, but if you're working
     * with groups for instance, you can have one tests tagged with both SYI and seller reg and buyer reg.
     * 
     * @param result
     * @return list of keys
     */
    public abstract List<String> getKeys(ITestResult result);

    public void setSuite(ISuite suite) {
        this.suite = suite;
    }

    public void organize() {
        logger.entering();
        if (suite == null) {
            ReporterException e = new ReporterException("Bug. Suite cannot be null");
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
        for (ISuiteResult suiteResult : suite.getResults().values()) {
            ITestContext ctx = suiteResult.getTestContext();
            organize(ctx.getPassedTests().getAllResults());
            organize(ctx.getFailedTests().getAllResults());
            organize(ctx.getSkippedTests().getAllResults());
        }
        logger.exiting();
    }

    private void organize(Collection<ITestResult> results) {
        logger.entering(results);
        for (ITestResult result : results) {
            if (filter.isValid(result)) {
                for (String key : getKeys(result)) {
                    if (key != null) {
                        Line l = lineById.get(key);
                        if (l == null) {
                            l = new Line(key, this);
                            lineById.put(key, l);
                        }
                        l.add(result);

                    }
                }
            }
        }
        logger.exiting();
    }

    public Map<String, Line> getLines() {
        return lineById;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;

    }

    public int getTotalInstancePassed() {
        return totalInstancePassed;
    }

    public int getTotalInstancePassedEnvt() {
        return totalInstancePassedEnvt;
    }

    public int getTotalInstanceFailed() {
        return totalInstanceFailed;
    }

    public int getTotalInstanceFailedEnvt() {
        return totalInstanceFailedEnvt;
    }

    public int getTotalInstanceSkipped() {
        return totalInstanceSkipped;
    }

    public int getTotalInstanceSkippedEnvt() {
        return totalInstanceSkippedEnvt;
    }

}
