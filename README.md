Dynamic Extensions for Alfresco
===============================

Rapid development of Alfresco repository extensions in Java. Deploy your code in seconds, not minutes. Life is too short for endless server restarts.

Dynamic Extensions adds an OSGi container to the Alfresco repository, enabling live deployment of Java code, with no need to restart the server. Alfresco itself is not "OSGi-fied" in any way; the OSGi container runs completely separate from the core Alfresco platform.

Latest release: Milestone 5
---------------------------

* <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/wiki/Building-Alfresco-repository-extensions-for-Dynamic-Extensions-using-Gradle">Gradle plugin for building your own repository extensions</a>
* `@RunAs`, `@RunAsSystem` annotations for running methods as a particular repository user
* `@Transactional` annotation for running methods within a repository transaction.
* `@Before` annotation for handling common Web Script preprocessing logic.
* `@ExceptionHandler` annotation for generalizing exception handling in Web Scripts.
* Bug fixes and fine-tunings throughout.

Many thanks to laurentvdl for his contributions.

Installing Dynamic Extensions
-----------------------------

Dynamic Extensions is distributed as an Alfresco Module Package (AMP).

* Download the <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/raw/mvn-repo/nl/runnable/alfresco/dynamicextensions/alfresco-module/1.0.0.M5/alfresco-module-1.0.0.M5.amp">Dynamic Extensions Milestone 5 AMP</a>.
* Use the <a href="http://docs.alfresco.com/4.0/index.jsp?topic=%2Fcom.alfresco.enterprise.doc%2Ftasks%2Famp-install.html">Module Management Tool</a> to install the AMP in the Alfresco repository of your choosing.
* After restarting Alfresco, open the Control Panel: <a href="http://localhost:8080/alfresco/service/dynamic-extensions/">http://localhost:8080/alfresco/service/dynamic-extensions/</a>.
* Accessing the Control Panel requires an admin account.

Support for Alfresco Community
------------------------------

Dynamic Extensions is developed and tested against:

* Alfresco Community 3.4
* Alfresco Community 4.0
* Alfresco Community 4.2

Support for Alfresco Enterprise
-------------------------------

* Alfresco Enterprise 4.0
* Alfresco Enterprise 4.1

The Enterprise editions are not part of the development and testing environment, but Dynamic Extensions is known to work on them.

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

The <a href="https://github.com/lfridael/example-dynamic-extension">example Dynamic Extension</a> provides a good starting point for creating your own extensions.

Clone the example repo and explore it. Here are some pointers to get you going:

* Annotation-based Web Scripts: <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/CategoriesWebScript.java">First example</a> and <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/HelloWebScript.java">another example</a>.
* <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/ExampleActions.java">Annotation-based Action example</a >  and a <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/SetDescriptionWebScript.java">Web Script</a> that invokes the action.
* <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/ExampleBehaviour.java">Annotation-based Behaviour example</a>.
* <a href="https://github.com/lfridael/example-dynamic-extension/tree/master/src/main/java/nl/runnable/alfresco/examples">All examples</a>.

See also: <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/wiki/Building-Alfresco-repository-extensions-for-Dynamic-Extensions-using-Gradle">Gradle plugin for building your own repository extensions</a>

More documentation on creating your own extensions will follow.

Milestone 6 (September 2013)
----------------------------------

This milestone fine-tunes and wraps up all the work that went into Dynamic Extensions over the past two years.

<a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/issues?milestone=4&state=open">Issue list for milestone 6</a>.
