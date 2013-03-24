<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Console - Dynamic Extensions">
  <h2>Extensions</h2>
  <table class="extensions table table-bordered table-striped">
    <thead>
      <tr>
        <th class="name">Extension</th>
        <th class="version">Version</th>
        <th class="actions">&nbsp;</th>
      </tr>      
    </thead>
    <tbody>
      <#list extensions as extension>
        <tr>          
          <td>
            <a href="${url.service}/bundles/${extension.bundleId}">${extension.name}</a>
          </td>
          <td>
            ${extension.version}
          </td>
          <td>
            <div class="btn-group">
              <a href="#" class="btn dropdown-toggle" data-toggle="dropdown">
                Actions
                <span class="caret"></span>
              </a>
              <ul class="dropdown-menu">
                <li><a href="#">Not yet implemented</a></li>
              </ul>
            </div>
          </td>
        </tr>
      </#list>      
    </tbody>
  </table>
  <h2>Installing Extensions</h2>
  <#if (fileInstallPaths?size > 0) >
    <p>Install new extensions by placing Bundle JARs in the following directories:</p>
    <ul>
      <#list fileInstallPaths as path>
        <li>
          <code>${path}</code>
        </li>
      </#list>
    </ul>
  <#else>
    <p>
      <em>Installing extensions through the filesystem is not available.</em>
    </p>
  </#if>
  <h2>Framework</h2>
  <table class="core-bundles table table-bordered table-striped">
    <thead>
      <tr>
        <th class="name">Bundle</th>
        <th class="version">Version</th>
      </tr>      
    </thead>
    <tbody>
      <#list coreBundles as bundle>
        <tr>          
          <td><a href="${url.service}/bundles/${bundle.bundleId}">${bundle.name}</td>
          <td>${bundle.version}</td>
        </tr>
      </#list>      
    </tbody>
  </table>
  <p>    
    <a href="${url.service}/framework/restart" 
      data-method="post" 
      data-wait="60000" 
      data-title="Restarting Framework" 
      data-message="Please wait while the Framework restarts. (You may have to refresh manually to see the latest status.)" 
      data-button="Update now"
      class="btn">
      Restart Framework
    </a>
  </p>
  <#-- Form for submitting POST requests -->
  <form id="post" method="post">
    <input type="hidden" id="wait" name="wait" value="0" />
  </form>
  <#-- 
  For now we include all scripts inline as there is no way of bundling CSS and JavaScript.

  One idea to resolve this is to add a helper that generates <script> and <link> tags by scanning
  for resources in the Bundle JAR.
  -->
  <script type="text/javascript">
    <#include "scripts/bootbox.min.js" />
    <#include "scripts/moment.min.js" />
    <#include "scripts/web-console.js" />
  </script>
</@html.document>
