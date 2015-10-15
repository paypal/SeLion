/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

import com.paypal.selion.utils.ConfigParser;

/**
 * Nodes servlets which implement this interface validate the presence of a the parameter {@link #TOKEN_PARAMETER}
 * against the {@link #CONFIGURED_TOKEN_VALUE} before proceeding with any HTTP POST operation. Failure to validate
 * should result in an HTTP status code which is not 200 OK.
 */
interface InsecureHttpPostAuthChallenge {
    String TOKEN_PARAMETER = "token";
    String DEFAULT_TOKEN_VALUE = "authorized";
    String CONFIGURED_TOKEN_VALUE = ConfigParser.parse().getString("nodeAuthToken", DEFAULT_TOKEN_VALUE);
}
