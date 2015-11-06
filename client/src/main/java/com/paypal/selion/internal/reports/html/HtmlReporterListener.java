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

package com.paypal.selion.internal.reports.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.testng.xml.XmlSuite;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.configuration.ListenerInfo;
import com.paypal.selion.configuration.ListenerManager;
import com.paypal.selion.internal.reports.model.BaseLog;
import com.paypal.selion.internal.reports.services.ReporterDateFormatter;
import com.paypal.selion.reports.services.ConfigSummaryData;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class is responsible for creating Html Reports using Velocity Templates. The data for the report is retrieved
 * from TestNG using the callback method {@link HtmlReporterListener#generateReport(List, List, String)}
 * 
 */
public class HtmlReporterListener implements IReporter, IInvokedMethodListener {
    /**
     * This String constant represents the JVM argument that can be enabled/disabled to enable/disable
     * {@link HtmlReporterListener}
     */
    public static final String ENABLE_HTML_REPORTER_LISTENER = "enable.html.reporter.listener";

    /**
     * This String constant holds the Key used by other classes to access the testName from the attribute map that is
     * maintained in ITestResult for the method
     */
    public static final String TEST_NAME_KEY = "testName";

    private static SimpleLogger logger = SeLionLogger.getLogger();
    private PrintWriter out;
    private final VelocityEngine ve;
    private String outputDir;
    private final Map<Integer, String> statusMap = new HashMap<Integer, String>();

    public HtmlReporterListener() {
        // Register this listener with the ListenerManager; disabled by default when not defined in VM argument.
        ListenerManager.registerListener(new ListenerInfo(this.getClass(), ENABLE_HTML_REPORTER_LISTENER, false));

        ve = new VelocityEngine();

        ve.setProperty("resource.loader", "class");
        ve.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        try {
            ve.init();
        } catch (Exception e) { // catching exception because thats what is
                                // mentioned as being thrown
            ReporterException re = new ReporterException(e);
            throw re;
        }
        statusMap.put(ITestResult.SUCCESS, "passed");
        statusMap.put(ITestResult.FAILURE, "failed");
        statusMap.put(ITestResult.SKIP, "skipped");
    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuite, List<ISuite> suites, String outputDir) {
        logger.entering(new Object[] { xmlSuite, suites, outputDir });
        if (ListenerManager.isCurrentMethodSkipped(this)) {
            logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
            return;
        }

        this.outputDir = outputDir;
        ReportDataGenerator.initReportData(suites);

        out = createWriter(outputDir);
        startHtml(out);

        List<Line> lines = createSummary(suites);
        createDetail(lines);
        createMethodContent(suites, outputDir);

        endHtml(out);

        out.flush();
        out.close();
        logger.exiting();
    }
    
    private void createDetail(List<Line> lines) {
        logger.entering(lines);
        for (Line line : lines) {
            createContent(line);
        }
        logger.exiting();
    }

    private void createContent(Line line) {
        logger.entering(line);
        try {
            File f = new File(outputDir + "/html/", line.getId() + ".html");
            logger.fine("generating method " + f.getAbsolutePath());
            Writer fileSystemWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(f), "UTF8")));

            Map<ITestNGMethod, List<ITestResult>> resultByMethod = new HashMap<ITestNGMethod, List<ITestResult>>();

            // find all methods
            for (ITestResult result : line.getAssociatedResults()) {
                List<ITestResult> list = resultByMethod.get(result.getMethod());
                if (list == null) {
                    list = new ArrayList<ITestResult>();
                    resultByMethod.put(result.getMethod(), list);
                }
                list.add(result);
            }

            // for each method, find all the status
            for (Entry<ITestNGMethod, List<ITestResult>> method : resultByMethod.entrySet()) {

                List<ITestResult> passed = new ArrayList<ITestResult>();
                List<ITestResult> failed = new ArrayList<ITestResult>();
                List<ITestResult> skipped = new ArrayList<ITestResult>();
                List<ITestResult> results = method.getValue();
                for (ITestResult result : results) {
                    switch (result.getStatus()) {
                    case ITestResult.SUCCESS:
                        passed.add(result);
                        break;
                    case ITestResult.FAILURE:
                        failed.add(result);
                        break;
                    case ITestResult.SKIP:
                        skipped.add(result);
                        break;
                    default:
                        throw new ReporterException(
                                "Implementation exists only for tests with status as : Success, Failure and Skipped");
                    }
                }

                // for each status // method, create the html
                if (passed.size() > 0) {
                    Template t = ve.getTemplate("/templates/method.part.html");
                    VelocityContext context = new VelocityContext();
                    context.put("status", "passed");
                    context.put("method", passed.get(0).getMethod());
                    StringBuilder buff = new StringBuilder();
                    for (ITestResult result : passed) {
                        buff.append(getContent(result));
                    }
                    context.put("content", buff.toString());
                    StringWriter writer = new StringWriter();
                    t.merge(context, writer);
                    fileSystemWriter.write(writer.toString());
                }

                if (failed.size() > 0) {
                    Template t = ve.getTemplate("/templates/method.part.html");
                    VelocityContext context = new VelocityContext();
                    context.put("status", "failed");
                    context.put("method", failed.get(0).getMethod());
                    StringBuilder buff = new StringBuilder();
                    for (ITestResult result : failed) {
                        buff.append(getContent(result));
                    }
                    context.put("content", buff.toString());
                    StringWriter writer = new StringWriter();
                    t.merge(context, writer);
                    fileSystemWriter.write(writer.toString());
                }
                if (skipped.size() > 0) {
                    Template t = ve.getTemplate("/templates/method.part.html");
                    VelocityContext context = new VelocityContext();
                    context.put("status", "skipped");
                    context.put("method", skipped.get(0).getMethod());
                    StringBuilder buff = new StringBuilder();
                    for (ITestResult result : skipped) {
                        buff.append(getContent(result));
                    }
                    context.put("content", buff.toString());
                    StringWriter writer = new StringWriter();
                    t.merge(context, writer);
                    fileSystemWriter.write(writer.toString());
                }
            }

            fileSystemWriter.flush();
            fileSystemWriter.close();
        } catch (Exception e) {
            ReporterException re = new ReporterException(e);
            throw re;
        }
        logger.exiting();

    }

    private void createMethodContent(List<ISuite> suites, String outdir) {
        logger.entering(new Object[] { suites, outdir });
        for (ISuite suite : suites) {
            Map<String, ISuiteResult> r = suite.getResults();
            for (ISuiteResult r2 : r.values()) {
                ITestContext ctx = r2.getTestContext();
                ITestNGMethod[] methods = ctx.getAllTestMethods();
                for (int i = 0; i < methods.length; i++) {
                    createMethod(ctx, methods[i], outdir);
                }
            }
        }
        logger.exiting();
    }

    private String getContent(ITestResult result) {
        logger.entering(result);

        StringBuilder contentBuffer = new StringBuilder();

        contentBuffer.append(String.format("Total duration of this instance run : %02d sec. ",
                (result.getEndMillis() - result.getStartMillis()) / 1000));
        Object[] parameters = result.getParameters();
        boolean hasParameters = parameters != null && parameters.length > 0;
        List<String> msgs = Reporter.getOutput(result);
        boolean hasReporterOutput = msgs.size() > 0;
        Throwable exception = result.getThrowable();
        boolean hasThrowable = exception != null;
        List<String> imgForFilmStrip = new ArrayList<String>();
        if (hasReporterOutput || hasThrowable) {
            if (hasParameters) {
                contentBuffer.append("<h2 class='yuk_grey_midpnl_ltitle'>");
                for (int i = 0; i < parameters.length; i++) {
                    Object p = parameters[i];
                    String paramAsString = "null";
                    if (p != null) {
                        paramAsString = p.toString() + "<i>(" + p.getClass().getSimpleName() + ")</i> , ";
                    }
                    contentBuffer.append(paramAsString);
                }
                contentBuffer.append("</h2>");
            }

            if (hasReporterOutput || hasThrowable) {
                contentBuffer.append("<div class='leftContent' style='float: left; width: 70%;'>");
                contentBuffer.append("<h3>Test Log</h3>");

                for (String line : msgs) {
                    BaseLog logLine = new BaseLog(line);
                    if (logLine.getScreen() != null) {
                        imgForFilmStrip.add(logLine.getScreenURL());
                    }
                    String htmllog = logLine.getMsg();
                    // Attaching ralogId to each of the page title.
                    if ((logLine.getHref() != null) && (logLine.getHref().length() > 1)) {
                        htmllog = "<a href='../" + logLine.getHref() + "' title='" + logLine.getLocation() + "' >"
                                + (StringUtils.isNotEmpty(htmllog) ? htmllog : "Page Source") + "</a>";
                        
                    }
                    // Don't output blank message w/o any Href.
                    if ((logLine.getHref() != null) || logLine.getMsg() != null && !logLine.getMsg().isEmpty()) {
                        contentBuffer.append(htmllog);
                        contentBuffer.append("<br/>");
                    }
                }

                if (hasThrowable) {
                    generateExceptionReport(exception, result.getMethod(), contentBuffer);
                }
            }
            contentBuffer.append("</div>"); // end of
            // leftContent

            contentBuffer.append("<div class='filmStripContainer' style='float: right; width: 100%;'>");
            contentBuffer.append("<b>Preview</b>");
            contentBuffer.append("<div class=\"filmStrip\">");
            contentBuffer.append("<ul>");
            for (String imgPath : imgForFilmStrip) {
                contentBuffer.append("<li>");
                contentBuffer.append("<a href=\"../" + imgPath + "\" > <img src=\"../" + imgPath
                        + "\" width=\"200\" height=\"200\" /> </a>");
                contentBuffer.append("</li>");
            }

            contentBuffer.append("</ul>");

            contentBuffer.append("</div>");
            contentBuffer.append("</div>");

        }

        contentBuffer.append("<div class='clear_both'></div>");
        // Not logging the return value, because it will clog the logs
        logger.exiting();
        return contentBuffer.toString();
    }

    protected void generateExceptionReport(Throwable exception, ITestNGMethod method, StringBuilder contentBuffer) {
        logger.entering(new Object[] { exception, method, contentBuffer });
        Throwable fortile = exception;

        String title = fortile.getMessage();
        if (title == null) {
            title = "Encountered problems when attempting to extract a meaningful Root cause.";
            if (fortile.getCause() != null && !fortile.getCause().getMessage().trim().isEmpty()) {
                title = fortile.getCause().getMessage();
            }
        }
        generateExceptionReport(exception, method, title, contentBuffer);
        logger.exiting();
    }

    private void generateExceptionReport(Throwable exception, ITestNGMethod method, String title,
            StringBuilder contentBuffer) {
        generateTheStackTrace(exception, method, title, contentBuffer);
    }

    private void generateTheStackTrace(Throwable exception, ITestNGMethod method, String title,
            StringBuilder contentBuffer) {
        logger.entering(new Object[] { exception, method, title, contentBuffer });
        contentBuffer.append(" <div class='stContainer' >" + exception.getClass() + ":" + title// escape(title)
                + "<a class='exceptionlnk' href='#'>(+)</a>");

        contentBuffer.append("<div class='exception' style='display:none'>");

        StackTraceElement[] s1 = exception.getStackTrace();
        Throwable t2 = exception.getCause();
        if ((t2 != null) && (t2.equals(exception))) {
            t2 = null;
        }

        for (int x = 0; x < s1.length; x++) {
            contentBuffer.append((x > 0 ? "<br/>at " : "") + escape(s1[x].toString()));
        }

        if (t2 != null) {
            generateExceptionReport(t2, method, "Caused by " + t2.getLocalizedMessage(), contentBuffer);
        }
        contentBuffer.append("</div></div>");
        logger.exiting();
    }

    private static String escape(String string) {
        if (null == string) {
            return string;
        }
        return string.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    private void createMethod(ITestContext ctx, ITestNGMethod method, String outdir) {
        logger.entering(new Object[] { ctx, method, outdir });
        try {
            File f = new File(outdir + "/html/", method.getId() + ".html");
            logger.fine("generating method " + f.getAbsolutePath());
            Writer fileSystemWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(f), "UTF8")));
            Template t = ve.getTemplate("/templates/method.part.html");

            Set<ITestResult> passed = ctx.getPassedTests().getResults(method);

            for (ITestResult result : passed) {
                VelocityContext context = new VelocityContext();
                context.put("method", method);
                context.put("status", "passed");
                context.put("result", result);
                context.put("content", getContent(result));
                StringWriter writer = new StringWriter();
                t.merge(context, writer);
                fileSystemWriter.write(writer.toString());
            }

            Set<ITestResult> failed = ctx.getFailedTests().getResults(method);
            for (ITestResult result : failed) {
                VelocityContext context = new VelocityContext();
                context.put("method", method);
                context.put("status", "failed");
                context.put("result", result);
                context.put("content", getContent(result));
                StringWriter writer = new StringWriter();
                t.merge(context, writer);
                fileSystemWriter.write(writer.toString());
            }

            Set<ITestResult> skipped = ctx.getSkippedTests().getResults(method);
            for (ITestResult result : skipped) {
                VelocityContext context = new VelocityContext();
                context.put("method", method);
                context.put("status", "skipped");
                context.put("result", result);
                context.put("content", getContent(result));
                StringWriter writer = new StringWriter();
                t.merge(context, writer);
                fileSystemWriter.write(writer.toString());
            }

            fileSystemWriter.flush();
            fileSystemWriter.close();
        } catch (Exception e) { // catching exception because velocity throws
                                // that and we can't change it
            ReporterException re = new ReporterException(e);
            throw re;
        }
        logger.exiting();

    }

    private List<Line> createSummary(List<ISuite> suites) {
        logger.entering(suites);
        try {

            Template t = ve.getTemplate("/templates/summaryTabs.part.html");
            VelocityContext context = new VelocityContext();

            List<GroupingView> views = new ArrayList<GroupingView>();

            GroupingView view = new GroupingView("managerView", "per class", "Overview organized per class", ve,
                    suites, new ByClassSplitter());
            views.add(view);

            GroupingView view2 = new GroupingView("managerView2", "per package", "Overview organized per package", ve,
                    suites, new ByPackageSplitter());
            views.add(view2);

            GroupingView view3 = new GroupingView("managerView3", "per method", "Overview organized per method", ve,
                    suites, new ByMethodSplitter());
            views.add(view3);
            GroupingView view9 = new GroupingView("managerView9", "per testName", "Overview organized per testName",
                    ve, suites, new ByTestNameSplitter());
            views.add(view9);
            /*********************************/

            Filter f2 = new StateFilter(ITestResult.FAILURE);
            GroupingView view6 = new GroupingView("managerView6", "failed methods only",
                    "Overview organized per failed methods", ve, suites, new ByMethodSplitter(), f2);
            views.add(view6);

            GroupingView view7 = new GroupingView("managerView7", "per group", "Overview organized per group", ve,
                    suites, new ByGroupSplitter());
            views.add(view7);

            context.put("views", views);

            StringWriter writer = new StringWriter();
            t.merge(context, writer);
            out.write(writer.toString());
            List<Line> lines = new ArrayList<Line>();
            for (GroupingView v : views) {
                for (Line line : v.getSplitter().getLines().values()) {
                    lines.add(line);
                }
            }
            logger.exiting(lines);
            return lines;

        } catch (Exception e) {
            ReporterException re = new ReporterException("Error occurred while generating report summary", e);
            throw re;
        }
    }

    /** Starts HTML stream */
    protected void startHtml(PrintWriter out) {
        logger.entering(out);
        try {

            Template t = ve.getTemplate("/templates/header.part.html");
            VelocityContext context = new VelocityContext();
            StringBuilder output = new StringBuilder();
            for (Entry<String, String> temp : ConfigSummaryData.getConfigSummary().entrySet()) {
                Entry<String, String> formattedTemp = ReporterDateFormatter.formatReportDataForBrowsableReports(temp);
                output.append(formattedTemp.getKey()).append(" : <b>").append(formattedTemp.getValue()).append("</b><br>");
            }

            context.put("configSummary", output.toString());
            StringWriter writer = new StringWriter();
            t.merge(context, writer);
            out.write(writer.toString());

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        logger.exiting();

    }

    private void endHtml(PrintWriter out) {
        logger.entering(out);
        try {
            Template t = ve.getTemplate("/templates/footer.part.html");
            VelocityContext context = new VelocityContext();
            StringWriter writer = new StringWriter();
            t.merge(context, writer);
            out.write(writer.toString());
        } catch (Exception e) {
            ReporterException re = new ReporterException(e);
            throw re;
        }
        logger.exiting();
    }

    protected PrintWriter createWriter(String outdir) {
        logger.entering(outdir);
        File f = new File(outdir + "/html/", "report.html");
        if (f.exists()) {
            Format formatter = new SimpleDateFormat("MM-dd-yyyy-HH-mm");
            String currentDate = formatter.format(new Date());
            f.renameTo(new File(outdir + "/html/", "report-" + currentDate + ".html")); // NOSONAR
        }
        logger.info("generating report " + f.getAbsolutePath());
        try {
            PrintWriter pw = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8")));
            logger.exiting(pw);
            return pw;
        } catch (Exception e) {
            ReporterException re = new ReporterException(e);
            throw re;
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        try {
            if (ListenerManager.isCurrentMethodSkipped(this)) {
                logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
                return;
            }
            Test testMethod = method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class);
            if (testMethod != null) {
                String testName = testMethod.testName();
                if (StringUtils.isNotEmpty(testName)) {
                    testResult.setAttribute(TEST_NAME_KEY, testName);
                }
            }
        } catch (Exception e) { //NOSONAR
            logger.log(Level.WARNING, "An error occurred while processing beforeInvocation: " + e.getMessage(), e);
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        try {
            // Below conditional check needs to be invoked in all TestNG Listener interface implementation.
            // Failing to do so can have un-predictable results.
            if (ListenerManager.isCurrentMethodSkipped(this)) {
                logger.exiting(ListenerManager.THREAD_EXCLUSION_MSG);
                return;
            }
        } catch (Exception e) { //NOSONAR
            logger.log(Level.WARNING, "An error occurred while processing afterInvocation: " + e.getMessage(), e);
        }
    }
}
