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
          caches them to speed up subsequent repository startups.
        </p>
      <div class="well">
        <#if systemPackageCacheExists>
          <p>
            If you change any of the Java libraries in the repository application, you should delete 
            <#assign systemPackageUrl = "${url.serviceContext}/api/node/content/${systemPackageCacheNodeRef?replace('://', '/')}" />
            the <a href="${systemPackageUrl}" target="_blank">System Package cache</a>.
          </p>
          <form action="container/system-package-cache/delete" method="post" 
            data-title="Delete System Package cache"
            data-confirm="Are you sure you want to delete the System Package cache?">
            <button class="btn btn-danger">Delete System Package cache</button>
          </form>
          <p>
            The cache will be recreated the next time your restart the framework or the repository.
          </p>
        <#else>
          <p>
            <em>The System Package cache cannot be found.</em>
          </p>
        </#if>
      </div>
      
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

</@html.document>
