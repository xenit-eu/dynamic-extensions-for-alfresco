Dynamic Extensions for Alfresco
===============================

Rapid development of Alfresco repository extensions in Java. Deploy your code in seconds, not minutes. Life is too short for endless server restarts.

2013-04-21: Milestone 4
-----------------------

* **New**: Control Panel web interface for managing Dynamic Extensions.
* **New**: Annotation-based Web Scripts now support Freemarker templates.
* **Improved**: Better integration with Alfresco repository, greatly reduced OSGi overhead.

Installing
----------

Dynamic Extensions is distributed as an Alfresco Module Package (AMP). For more info on AMPs see: <a href="http://docs.alfresco.com/4.0/index.jsp?topic=%2Fcom.alfresco.enterprise.doc%2Ftasks%2Famp-install.html">Installing an Alfresco Module Package</a>.

* Download the <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/raw/mvn-repo/nl/runnable/alfresco/dynamicextensions/alfresco-module/1.0.0.M4/alfresco-module-1.0.0.M4.amp">Dynamic Extensions Milestone 4 AMP</a>.
* Install the AMP in your local Alfresco development environment using the Module Management Tool.
* Open the <a href="http://localhost:8080/alfresco/service/dynamic-extensions/">Control Panel</a>.

Creating your own Alfresco extensions
-------------------------------------

While there is currently very little documentation on creating your own extensions, the <a href="https://github.com/lfridael/example-dynamic-extension">example Dynamic Extension</a> provides a good starting point.

Clone the example repo and explore it. Here are some pointers to get you going:

* The <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/pom.xml">pom.xml</a> serves as a template for your own Maven-based projects.
* Annotation-based Web Scripts: <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/CategoriesWebScript.java">First example</a> and <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/HelloWebScript.java">another example</a>.
* <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/ExampleActions.java">Annotation-based Action example</a >  and a <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/SetDescriptionWebScript.java">Web Script</a> that invokes the action.
* <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/ExampleBehaviour.java">Annotation-based Behaviour example</a>.
* <a href="https://github.com/lfridael/example-dynamic-extension/tree/master/src/main/java/nl/runnable/alfresco/examples">All examples</a>.

Last milestone
--------------

Milestone 5 will be the last before the 1.0 beta stage and is focused on making Dynamic Extensions ready for production. 

<a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/issues?milestone=2&state=open">Open issues for milestone 5</a>

Release: June 2013

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

Even though the Enterprise editions are not part of the development and testing environment, there is is no reason why Dynamic Extensions should not work on them.

OSGi under the hood
-------------------

Dynamic Extensions adds an OSGi container to Alfresco, enabling live deployment of Java code packaged as OSGi bundles. This is where the magic happens. Alfresco itself is not "OSGi"-fied in any way: the OSGi container runs completely separate from the core Alfresco platform.

Basically the OSGi container is responsible for:

* Loading and managing the lifecycle of OSGi bundles.
* Injecting dependencies on Alfresco repository services. I.e. `NodeService`, `CategoryService`, `FileFolderService`, etc.
* Providing support for Java annotations that reduce boilerplate code for Web Scripts, Behaviours and Actions. These annotations are unique to Dynamic Extensions.

OSGi usage is kept completely under the hood. Code written against Dynamic Extensions has no OSGi dependencies. If you see  `import org.osgi.*` somewhere you're not doing it right.
