Dynamic Extensions for Alfresco
===============================

[![Build Status](https://travis-ci.org/xenit-eu/dynamic-extensions-for-alfresco.svg)](https://travis-ci.org/xenit-eu/dynamic-extensions-for-alfresco)

Rapid development of Alfresco repository extensions in Java. Deploy your code in seconds, not minutes. Life is too short for endless server restarts.

Dynamic Extensions adds an OSGi container to the Alfresco repository, enabling live deployment of Java code, with no need to restart the server.
Alfresco itself is not "OSGi-fied" in any way; the OSGi container runs on top of the core Alfresco platform.

Installing Dynamic Extensions
-----------------------------

Dynamic Extensions is distributed as an Alfresco Module Package (AMP).

* Download the latest <a href="https://github.com/xenit-eu/dynamic-extensions-for-alfresco/releases">Dynamic Extensions AMP</a>.
* Use the <a href="http://docs.alfresco.com/4.0/index.jsp?topic=%2Fcom.alfresco.enterprise.doc%2Ftasks%2Famp-install.html">Module Management Tool</a> to install the AMP in the Alfresco repository of your choosing.
* After restarting Alfresco, open the Control Panel: <a href="http://localhost:8080/alfresco/service/dynamic-extensions/">http://localhost:8080/alfresco/service/dynamic-extensions/</a>.
* Accessing the Control Panel requires an admin account.

Supported Alfresco versions
---------------------------

Dynamic Extensions is developed and tested against:
* Alfresco Community 5.2.g


Example extension code
----------------------

This example Web Script examines a node and passes information to a Freemarker template:
```java
@Component
@WebScript
public ExampleWebScript {

  @Autowired
  private NodeService nodeService;

  @Uri("/show-node")
  // Example: http://localhost/alfresco/service/show-node?nodeRef=workspace://SpacesStore/12345
  public Map<String, Object> displayNodeName(@RequestParam NodeRef nodeRef) {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("properties", nodeService.getProperties(nodeRef));    
    return model; // Model is passed to Freemarker template.
  }
}
```

Note that this is an _annotation Web Script_. These types of Web Script are configured through Java annotations instead of `*.desc.xml` descriptors. Annotation Web Scripts are similar to Spring MVC's annotation-based controllers.

Here's the accompanying Freemarker template fragment:

```html
<table>
  <#list properties?keys as name>    
    <tr>
      <th>${name}</th>
      <td>${properties[name]!''}</td>
    </tr>
  </#list>
</table>
```

This is all the code that is required; there's no need for Spring XML config or Web Script XML descriptors. Hot-reloading and reducing configuration overhead are not particularly novel concepts in the Java development world at large. Essentially, Dynamic Extensions modernizes the development of Alfresco repository extensions.

The example above may be trivial, but the point is that, behind the scenes, services are still wired together through Spring and handled by the Web Script framework. Conceptually there is no real difference between a Dynamic Extension and a regular Alfresco extension. There's just less overhead and more convenience.

Creating your own Alfresco extensions
-------------------------------------

The <a href="https://github.com/laurentvdl/example-dynamic-extension">example Dynamic Extension</a> provides a good starting point for creating your own extensions.

Clone the example repo and explore it. Here are some pointers to get you going:

* Annotation-based Web Scripts: <a href="https://github.com/laurentvdl/example-dynamic-extension/blob/master/src/main/java/com/github/dynamicextensionsalfresco/examples/CategoriesWebScript.java">First example</a> and <a href="https://github.com/laurentvdl/example-dynamic-extension/blob/master/src/main/java/com/github/dynamicextensionsalfresco/examples/HelloWebScript.java">another example</a>.
* <a href="https://github.com/laurentvdl/example-dynamic-extension/blob/master/src/main/java/com/github/dynamicextensionsalfresco/examples/ExampleActions.java">Annotation-based Action example</a >  and a <a href="https://github.com/laurentvdl/example-dynamic-extension/blob/master/src/main/java/com/github/dynamicextensionsalfresco/examples/SetDescriptionWebScript.java">Web Script</a> that invokes the action.
* <a href="https://github.com/laurentvdl/example-dynamic-extension/blob/master/src/main/java/com/github/dynamicextensionsalfresco/examples/ExampleBehaviour.java">Annotation-based Behaviour example</a>.
* <a href="https://github.com/laurentvdl/example-dynamic-extension/tree/master/src/main/java/com/github/dynamicextensionsalfresco/examples">All examples</a>.

See also: <a href="https://github.com/xenit-eu/dynamic-extensions-for-alfresco/wiki/Building-Alfresco-repository-extensions-for-Dynamic-Extensions-using-Gradle">Gradle plugin for building your own repository extensions</a>

Configuring osgi-container
--------------------------
The configuration is optional. The default settings are suitable for
development environments.

For production environments you should review each setting carefully!

#### Configuration file location
This file should be placed on the classpath at this location: /dynamic-extensions/osgi-container.properties

Alfresco installations typically use the 'tomcat/shared/classes' directory
to store custom configuration. The full path would then be:
<alfresco>/tomcat/shared/classes/dynamic-extensions/osgi-container.properties

For directory layouts that differ from this standard Tomcat layout, you can
determine the root of the classpath from the location of 'alfresco-global.properties'.

#### Configuration settings

| Description                                                                   | Default                    | Example |
------------------------------------------------------------------------------- | :------------------------  | -------------------
| Allow restart of the container: useful for development                          | true                   | osgi.container.restartable=true/false |
| Enable starting bundles stored in the repository.                               | true                   | osgi.container.repository-bundles=true/false |
| Hot-deploy bundles on a running Alfresco If disabled, only classpath bundles and/or optionally repository bundles will be loaded. | true | osgi.container.hot-deploy=true/false |
| Configures the OSGI framework's storage directory. This can be either a relative or absolute path. Specifying an absolute path is recommended. (absolute or relative path) | tmp (relative to the current working directory) | osgi.container.storage-directory=tmp |
| Configures the OSGI framework's directory from which to automatically install or uninstall bundles. (absolute or relative path) | dynamic-extensions (relative to the current working directory) | osgi.container.bundle-directory=dynamic-extensions |
| Web application classpath scanning behaviour <br><br> ENABLE: Use existing cache or create a new cache if a: none exists or b: cache ts < WEB-INF/lib ts. Default setting. <br><br> DISABLE: Disable the cache. Rescans the WEB-INF/lib directory at every startup boot, but does not persist it. <br><br> UPDATE: Force update of the existing cache. | ENABLE | osgi.container.system-package-cache.mode = ENABLE/DISABLE/UPDATE |

Barcelona 2013 presentation
---------------------------

<a href="http://summit.alfresco.com/barcelona/sessions/life-too-short-endless-restarts">One hour presentation on using and exploring Dynamic Extensions for Alfresco</a>
