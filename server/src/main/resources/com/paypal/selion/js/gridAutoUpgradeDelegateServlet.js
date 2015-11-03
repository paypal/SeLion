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

(function main($, document, alert) {
  "use strict";

  var name = 'name',
    validKeys = [name, 'roles'],
    validPlatforms = ['any', 'windows', 'linux', 'mac'],
    validRoles = ['node', 'hub', 'standalone', 'sauce', 'ios-driver', 'selendroid'],
    // The indexOf method of Array objects method is not supported in some browsers (IE versions 8 and below). The below
    // implementation falls back to the original if there is one, or defines one if the method is not existant.
    indexOf = function (needle) {
      if (typeof Array.prototype.indexOf === 'function') {
        indexOf = Array.prototype.indexOf;
      } else {
        indexOf = function (needle) {
          var i = -1, index = -1;
          for (i = 0; i < this.length; i += 1) {
            if (this[i] === needle) {
              index = i;
              break;
            }
          }
          return index;
        };
      }
      return indexOf.call(this, needle);
    };

  function isPlatforms(key) {
    return indexOf.call(validPlatforms, key) !== -1;
  }

  function isRoles(key) {
    return indexOf.call(validKeys, key) !== -1 && key === 'roles';
  }

  function isValidPlatform(platform) {
    var regExpr = '^(http|https){1}://[A-Za-z0-9-_]+\\.[A-Za-z0-9-_%%&\?\/.=]+$';
    if (!platform.hasOwnProperty('url')) {
      alert('There is no "url" attribute in the below JSON element: ' + JSON.stringify(platform, undefined, 2));
      return false;
    }
    var url = platform.url;
    if (url.match(regExpr) === null) {
      alert(url + ' is an unsupported or invalid URL. Hint: URL must start with http or https');
      return false;
    }
    if (!platform.hasOwnProperty('checksum')) {
      alert('There is no "checksum" attribute in the below JSON element: ' + JSON.stringify(platform, undefined, 2));
      return false;
    }
    return true;
  }

  function isInValidRole(roles) {
    var i;
    for (i = 0; i < roles.length; i += 1) {
      if (indexOf.call(validRoles, roles[i]) === -1) {
        return roles[i];
      }
    }
  }

  function validateForm() {
    var json = document.getElementById('downloadJSON').value,
      downloads,
      isSuccess = true;

    try {
      downloads = $.parseJSON(json);
    } catch (err) {
      alert('Invalid JSON ' + err);
      return false;
    }

    $.each(downloads, function (ignore, artifact) {
      if (!artifact.hasOwnProperty(name)) {
        alert('There is no "name" attribute in the below JSON element: ' + JSON.stringify(artifact, undefined, 2));
        isSuccess = false;
        return isSuccess;
      }
      $.each(artifact, function (key, attr) {
        if (indexOf.call(validKeys, key) === -1 && indexOf.call(validPlatforms, key) === -1) {
          alert('Attribute "' + key + '" is invalid for element ' + artifact.name
            + '. Valid values: ' + validKeys.valueOf() + ',' + validPlatforms.valueOf());
          isSuccess = false;
          return isSuccess;
        }
        if (isPlatforms(key) && !isValidPlatform(attr)) {
          isSuccess = false;
          return isSuccess;
        }
        if (isRoles(key)) {
          var inValidRole = isInValidRole(attr);
          if (inValidRole) {
            alert('Role "' + inValidRole + '" is invalid for element ' + artifact.name
              + '. Valid roles: ' + validRoles.valueOf());
            isSuccess = false;
            return isSuccess;
          }
        }
        if (!isSuccess) {
          return isSuccess;
        }
      });
    });
    return isSuccess;
  }

  $(document).ready(function () {
    $('.javascript-enabled').removeAttr('class');

    $('#upgrade_form').submit(function () {
      return validateForm();
    });
  });

}($, document, alert));
