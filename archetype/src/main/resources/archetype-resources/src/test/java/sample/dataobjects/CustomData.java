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

package ${package}.sample.dataobjects;

/**
 * This is a simple POJO (Plain Old Java Object) that represents the information that is stored
 * in the excel workbook located @ src/test/resources/testdata/MyDataFile.xls.
 * The class name is intentionally named to match with the worksheet "CustomData" in the above
 * mentioned spreadsheet, because that is how {@link com.paypal.selion.platform.dataprovider.SimpleExcelDataProvider}
 * will understand as to what sheet to read data from.
 */
public class CustomData {
    private String employeeName;
    private Country country;

    public String getEmployeeName () {
        return employeeName;
    }

    public void setEmployeeName (String employeeName) {
        this.employeeName = employeeName;
    }

    public Country getCountry () {
        return country;
    }

    public void setCountry (Country country) {
        this.country = country;
    }

    @Override
    public String toString () {
        final StringBuilder sb = new StringBuilder("CustomData{");
        sb.append("employeeName='").append(employeeName).append('\'');
        sb.append(", country=").append(country);
        sb.append('}');
        return sb.toString();
    }
}
