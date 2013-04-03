<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Control Panel - Dynamic Extensions">

  <#macro bundleTable bundles>
    <table class="bundles table table-bordered table-striped">
      <thead>
        <tr>
          <th class="name">Bundle</th>
          <th class="state">Status</th>        
          <th class="description">Description</th>
        </tr>      
      </thead>
      <tbody>
        <#list bundles as bundle>
          <tr class="<#if bundle.state == 'installed'>error</#if>">
            <td>
              <a href="bundles/${bundle.id}">${bundle.name} ${bundle.version}</a>
            </td>
            <td>
              ${bundle.state}
            </td>
            <td>
              ${bundle.manifest.bundleDescription!''}
            </td>
          </tr>
        </#list>      
      </tbody>  
    </table>
  </#macro>

  <h2>Extensions</h2>
  <@bundleTable bundles=extensionBundles />

  <p>Install extensions by placing their OSGi bundle JARs in one of the following directories:</p>
  <ul>
    <#list fileInstallPaths as path>
      <li>
        <code>${path}</code>
      </li>
    </#list>
  </ul>

  <h2>Framework</h2>
  <@bundleTable bundles=frameworkBundles />  

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
