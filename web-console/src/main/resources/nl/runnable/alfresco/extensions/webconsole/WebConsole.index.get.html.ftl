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
            <a href="${url.service}/extensions/${extension.name}">${extension.name}</a>
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
  <h2>Install Extensions</h2>
  <#if (fileInstallPaths?size > 0) >
    <p>Install Dynamic Extensions by placing their JARs in any of the following directories:</p>
    <ul>
      <#list fileInstallPaths as path>
        <li>
          <code>${path}</code>
        </li>
      </#list>
    </ul>
    <p>Uninstall extensions by deleting the JARs.</p>
  <#else>
    <p>
      <em>Installing extensions through the filesystem is not available.</em>
    </p>
  </#if>
  <h2>Framework</h2>
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
</@html.document>
