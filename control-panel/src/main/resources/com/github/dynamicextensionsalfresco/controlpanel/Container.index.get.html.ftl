<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Configuration - Dynamic Extensions" active="container">

  <div class="row">

    <div class="span6">
      
      <h2>System Packages</h2>
      <p>
        <a href="container/system-packages">View all System Packages</a>
      </p>
        <p>
          System Packages are Java libraries from the Alfresco repository. Classes from these packages
          are available to OSGi bundles.
        </p>
        <p>
          Dynamic Extensions scans the System Packages when it starts up for the first time and
          caches them to speed up subsequent repository startups. Updating the WEB-INF/lib folder will expire the cache.
        </p>

      <h2>Services</h2>
      <p>
        <a href="container/services">View all Services</a>
      </p>

    </div> <#-- .span6 -->

    <div class="span6">
      <h2>Restart OSGi container</h2>
      <p>
        Restart the OSGi container to reinitialize Dynamic Extensions:
      </p>
      <div class="well">
        <#if canRestartFramework>
          <p>            
            The Control Panel will be unavailable while the OSGI container is restarting.
          </p>
          <p>
            <a href="osgi/restart" 
              data-title="Restart OSGi container" 
              data-confirm="Are you sure you want to restart the OSGi container?"
              data-method="post" 
              data-pending-title="Restarting OSGi container"
              data-pending-message="Please wait while the OSGi container is restarting."
              data-complete-title="Restarted OSGI container"
              data-complete-message="The OSGi container was succesfully restarted."
              class="btn btn-primary">
              Restart OSGi container
            </a>
          </p>
          <p>
            If you get a "Not Found" or "Service Unavailable" response afterwards, wait a few 
            seconds, then refresh the page.
          </p>
        <#else>
          <p>
            <em>The OSGi container cannot be restarted.</em>
          </p>
        </#if>        
      </div>
      <h3>REST API</h3>
      <#assign restartApiUrl = "${url.serviceContext}/dynamic-extensions/osgi/restart" />
      <p>
        You can also restart the OSGi container using the REST API:<br/>
      </p>
      <p>
        <code>POST ${restartApiUrl}</code>
      </p>
      <p>
        This API requires adminstrator-level access.
      </p>
      <div class="well">
        <p>
          Example of using the REST API through cURL:
        </p>
        <p>
          <code>curl -X POST ${restartApiUrl} -u admin</code>
        </p>
        <p>
          cURL will prompt you for the password.
        </p>
      </div>

    </div> <#-- .span6 -->

  </div> <#-- .row -->

  <div class="row">
    <div class="span12">
      <h2>Settings</h2>
      <p>Change these settings via the <code>osgi-container.properties</code> file.</p>
      <table class="table table-bordered table-striped">
        <thead>
        <tr>
          <th class="name">Key</th>
          <th class="status">Value</th>
          <th class="status">Description</th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td><code>osgi.container.restartable</code></td>
          <td><@html.check configuration.frameworkRestartEnabled /></td>
          <td>
            Restart OSGi container @ runtime
          </td>
        </tr>
        <tr>
          <td><code>osgi.container.hot-deploy</code></td>
          <td><@html.check configuration.hotDeployEnabled /></td>
          <td>
            Installing bundles via control-panel @ runtime
          </td>
        </tr>
        <tr>
          <td><code>osgi.container.repository-bundles</code></td>
          <td><@html.check configuration.repositoryBundlesEnabled /></td>
          <td>
            Installing/starting bundles in the repository. (instead of classpath)
          </td>
        </tr>
        <tr>
          <td><code>osgi.container.system-package-cache.mode</code></td>
          <td>${configuration.systemPackageCacheMode}</td>
          <td>
            System classpath scanning: cache mode
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>

</@html.document>
