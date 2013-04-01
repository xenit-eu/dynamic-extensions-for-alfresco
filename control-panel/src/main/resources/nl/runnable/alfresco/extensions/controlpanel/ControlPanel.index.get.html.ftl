<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Control Panel - Dynamic Extensions">

  <h2>Extensions</h2>
  <@html.bundleTable bundles=extensionBundles />

  <p>Install extensions by placing their OSGi bundle JARs in one of the following directories:</p>
  <ul>
    <#list fileInstallPaths as path>
      <li>
        <code>${path}</code>
      </li>
    </#list>
  </ul>

  <h2>Framework</h2>
  <@html.bundleTable bundles=frameworkBundles />  

  <p>    
    <a href="${url.service}/framework/restart" 
      data-method="post" 
      data-wait="5000" 
      data-title="Restarting Framework" 
      data-message="Please wait while the Framework restarts. (You may have to refresh manually to see the latest status.)" 
      data-button="Update now"
      class="btn">
      Restart Framework
    </a>
  </p>

  <form id="post" method="post" target="postFrame"></form>
  <iframe name="postFrame" style="display: none;"></iframe>

</@html.document>
