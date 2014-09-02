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

package com.paypal.selion.grid.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import com.paypal.selion.node.servlets.NodeAutoUpgradeServlet;
import com.paypal.selion.pojos.ArtifactDetails;
import com.paypal.selion.pojos.ArtifactDetails.URLChecksumEntity;
import com.paypal.selion.pojos.PropsKeys;
import com.paypal.selion.proxy.SeLionRemoteProxy;
import com.paypal.selion.utils.ServletHelper;

/**
 * This {@link RegistryBasedServlet} servlet is responsible for getting the
 * following information from a Grid admin, and relaying it to each of the
 * nodes, so that they may go about gracefully upgrading themselves once they
 * are done with servicing any of the tests that are already running. This
 * information is captured by {@link NodeAutoUpgradeServlet} which actually
 * undertakes the task of creating a simple properties file and dumps in all of
 * this relayed information.
 * <ul>
 * <li>The URL from where Selenium jar, ChromeDriver binary, IEDriverServer
 * binary can be downloaded.
 * <li>The checksum associated with the each of the artifacts so that it can be
 * cross checked to ascertain validity of the same.
 * </ul>
 * 
 */
public class GridAutoUpgradeDelegateServlet extends RegistryBasedServlet {

    private static final long serialVersionUID = 1L;

    public GridAutoUpgradeDelegateServlet() {
        this(null);
    }

    public GridAutoUpgradeDelegateServlet(Registry registry) {
        super(registry);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);

    }
    
    private String stringify(NameValuePair nvp) {
        return "<input type='hidden' name='" + nvp.getName() + "' value='" + nvp.getValue()+ "'>";
    }

    /**
     * This method constructs the html page that gets the information pertaining
     * to the jars/binaries and their artifact checksums from the user. The same
     * method can also act as the end point that relays this information to each
     * of the nodes as well.
     * 
     * @param request
     *            - {@link HttpServletRequest} that represent the servlet
     *            request
     * @param response
     *            - {@link HttpServletResponse} that represent the servlet
     *            response
     * @throws IOException
     */
    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // No active session ? Redirect the user to the login page.
        if (request.getSession(false) == null) {
            response.sendRedirect(LoginServlet.class.getSimpleName());
            return;
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.SC_OK);
        // idList will have the list of all the nodes that are failed to
        // auto-upgrade
        // (node may be restarting at the time of issuing the command to
        // upgrade).
        String idList = request.getParameter("ids");
        PrintWriter writer = response.getWriter();
        Map<String, String> allParameters = ServletHelper.getParameters(request);
        if (ServletHelper.hasAllParameters(allParameters)) {
            // proceed with relaying the information to each of the nodes if and
            // only if the user has provided all
            // information for performing the upgrade.

            ArtifactDetails artifacts = new ArtifactDetails(allParameters);
            List<String> pendingProxy = new ArrayList<String>();
            if (idList == null) {
                // there were no nodes that failed to auto upgrade
                for (RemoteProxy eachProxy : this.getRegistry().getAllProxies()) {
                    SeLionRemoteProxy proxy = (SeLionRemoteProxy) eachProxy;
                    if (!proxy.release(artifacts)) {
                        pendingProxy.add(proxy.getId());
                    }
                }
            } else {
                // hmm.. there were one or more nodes that didn't go through
                // with the upgrade (maybe coz they
                // were processing some tests).
                for (String eachId : idList.split(",")) {
                    if (!eachId.trim().isEmpty()) {
                        SeLionRemoteProxy proxy = (SeLionRemoteProxy) getRegistry().getProxyById(eachId.trim());
                        if ((proxy != null) && (!proxy.release(artifacts))) {
                            pendingProxy.add(proxy.getId());
                        }
                    }
                }
            }

            if (pendingProxy.size() > 0) {
                writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
                writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
                writer.write("<body id='main_body'>");
                writer.write("<form id='myForm' name='myForm' class='appnitro' method='post' onsubmit='validateForm()' action='"
                        + GridAutoUpgradeDelegateServlet.class.getSimpleName() + "' >");
                String ids = "";
                for (String temp : pendingProxy) {
                    ids = ids + temp + ",";
                }
                writer.write("Following nodes are not auto upgraded: " + ids.substring(0, ids.length() - 2));
                writer.write("<br>Click Submit button to retry.");
                writer.write("<input type='hidden' name='ids' value='" + ids + "'>");
                
                Map<String, URLChecksumEntity> artifactDetails = artifacts.getArtifactDetailsAsMap();
                for (URLChecksumEntity eachArtifact : artifactDetails.values()) {
                    writer.write(stringify(eachArtifact.getChecksum()));
                    writer.write(stringify(eachArtifact.getUrl()));
                }
                writer.write("<input id='saveForm' class='button_text' type='submit' name='submit' value='Submit' />");
                writer.write("</form>");
                writer.write("</body>");
                writer.write("</html>");
            } else {
                ServletHelper.displayMessageOnRedirect(writer, "Auto Upgrade was successful.");
            }
        } else {
            /*
             * Auto Upgrade form will be displayed. This the default page for
             * GridAutoUpgradeDelegateServlet
             */
            showDefaultPage(writer);

        }
    }
    
    private void showDefaultPage(PrintWriter writer) throws IOException {
        writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        writer.write("<head>");
        writer.write("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
        writer.write("<title>SeLion Grid Marimba</title>");
        writer.write("<link rel='stylesheet' type='text/css' href='/grid/resources/form/view.css' media='all' >");
        writer.write("<script type='text/javascript' src='/grid/resources/form/view.js'></script>");
        writer.write("</head>");
        writer.write("<body id='main_body'>");
        writer.write("<script>function validateForm(){if(document.getElementById('selenium_url').value==null||document.getElementById('selenium_url').value==''||document.getElementById('selenium_checksum').value==null||document.getElementById('selenium_checksum').value==''||document.getElementById('ie_url').value==null||document.getElementById('ie_url').value==''||document.getElementById('ie_checksum').value==null||document.getElementById('ie_checksum').value==''||document.getElementById('chrome_url').value==null||document.getElementById('chrome_url').value==''||document.getElementById('chrome_checksum').value==null||document.getElementById('chrome_checksum').value==''){alert('Fields cannot be empty: All Fields are required');return false;}");
        writer.write("var regExpr = '^[A-Za-z]+://[A-Za-z0-9-_]+\\\\.[A-Za-z0-9-_%&\\?\\/.=]+$\';");
        StringBuffer urlPropertyNames = new StringBuffer();
        for(PropsKeys eachKey : PropsKeys.values()){
            String currentKey = eachKey.getKey();
            if(currentKey.endsWith("url")){
                urlPropertyNames.append("'").append(currentKey).append("',");
            }
        }
        writer.write("var urlArray = [");
        writer.write(urlPropertyNames.toString().substring(0, urlPropertyNames.length()-1));
        writer.write("];");
        writer.write("for(i=0; i<urlArray.length ;i++){");
        writer.write("var currentField = urlArray[i];");
        writer.write("var inputUrl = document.getElementById(currentField).value;");
        writer.write("var matchedUrl = inputUrl.match(regExpr);");
        writer.write("if(matchedUrl === null){");
        writer.write("alert(currentField.toUpperCase()+' is invalid. Hint:URL must start with http or https');");
        writer.write("document.getElementById(currentField).focus();");
        writer.write("return false;}}return true;}");
        writer.write("</script>");
        writer.write("<img id='top' src='/grid/resources/form/top.png' alt='' >");
        writer.write("<div id='form_container'>");
        writer.write("<h1>");
        writer.write("<a>SeLion Grid Marimba</a>");
        writer.write("</h1>");
        writer.write("<form id='myForm' name='myForm' class='appnitro' method='post' onsubmit='return validateForm()' action='"
                + GridAutoUpgradeDelegateServlet.class.getSimpleName() + "' >");
        writer.write("<div class='form_description'>");
        writer.write("<h2>SeLion Grid Marimba</h2>");
        writer.write("<p>This is used to upgrade our infrastructure</p>");
        writer.write("</div>");
        writer.write("<ul>");

        int labelIndex = 1;
        writer.write("<h3 align='center'>Enter Details below to download the artifacts</h3>");
        for (PropsKeys eachKeys : PropsKeys.values()) {
            String currentName = eachKeys.getKey();
            if (labelIndex % 2 != 0) {
                writer.write("<li class='section_break'></li>");
            }
            writer.write("<li id='li_" + labelIndex + "'><label class='description' for='" + currentName + "'>"
                    + eachKeys.getLabelText() + " </label>");
            writer.write("<div>");
            writer.write("<input id='" + currentName + "' name='" + currentName+"'");
            writer.write(" class='element text medium' type='text' maxlength='255' value='' />");
            writer.write("</div></li>");
            labelIndex++;
        }
        writer.write("<li class='buttons'><input type='hidden' name='form_id' value='710528' />");
        writer.write("<input id='saveForm' class='button_text' type='submit' name='submit' value='Submit' /></li>");
        writer.write("</ul>");
        writer.write("</form>");
        ServletHelper.displayFooter(writer);
        writer.write("</body>");
        writer.write("</html>");
    }
}
