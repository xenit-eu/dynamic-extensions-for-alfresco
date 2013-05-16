<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Web Scripts - Dynamic Extensions" active="web-scripts">

  <h2>Annotation-based Web Scripts</h2>

  <#if (webScripts?size > 0) >
    <table class="web-script table table-striped table-bordered">
      <thead>
        <tr>
          <th class="method">
            Method
          </th>
          <th class="uri">
            URI
          </th>
          <th class="handler">
            Handler
          </th>
        </tr>
      </thead>
      <tbody>
        <#list webScripts as webScript>
          <tr>
            <td class="method">
              ${webScript.method}
            </td>
            <td class="uri">
              <ul>
                <#list webScript.uris as uri>
                  <li>${uri}</li>
                </#list>
              </ul>
            </td>
            <td class="handler">
              ${webScript.handler!'n/a'}
            </td>
          </tr>
        </#list>
      </tbody>
    </table>
  <#else>
    <p>No Web Scripts found.</p>
  </#if>

  <h2>Regular Web Scripts</h2>

  <p>
    Regular Web Scripts are listed in the <a href="${url.serviceContext}/">Alfresco Web Script index</a>.
    Note: annotation-based Web Scripts do not show up in the Alfresco Web Script index.
  </p>
  <div class="well">
    <p>
      To deploy regular Web Scripts as Dynamic Extensions, simply include their resources in the 
      bundle, for example:
    </p>
    <ul>
      <li>/path/to/webscript.get.desc.xml</li>
      <li>/path/to/webscript.get.js</li>
      <li>/path/to/webscript.get.html.ftl</li>
    </ul>
    <p>
      To register regular Web Scripts, you must refresh the 
      <a href="${url.serviceContext}/">Alfresco Web Script index</a> manually.
    </p>
  </div>

</@html.document>
