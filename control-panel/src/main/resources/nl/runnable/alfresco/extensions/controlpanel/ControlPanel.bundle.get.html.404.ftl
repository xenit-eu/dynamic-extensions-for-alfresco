<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Bundle Not Found - Dynamic Extensions">

  <h2>Bundle Not Found</h2>

  <p>
    The Bundle with the id <code>${id?string.computer}</code> could not be found.
  </p>
  <p>
    The Bundle may have been uninstalled from the OSGi framework.
  </p>

</@html.document>
