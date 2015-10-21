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

(function main() {
  "use strict";

  // add the slot images or text if the png is not available.
  $(document).on('updateIcons', function renderSlotIcons() {
    $('.icon').each(function (index, element) {
      var browserType = $(element).attr('data-value'),
        proxyIndex = $(element).parent().attr('data-src'),
        // strip the version and replace space with '_'
        icon = browserType.replace(new RegExp(':v(.*)$', 'g'), '').replace(new RegExp(' ', 'g'), '_') + '.png',
        // extract the version, if present
        version = browserType.replace(new RegExp('^(.*):', 'g'), ''),
        iconPath = '/grid/resources/org/openqa/grid/images/' + icon,
        slotUsage = nodeJson[proxyIndex].slotUsage,
        toolTip = JSON.stringify(slotUsage[browserType]).replace(new RegExp('"', 'g'), ''),
        busyState = (function () {
          if (slotUsage[browserType].used > 0) {
            return ' class="busy"';
          }
          return '';
        }());

      version = (version !== browserType) ? version + ':' : '';

      //attempt to load the icon via async http call. use image on success.
      $.get(iconPath, function httpGet(data, status) {
        if (status === "success") {
          $(element).append(version + '<img src="' + iconPath + '" title="' + toolTip + '"' + busyState + '>');
        }
      }).fail(function () {
        $(element).append(version + '<a title="' + toolTip + '"' + busyState + '>' + browserType + '</a>');
      });
    });
  });

  // render the node info
  $(document).ready(function renderNodes() {
    //show the javascript-enabled-div 'main'
    $('.javascript-enabled').removeAttr('class');

    // new parent div for all proxy info
    var proxyDiv = $('<div>');

    // # of nodes connected
    $('<h4>').text(nodeJson.length + ' node(s) connected').appendTo('#content');

    //iterate over each node and create node info container
    $(nodeJson).each(function addNodeInfo(index, node) {
      // new div for proxy info list items
      var proxyInfoDiv = $('<div>').attr('class', 'row'),
        // new left and right columns of proxy info list items
        proxyInfoLeftColumnDiv = $('<div>').attr('class', 'left-cell'),
        proxyInfoRightColumnDiv = $('<div>').attr('class', 'right-cell'),
        // strips the package from the proxy class name and returns the result
        proxyClass = (function () {
          var p = node.configuration.proxy,
            indexOf = p.lastIndexOf('.');
          if (indexOf === -1) {
            return p;
          }
          return p.substr(indexOf + 1, p.length);
        }());

      // add the proxy header row
      function createProxyHeaderDiv() {
        var div = $('<div>'),
          // sets the header css class based on proxy status
          headerCssClass = (function () {
            var cssClass = 'header';
            if (node.status === 'offline') {
              cssClass += ' offline';
            }
            if (node.isShuttingDown) {
              cssClass += ' shuttingdown';
            }
            return cssClass;
          }());

        // adds the node version and os, if available
        function createVersionAndOSSpan() {
          var span = $('<span>').attr('class', 'version-info');
          if (node.status === 'online') {
            $(span).text('v' + node.version);
          }
          if (node.os !== 'not available') {
            $(span).text($(span).text() + ' on ' + node.os);
          }
          return span;
        }

        // add the remote host / slot id info
        function createRemoteHostSpan() {
          var span = $('<span>');
          if (node.logsLocation !== 'not available') {
            $('<a>')
              .attr('target', '_blank')
              .attr('href', node.logsLocation)
              .text(node.configuration.remoteHost)
              .appendTo(span);
          } else {
            $(span).text(node.configuration.remoteHost);
          }
          return span;
        }

        return $(div)
          .attr('class', headerCssClass)
          .append(createRemoteHostSpan())
          .append(createVersionAndOSSpan());
      }

      // add the slot info
      function addSlotInfo(slots) {
        var slotsDiv = $('<div>')
          .attr('class', 'icon-row')
          .attr('data-src', index);
        if (typeof slots !== 'object') {
          return;
        }
        $.each(slots, function addli(k, v) {
          $(slotsDiv).append($('<li>').attr('class', 'icon').attr('data-value', k));
        });
        return slotsDiv;
      }

      // add a <li> item to display
      function createInfoLi(label, item, valueType, isSelionItem) {
        // sanitize known values which mean the proxy did not provide any data.
        function supported(something) {
          if (proxyClass === 'SeLionRemoteProxy') {
            return something;
          }
          if (something && (something !== -1)) {
            return something;
          }
          return 'not supported';
        }

        if (isSelionItem === undefined) {
          isSelionItem = false;
        }
        if (valueType === undefined) {
          valueType = '';
        }
        if (isSelionItem && (supported(item) === 'not supported')) {
          return '';
        }
        return $('<li><b>' + label + '</b>: ' + item + ' ' + valueType + ' </li>');
      }

      // add the header row
      createProxyHeaderDiv().appendTo(proxyDiv);

      // add left column list items
      $(proxyInfoLeftColumnDiv)
        .append(createInfoLi('Proxy', proxyClass))
        .append(createInfoLi('Busy', node.isBusy))
        .append(createInfoLi('Uptime', node.uptimeInMinutes, 'min', true))
        .append(createInfoLi('Resource usage', node.percentResourceUsage, '&#37;', false))
        .append(createInfoLi('Idle timeout', node.configuration.timeout, 'ms', false))
        .append(createInfoLi('Recycyle wait timeout', node.configuration.nodeRecycleThreadWaitTimeout, 'sec', true))
        .append(createInfoLi('Max new sessions allowed', node.configuration.uniqueSessionCount, '', true));
      $(proxyInfoLeftColumnDiv).appendTo(proxyInfoDiv);

      // add right column list items
      $(proxyInfoRightColumnDiv)
        .append(createInfoLi('Total slots used', node.totalUsed))
        .append(createInfoLi('Max concurrent slots', node.configuration.maxSession))
        .append(createInfoLi('Total sessions started', node.totalSessionsStarted, '', true))
        .append(createInfoLi('Total sessions complete', node.totalSessionsComplete, '', true))
        .append(createInfoLi('Register cycle', node.configuration.registerCycle, 'ms', false))
        .append(createInfoLi('Cleanup cycle', node.configuration.cleanUpCycle, 'ms', false));
      $(proxyInfoRightColumnDiv).appendTo(proxyInfoDiv);

      //slot usage
      $(proxyInfoDiv).append('<br>').append(addSlotInfo(node.slotUsage));

      //proxy info row
      $(proxyDiv).append(proxyInfoDiv);

      //add a line break content
      $(proxyDiv).append('<br>');
    });

    //add the proxy div to content
    $(proxyDiv).appendTo('#content');
    $(this).trigger('updateIcons');
  });

}());