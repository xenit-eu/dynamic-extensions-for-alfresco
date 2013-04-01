<#-- Macro that generates a Bootstrap document. -->
<#macro document title="">
<!DOCTYPE html>
<html>
  <head>
    <title>${title}</title>
    <base href="${url.serviceContext}/dynamic-extensions/"/>
    <link rel="stylesheet" type="text/css" href="resources/stylesheets/bootstrap/css/bootstrap.min.css"/>
    <#-- <link rel="stylesheet" type="text/css" href="resources/stylesheets/bootstrap/css/bootstrap-responsive.min.css"/> -->
    <link rel="stylesheet" type="text/css" href="resources/stylesheets/sticky-footer.css"/>
    <link rel="stylesheet" type="text/css" href="resources/stylesheets/screen.css"/>
  </head>
  <body>
    <div id="wrap">
      <div class="navbar navbar-fixed-top">
        <div class="navbar-inner">
          <div class="container">
            <a class="brand" href="" title="Click to refresh">Dynamic Extensions for Alfresco</a>
            <ul class="nav pull-right">
              <li>
                <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco" target="_blank">Github project</a>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="container">
        <#nested/>
      </div>
      <div id="push"></div>
    </div>
    <div id="footer">
      <div class="container">
        <p class="last-updated">Last updated: <span id="last-updated">just now</span></p>
      </div>
    </div>
    <script type="text/javascript" src="resources/scripts/jquery/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="resources/scripts/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="resources/scripts/bootbox/bootbox.min.js"></script>
    <script type="text/javascript" src="resources/scripts/moment/moment.min.js"></script>
    <script type="text/javascript" src="resources/scripts/control-panel.js"></script>
  </body>
</html>
</#macro>
