<#-- Macro that generates a Bootstrap document. -->
<#macro document title="" active="extensions">
<!DOCTYPE html>
<html>
  <head>
    <title>${title}</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <base href="${url.serviceContext}/dynamic-extensions/"/>
    <#assign resourceBase = "${url.serviceContext}/eu-xenit-de-control-panel/web/" />

    <link rel="stylesheet" type="text/css" href="${resourceBase}stylesheets/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="${resourceBase}stylesheets/bootstrap/css/bootstrap-responsive.min.css"/>
    <link rel="stylesheet" type="text/css" href="${resourceBase}stylesheets/jasny-bootstrap/bootstrap-fileupload.css"/>
    <link rel="stylesheet" type="text/css" href="${resourceBase}stylesheets/sticky-footer.css"/>
    <link rel="stylesheet" type="text/css" href="${resourceBase}stylesheets/screen.css"/>
  </head>
  <body>
    <div id="wrap">
      <div class="navbar navbar-fixed-top">
        <div class="navbar-inner">
          <div class="container">
            <a class="brand" href="" title="Click to refresh">Dynamic Extensions for Alfresco</a>
            <ul class="nav">
              <li class="<#if (active == 'bundles')>active</#if>">
                <a href="bundles">Bundles</a>
              </li>
              <li class="<#if (active == 'web-scripts')>active</#if>">
                <a href="web-scripts">Web Scripts</a>
              </li>
              <li class="<#if (active == 'container')>active</#if>">
                <a href="container">Container</a>
              </li>
            </ul>
            <ul class="nav pull-right">
              <li>
                <a href="https://github.com/xenit-eu/dynamic-extensions-for-alfresco">
                  Github project
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="message container">
        <#if errorMessage??>
          <@alert type="error">
            ${errorMessage}
          </@alert>
        </#if>
        <#if successMessage??>
          <@alert type="success">
            ${successMessage}
          </@alert>
        </#if>
        <#nested/>
      </div>
      <div id="push"></div>
    </div>
    <div id="footer">
      <div class="container">
        <div class="row">
            <div class="span6">
            <p class="last-updated">Last refreshed: <span id="last-updated">just now</span></p>
            </div>
          <div class="span6">
            <p class="login">Logged in as: ${currentUser}</p>
          </div>
        </div>
      </div>
    </div>
    <script type="text/javascript" src="${resourceBase}scripts/jquery/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="${resourceBase}scripts/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${resourceBase}scripts/jasny-bootstrap/bootstrap-fileupload.js"></script>
    <script type="text/javascript" src="${resourceBase}scripts/bootbox/bootbox.min.js"></script>
    <script type="text/javascript" src="${resourceBase}scripts/moment/moment.min.js"></script>
    <script type="text/javascript" src="${resourceBase}scripts/control-panel.js"></script>
  </body>
</html>
</#macro>

<#macro bundleTable bundles>
  <table class="bundles table table-bordered table-striped">
    <thead>
      <tr>
        <th class="name">Bundle</th>
        <th class="status">Status</th>        
        <th class="modified">Modified</th>
        <th class="store">Store</th>
      </tr>      
    </thead>
    <tbody>
      <#list bundles as bundle>
        <tr class="<#if bundle.status == 'installed' && !bundle.fragmentBundle>error</#if>">
          <td>
            <@bundleLink bundle=bundle/>
          </td>
          <td>
            ${bundle.status}
          </td>
          <td>
            <span data-time="${bundle.lastModified!}"></span>
          </td>
          <td>
            ${bundle.store}
          </td>
        </tr>
      </#list>      
    </tbody>  
  </table>
</#macro>

<#macro alert type="success">
  <div class="alert alert-${type} alert-block">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    <#nested/>
  </div>
</#macro>

<#macro bundleLink bundle>
  <a class="bundle"
    href="bundles/${bundle.bundleId?string.computer}"
    data-trigger="hover"
    data-content="${bundle.description!}"
    data-delay="1000">
    ${bundle.name} ${bundle.version}
  </a>
</#macro>

<#macro check test><#if test>&#x2713<#else>&#x2718</#if></#macro>