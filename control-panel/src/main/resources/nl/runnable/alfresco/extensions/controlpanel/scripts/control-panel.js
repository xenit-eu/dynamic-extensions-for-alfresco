(function($) {

  'use strict';

  var TIME_REFRESH_INTERVAL = 30000;
  var ALERT_TIMEOUT = 3000;

  /**
   * Page setup.
   */
  $(function() {
    bootbox.animate(false);
    $('a.bundle').popover();
  });

  /**
   * Sets up the display of relative times.
   */
  $(function() {
    var lastUpdated = new Date();
    var refreshTimes = function() {
      $('#last-updated').text(moment(lastUpdated).fromNow());
      $('span[data-time]').each(function() {
        var time = $(this).data('time');
        var label;
        if (time) {
          label = moment(time, 'YYYY-MM-DD HH:mm:ss Z').fromNow();
        } else {
          label = 'unknown';
        }
        $(this).text(label);
      });
    };
    refreshTimes();
    window.setInterval(refreshTimes, TIME_REFRESH_INTERVAL);
  });

  /**
   * Sets up fadeout of alert success messages.
   * 
   * Error messages have to be dismissed manually.
   */
  $(function() {
    window.setTimeout(function() {
      $('.alert-success').fadeOut();
    }, ALERT_TIMEOUT);
  });

  /**
   * Handles asynchronous POST requests.
   */
  $(function() {
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
  });

  /**
   * Handles confirmation of form submits.
   */
  $(function() {
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
  });

  /**
   * Handles automatic form submit on input changes.
   */
  $(function() {
    $('form input[data-autosubmit="true"]').on('change', function() {
      this.form.submit();
    });
  });

})($);
