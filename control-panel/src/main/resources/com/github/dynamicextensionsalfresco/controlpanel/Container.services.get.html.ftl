<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Services - Dynamic Extensions" active="container">

  <h2>Services</h2>

  <#list servicesByBundle as bundle>

    <h3>
      <a href="bundles/${bundle.bundleId?string.computer}">${bundle.name} ${bundle.version}</a>
    </h3>

    <table class="service table table-striped table-bordered">
      <thead>
        <tr>
          <th class="name">Service classes</th>
          <th class="bean">Spring bean</th>
        </tr>
      </thead>
      <tbody>
        <#list bundle.services as service>
          <tr>
            <td class="name">
			  <#if service.springContext><a href="container/services/bundle/${bundle.bundleId}/${service_index}"></#if>
              <ul>
                <#list service.objectClasses as objectClass>
                  <li>${objectClass}</li>
                </#list>
              </ul>
    		  <#if service.springContext></a></#if>
            </td>
            <td class="bean">
              ${service.beanName!'n/a'}
            </td>
          </tr>
        </#list>
      </tbody>
    </table>
  </#list>

</@html.document>
