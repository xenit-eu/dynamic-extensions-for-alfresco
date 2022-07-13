


# Configuring the OSGi-container

The configuration is optional. The default settings are suitable for
development environments.

For production environments you should review each setting carefully!

## Configuration file location
This file should be placed on the classpath at this location: /dynamic-extensions/osgi-container.properties

Alfresco installations typically use the 'tomcat/shared/classes' directory
to store custom configuration. The full path would then be:
<alfresco>/tomcat/shared/classes/dynamic-extensions/osgi-container.properties

For directory layouts that differ from this standard Tomcat layout, you can
determine the root of the classpath from the location of 'alfresco-global.properties'.

## Configuration settings

| Description                                                                                                                                                                                                                                                                                                                                            | Default                                                       | Example                                                          |
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------|------------------------------------------------------------------
| Allow restart of the container: useful for development                                                                                                                                                                                                                                                                                                 | true                                                          | osgi.container.restartable=true/false                            |
| Enable starting bundles stored in the repository.                                                                                                                                                                                                                                                                                                      | true                                                          | osgi.container.repository-bundles=true/false                     |
| Hot-deploy bundles on a running Alfresco If disabled, only classpath bundles and/or optionally repository bundles will be loaded.                                                                                                                                                                                                                      | true                                                          | osgi.container.hot-deploy=true/false                             |
| Configures the OSGI framework's storage directory. This can be either a relative or absolute path. Specifying an absolute path is recommended. (absolute or relative path)                                                                                                                                                                             | tmp (relative to the current working directory)               | osgi.container.storage-directory=tmp                             |
| Configures the OSGI framework's directory from which to automatically install or uninstall bundles. (absolute or relative path)                                                                                                                                                                                                                        | dynamic-extensions (relative to the current working directory) | osgi.container.bundle-directory=dynamic-extensions               |
| Web application classpath scanning behaviour <br><br> ENABLE: Use existing cache or create a new cache if a: none exists or b: cache ts < WEB-INF/lib ts. Default setting. <br><br> DISABLE: Disable the cache. Rescans the WEB-INF/lib directory at every startup boot, but does not persist it. <br><br> UPDATE: Force update of the existing cache. | DISABLE                                                       | osgi.container.system-package-cache.mode = ENABLE/DISABLE/UPDATE |
| Configure the OSGI framework's boot delegation. This property is added for supporting [AppDynamics](https://docs.appdynamics.com/21.3/en/application-monitoring/install-app-server-agents/java-agent/install-the-java-agent/agent-installation-by-java-framework/osgi-infrastructure-configuration).                                                   |                                                              | osgi.container.boot-delegation=com.singularity.*                 |

