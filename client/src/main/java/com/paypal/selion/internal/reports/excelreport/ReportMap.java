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

package com.paypal.selion.internal.reports.excelreport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Generates a map of report to be generated. <br>
 * Maps Report sheet name to list of reports on the sheet to the data it contains </br> Each object represents one
 * sheet.
 * 
 * @param <V>
 */
@SuppressWarnings(value = { "unchecked" })
public class ReportMap<V> {
    private static SimpleLogger logger = SeLionLogger.getLogger();

    private String sReportSheetName;
    private Map<String, List<V>> reportData;
    private List<BaseReport<V>> generatedReport;

    public ReportMap(String sSheetName, Map<String, List<V>> mpRep, int iTypeOfReport) {

        this.sReportSheetName = sSheetName;
        reportData = mpRep;

        this.generatedReport = constructReport(iTypeOfReport);
    }

    public ReportMap(String sSheetName, List<BaseReport<V>> lsReports) {

        this.sReportSheetName = sSheetName;
        this.generatedReport = lsReports;

    }

    public String getName() {
        return sReportSheetName;
    }

    public void setName(String sName) {
        this.sReportSheetName = sName;
    }

    public Map<String, List<V>> getReportData() {
        return reportData;
    }

    public void setReportData(Map<String, List<V>> reportData) {
        this.reportData = reportData;
    }

    public List<BaseReport<V>> getGeneratedReport() {
        return generatedReport;
    }

    public void setGeneratedReport(List<BaseReport<V>> generatedReport) {
        this.generatedReport = generatedReport;
    }

    public void addToGeneratedReport(List<BaseReport<V>> lstCustomReport) {
        this.generatedReport.addAll(lstCustomReport);
    }

    private List<BaseReport<V>> constructReport(int iTypeOfReport) {
        logger.entering(iTypeOfReport);
        List<BaseReport<V>> lb = new ArrayList<BaseReport<V>>();
        @SuppressWarnings("rawtypes")
        BaseReport b;
        for (String indReport : this.reportData.keySet()) {
            if (iTypeOfReport == 0) {
                b = new SummaryReport(indReport);
            } else if (1 == iTypeOfReport) {
                b = new DetailsReport(indReport);
            } else {
                b = new TestOutputReport(indReport);
            }

            b.setLstEntities(this.reportData.get(indReport));
            lb.add(b);
        }
        logger.exiting(lb);
        return lb;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sReportSheetName == null) ? 0 : sReportSheetName.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ReportMap)) {
            return false;
        }
        ReportMap<?> other = (ReportMap<?>) obj;
        if (sReportSheetName == null) {
            if (other.sReportSheetName != null) {
                return false;
            }
        } else if (!sReportSheetName.equals(other.sReportSheetName)) {
            return false;
        }
        return true;
    }

}
