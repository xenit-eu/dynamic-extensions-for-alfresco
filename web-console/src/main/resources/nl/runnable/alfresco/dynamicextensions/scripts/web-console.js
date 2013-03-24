(function($) {

  'use strict';

  $(function() {
    bootbox.animate(false);

    var lastUpdated = new Date();

    var refreshLastUpdated = function() {
      $('#last-updated').text(moment(lastUpdated).fromNow());
    };
    refreshLastUpdated();

    window.setInterval(refreshLastUpdated, 10000);

    $('table a').on('click', function(event) {
      event.preventDefault();
      bootbox.alert('<h2>Not yet implemented</h2><p>This action is not yet implemented.</p>');
    });

    $('a[data-method="post"]').on('click', function(event) {
      event.preventDefault();
      var wait = $(this).data('wait');
      if (wait) {
        $('input#wait').val(wait);
      }
      $('form#post').attr('action', $(this).attr('href')).submit();
      var message = $(this).data('message');
      var title = $(this).data('title');
      if (message || title) {
        var html = "";
        if (title) {
          html += "<h2>" + title + "</h2>";
        }
        if (message) {
          html += "<p>" + message + "</p>";
        }
        bootbox.alert(html, $(this).data('button'), function() {
          window.location.reload();
        });
      }
    });
  });

})($);
