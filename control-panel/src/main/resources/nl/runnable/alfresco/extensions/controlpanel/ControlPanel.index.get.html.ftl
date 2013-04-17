<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Control Panel - Dynamic Extensions" active="extensions">

  <#if installedBundle??>
    <@html.alert type="success">
      Installed bundle <a href="bundles/${installedBundle.bundleId}">${installedBundle.name} ${installedBundle.version}</a>.
    </@html.alert>
  </#if>

  <h2>Extensions</h2>

  <@html.bundleTable bundles = extensionBundles />

  <h2>Manage bundles</h2>

  <div class="row">
    <div class="span6">
      <h3>Filesystem</h3>
      <#if (fileInstallPaths?size > 0)>
        <p>Manage bundles from one of the following filesystem directories:</p>
        <ul>
          <#list fileInstallPaths as path>
            <li>
              <code>${path}</code>
            </li>
          </#list>
        </ul>
        <p>
          Add or update bundle JAR files to install them in the framework. Delete them to 
          uninstall.
        </p>
      <#else>
        <p>Managing bundles through the filesystem is currently disabled.</p>
        <p>
          <a href="configuration" class="btn">Configure</a>
        </p>
      </#if>
    </div>
    <div class="span6">
      <h3>Repository</h3>
      <form action="install-bundle" enctype="multipart/form-data" method="post">
        <div class="control-group">
          <div class="control-label">
            <label>Alternatively, upload a bundle JAR file here:</label>
          </div>
          <div class="controls">
            <div class="fileupload fileupload-new" data-provides="fileupload">
              <div class="input-append">
                <div class="uneditable-input span4">
                  <i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span>
                </div>
                <span class="btn btn-file">
                  <span class="fileupload-new">Select JAR file</span>
                  <span class="fileupload-exists">Select JAR file</span>
                  <input type="file" name="file" data-autosubmit="true"/>
                </span>
              </div>
            </div>
          </div>
        </div>
        <p>
          The bundle JAR file will be stored in:<br/>
          <code>/Company Home/Data Dictionary/Dynamic Extensions/Bundles</code>
        </p>
      </form>
    </div>
  </div>


</@html.document>
