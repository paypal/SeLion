/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.proxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

import com.paypal.selion.node.servlets.NodeAutoUpgradeServlet;
import com.paypal.selion.node.servlets.NodeForceRestartServlet;
import com.paypal.selion.pojos.ArtifactDetails;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.pojos.ArtifactDetails.URLChecksumEntity;
import com.paypal.selion.utils.ConfigParser;
import com.paypal.selion.utils.ConfigParser.ConfigParserException;

/**
 * This is a customized {@link DefaultRemoteProxy} for SeLion. This proxy when injected into the Grid, starts counting
 * unique test sessions. After "n" test sessions, the proxy unhooks the node gracefully from the grid and self
 * terminates gracefully. The number of unique sessions is controlled via a properties file : "selion.grid.properties".
 * A typical content of the file would be as below :
 * 
 * <pre>
 * UniqueSessionCount = 2
 * </pre>
 * 
 * Here UniqueSessionCount represents the max. number of tests that the node will run before recycling itself.
 */
public class SeLionRemoteProxy extends DefaultRemoteProxy {

    private static final Logger log = Logger.getLogger(SeLionRemoteProxy.class.getName());
    private static final int MAX_SESSION_ALLOWED = 50;

    private int maxSessionsAllowed, totalSessionsCompleted = 0, totalSessionsStarted = 0;
    private boolean shutDownInProgress = false, forceShutDown = false;
    private String machine;
    private List<String> errorSessions = new ArrayList<String>();
    private File logFile = null;

    private int getUniqueSessionCount() {
        try {
            return ConfigParser.getInstance().getInt("uniqueSessionCount");
        } catch (ConfigParserException e) {// NOSONAR
            // Purposefully gobbling the exception here and NOT doing anything
            // with it.
            // We cannot afford to throw exceptions from within a Proxy
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        // if we are here, then it means there was a problem loading the
        // session count value from the configuration json file.
        // so lets return back a default session count.
        return MAX_SESSION_ALLOWED;

    }

    /**
     * @param request
     *            - a {@link RegistrationRequest} request which represents the basic information that is to be consumed
     *            by the grid when it is registering a new node.
     * @param registry
     *            - A {@link Registry} object that represent's the Grid's registry.
     * @throws IOException
     */
    public SeLionRemoteProxy(RegistrationRequest request, Registry registry) throws IOException {
        super(request, registry);
        StringBuffer info = new StringBuffer();
        maxSessionsAllowed = getUniqueSessionCount();
        machine = getRemoteHost().getHost();
        String logFileName = SeLionGridConstants.LOGS_FOLDER_PATH + machine + ".log";
        logFile = new File(logFileName);
        if (logFile.exists()) {
            FileUtils.deleteQuietly(logFile);
        }
        info.append("New proxy instantiated for the machine :").append(machine).append("\n");
        info.append("Autobot will attempt to recycle the node [");
        info.append(machine).append("] after ").append(maxSessionsAllowed);
        info.append(" unique sessions");
        appendMsgToCustomLog(info.toString());
    }

    private void appendMsgToCustomLog(String msg) {
        FileOutputStream fos = null;
        StringBuilder sb = new StringBuilder("\n");
        sb.append(MessageFormat.format("{0, date} {0, time} ", new Object[] { new Date(System.currentTimeMillis()) }));
        sb.append(msg).append("\n");
        try {
            fos = new FileOutputStream(logFile, true);
            fos.write(sb.toString().getBytes());
            fos.flush();
        } catch (IOException e) {// NOSONAR
            // Gobble exceptions and chose to do nothing with it.
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {// NOSONAR
                    // Gobble exceptions and chose to do nothing with it.
                }
            }
        }
    }

    public boolean release(ArtifactDetails artifactDetails) {

        final int TIME_OUT = 30 * 1000;
        RequestConfig config = RequestConfig.custom().setConnectTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).build();

        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        StringBuilder url = new StringBuilder();
        url.append(getId());
        url.append("/extra/");
        url.append(NodeAutoUpgradeServlet.class.getSimpleName());
        HttpPost post = new HttpPost(url.toString());

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        
        Map<String, URLChecksumEntity> artifactDetailsAsMap = artifactDetails.getArtifactDetailsAsMap();
        for (URLChecksumEntity eachArtifact  : artifactDetailsAsMap.values()) {
            nvps.add(eachArtifact.getChecksum());
            nvps.add(eachArtifact.getUrl());
        }
        
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps));
            client.execute(post);
        } catch (ClientProtocolException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        synchronized (this) {
            forceShutDown = true;
            shutDownInProgress = true;
            if (getTotalUsed() <= 1) {
                shutdownNode();
            }
        }

        return true;
    }

    public void beforeRelease(TestSession session) {
        // release the resources remotely.
        if (session.getExternalKey() == null) {
            throw new IllegalStateException("cannot release the resources, they haven't been reserved properly.");
        }
        boolean ok = session.sendDeleteSessionRequest();
        if (!ok) {
            StringBuilder error = new StringBuilder();
            synchronized (this) {

                forceShutDown = true;
                shutDownInProgress = true;
                error.append("Session :").append(session.toString()).append(", ");

                String testName = (String) session.getRequestedCapabilities().get("name");
                error.append("Host :").append(getId()).append(", ");
                if (testName != null && !testName.trim().isEmpty()) {
                    error.append("Test case :").append(testName).append(", ");

                }
                String browserName = (String) session.getRequestedCapabilities().get("browserName");
                if (browserName != null && !browserName.trim().isEmpty()) {
                    error.append("Browser :").append(browserName);
                }
                errorSessions.add(error.toString());
            }
            appendMsgToCustomLog("Error releasing the resources on timeout for session " + session);
            appendMsgToCustomLog("Debug Information [" + error.toString());
        }
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapability) {
        TestSession session = null;
        synchronized (this) {
            if (totalSessionsStarted >= maxSessionsAllowed || forceShutDown) {
                shutDownInProgress = true;
                // TODO: Remove me once Node stability has been ascertained
                // This is being included here intentionally since this is the
                // only way to debug issues
                // This will be removed once stability has been fully
                // ascertained.
                appendMsgToCustomLog("Was Max Sessions reached : " + (totalSessionsStarted >= maxSessionsAllowed)
                        + " on node " + getId());
                appendMsgToCustomLog("Was this a forcible shutdown ? " + (forceShutDown) + " on node " + getId());
                return null;
            }
            session = super.getNewSession(requestedCapability);
            if (session != null) {
                // count ONLY if the session was a valid one
                totalSessionsStarted++;
                if (totalSessionsStarted >= maxSessionsAllowed) {
                    shutDownInProgress = true;
                }
                appendMsgToCustomLog("Beginning session #" + totalSessionsStarted);
            }
            return session;
        }
    }

    @Override
    public void afterSession(TestSession session) {
        synchronized (this) {
            totalSessionsCompleted++;
            if (totalSessionsCompleted <= maxSessionsAllowed) {
                appendMsgToCustomLog("Completed session #" + totalSessionsCompleted);
            }
            appendMsgToCustomLog("Total Number of slots used : " + getTotalUsed() + " on node :" + getId());
            if (shutDownInProgress) {
                appendMsgToCustomLog("Proceeding with shutdown of node " + getId());
                if (getTotalUsed() <= 1) {
                    shutdownNode();
                    if (forceShutDown) {
                        appendMsgToCustomLog("Alert..Alert !! Grid is forcibly shutting down the node " + getId());
                    }
                }
            } else {
                appendMsgToCustomLog(totalSessionsCompleted + " session(s) completed.");
            }
        }
    }

    public synchronized void shutdownNode() {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        StringBuilder url = new StringBuilder();
        url.append("http://");
        url.append(machine);
        url.append(":").append(this.getRemoteHost().getPort());
        url.append("/extra/");
        url.append(NodeForceRestartServlet.class.getSimpleName());
        HttpPost post = new HttpPost(url.toString());
        try {
            client.execute(post);
            appendMsgToCustomLog("Node " + machine + " shut-down successfully.");
        } catch (ClientProtocolException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

}