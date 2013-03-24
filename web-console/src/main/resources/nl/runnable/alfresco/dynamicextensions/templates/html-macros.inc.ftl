<#-- Macro that generates a Bootstrap document. -->
<#macro document title="">
<!DOCTYPE html>
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="${url.context}/css/dynamic-extensions/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="${url.context}/css/dynamic-extensions/bootstrap/css/bootstrap-responsive.min.css"/>
    <script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/jquery/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/bootstrap/js/bootstrap.min.js"></script>
    <style type="text/css">
      <#include "../styles/sticky-footer.css" >
      <#include "../styles/screen.css" >
    </style>
    <title>${title}</title>
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
  </body>
</html>
</#macro>
