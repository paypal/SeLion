/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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
    $('.modal-image .modal-body').find('img').attr('src',src);
    $('.modal-image .modal-header .modal-title').text(title);
    $('.modal-image').modal();
  }

function displaySource(src, title) {
    $('.modal-source .modal-body').find('iframe').attr("src", src);
    $('.modal-source .modal-header .modal-title').text(title);
    $('.modal-source').modal();
  }
  
/* Main JavaScript for RuntimeReporter */
(function main() {
  var pageSize = 10, configPageSize = 10;
  var index = 1, configIndex = 1;
  var total, configTotal;
  var noOfPage, configNoOfPage;
  var sortedRecords, sortingDirection = {suite: "1", test: "1", packageInfo: "1", className: "1", methodName: "1",
    parameters : "1" };
  var configSortedRecords, configSortingDirection = {suite: "1", test: "1", packageInfo: "1", className: "1",
    methodName: "1", type : "1" };
  var filterSearch, configFilterSearch;
  var autoRefresh;
  var timeRefresh = 300 * 1000;//300 seconds refresh interval
  var localConfigMap = new Object();
  var reports = window.reports;
  var reportMetaData = reports.reporterMetadata;
  var DISPLAY_LABEL = "displayLabel";

  var dateFormatOptions = {
      year: "numeric", month: "short", day: "numeric",
      hour: "2-digit", minute: "2-digit", second:"2-digit"
  };
  var helpers = {
//    Declare date format helper to get readable name and value.
      formatDateValue : function(val, key) {
        switch (key) {
        case "currentDate":
          var d = new Date(val);
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
    $('#config-popover').popover();
    var configData = $.map(reports.configSummary, function(value, key) {
      return {value: helpers.formatDateValue(value, key), key: helpers.formatDisplayName(key)}});
    $('#config-popover').attr('data-content', $('#configImpl').render({'results' : configData}));

    //Prepare local config summary map
    $.each(reports.localConfigSummary, function(idx, obj) {
      localConfigMap[obj.test] = $.map(obj, function(value, key) {
        return {value: helpers.formatDateValue(value, key), key: helpers.formatDisplayName(key)}}); 
    });

    autoRefresh = setInterval(reloadPage,timeRefresh);

    //Calulate the test case summary
    var passed = 0, failed = 0, skipped = 0, running = 0;
    $.each(reports.testMethods,function(index, item) {
      if (item.status == 'Passed') {
        passed++;
      } else if (item.status == 'Failed') {
        failed++;
      } else if (item.status == 'Skipped') {
        skipped++;
      } else {
        running++;
      }
    })

    total = passed + failed + skipped + running;

    if (passed > 0) {
      $('#statistics-data-passed').css('width',(passed * 100 / total)+ '%').html(passed + ' passed');
      $('#statistics-progress-passed').css('width',(passed * 100 / total)+ '%');
    }
    if (failed > 0) {
      $('#statistics-data-failed').css('width',(failed * 100 / total)+ '%').html(failed + ' failed');
      $('#statistics-progress-failed').css('width',(failed * 100 / total)+ '%');
    }
    if (skipped > 0) {
      $('#statistics-data-skipped').css('width',(skipped * 100 / total)+ '%').html(skipped + ' skipped');
      $('#statistics-progress-skipped').css('width',(skipped * 100 / total)+ '%');
    }
    if (running > 0) {
      $('#statistics-data-running').css('width',(running * 100 / total)+ '%').html(running + ' running');
      $('#statistics-progress-running').css('width',(running * 100 / total)+ '%');
    }

    //Pagination for Test case table
    sortedRecords = reports.testMethods;
    filterSearch = sortedRecords;
    total = filterSearch.length;
    noOfPage = Math.ceil(total/pageSize);

    renderPageCombo("#", ".pageSizeCombo");
    renderSearch("#", "#testCaseSearch", sortedRecords);
    renderPageNumbers("#", 1, noOfPage);
    renderPagination("#", noOfPage);
    renderTable("#", filterSearch,index,pageSize);

    //Pagination for Config Test case table
    configSortedRecords = reports.configurationMethods;
    configFilterSearch = configSortedRecords;
    configTotal = configFilterSearch.length;
    configNoOfPage = Math.ceil(configTotal/configPageSize);

    renderPageCombo("#config-", ".config-pageSizeCombo");
    renderSearch("#config-", "#config-testCaseSearch", configSortedRecords);
    renderPageNumbers("#config-", 1, configNoOfPage);
    renderPagination("#config-", configNoOfPage);
    renderTable("#config-", configFilterSearch, configIndex, configPageSize);

    $('#testcase-btn-grid').click(function() {
      $('#testcaseParentBody .fixed-table-pagination').hide();
      $('#testcase-btn-list').removeClass('active');
      $('#testcase-btn-grid').addClass('active');
      $('#testcaseBody').html($('#gridImpl').render({'results' : reports.testMethods}));
      afterRender(reports.testMethods, '');
    });

    $('#testcase-btn-list').click(function() {
      $('#testcase-btn-grid').removeClass('active');
      $('#testcase-btn-list').addClass('active');
      renderTable("#", filterSearch,index,pageSize);
      $('#testcaseParentBody .fixed-table-pagination').show();
      afterRender(filterSearch,'');
    });

    $('.btn-auto-update').click(function() {
      var span = $(this).find('span');
      if ($(span).hasClass('label-success')) {
        $(span).removeClass('label-success').addClass('label-danger').html('Off');
        if (autoRefresh != null) {
          clearInterval(autoRefresh);
        }
      } else {
        $(span).removeClass('label-danger').addClass('label-success').html('On');
        autoRefresh = setInterval(reloadPage,timeRefresh);
      }
    });

    enableClickForProgressBar("passed");
    enableClickForProgressBar("failed");
    enableClickForProgressBar("skipped");
    enableClickForProgressBar("running");
    $('#statistics-data').click(function() {
      searchResults('');
    });

  }); // end of ready

  function renderPageCombo(pageAdditionalId, comboClassName) {
    $(comboClassName + " li a").click(function(){
      var selText = $(this).text();
      $(this).parents('.btn-group').find('.dropdown-toggle').html(selText+' <span class="caret"></span>');

      var mPageSize = parseInt(selText);
      var mNoOfPage;
      var mFilterSearch;

      if(pageAdditionalId == "#") {
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

      $(pageAdditionalId + 'testcase-pagination ul .page-number').remove();
      renderPageNumbers(pageAdditionalId, 1, mNoOfPage);
      renderTable(pageAdditionalId, mFilterSearch, 1, mPageSize);
    });
  }

  function renderSorting(pageAdditionalId, tableId, records) {
    $(tableId + ' thead tr th').click(function() {
      var col = $(this).attr('data-index');
      if(col != 'nil') {
        var direction = getSortingDirection(pageAdditionalId);
        var asc = direction[col];
        direction[col] = -1 * asc;
        setSortingDirection(pageAdditionalId, direction);
        records.sort(function(a, b){
          return (a[col] == b[col]) ? 0 : ((a[col] > b[col]) ? asc : -1 * asc);
        });
        renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), getIndex(pageAdditionalId),
          getPageSize(pageAdditionalId));

        var arrowDirection = '';
        if(asc == "1") {
          arrowDirection = 'dropup';
        }
        $(tableId + " thead tr th[data-index$=" + col + "]").html($(tableId + " thead tr th[data-index$=" + col + "]").text() + "<span class='order "+ arrowDirection + "'><span class='caret' style='margin: 10px 5px;'></span></span>");
      }
    });

  }

  function renderSearch(pageAdditionalId, searchBoxId, records) {
    $(searchBoxId).keyup(function() {

      var val = $.trim($(this).val()).replace(/ + /g, ' ').toLowerCase();
      var searchArray = new Array(); var j=0;

      for (i = 0; i < records.length; i++) {
        var testCase = records[i];
        if(matchTestCase(testCase, val)) {
          searchArray[j++] = testCase;
        }
      }

      setFilterSearch(pageAdditionalId, searchArray);
      setSortedRecords(pageAdditionalId, searchArray);
      setIndex(pageAdditionalId, 1);
      setNoOfPage(pageAdditionalId, 
        Math.ceil(getFilterSearch(pageAdditionalId).length / getPageSize(pageAdditionalId)));

      if(searchBoxId == "#testCaseSearch") {
        total = filterSearch.length;
      } else {
        configTotal = configFilterSearch.length;
      }

      $(pageAdditionalId + 'testcase-pagination ul .page-number').remove();
      renderPageNumbers(pageAdditionalId, 1, getNoOfPage(pageAdditionalId));
      renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), 1, getPageSize(pageAdditionalId));
    });
  }

  function renderPagination(pageAdditionalId, mNoOfPage) {
    $(pageAdditionalId + 'testcase-pagination ul .page-first').click(function() {
      if(1 != getIndex(pageAdditionalId)) {
        setIndex(pageAdditionalId, 1);
        $(pageAdditionalId + 'testcase-pagination ul .page-number').remove();
        renderPageNumbers(pageAdditionalId, 1, getNoOfPage(pageAdditionalId));
        renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), getIndex(pageAdditionalId),
          getPageSize(pageAdditionalId));
      }
    });

    $(pageAdditionalId + 'testcase-pagination ul .page-pre').click(function() {
      if(0 < (getIndex(pageAdditionalId) - getPageSize(pageAdditionalId))) {
        setIndex(pageAdditionalId, (getIndex(pageAdditionalId) - getPageSize(pageAdditionalId)));
        var k = $(pageAdditionalId + 'testcase-pagination ul .page-number').first().find('a').text();
        var n = parseInt(k) - 1;
        if(n > 0) {
          $(pageAdditionalId + 'testcase-pagination ul .page-number').last().remove();
          $(pageAdditionalId + "testcase-pagination ul .page-pre").after($('<li>').attr('data-index',n).addClass('page-number').append($('<a>').attr('href','javascript:void(0)').append(n) ));
        }

        renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), getIndex(pageAdditionalId),
          getPageSize(pageAdditionalId));
      }
    });

    $(pageAdditionalId + 'testcase-pagination ul .page-number').click(function() {
      setIndex(pageAdditionalId,((parseInt($(this).attr('data-index')) - 1) * getPageSize(pageAdditionalId)) + 1);
      renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), getIndex(pageAdditionalId),
        getPageSize(pageAdditionalId));
    });

    $(pageAdditionalId + 'testcase-pagination ul .page-next').click(function() {
      if(getFilterSearch(pageAdditionalId).length >= (getIndex(pageAdditionalId) + getPageSize(pageAdditionalId))) {
        setIndex(pageAdditionalId, (getIndex(pageAdditionalId) + getPageSize(pageAdditionalId)));
        var k = $(pageAdditionalId + 'testcase-pagination ul .page-number').last().find('a').text();
        var n = parseInt(k) + 1;
        if(n <= getNoOfPage(pageAdditionalId)) {
          $(pageAdditionalId + 'testcase-pagination ul .page-number').first().remove();
          $(pageAdditionalId + "testcase-pagination ul .page-next").before($('<li>').attr('data-index',n).addClass('page-number').append($('<a>').attr('href','javascript:void(0)').append(n) ));
        }
        renderTable(pageAdditionalId, getFilterSearch(pageAdditionalId), getIndex(pageAdditionalId),
          getPageSize(pageAdditionalId));
      }
    });

    $(pageAdditionalId + 'testcase-pagination ul .page-last').click(function() {
      if(getFilterSearch(pageAdditionalId).length >= (getIndex(pageAdditionalId) + getPageSize(pageAdditionalId))) {
        var divIndex = Math.floor(getFilterSearch(pageAdditionalId).length / getPageSize(pageAdditionalId));
        var modIndex = getFilterSearch(pageAdditionalId).length % getPageSize(pageAdditionalId);
        setIndex(pageAdditionalId, (divIndex * getPageSize(pageAdditionalId)) + 1);
        if(modIndex == 0) {
          setIndex(pageAdditionalId, ((divIndex - 1) * getPageSize(pageAdditionalId)) + 1);
        }

        $(pageAdditionalId + 'testcase-pagination ul .page-number').remove();
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
    $(pageAdditionalId + 'testcaseBody').html($(pageAdditionalId + 'tableImpl').render({'results' : filterResult}, 
      {"startIndex":rtIndex}, {"count": endIndex}));

    if(pageAdditionalId == '#') {
      afterRender(filterResult, '');
      renderSorting("#", "#paginationTextCaseTable", sortedRecords);
    } else {
      afterRender(filterResult, '-config');
      renderSorting("#config-", "#paginationTextConfigTable", configSortedRecords);
    }

    if(filterSearch.length <= (endIndex - rtIndex)) {
      endIndex = rtFilterSearch.length;
    }

    if(getPageSize(pageAdditionalId) >= rtFilterSearch.length) {
      $(pageAdditionalId + 'testcase-page-info').html("Showing all rows");
    } else {
      $(pageAdditionalId + 'testcase-page-info').html("Showing " + rtIndex + " to " + endIndex + " of " + rtFilterSearch.length + " rows");
    }

    $(pageAdditionalId + 'testcase-pagination ul .page-number').click(function() {
      var mIndex = ((parseInt($(this).attr('data-index')) - 1) * rtPageSize) + 1;

      if(pageAdditionalId == '#') {
        index = mIndex;
      } else {
        configIndex = mIndex;
      }
      renderTable(pageAdditionalId, rtFilterSearch,mIndex, rtPageSize);
    });
  }

  function renderPageNumbers(paginationId, startNumber, mNoOfPage) {
    var pageLoaded = 0;
    for (i = startNumber; (i <= mNoOfPage) && (pageLoaded < 5); i++) {
      $(paginationId + "testcase-pagination ul .page-next").before($('<li>').attr('data-index',i).addClass('page-number').append($('<a>').attr('href','javascript:void(0)').append(i) ));
      pageLoaded++;
    }
  }

  function matchTestCase(testCase, val) {
    if(!matchValue(testCase.status, val) || !matchValue(testCase.suite, val)  || !matchValue(testCase.test, val) ||
      !matchValue(testCase.packageInfo, val) || !matchValue(testCase.className, val) ||
      !matchValue(testCase.methodName, val)) {
      return true;
    }
    return false;
  }

  function matchValue(left, right) {
    var text = left.replace(/\s+/g, ' ').toLowerCase();
    return !~text.indexOf(right);
  }

  function extractResults(data, startIndex, endIndex) {
    var result = new Array(); var j=0;
    for (i = (startIndex-1); (i < endIndex) && (i < data.length); i++) {
      result[j++] = data[i];
    }
    return result;
  }

  function enableClickForProgressBar(status) {
    $('#statistics-progress-' + status).click(function() {
      searchResults(status);
    });
  }

  function searchResults(data) {
    var filterCtrl = $("#testCaseSearch");
    filterCtrl.val(data);
    filterCtrl.keyup();
  }

  function afterRender(data, buttonAdditonalClassName) {
    $("a[data-toggle='tooltip']").tooltip();

    $('.btn-config' + buttonAdditonalClassName).click(function() {
      var test = $(this).attr('data-index');
      $('.modal-config .modal-body').html($("#configImpl").render({'results' : localConfigMap[test]}));
      $('.modal-config').modal();
    });

    $('.btn-screenshot' + buttonAdditonalClassName).click(function() {
      var testcase = data[parseInt($(this).attr('data-index'))];
      var params = ' ';
      if (testcase['parameters'] != null) {
        params = testcase['parameters'];
      }
      $('.modal-screenshot .modal-header .modal-title').text(
          testcase['packageInfo'] + '.' + testcase['className'] + '.' + testcase['methodName']
          + '(' + params + ')');
      $('.modal-screenshot .modal-body').html($("#screenshotImpl").render(testcase));
      $('.modal-screenshot').modal();
    });

    $('.btn-stacktrace' + buttonAdditonalClassName).click(function() {
      var testcase = data[parseInt($(this).attr('data-index'))];
      var params = ' ';
      if (testcase['parameters'] != null) {
        params = testcase['parameters'];
      }
      $('.modal-stacktrace .modal-header .modal-title').text(
          testcase['packageInfo'] + '.' + testcase['className'] + '.' + testcase['methodName']
          + '(' + params + ')');
      $('.modal-stacktrace .modal-body').html($("#stacktraceImpl").render(testcase, {
        getUpdatedStackTrace : function(value) {
          var newValue = 'No data to display';
          if (value != null) {
            var temp = value.replace(/\n/g, '<br/>');
            newValue = temp.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;');
          }
          return newValue;
        }
      }));
      $('.modal-stacktrace').modal();
    });
  }

  function setIndex(pageAdditionalId, val) {
    if(pageAdditionalId == '#') {
      index = val;
    } else {
      configIndex = val;
    }
  }

  function getIndex(pageAdditionalId) {
    if(pageAdditionalId == '#') {
      return index;
    } else {
      return configIndex;
    }
  }

  function setNoOfPage(pageAdditionalId, val) {
    if(pageAdditionalId == '#') {
      noOfPage = val;
    } else {
      configNoOfPage = val;
    }
  }

  function getNoOfPage(pageAdditionalId) {
    if(pageAdditionalId == '#') {
      return noOfPage;
    } else {
      return configNoOfPage;
    }
  }

  function setPageSize(pageAdditionalId, val) {
    if(pageAdditionalId == '#') {
      pageSize = val;
    } else {
      configPageSize = val;
    }
  }

  function getPageSize(pageAdditionalId) {
    if(pageAdditionalId == '#') {
      return pageSize;
    } else {
      return configPageSize;
    }
  }

  function setFilterSearch(pageAdditionalId, val) {
    if(pageAdditionalId == '#') {
      filterSearch = val;
    } else {
      configFilterSearch = val;
    }
  }

  function getFilterSearch(pageAdditionalId) {
    if(pageAdditionalId == '#') {
      return filterSearch;
    } else {
      return configFilterSearch;
    }
  }

  function setSortedRecords(pageAdditionalId, val) {
    if(pageAdditionalId == '#') {
      sortedRecords = val;
    } else {
      configSortedRecords = val;
    }
  }

  function getSortedRecords(pageAdditionalId) {
    if(pageAdditionalId == '#') {
      return sortedRecords;
    } else {
      return configSortedRecords;
    }
  }

  function setSortingDirection(pageAdditionalId, val) {
    if(pageAdditionalId == '#') {
      sortingDirection = val;
    } else {
      configSortingDirection = val;
    }
  }

  function getSortingDirection(pageAdditionalId) {
    if(pageAdditionalId == '#') {
      return sortingDirection;
    } else {
      return configSortingDirection;
    }
  }
}()); // end of main function
