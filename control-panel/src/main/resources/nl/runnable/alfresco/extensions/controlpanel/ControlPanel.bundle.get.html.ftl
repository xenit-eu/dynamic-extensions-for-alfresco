<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="${bundle.name} ${bundle.version} - Dynamic Extensions">

  <#assign manifest = bundle.manifest />

  <#macro header name value="">
    <tr>
      <th class="name">
        ${name}
      </th>
      <td class="value">
        <#nested />
      </td>
    </tr>
  </#macro>

  <#macro listPackages packages>
      <#if (packages?size > 0) >
        <ul>
          <#list packages as package>
            <li>
              <code>${package.packageName}</code>
            </li>
          </#list>
        </ul>
      <#else>
        <span class="none">None</span>
      </#if>
  </#macro>

  <h2>${bundle.name} ${bundle.version}</h2>

  <table class="bundle table table-striped table-bordered">
    <@header name="Symbolic Name">${bundle.symbolicName}</@header>
    <@header name="Version">${bundle.version}</@header>
    <@header name="Status">${bundle.state}</@header>
    <@header name="Description">${manifest.bundleDescription}</@header>
    <@header name="Imported Packages">
      <@listPackages packages = manifest.importPackage.importedPackages/>
    </@header>
    <@header name="Exported Packages">
      <@listPackages packages = manifest.exportPackage.exportedPackages/>
    </@header>
  </table>

</@html.document>
