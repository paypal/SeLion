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

package com.paypal.selion.node.servlets;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.utils.FileBackedStringBuffer;

/**
 * This is simple servlet which basically display the logs of specified node connected to the grid. This servlet would
 * have to be injected into the Node. This class will get logs files from Logs folder in the Current directory of the
 * node machine.
 * 
 */
public class LogServlet extends HttpServlet {

    /**
     * This class filter the .log files from the current directory
     */
    public class LogFilesFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith(PREFIX) && name.endsWith(EXTENSION);
        }

    }

    private static final long serialVersionUID = -445566L;
    private File logsDirectory;

    private static final String PREFIX = "selion-grid-node";
    private static final String EXTENSION = ".log";

    /**
     * This method helps to display More log information of the node machine.
     * 
     * @param fileName
     *            - It is log file name available in node machine current directory Logs folder, it is used to identify
     *            the current file to display in the web page.
     * @param url
     *            - It is node machine url (ex: http://10.232.88.10:5555)
     * @return String vlaue to add Form in html page
     * @throws IOException
     */
    private String appendMoreLogsLink(final String fileName, String url) throws IOException {
        FileBackedStringBuffer buffer = new FileBackedStringBuffer();
        int index = retrieveIndexValueFromFileName(fileName);
        index++;
        File logFileName = retrieveFileFromLogsFolder(Integer.toString(index));
        if (logFileName == null) {
            return "";
        }

        buffer.append("<form name ='myform' action=").append(url).append(" method= 'post'>");
        buffer.append("<input type='hidden'").append(" name ='fileName'").append(" value ='")
                .append(logFileName.getName()).append("'>");
        buffer.append("<a href= 'javascript: submitform();' > More Logs </a>");
        buffer.append("</form>");
        return buffer.toString();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String fileName = request.getParameter("fileName");
        process(request, response, fileName);

    }

    private void dumpStringToStream(FileBackedStringBuffer buffer, ServletOutputStream outputStream) throws IOException {
        InputStream in = new ByteArrayInputStream(buffer.toString().getBytes("UTF-8"));
        try {
            ByteStreams.copy(in, outputStream);
        } finally {
            in.close();
            outputStream.flush();
        }

    }

    /**
     * This method get the Logs file directory
     * 
     * @return A {@link File} that represents the location where the logs can be found.
     */
    private File getLogsDirectory() {
        if (logsDirectory != null) {
            return logsDirectory;
        }
        logsDirectory = new File(SeLionGridConstants.LOGS_DIR);

        if (!logsDirectory.exists()) {
            logsDirectory.mkdirs();
        }

        return logsDirectory;
    }

    /**
     * Check whether the Logs directory exist or not in the current directory.
     * 
     * @return a {@link boolean}
     */
    private boolean isLogsDirectoryEmpty() {
        return (getLogsDirectory().listFiles(new LogFilesFilter()).length == 0);

    }

    /**
     * This method display the log file content
     * 
     * @param request
     *            - HttpServletRequest
     * @param response
     *            - HttpServletResponse
     * @param fileName
     *            - To display the log file content in the web page.
     * @throws IOException
     */
    protected void process(HttpServletRequest request, HttpServletResponse response, String fileName)
            throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        FileBackedStringBuffer buffer = new FileBackedStringBuffer();

        buffer.append("<html><head><title>");
        buffer.append(request.getRemoteHost());
        buffer.append("</title><script type=text/javascript>");
        buffer.append("function submitform() { document.myform.submit(); } </script>");
        buffer.append("</head><body><H1>View Logs on - ");
        buffer.append(request.getRemoteHost()).append("</H1>");

        if (isLogsDirectoryEmpty()) {
            buffer.append("<br>No Logs available.</br></body></html>");
            dumpStringToStream(buffer, response.getOutputStream());
            return;
        }
        buffer.append(appendMoreLogsLink(fileName, request.getRequestURL().toString()));
        buffer.append(renderLogFileContents(fileName));
        buffer.append("</body></html>");
        dumpStringToStream(buffer, response.getOutputStream());
    }

    /**
     * This method read the content of the file and append into FileBackedStringBuffer
     * 
     * @param fileName
     *            - Read the content of the log file
     * @return a {@link buffer} buffer string to display in web page
     * @throws IOException
     */
    private String renderLogFileContents(String fileName) throws IOException {
        FileBackedStringBuffer buffer = new FileBackedStringBuffer();
        int index = retrieveIndexValueFromFileName(fileName);
        int runningIndex = 0;
        File eachFile = null;
        while ((eachFile = retrieveFileFromLogsFolder(Integer.toString(runningIndex))) != null
                && (runningIndex <= index)) {
            BufferedReader reader = new BufferedReader(new FileReader(eachFile));
            String line = "";
            buffer.append("<pre>");
            while ((line = reader.readLine()) != null) {
                buffer.append("<br>").append(line).append("</br>");
            }
            buffer.append("</pre>");
            reader.close();
            runningIndex++;
        }
        return buffer.toString();
    }

    /**
     * Get the log files from the directory
     * 
     * @param index
     * @return A {@link File} that represent the file to read from current directory.
     */
    private File retrieveFileFromLogsFolder(String index) {
        File[] logFiles = getLogsDirectory().listFiles(new LogFilesFilter());
        File fileToReturn = null;
        for (File eachLogFile : logFiles) {
            String fileName = eachLogFile.getName().split("\\Q.\\E")[0];
            if (fileName.endsWith(index)) {
                fileToReturn = eachLogFile;
                break;
            }
        }
        return fileToReturn;
    }

    /**
     * This method return index of the file name (example selion-grid-1.log)
     * 
     * @param fileName
     *            -log file name
     * @return an {@link index} index value of the file
     */
    private int retrieveIndexValueFromFileName(String fileName) {
        int index = 0;
        if (fileName != null && !fileName.trim().isEmpty()) {
            index = Integer.parseInt(fileName.substring(fileName.lastIndexOf('-') + 1,
                    fileName.length() - EXTENSION.length()));
        }
        return index;
    }
}