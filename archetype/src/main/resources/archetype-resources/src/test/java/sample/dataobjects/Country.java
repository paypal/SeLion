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
 * This is a simple enum that represents a bunch of countries.
 */
public enum Country {
  UNITED_STATES("us"),
  UNITED_KINGDOM("gb"),
  INDIA("in");

  private Country (String code) {
    this.code = code;
  }

  private String code;

  public static Country getCountry(String code){
    for(Country each : Country.values()){
      if (each.code.equalsIgnoreCase(code)){
        return each;
      }
    }
    return null;
  }
}
