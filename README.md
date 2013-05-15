Dynamic Extensions for Alfresco
===============================

Rapid development of Alfresco repository extensions in Java. Deploy your code in seconds, not minutes. Less overhead, more convenience. Life is too short for endless server restarts.

Dynamic Extensions adds an OSGi container to the Alfresco repository, enabling live deployment of Java code packaged as OSGi bundles. Alfresco itself is not "OSGi-fied" in any way. The OSGi container runs completely separate from the core Alfresco platform.

Latest release: Milestone 4
---------------------------

* **New**: Control Panel web interface for managing Dynamic Extensions.
* **New**: Annotation-based Web Scripts now support Freemarker templates.

In progress: Milestone 5
------------------------

* <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/wiki/Building-Alfresco-repository-extensions-for-Dynamic-Extensions-using-Gradle">Gradle plugin for building your own repository extensions</a>
* Spring AOP support. (The Gradle plugin configures the OSGi bundle so as to avoid classpath issues.)
* `@RunAs` and `@RunAsSystem` annotations for running code as a particular user.
* `@Transactional` annotation for running code within a transaction.
* Refactoring of annotation-based Web Scripts implementation.

Next up: Milestone 6
--------------------

Here we wrap up all the work that went into Dynamic Extensions over the past two years, improving documentation and test coverage.

While the project has seen a great amount of commits over the past 2 months, there will be a break in activity during the summer.

Milestone 6 is scheduled for September.

Installing Dynamic Extensions in an Alfresco repository
-------------------------------------------------------

Dynamic Extensions is distributed as an Alfresco Module Package (AMP).

* Download the <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/raw/mvn-repo/nl/runnable/alfresco/dynamicextensions/alfresco-module/1.0.0.M4/alfresco-module-1.0.0.M4.amp">Dynamic Extensions Milestone 4 AMP</a>.
* Use the <a href="http://docs.alfresco.com/4.0/index.jsp?topic=%2Fcom.alfresco.enterprise.doc%2Ftasks%2Famp-install.html">Module Management Tool</a> to install the AMP in the Alfresco repository.
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

This example Web Script examines a node and passed information to a Freemarker template:
```java
@ManagedBean
@WebScript
public ExampleWebScript {

  @Inject
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

Note that this is an _annotation-based Web Script_. These types of Web Scripts are configured through Java annotations instead of `*.desc.xml` descriptors.

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

