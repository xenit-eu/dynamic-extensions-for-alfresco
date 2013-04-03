<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="${bundle.name} ${bundle.version} - Dynamic Extensions">

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
  </#macro>

  <h2>${bundle.name} ${bundle.version}</h2>

  <table class="bundle table table-striped table-bordered">
    <@header name="Symbolic Name">${bundle.symbolicName}</@header>
    <@header name="Version">${bundle.version}</@header>
    <@header name="Status">${bundle.state}</@header>
    <@header name="Description">${bundle.manifest.bundleDescription!""}</@header>
  </table>

  <#if bundle.state == 'installed'>
    <div class="alert alert-error alert-block">
      <p>
        If this bundle remains in the <code>installed</code> state, the Java packages that 
        this bundle depends on have not all been resolved. (If the OSGi framework is currently 
        restarting this page may not reflect the latest status.)
      </p>
      <p>
        Java packages can be imported from:
      </p>
      <ul>
        <li>
          <a href="bundles/0">The OSGi Framework</a>: provides Java, OSGI, Alfresco API and Spring 
          packages from the Alfresco repository application.
        </li>
        <li>
          <a href="">Other OSGi bundles</a>
        </li>
      </ul>
      <p>
        Once the dependencies are available, this bundle's state should change to
        <code>resolved</code> or <code>active</code>.
      </p>
      <p>
        <em>
          The bundle may also have missing <code>Import-Bundle</code> or <code>Import-Library</code> dependencies.
          These types of dependency should generally not be used with Dynamic Extensions.
        </em>
      </p>
    </div>
  </#if>


  <h2>Imported packages</h2>  
  <#if (bundle.importedPackages?size > 0)>
    <table class="imported package table table-striped table-bordered">
      <thead>
        <tr>
          <th class="name">Imported Java Package</th>
          <th class="min version">Min Version</th>
          <th class="max version">Max Version</th>
        </tr>
      </thead>
      <tbody>
        <#list bundle.importedPackages as package>
          <tr>
            <td class="name">${package.name}</td>
            <td class="min version">${package.minVersion!'unbounded'}</td>
            <td class="max version">${package.maxVersion!'unbounded'}</td>
          </tr>
        </#list>
      </tbody>
    </table>
  <#else>
    <p>
      This bundle does not import any packages.
    </p>
  </#if>

  <h2>Exported packages</h2>
  <#if (bundle.exportedPackages?size > 0)>
    <table class="package table table-striped table-bordered">
      <thead>
        <tr>
          <th class="name">Exported Java Package</th>
          <th class="version">Version</th>
        </tr>
      </thead>
      <tbody>
        <#list bundle.exportedPackages as package>
          <tr>
            <td class="name">${package.packageName}</td>
            <td class="version">${package.version!''}</td>
          </tr>
        </#list>
      </tbody>
    </table>
  <#else>
  <p>
    This bundle does not export any packages.
  </p>
  </#if>

</@html.document>
