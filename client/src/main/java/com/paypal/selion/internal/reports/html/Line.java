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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.paypal.selion.logger.SeLionLogger;

/**
 * Holds single unit of information for each type of {@link GroupingView}.
 * 
 */
public class Line {
    private final String id;
    private final String label;

    private final Set<ITestNGMethod> methods = new HashSet<ITestNGMethod>();
    private final CollectionSplitter splitter;

    private int instancePassed;
    private int instanceFailed ;
    private int instanceSkipped;

    private final List<ITestResult> associatedResults = new ArrayList<ITestResult>();

    public Line(String label, CollectionSplitter splitter) {
        this.splitter = splitter;
        id = UUID.randomUUID().toString();
        this.label = label;
    }

    public int getTotalMethods() {
        return methods.size();
    }

    public void add(ITestResult result) {
        SeLionLogger.getLogger().entering(result);
        associatedResults.add(result);
        methods.add(result.getMethod());

        switch (result.getStatus()) {
        case ITestResult.SUCCESS:
            instancePassed++;
            splitter.incrementTotalInstancePassed();
            break;
        case ITestResult.FAILURE:
            instanceFailed++;
            splitter.incrementTotalInstanceFailed();
            break;
        case ITestResult.SKIP:
            instanceSkipped++;
            splitter.incrementTotalInstanceSkipped();
            break;
        default:
            throw new ReporterException("Result Status is Invalid");
        }
        SeLionLogger.getLogger().exiting();
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getInstancePassed() {
        return instancePassed;
    }

    public int getInstanceFailed() {
        return instanceFailed;
    }

    public int getInstanceSkipped() {
        return instanceSkipped;
    }

    public List<ITestResult> getAssociatedResults() {
        return associatedResults;
    }

    public CollectionSplitter getSplitter() {
        return splitter;
    }
}
