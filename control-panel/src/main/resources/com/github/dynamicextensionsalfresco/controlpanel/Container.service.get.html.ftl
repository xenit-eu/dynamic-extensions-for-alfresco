<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Spring context - Dynamic Extensions" active="container">

  <h2>Spring context</h2>

<table class="service table table-striped table-bordered">
  <thead>
	<tr>
	  <th class="name">Type</th>
	  <th class="bean">Id</th>
	</tr>
  </thead>
  <tbody>
	<#list contextBeans?keys as name>
	  <tr>
		<td class="bean">
		  ${contextBeans[name]}
		</td>
          <td class="name">
		  ${name}
          </td>
	  </tr>
	</#list>
  </tbody>
</table>

</@html.document>
