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

import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.testng.ISuite;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * View that takes a list of result, group them based on type of {@link CollectionSplitter} associated with it, and
 * display the result in a table, 1 line for all the test that match a given criteria
 * 
 */
public class GroupingView implements View {

    private List<ISuite> suites;
    private String id;
    private VelocityEngine ve;
    private String title;
    private String description;
    private CollectionSplitter splitter;

    private static SimpleLogger logger = SeLionLogger.getLogger();

    public GroupingView(String id, String title, String description, VelocityEngine ve, List<ISuite> suites,
            CollectionSplitter splitter) {
        this(id, title, description, ve, suites, splitter, new BlankFilter());
    }

    public GroupingView(String id, String title, String description, VelocityEngine ve, List<ISuite> suites,
            CollectionSplitter splitter, Filter filter) {
        this.id = id;
        this.ve = ve;
        this.title = title;
        this.description = description;
        this.suites = suites;
        this.splitter = splitter;
        this.splitter.setFilter(filter);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContent() {
        logger.entering();
        try {
            Template t = ve.getTemplate("/templates/ManagerViewTable.part.html");

            StringWriter writer = new StringWriter();
            VelocityContext context = new VelocityContext();

            context.put("title", title);
            context.put("description", description);
            context.put("view", this);

            for (ISuite suite : suites) {
                context.put("suiteName", suite.getName());
                splitter.setSuite(suite);
                splitter.organize();
                context.put("lines", splitter.getLines().values());
            }

            t.merge(context, writer);
            // Not logging the return value
            logger.exiting();
            return writer.toString();

        } catch (Exception e) { // the method throws Exception and we dont have access to that code
            logger.log(Level.SEVERE, e.getMessage(), e);
            String returnValue = "Error generating manager view " + e.getMessage();
            logger.exiting(returnValue);
            return returnValue;
        }
    }

    public CollectionSplitter getSplitter() {
        return splitter;
    }

    @Override
    public void setData(List<ISuite> suites) {
        this.suites = suites;
    }

}
