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

import java.util.List;

import org.testng.ISuite;

/**
 * This interface represents Simple View in HtmlReport.
 * 
 */
interface View {

    /**
     * Associates the list of {@link ISuite} to the current view.
     * 
     * @param suites
     */
    void setData(List<ISuite> suites);

    /**
     * @return - A String that represents the content which is part of the current view.
     */
    String getContent();

    /**
     * @return - The id that is associated with the current view.
     */
    String getId();

    /**
     * @return - The title associated with the current view.
     */
    String getTitle();
}
