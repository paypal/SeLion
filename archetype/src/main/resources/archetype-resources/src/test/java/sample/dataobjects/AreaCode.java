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
 * in the yaml files located @ src/test/resources/testdata/.
 */
public class AreaCode {

    private String areaCode;

    public AreaCode() {
    }

    public AreaCode(String areaCode) {
        this.setAreaCode(areaCode);
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String toString () {
        final StringBuilder sb = new StringBuilder("AreaCode{");
        sb.append("areaCode='").append(areaCode).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
