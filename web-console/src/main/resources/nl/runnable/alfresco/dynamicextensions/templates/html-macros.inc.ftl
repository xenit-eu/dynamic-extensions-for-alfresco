<#-- Macro that generates a Bootstrap document. -->
<#macro document title="">
<!DOCTYPE html>
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="${url.context}/css/dynamic-extensions/bootstrap/css/bootstrap.min.css"/>
    <script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/jquery/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/bootstrap/js/bootstrap.min.js"></script>
    <style type="text/css">
      <#include "../styles/screen.css" >
    </style>
    <title>${title}</title>
  </head>
  <body>
    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="" title="Click to refresh">Dynamic Extensions for Alfresco</a>
          <ul class="nav pull-right">
            <li>
              <a href="">Last updated: <span id="last-updated">just now</span></a>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="container">
      <#nested/>
      <div>
      </div>
    </div>
  </body>
</html>
</#macro>
