<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Bundles - Dynamic Extensions" active="bundles">

  <#if installedBundle??>
    <@html.alert type="success">
      Installed bundle <a href="bundles/${installedBundle.bundleId}">${installedBundle.name} ${installedBundle.version}</a>.
    </@html.alert>
  </#if>

  <h2>Extension Bundles</h2>

  <@html.bundleTable bundles = extensionBundles />

  <h2>Framework Bundles</h2>

  <@html.bundleTable bundles = frameworkBundles />  

  <div class="row">
    <div class="span6">
      <h2>Filesystem Bundles</h2>
      <#if configuration.mode.bundleInstallEnabled>
        <p>Manage OSGi bundles from this filesystem directory:</p>
        <p>
           <code>${configuration.bundleDirectory.absolutePath}</code>
        </p>
        <p>
          Add bundle JAR files to have them installed in the OSGi framework. Bundle installation
          may take several seconds. Refresh this page to see the latest status.
        </p>
        <p>
          Delete the bundle JAR to uninstall it from the framework.
        </p>
      <#else>
        <p>Managing OSGi bundles through the filesystem is disabled.</p>
      </#if>
    </div>
    <div class="span6">
      <h2>Repository Bundles</h2>
      <form action="install-bundle" enctype="multipart/form-data" method="post">
        <div class="control-group">
          <div class="control-label">
            <label>Alternatively, upload an OSGi bundle here:</label>
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
        <p>
          Bundles stored in the repository can be deleted from the bundle detail pages.
        </p>
      </form>
    </div>
  </div>


</@html.document>
