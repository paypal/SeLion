if (typeof jQuery === 'undefined') {
  throw new Error('SeLion\'s JavaScript requires jQuery');
}

$(function (window, $) {
  'use strict';

  //return the css selector based on location.hash
  function getSelectorFromLocationHash() {
    var anchor, selector;
    anchor = $(window.location).attr('hash');

    //default to #getting-started
    if (anchor === '') {
      $(window.location).attr('href', 'documentation.html#getting-started');
    }
    selector = "a[href='" + anchor + "']";
    return selector;
  }

  //sets the active document on the menu
  function highlightActiveSelection() {
    var objCssSelector = getSelectorFromLocationHash();
    //add active new selection
    $(objCssSelector).parent().addClass('active');
  }

  //forward and back button support
  $(window).on('hashchange', function () {
    window.document.location.reload();
  });

  //loads content into the #page-content div
  function loadPage(url) {
    $("#page-content").load(url, function () {
      highlightActiveSelection();
    });
  }

  //handle any document initilization needs
  $(window.document).ready(function () {
    var selector = getSelectorFromLocationHash();

    //copy the nav menu content into the top and left menu navs
    $("#nav-menu-content").children().each(function () {
      $("#top-nav-menu").append($(this).clone());
      $("#left-nav-menu").append($(this).clone());
    });

    //setup togglers for nav menu
    //TODO :: figure out why child <li> nodes collapse the tree
//    $('label.tree-toggler').parent().click(function () {
//      $(this).children('ul.tree').toggle(300);
//    });

    //collapses all trees on the nav menu
    //$('label.tree-toggler').parent().children('ul.tree').toggle(300);

    loadPage($(selector).attr('data-src'));
  });

}(window, jQuery));