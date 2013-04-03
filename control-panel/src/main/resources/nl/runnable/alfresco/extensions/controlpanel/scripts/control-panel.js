(function($) {

  'use strict';

  $(function() {
    bootbox.animate(false);

    var lastUpdated = new Date();
    var refreshTimes = function() {
      $('#last-updated').text(moment(lastUpdated).fromNow());
      $('span[data-time]').each(function() {
        $(this).text(moment($(this).data('time')).fromNow());
      });
    };
    refreshTimes();
    window.setInterval(refreshTimes, 60000);

    $('a[data-method="post"]').on('click', function(event) {
      event.preventDefault();

      $('form#post').attr('action', $(this).attr('href')).submit();

      // Show dialog
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
      // Reload after wait.
      var wait = $(this).data('wait') || 0;
      window.setTimeout(function() {
        window.location.reload();
      }, wait);
    });

    $('form[data-confirm]').on('submit', function(event) {
      event.preventDefault();

      var title = $(this).data('title') || 'Confirm';
      var contents = "<h2>" + title + "</h2>";
      contents += "<p>" + $(this).data('confirm') + "</p>";

      var self = this;
      bootbox.confirm(contents, function(confirmed) {
        if (confirmed) {
          self.submit();
        }
      });
    });

    $('form input[data-autosubmit="true"]').on('change', function() {
      this.form.submit();
    });

  });

})($);
