/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

(function main($, document, window, alert) {
  "use strict";

  function isValidField(field, name) {
    var x = $(field).val();
    if (x === "" || x === undefined) {
      alert(name + " must be filled out");
      return false;
    }
    return true;
  }

  function validateForm() {
    if (!(isValidField('#sauceURL', 'Sauce URL') && isValidField('#username', 'Username') &&
        isValidField('#accessKey', 'Access Key'))) {
      return false;
    }
    return true;
  }

  function getValue(field, fallback) {
    if (field) {
      return field;
    }
    return fallback;
  }

  function applyValues() {
    var decoded = window.atob(getValue(sauceConfigJson.authenticationKey,
      window.btoa('sauce-username:sauce-api-key'))).split(':');
    $('#username').attr('value', decoded[0]);
    $('#accessKey').attr('value', decoded[1]);
    $('#sauceURL').attr('value', getValue(sauceConfigJson.sauceURL, 'https://saucelabs.com/rest/v1'));
    $('#tunnelIdentifier').attr('value', getValue(sauceConfigJson.tunnelIdentifier));
    $('#parentTunnel').attr('value', getValue(sauceConfigJson.parentTunnel));
    $('#retry').attr('value', getValue(sauceConfigJson.sauceRetries, 3));
    $('#timeout').attr('value', getValue(sauceConfigJson.sauceTimeout, 10000));
    if (getValue(sauceConfigJson.requireUserCredentials, false) === true) {
      $('#requireUserCredentials').attr('checked', true);
    }
  }

  // init the form
  $(document).ready(function () {
    //show the javascript-enabled-div 'main'
    $('.javascript-enabled').removeAttr('class');
    //apply the values from the current config
    applyValues();
    //wire the form's submit event to the form validator
    $('#sauceGridConfigForm').submit(function () {
      return validateForm();
    });
  });

}($, document, window, alert));
