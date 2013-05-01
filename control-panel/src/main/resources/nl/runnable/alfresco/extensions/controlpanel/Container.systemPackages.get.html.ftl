<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="System Packages - Dynamic Extensions" active="container">

  <h2>System Packages</h2>
  
  <table class="package table table-striped table-bordered">
    <thead>
      <tr>
        <th class="name">System Package</th>
        <th class="version">Version</th>
      </tr>
    </thead>
    <tbody>
      <#list systemPackages as package>
        <tr>
          <td class="name">${package.name}</td>
          <td class="version">${package.version!'1.0'}</td>
        </tr>
      </#list>
    </tbody>
  </table>

</@html.document>
