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
            <#assign url = "${url.serviceContext}/api/node/content/${systemPackageCacheNodeRef?replace('://', '/')}" />
            the <a href="${url}" target="_blank">System Package cache</a>.
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
      <p>
        The use of OSGi services is currently internal to Dynamic Extensions.
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
            The Control Panel will be available while the OSGI container is restarting.
          </p>
          <p>
            <a href="container/restart" 
              data-title="Restart OSGi container" 
              data-confirm="Are you sure you want to restart the OSGi container?"
              data-method="post" 
              data-wait="15000" 
              data-pending-title="Restarting OSGi container"
              data-pending-message="<p>Please wait while the OSGi container is restarting. The page will refresh automatically.</p>" 
              data-pending-button="Refresh page now"
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

    </div> <#-- .span6 -->

  </div> <#-- .row -->

</@html.document>
