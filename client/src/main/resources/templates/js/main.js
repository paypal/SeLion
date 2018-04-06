/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

function displayImage(src, title) {
  "use strict";
  $(".modal-image .modal-body").find("img").attr("src",src);
  $(".modal-image .modal-header .modal-title").text(title);
  $(".modal-image").modal();
}

function displaySource(src, title) {
  "use strict";
  $(".modal-source .modal-body").find("iframe").attr("src", src);
  $(".modal-source .modal-header .modal-title").text(title);
  $(".modal-source").modal();
}
  
/* Main JavaScript for RuntimeReporter */
(function main() {
  "use strict";
  var reports = window.reports;
  var tree = Object.create(null);
  var treeFilter = reports.testMethods, configTreeFilter = reports.configurationMethods;
  var autoHideTree = true;
  var pageSize = 10, configPageSize = 10;
  var index, configIndex;
  var total, configTotal;
  var noOfPage, configNoOfPage;
  var sortedRecords, sortingDirection = {suite: "1", test: "1", packageInfo: "1", className: "1", methodName: "1",
    parameters : "1" };
  var configSortedRecords, configSortingDirection = {suite: "1", test: "1", packageInfo: "1", className: "1",
    methodName: "1", type : "1" };
  var filterSearch, configFilterSearch;
  var autoRefresh;
  var timeRefresh = 300 * 1000;//300 seconds refresh interval
  var localConfigMap = Object.create(null);
  var reportMetaData = reports.reporterMetadata;
  var DISPLAY_LABEL = "displayLabel";

  tree.AllSuites = Object.create(null);
  var root = tree.AllSuites;
  $.each(reports.testMethods, function(ignore, item) {
    var suiteMap = getMap(root, item.suite);
    var testMap = getMap(suiteMap, item.test);
    var packageMap = getMap(testMap, item.packageInfo);
    getMap(packageMap, item.className);
  });

  var uniqueId = 0;
  $.each(root, function(key, value) {
    var id = "#AllSuites";
    uniqueId = uniqueId + 1;
    var suiteId = uniqueId;
    generateInnerTree(id, key, "suite", suiteId);
    $.each(value, function(key1, value1) {
      var id1 = "#" + key + "suite" + suiteId;
      uniqueId = uniqueId + 1;
      var testId = uniqueId;
      generateInnerTree(id1, key1, "test", testId);
      $.each(value1, function(key2, value2) {
        var id2 = "#" + key1 + "test" + testId;
        uniqueId = uniqueId + 1;
        var packageId = uniqueId;
        generateInnerTree(id2, key2, "packageInfo", packageId);
        $.each(value2, function(key3) {
          var id3 = "#" + key2 + "packageInfo" + packageId;
          uniqueId = uniqueId + 1;
          var classId = uniqueId;
          generateInnerTree(id3, key3, "className", classId);
        });
      });
    });
  });

  $(".btn-tree-autohide").click(function() {
    var span = $(".btn-tree-autohide").find("span");
    if ($(span).hasClass("label-success")) {
      $(span).removeClass("label-success").addClass("label-danger").html("Off");
      autoHideTree = false;
    } else {
      $(span).removeClass("label-danger").addClass("label-success").html("On");
      autoHideTree = true;
    }
  });
  
  $("#tree").jstree({
    "core" : { "themes" : { "variant" : "medium" } },
    "checkbox" : { "keep_selected_style" : true }
  });

  $("#tree").on("changed.jstree", function (ignore, data) {
    var searchType = data.node.li_attr["data-type"];
    var searchMap = Object.create(null);

    if (searchType === "all") {
      treeFilter = reports.testMethods;
      configTreeFilter = reports.configurationMethods;
    } else {
      searchMap[searchType] = data.node.text;
      var k = data.node.parents.length - 3, parent;
      while (k >= 0) {
        parent = $("#" + data.node.parents[k]);
        searchMap[parent.attr("data-type")] = parent.attr("data-value");
        k -= 1;
      }
      treeFilter = filterResults(searchMap, reports.testMethods);
      configTreeFilter = filterResults(searchMap, reports.configurationMethods);
    }
    refreshResults();
    if (autoHideTree) {
      $("#bodycontent").toggleClass("toggled");
    }
  });

  $("#display-tree").click(function(e) {
    e.preventDefault();
    $("#bodycontent").toggleClass("toggled");
    $("#treeview-left").css("height", (window.innerHeight - 85) + "px");
  });

  var dateFormatOptions = {
    year: "numeric", month: "short", day: "numeric",
    hour: "2-digit", minute: "2-digit", second:"2-digit"
  };

  var helpers = {
    // Declare date format helper to get readable name and value.
    formatDateValue : function(val, key) {
      var d = new Date(val);
      switch (key) {
      case "currentDate":
        return d.toLocaleString("en-US", dateFormatOptions);
      default:
        return val;
      }
    },
    formatDisplayName : function(key) {
      var labelToRender = key;
      if (reportMetaData.hasOwnProperty(key)) {
        var configProperty = reportMetaData[key];
        if (configProperty.hasOwnProperty(DISPLAY_LABEL)) {
          labelToRender = configProperty.displayLabel;
        }
      }
      return labelToRender;
    }
  };

  $(document).ready(function() {

    function reloadPage() {
      location.reload();
    }

    //Prepare configuration summary content
    $("#config-popover").popover();
    var configData = $.map(reports.configSummary, function(value, key) {
      return {value: helpers.formatDateValue(value, key), key: helpers.formatDisplayName(key)};});
    $("#config-popover").attr("data-content", $("#configImpl").render({"results" : configData}));

    //Prepare local config summary map
    $.each(reports.localConfigSummary, function(ignore, obj) {
      localConfigMap[obj.test] = $.map(obj, function(value, key) {
        return {value: helpers.formatDateValue(value, key), key: helpers.formatDisplayName(key)};});
    });

    autoRefresh = window.setInterval(reloadPage, timeRefresh);

    refreshResults();

    $(".btn-auto-update").click(function() {
      var span = $(this).find("span");
      if ($(span).hasClass("label-success")) {
        $(span).removeClass("label-success").addClass("label-danger").html("Off");
        if (autoRefresh !== null) {
          window.clearInterval(autoRefresh);
        }
      } else {
        $(span).removeClass("label-danger").addClass("label-success").html("On");
        autoRefresh = window.setInterval(reloadPage, timeRefresh);
      }
    });
  });

  function refreshResults() {
    //Load the test case results
    var passed = 0, failed = 0, skipped = 0, running = 0;
    if (reports.reportSummary.testMethodsSummary.passed) {
      passed = reports.reportSummary.testMethodsSummary.passed;
    }
    if (reports.reportSummary.testMethodsSummary.failed) {
      failed = reports.reportSummary.testMethodsSummary.failed;
    }
    if (reports.reportSummary.testMethodsSummary.skipped) {
      skipped = reports.reportSummary.testMethodsSummary.skipped;
    }
    if (reports.reportSummary.testMethodsSummary.running) {
      running = reports.reportSummary.testMethodsSummary.running;
    }

    total = passed + failed + skipped + running;
    generateSummary("passed", passed, total);
    generateSummary("failed", failed, total);
    generateSummary("skipped", skipped, total);
    generateSummary("running", running, total);

    //Pagination for Test case table
    index = 1;
    configIndex = 1;
    sortedRecords = treeFilter;
    filterSearch = sortedRecords;
    total = filterSearch.length;
    noOfPage = Math.ceil(total/pageSize);

    //Registering the util method with view helpers during refresh
    $.views.helpers({
      setStatusAndTimeAsToolTip: function (status, startTime, endTime) {
        "use strict";
        var eStart = "Unavailable";
        if (status !== "Skipped") {
          eStart = helpers.formatDateValue(startTime, "currentDate");
        }
        var eEnd = "Unavailable";
        if (status === "Passed" || status === "Failed") {
          eEnd = helpers.formatDateValue(endTime, "currentDate");
        }
        return "Status: " + status + "\n" + "Started on: " + eStart + "\n" + "Ended at: " + eEnd;
      },
      getLatestImage: function (logs) {
        "use strict";
        if (!logs) {
          return null;
        }
        var i = logs.length - 1;
        while (i >= 0) {
          if (logs[i].image) {
            return logs[i].image;
          }
          i -= 1;
        }
        return null;
      }
    });

    renderPageCombo("#", ".pageSizeCombo");
    renderSearch("#", "#testCaseSearch", sortedRecords);
    $("#" + "testcase-pagination ul .page-number").remove();
    renderPageNumbers("#", 1, noOfPage);
    renderPagination("#");
    if ($("#testcaseParentBody .fixed-table-pagination").is(":visible")) {
      renderTable("#", filterSearch,index,pageSize);
    } else {
      $("#testcaseBody").html($("#gridImpl").render({"results" : treeFilter}));
      afterRender(treeFilter, "");
    }

    //Pagination for Config Test case table
    configSortedRecords = configTreeFilter;
    configFilterSearch = configSortedRecords;
    configTotal = configFilterSearch.length;
    configNoOfPage = Math.ceil(configTotal/configPageSize);

    renderPageCombo("#config-", ".config-pageSizeCombo");
    renderSearch("#config-", "#config-testCaseSearch", configSortedRecords);
    $("#config-" + "testcase-pagination ul .page-number").remove();
    renderPageNumbers("#config-", 1, configNoOfPage);
    renderPagination("#config-");
    renderTable("#config-", configFilterSearch, configIndex, configPageSize);

    $("#testcase-btn-grid").click(function() {
      $("#testcaseParentBody .fixed-table-pagination").hide();
      $("#testcase-btn-list").removeClass("active");
      $("#testcase-btn-grid").addClass("active");
      $("#testcaseBody").html($("#gridImpl").render({"results" : treeFilter}));
      afterRender(treeFilter, "");
    });

    $("#testcase-btn-list").click(function() {
      $("#testcase-btn-grid").removeClass("active");
      $("#testcase-btn-list").addClass("active");
      renderTable("#", filterSearch,index,pageSize);
      $("#testcaseParentBody .fixed-table-pagination").show();
      afterRender(filterSearch,"");
    });

    enableClickForProgressBar("passed");
    enableClickForProgressBar("failed");
    enableClickForProgressBar("skipped");
    enableClickForProgressBar("running");
    $("#statistics-data").click(function() {
      searchResults("");
    });
  }

  function generateSummary(id, status, total) {
    $("#statistics-progress-" + id).css("width",(status * 100 / total)+ "%");
    if (status > 0) {
        $("#statistics-data-" + id).css("width",(status * 100 / total)+ "%").html(status + " " + id);
    } else {
        $("#statistics-data-" + id).css("width","0%").html("");
    }
  }

  function filterResults(searchMap, results) {
    var count = 0, tmpTreeFilter = [];
    $.each(results, function(ignore, testCase) {
        var searchAll = true;
        $.each(searchMap, function(key, value) {
            if(testCase[key] !== value) {
                searchAll = false;
            }
        });
        if (searchAll) {
            tmpTreeFilter[count] = testCase;
            count = count + 1;
        }
    });
    return tmpTreeFilter;
  }

  function generateInnerTree(id, key, type, uniqueId) {
    var leaf = "";
    if (type === "className") {
        leaf = "data-jstree=\"{\"icon\":\"glyphicon glyphicon-leaf\"}\"";
    }
    //Remove dot and spaces from the key
    var id1 = id.replace(/\./g, "").replace(/\s+/g, "");
    var newId = key.replace(/\./g, "").replace(/\s+/g, "") + type + uniqueId;
    if ($(id1 + " ul").length === 0) {
        $(id1).first().append("<ul><li id=\"" + newId + "\" data-value=\"" + key + "\" data-type=\"" + type + "\" "
          + leaf + ">" + key + "</li></ul>");
    } else {
        $(id1 + " ul").first().append("<li id=\"" + newId + "\" data-value=\"" + key + "\" data-type=\"" + type + "\" "
          + leaf + ">" + key + "</li>");
    }
  }

  function getMap(map, key) {
    if (!map[key]) {
        map[key] = Object.create(null);
    }
    return map[key];
  }

  function renderPageCombo(pageAdditionalId, comboClassName) {
    $(comboClassName + " li a").click(function(){
      var selText = $(this).text();
      $(this).parents(".btn-group").find(".dropdown-toggle").html(selText+" <span class=\"caret\"></span>");

      var mPageSize = parseInt(selText);
      var mNoOfPage;
      var mFilterSearch;

      if(pageAdditionalId === "#") {
        pageSize = mPageSize;
        noOfPage = Math.ceil(total/pageSize);
        mNoOfPage = noOfPage;
        index = 1;
        mFilterSearch = filterSearch;
      } else {
        configPageSize = mPageSize;
        configNoOfPage = Math.ceil(configTotal / configPageSize);
        mNoOfPage = configNoOfPage;
        configIndex = 1;
        mFilterSearch = configFilterSearch;
      }

      $(pageAdditionalId + "testcase-pagination ul .page-number").remove();
      renderPageNumbers(pageAdditionalId, 1, mNoOfPage);
      renderTable(pageAdditionalId, mFilterSearch, 1, mPageSize);
    });
  }

  function renderSorting(pageAdditionalId, tableId, records) {
    $(tableId + " thead tr th").click(function() {
      var col = $(this).attr("data-index");
      if(col !== "nil") {
        var direction = getSortingDirection(pageAdditionalId);
        var asc = direction[col];
        direction[col] = -1 * asc;
        setSortingDirection(pageAdditionalId, direction);
        records.sort(function(a, b){
          return (a[col] === b[col]) ? 0 : ((a[col] > b[col]) ? asc : -1 * asc);
        });
        renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), getIndex(pageAdditionalId),
          getPageSize(pageAdditionalId));

        var arrowDirection = "";
        if(asc === "1") {
          arrowDirection = "dropup";
        }
        $(tableId + " thead tr th[data-index$=" + col + "]").html($(tableId + " thead tr th[data-index$=" + col
          + "]").text() + "<span class=\"order "+ arrowDirection
          + "\"><span class=\"caret\" style=\"margin: 10px 5px;\"></span></span>");
      }
    });

  }

  function renderSearch(pageAdditionalId, searchBoxId, records) {
    $(searchBoxId).keyup(function() {

      var val = $.trim($(this).val()).replace("/" + "/g", " ").toLowerCase();
      var searchArray = [], j = 0;
      var i = 0, testCase;
      while (i < records.length) {
        testCase = records[i];
        if(matchTestCase(testCase, val)) {
          searchArray[j] = testCase;
          j += 1;
        }
        i += 1;
      }

      setFilterSearch(pageAdditionalId, searchArray);
      setSortedRecords(pageAdditionalId, searchArray);
      setIndex(pageAdditionalId, 1);
      setNoOfPage(pageAdditionalId, 
        Math.ceil(getFilterSearch(pageAdditionalId).length / getPageSize(pageAdditionalId)));

      if(searchBoxId === "#testCaseSearch") {
        total = filterSearch.length;
      } else {
        configTotal = configFilterSearch.length;
      }

      $(pageAdditionalId + "testcase-pagination ul .page-number").remove();
      renderPageNumbers(pageAdditionalId, 1, getNoOfPage(pageAdditionalId));
      renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), 1, getPageSize(pageAdditionalId));
    });
  }

  function renderPagination(pageAdditionalId) {
    $(pageAdditionalId + "testcase-pagination ul .page-first").click(function() {
      if(1 !== getIndex(pageAdditionalId)) {
        setIndex(pageAdditionalId, 1);
        $(pageAdditionalId + "testcase-pagination ul .page-number").remove();
        renderPageNumbers(pageAdditionalId, 1, getNoOfPage(pageAdditionalId));
        renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), getIndex(pageAdditionalId),
          getPageSize(pageAdditionalId));
      }
    });

    $(pageAdditionalId + "testcase-pagination ul .page-pre").click(function() {
      if(0 < (getIndex(pageAdditionalId) - getPageSize(pageAdditionalId))) {
        setIndex(pageAdditionalId, (getIndex(pageAdditionalId) - getPageSize(pageAdditionalId)));
        var k = $(pageAdditionalId + "testcase-pagination ul .page-number").first().find("a").text();
        var n = parseInt(k) - 1;
        if(n > 0) {
          $(pageAdditionalId + "testcase-pagination ul .page-number").last().remove();
          $(pageAdditionalId + "testcase-pagination ul .page-pre").after($("<li>").attr("data-index",n)
            .addClass("page-number").append($("<a>").attr("href","#").append(n) ));
        }

        renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), getIndex(pageAdditionalId),
          getPageSize(pageAdditionalId));
      }
    });

    $(pageAdditionalId + "testcase-pagination ul .page-number").click(function() {
      setIndex(pageAdditionalId,((parseInt($(this).attr("data-index")) - 1) * getPageSize(pageAdditionalId)) + 1);
      renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), getIndex(pageAdditionalId),
        getPageSize(pageAdditionalId));
    });

    $(pageAdditionalId + "testcase-pagination ul .page-next").click(function() {
      if(getFilterSearch(pageAdditionalId).length >= (getIndex(pageAdditionalId) + getPageSize(pageAdditionalId))) {
        setIndex(pageAdditionalId, (getIndex(pageAdditionalId) + getPageSize(pageAdditionalId)));
        var k = $(pageAdditionalId + "testcase-pagination ul .page-number").last().find("a").text();
        var n = parseInt(k) + 1;
        if(n <= getNoOfPage(pageAdditionalId)) {
          $(pageAdditionalId + "testcase-pagination ul .page-number").first().remove();
          $(pageAdditionalId + "testcase-pagination ul .page-next").before($("<li>").attr("data-index",n)
            .addClass("page-number").append($("<a>").attr("href","#").append(n) ));
        }
        renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), getIndex(pageAdditionalId),
          getPageSize(pageAdditionalId));
      }
    });

    $(pageAdditionalId + "testcase-pagination ul .page-last").click(function() {
      if(getFilterSearch(pageAdditionalId).length >= (getIndex(pageAdditionalId) + getPageSize(pageAdditionalId))) {
        var divIndex = Math.floor(getFilterSearch(pageAdditionalId).length / getPageSize(pageAdditionalId));
        var modIndex = getFilterSearch(pageAdditionalId).length % getPageSize(pageAdditionalId);
        setIndex(pageAdditionalId, (divIndex * getPageSize(pageAdditionalId)) + 1);
        if(modIndex === 0) {
          setIndex(pageAdditionalId, ((divIndex - 1) * getPageSize(pageAdditionalId)) + 1);
        }

        $(pageAdditionalId + "testcase-pagination ul .page-number").remove();
        var siForPage = (getNoOfPage(pageAdditionalId) - 4);
        if(siForPage < 1) {
          siForPage = 1;
        }
        renderPageNumbers(pageAdditionalId, siForPage, getNoOfPage(pageAdditionalId));

        renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), getIndex(pageAdditionalId),
          getPageSize(pageAdditionalId));
      }
    });
  }

  function renderTable(pageAdditionalId, rtFilterSearch, rtIndex, rtPageSize) {
    var endIndex = rtIndex + rtPageSize - 1;
    var filterResult = extractResults(rtFilterSearch,rtIndex,endIndex);
    $(pageAdditionalId + "testcaseBody").html($(pageAdditionalId + "tableImpl").render({"results" : filterResult}, 
      {"startIndex":rtIndex}, {"count": endIndex}));

    if(pageAdditionalId === "#") {
      afterRender(filterResult, "");
      renderSorting("#", "#paginationTextCaseTable", sortedRecords);
    } else {
      afterRender(filterResult, "-config");
      renderSorting("#config-", "#paginationTextConfigTable", configSortedRecords);
    }

    if(filterSearch.length <= (endIndex - rtIndex)) {
      endIndex = rtFilterSearch.length;
    }

    if(getPageSize(pageAdditionalId) >= rtFilterSearch.length) {
      $(pageAdditionalId + "testcase-page-info").html("Showing all rows");
    } else {
      $(pageAdditionalId + "testcase-page-info").html("Showing " + rtIndex + " to " + endIndex + " of "
        + rtFilterSearch.length + " rows");
    }

    $(pageAdditionalId + "testcase-pagination ul .page-number").click(function() {
      var mIndex = ((parseInt($(this).attr("data-index")) - 1) * rtPageSize) + 1;

      if(pageAdditionalId === "#") {
        index = mIndex;
      } else {
        configIndex = mIndex;
      }
      renderTable(pageAdditionalId, rtFilterSearch,mIndex, rtPageSize);
    });
  }

  function renderPageNumbers(paginationId, startNumber, mNoOfPage) {
    var pageLoaded = 0;
    var i = startNumber;
    while ((i <= mNoOfPage) && (pageLoaded < 5)) {
      $(paginationId + "testcase-pagination ul .page-next").before($("<li>").attr("data-index",i)
        .addClass("page-number").append($("<a>").attr("href","#").append(i) ));
      pageLoaded += 1;
      i += 1;
    }
  }

  function matchTestCase(testCase, val) {
    if(!matchValue(testCase.status, val) || !matchValue(testCase.suite, val)  || !matchValue(testCase.test, val) ||
      !matchValue(testCase.packageInfo, val) || !matchValue(testCase.className, val) ||
      !matchValue(testCase.methodName, val) || !matchValue(testCase.stacktrace, val)) {
      return true;
    }
    return false;
  }

  function matchValue(left, right) {
    if(left === undefined) {
      left = "";
    }
    var text = left.replace(/\s+/g, " ").toLowerCase();
    return !~text.indexOf(right);
  }

  function extractResults(data, startIndex, endIndex) {
    var result = [], j = 0;
    var i = startIndex - 1;
    while ((i < endIndex) && (i < data.length)) {
      result[j] = data[i];
      j += 1;
      i += 1;
    }
    return result;
  }

  function enableClickForProgressBar(status) {
    $("#statistics-progress-" + status).click(function() {
      searchResults(status);
    });
  }

  function searchResults(data) {
    var filterCtrl = $("#testCaseSearch");
    filterCtrl.val(data);
    filterCtrl.keyup();
  }

  function afterRender(data, buttonAdditonalClassName) {
    // The following code snippet helps displaying the tool tip in older version of IE viz 7,8,9
    $(document).on("mouseenter", "[data-toggle=tooltip]", function () {
      "use strict";
      $(this).tooltip({
        container: "body",
        trigger: "manual"
      }).tooltip("show");
    });

    $(document).on("mouseleave", "[data-toggle=tooltip]", function () {
      "use strict";
      $(this).tooltip("hide");
    });

    $(".btn-config" + buttonAdditonalClassName).click(function() {
      var test = $(this).attr("data-index");
      $(".modal-config .modal-body").html($("#configImpl").render({"results" : localConfigMap[test]}));
      $(".modal-config").modal();
    });

    $(".btn-screenshot" + buttonAdditonalClassName).click(function() {
      var testcase = data[parseInt($(this).attr("data-index"))];
      var params = " ";
      if (testcase.parameters) {
        params = testcase.parameters;
      }
      $(".modal-screenshot .modal-header .modal-title").text(
          testcase.packageInfo + "." + testcase.className + "." + testcase.methodName + "(" + params + ")");
      $(".modal-screenshot .modal-body").html($("#screenshotImpl").render(testcase));
      $(".modal-screenshot").modal();
    });

    $(".btn-stacktrace" + buttonAdditonalClassName).click(function() {
      var testcase = data[parseInt($(this).attr("data-index"))];
      var params = " ";
      if (testcase.parameters) {
        params = testcase.parameters;
      }
      $(".modal-stacktrace .modal-header .modal-title").text(
          testcase.packageInfo + "." + testcase.className + "." + testcase.methodName + "(" + params + ")");
      $(".modal-stacktrace .modal-body").html($("#stacktraceImpl").render(testcase, {
        getUpdatedStackTrace : function(value) {
          var newValue = "No data to display";
          if (value !== null) {
            var temp = value.replace(/\n/g, "<br/>");
            newValue = temp.replace(/\t/g, "&nbsp;&nbsp;&nbsp;&nbsp;");
          }
          return newValue;
        }
      }));
      $(".modal-stacktrace").modal();
    });
  }

  function setIndex(pageAdditionalId, val) {
    if(pageAdditionalId === "#") {
      index = val;
    } else {
      configIndex = val;
    }
  }

  function getIndex(pageAdditionalId) {
    if(pageAdditionalId === "#") {
      return index;
    } else {
      return configIndex;
    }
  }

  function setNoOfPage(pageAdditionalId, val) {
    if(pageAdditionalId === "#") {
      noOfPage = val;
    } else {
      configNoOfPage = val;
    }
  }

  function getNoOfPage(pageAdditionalId) {
    if(pageAdditionalId === "#") {
      return noOfPage;
    } else {
      return configNoOfPage;
    }
  }

  function getPageSize(pageAdditionalId) {
    if(pageAdditionalId === "#") {
      return pageSize;
    } else {
      return configPageSize;
    }
  }

  function setFilterSearch(pageAdditionalId, val) {
    if(pageAdditionalId === "#") {
      filterSearch = val;
    } else {
      configFilterSearch = val;
    }
  }

  function getFilterSearch(pageAdditionalId) {
    if(pageAdditionalId === "#") {
      return filterSearch;
    } else {
      return configFilterSearch;
    }
  }

  function setSortedRecords(pageAdditionalId, val) {
    if(pageAdditionalId === "#") {
      sortedRecords = val;
    } else {
      configSortedRecords = val;
    }
  }

  function setSortingDirection(pageAdditionalId, val) {
    if(pageAdditionalId === "#") {
      sortingDirection = val;
    } else {
      configSortingDirection = val;
    }
  }

  function getSortingDirection(pageAdditionalId) {
    if(pageAdditionalId === "#") {
      return sortingDirection;
    } else {
      return configSortingDirection;
    }
  }
}());
