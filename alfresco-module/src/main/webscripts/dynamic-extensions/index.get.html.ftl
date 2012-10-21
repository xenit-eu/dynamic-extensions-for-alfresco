<#import "handlebars-macros.inc.ftl" as handlebars>
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<title>Dynamic Extensions for Alfresco</title>
		<link rel="stylesheet" type="text/css" media="screen" href="${url.context}/css/dynamic-extensions/bootstrap/bootstrap.css" />
		<link rel="stylesheet" type="text/css" media="screen" href="${url.context}/css/dynamic-extensions/screen.css" />
	</head>
	<body>
	  <div id="application">
    </div>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/jquery/jquery-1.8.1.min.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/jquery/jquery.timeout-1.1.0.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/ember/handlebars-latest.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/ember/ember-latest.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/bootstrap/bootstrap.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/javascript/application.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/javascript/util.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/javascript/app-models.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/javascript/dictionary-models.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/javascript/management-models.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/javascript/views.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/javascript/bootstrap-views.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/javascript/controllers.js"></script>
		<script type="text/javascript" src="${url.context}/scripts/dynamic-extensions/javascript/router.js"></script>
    <@handlebars.templates names = [
      "application", 
      "dashboard", 
      "dictionary",
      "extension-index",
      "extension-table"
      "repository", 
      "model-index", 
      "model", 
      "model-navigation",
      "model-definition",
      "class-definition", 
      "data-types",
      "namespaces",
      "json-icon",
      "bundles-table", 
      "navigation-bar"
      ] />
	</body>
</html>