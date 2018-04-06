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

(function main() {
  "use strict";

  // render the node info
  $(document).ready(function () {

    //iterate over each node and create node with checkbox
    $.each(nodeJson, function (index, node) {
      var id = node.configuration.remoteHost;
      var displayId = node.isShuttingDown ? id + ' (restart scheduled)' : id;
      $('.description').after($('<label>').addClass('choice').append(displayId)).after($('<input/>')
        .addClass('element checkbox').attr('type', 'checkbox').val(id).attr('name', 'nodes'));
    });

    if (nodeJson.length) {
      $('li').after($('<li>').addClass('buttons')
        .append($('<input/>').attr('type', 'hidden').attr('name', 'form_id').val('restart_nodes'))
        .append($('<input/>').attr('type', 'submit').attr('name', 'submit').val('Restart').attr('id', 'saveForm'))
        .append($('<input/>').attr('type', 'submit').attr('name', 'submit').val('Force Restart').attr('id', 'saveForm')));
    } else {
      $('li').after($('<div>').attr('style', 'padding-top: 20px').append($('<label>').addClass('choice')
        .append('No nodes are available to restart')));
    }

  });

}());
