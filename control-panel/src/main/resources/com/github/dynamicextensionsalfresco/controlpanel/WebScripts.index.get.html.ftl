<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Web Scripts - Dynamic Extensions" active="web-scripts">

  <h2>Annotation Web Scripts</h2>

  <#if (webScriptsByFamily?size > 0) >
    <#list webScriptsByFamily?keys as family>
        <#assign webScripts=webScriptsByFamily[family]>
	    <h3>${family}</h3>
	    <table class="web-script table table-striped table-bordered">
	      <thead>
	        <tr>
	          <th class="method">
	            Method
	          </th>
	          <th class="method" title="default format">
	            Format
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
	            <td class="method">
	              ${webScript.defaultFormat!""}
	            </td>
	            <td class="uri">
	              <ul>
	                <#list webScript.uris as uri>
	                  <li>
	                    <a href="${url.serviceContext}${uri}" data-content="${webScript.description!""}" data-trigger="hover" data-delay="1000" class="webscript">${uri}</a>
	                  </li>
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
    </#list>
  <#else>
    <p>No Web Scripts found.</p>
  </#if>

  <div class="row">
    <div class="span6">
      <h3>Web Script URI mappings</h3>
      <p>
        The Web Script engine caches URI mappings. Once a URI is called, it will 
        continue to be mapped to a particular Web Script or &quot;404 Not Found&quot;.
      </p>
      <p>
        Calling a URI for an annotation Web Script that has, in the meantime, been undeployed,
        will result in a &quot;503 Service Unavailable&quot; response.
      </p>
    </div>
    <div class="span6">
      <form action="${url.serviceContext}/" method="POST" class="well">
        <p>
          To clear all URI mappings, reset the Alfresco Web Script index:
        </p>
        <p>
          <input type="hidden" name="reset" value="on" />
          <input type="submit" value="Reset Alfresco Web Script index" class="btn btn-primary" />
        </p>
        <p>
          Resetting the Web Script engine will also deploy any new regular Web Scripts.
        </p>
      </form>
    </div>
  </div>

  <div class="row">
    <div class="span6">
      <h3>Regular Web Scripts</h3>
      <p>
        Regular Web Scripts are listed in the 
        <a href="${url.serviceContext}/">Alfresco Web Script index</a>.
      </p>
      <p>
        Note that annotation Web Scripts do not show up in the Alfresco Web Script index. 
        For annotation Web Scripts see the list above instead.
      </p>
    </div>
    <div class="span6">
      <div class="well">
        <p>
          To deploy regular Web Scripts as Dynamic Extensions, simply include the XML, JavaScript 
          and Freemarker resources in the OSGi bundle. For example:
        </p>
        <ul>
          <li>/path/to/webscript.get.desc.xml</li>
          <li>/path/to/webscript.get.js</li>
          <li>/path/to/webscript.get.html.ftl</li>
        </ul>
        <p>
          To register the Web Scripts, reset the 
          <a href="${url.serviceContext}/">Alfresco Web Script index</a>.
        </p>
      </div>
    </div>
  </div>

</@html.document>
