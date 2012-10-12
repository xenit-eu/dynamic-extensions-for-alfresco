<#-- 
 Renders a Handlebars template using <script> tag.
-->
<#macro template name = "">
  <#if name != "">
    <script type="text/x-handlebars" data-template-name="${name}">
      <#include "./handlebars/${name}.html" />
  <#else>
    <script type="text/x-handlebars">
      <#nested/>
    </script>
  </#if>
</script>
</#macro>

<#-- 
 Renders multiple Handlebars templates using <script> tags.
-->
<#macro templates names>
  <#list names as name>
    <@template name=name />
  </#list>
</#macro>
