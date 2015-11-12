/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 PayPal                                                                                     |
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

$(document).ready(function () {

  "use strict";
  var iframe = $("iframe#currentFrameId");

  // logic to show and add the tabs.
  $(".tab").click(function () {
    $(this).parent().children().removeClass("active");
    $(this).addClass("active");
    var tabId = $(this).attr("id");
    $("div.tabContentContainer").hide();
    $("div#container_" + tabId).show();
  });

  // by default load the first visible one.
  $(".tab:first").click();

  // when a clickable item is clicked, propagate that click to the element with the same id on the
  // page ( the same method could be in several tabs ) and to the server.
  $(".clickable").click(function () {
    var contentId = $(this).attr("content_id");

    if ("NI" === contentId) {
      return;
    }

    $("td.first").each(function () {
      if ($(this).attr("content_id") === contentId) {
        $(this).toggleClass("clickedOnce");
      }
    });

    // display the result in the iframe at the bottom of the page
    iframe.attr("src", contentId + ".html");
  });
});