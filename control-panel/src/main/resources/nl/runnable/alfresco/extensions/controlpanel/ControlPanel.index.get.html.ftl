<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Bundles - Dynamic Extensions" active="bundles">

  <#if installedBundle??>
    <@html.alert type="success">
      Installed bundle <a href="bundles/${installedBundle.bundleId}">${installedBundle.name} ${installedBundle.version}</a>.
    </@html.alert>
  </#if>

  <h2>Extension bundles</h2>

  <@html.bundleTable bundles = extensionBundles />

  <h2>Framework bundles</h2>

  <@html.bundleTable bundles = frameworkBundles />  

  <div class="row">
    <div class="span6">
      <h2>Filesystem bundles</h2>
      <#if configuration.mode.bundleInstallEnabled>
        <p>Manage OSGi bundles from the filesystem:</p>
        <p>
           <code>${configuration.bundleDirectory.absolutePath}</code>
        </p>
        <p>
          Add bundle files to this directory to install them in the OSGi container.
          Installation may take several seconds, refresh this page to see the latest status.
        </p>
        <p>
          Remove bundles from this directory to uninstall them from the OSGi container.
        </p>
      <#else>
        <p>Managing OSGi bundles through the filesystem is disabled.</p>
      </#if>
    </div>
    <div class="span6">
      <h2>Repository bundles</h2>
      <form action="install-bundle" enctype="multipart/form-data" method="post">
        <div class="control-group">
          <div class="control-label">
            <label>Or upload an OSGi bundle here:</label>
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
          The bundle file will be stored in:<br/>
          <code>/Company Home/Data Dictionary/Dynamic Extensions/Bundles</code>
        </p>
        <p>
          Bundles stored in the repository can be deleted from the bundle detail pages. (Deleting
          the bundle files using Alfresco Share or Alfresco Explorer will not uninstall them from 
          the OSGi container.)
        </p>
      </form>
    </div>
  </div>

</@html.document>
