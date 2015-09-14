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


/**
 * Represents summary counts like total passed, failed, skipped</br>
 */

public class SummarizedData implements Comparable<SummarizedData> {

    public static final int PASS = 1;
    public static final int FAIL = 2;
    public static final int SKIP = 3;

    private String sName;
    private int iPassedCount, iFailedCount, iSkippedCount, iTotal;
    private long lRuntime;

    /**
     * @return the sName
     */
    public String getsName() {
        return sName;
    }

    /**
     * @param sName
     *            the sName to set
     */
    public void setsName(String sName) {
        this.sName = sName;
    }

    /**
     * @return the iPassedCount
     */
    public int getiPassedCount() {
        return iPassedCount;
    }

    /**
     * @param iPassedCount
     *            the iPassedCount to set
     */
    public void setiPassedCount(int iPassedCount) {
        this.iPassedCount = iPassedCount;
    }

    /**
     * @return the iFailedCount
     */
    public int getiFailedCount() {
        return iFailedCount;
    }

    /**
     * @param iFailedCount
     *            the iFailedCount to set
     */
    public void setiFailedCount(int iFailedCount) {
        this.iFailedCount = iFailedCount;
    }

    /**
     * @return the iSkippedCount
     */
    public int getiSkippedCount() {
        return iSkippedCount;
    }

    /**
     * @param iSkippedCount
     *            the iSkippedCount to set
     */
    public void setiSkippedCount(int iSkippedCount) {
        this.iSkippedCount = iSkippedCount;
    }

    /**
     * @return the iTotal
     */
    public int getiTotal() {
        return iTotal;
    }

    /**
     * @param iTotal
     *            the iTotal to set
     */
    public void setiTotal(int iTotal) {
        this.iTotal = iTotal;
    }

    public void incrementiTotal() {
        this.iTotal++;
    }

    public void incrementiTotal(int iAddToTotal) {
        this.setiTotal(iAddToTotal + this.getiTotal());
    }

    /**
     * @return the iRuntime
     */
    public long getlRuntime() {
        return lRuntime;
    }

    public void incrementDuration(long testRunTime) {
        this.setlRuntime(this.getlRuntime() + testRunTime);
    }

    /**
     * @param iRuntime
     *            - the iRuntime to set
     */
    public void setlRuntime(long iRuntime) {
        this.lRuntime = iRuntime;
    }

    public void incrementCount(int toIncrement) {
        switch (toIncrement) {
        case SummarizedData.PASS:
            this.iPassedCount++;
            break;
        case SummarizedData.FAIL:
            this.iFailedCount++;
            break;
        case SummarizedData.SKIP:
            this.iSkippedCount++;
            break;
        }

    }

    @Override
    public int compareTo(SummarizedData o) {
        return this.getsName().compareTo(o.getsName());
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
        result = prime * result + ((sName == null) ? 0 : sName.hashCode());
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
        if (!(obj instanceof SummarizedData)) {
            return false;
        }
        SummarizedData other = (SummarizedData) obj;
        if (sName == null) {
            if (other.sName != null) {
                return false;
            }
        } else if (!sName.equals(other.sName)) {
            return false;
        }
        return true;
    }

}
