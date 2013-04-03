<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Control Panel - Dynamic Extensions" active="framework">

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

</@html.document>
