<#import "templates/html-macros.inc.ftl" as html>
<@html.document title="Configuration - Dynamic Extensions" active="configuration">

  <h2>Configuration</h2>

  <div class="row">
    <div class="span6">
      <h3>System Packages</h3>
      <p>
        System Packages are Java libraries from the Alfresco repository. Classes from these packages
        are available to OSGi bundles.
      </p>
      <p>
        <a href="system-packages">View all ${systemPackageCount?string.computer} System Packages</a>
      </p>
      <div class="well">
        <p>
          Dynamic Extensions scans the System Packages when it starts up for the first time and 
          caches them to speed up subsequent repository startups.
        </p>
        <#if systemPackageCacheExists>
          <p>
            If you change any of the Java libraries in the repository application, you should delete 
            <#assign url = "${url.serviceContext}/api/node/content/${systemPackageCacheNodeRef?replace('://', '/')}" />
            the <a href="${url}" target="_blank">System Package cache</a>.
          </p>
          <p>
            With the cache deleted, the System Packages will be rescanned on the next repository 
            startup.
          </p>
          <form action="delete-system-package-cache" method="post" 
            data-title="Delete System Package cache"
            data-confirm="Are you sure you want to delete the System Package cache?">
            <button class="btn btn-danger">Delete System Package cache</button>
          </form>
        <#else>
          <p>
            <em>The System Package cache cannot be found.</em>
          </p>
        </#if>
        <p>
          Note: When the System Package cache is not available the <a href="framework">Framework</a>
          cannot be restarted. In this case you should restart the repository to have the cache
          rebuilt on startup.
        </p>
      </div>
    </div>
    <div class="span6">
      <h3>OSGi Services</h3>
      <p>
        <em>TODO: show OSGi services</em>
      </p>
    </div>
  </div>

</@html.document>
