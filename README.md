Dynamic Extensions for Alfresco
===============================

Rapid development of Alfresco repository extensions in Java. Deploy your code in seconds, not minutes. Life is too short for endless server restarts.

Milestone 3 available
---------------------

This release has been sitting in limbo for far too long. 

The Maven artifacts for this release are hosted in this repo, under the <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/tree/mvn-repo">`mvn-repo`</a> tag.

Installing
----------

Dynamic Extensions is distributed as an Alfresco Module Package (AMP). For more info on AMPs see: <a href="http://docs.alfresco.com/4.0/index.jsp?topic=%2Fcom.alfresco.enterprise.doc%2Ftasks%2Famp-install.html">Installing an Alfresco Module Package</a>.

* Download the <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/raw/mvn-repo/nl/runnable/alfresco/dynamicextensions/alfresco-module/1.0.0.M3/alfresco-module-1.0.0.M3.amp">Milestone 3 AMP</a>.
* Install the AMP in your local Alfresco repository using the Module Management Tool.
* Optional: Configure the directory for deploying Dynamic Extensions using the `osgi.container.bundle-directory` setting in the <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/blob/master/alfresco-module/configuration/osgi-container.properties">configuration file</a>. 
* Build and deploy the <a href="https://github.com/lfridael/example-dynamic-extension">example Dynamic Extension</a>.
* Open the <a href="http://localhost:8080/alfresco/service/dynamic-extensions/hello">Hello Web Script</a> to verify everything is working.

Creating your own Alfresco extensions
-------------------------------------

While there is currently very little documentation on creating your own extensions, the <a href="https://github.com/lfridael/example-dynamic-extension">example Dynamic Extension</a> provides a good starting point.

Clone the example repo and explore it. Here are some pointers to get you going:

* The <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/pom.xml">pom.xml</a> serves as a template for your own Maven-based projects.
* Annotation-based Web Scripts: <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/CategoriesWebScript.java">First example</a> and <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/HelloWebScript.java">another example</a>.
* <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/ExampleActions.java">Annotation-based Action example</a >  and a <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/SetDescriptionWebScript.java">Web Script</a> that invokes the action.
* <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/src/main/java/nl/runnable/alfresco/examples/ExampleBehaviour.java">Annotation-based Behaviour example</a>.
* <a href="https://github.com/lfridael/example-dynamic-extension/tree/master/src/main/java/nl/runnable/alfresco/examples">All examples</a>.

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

Coming soon: Milestone 4
------------------------

This milestone focuses on making Dynamic Extensions production-ready and making strides towards a 1.0 final release. See the <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/issues?milestone=1">issue list</a> for what's coming.

OSGi under the hood
-------------------

Dynamic Extensions adds an OSGi container to Alfresco, enabling live deployment of Java code packaged as OSGi bundles. This is where the magic happens. Alfresco itself is not "OSGi"-fied in any way: the OSGi container runs completely separate from the core Alfresco platform.

Basically the OSGi container is responsible for:

* Loading and managing the lifecycle of OSGi bundles.
* Injecting dependencies on Alfresco repository services. I.e. `NodeService`, `CategoryService`, `FileFolderService`, etc.
* Providing support for Java annotations that reduce boilerplate code for Web Scripts, Behaviours and Actions. These annotations are unique to Dynamic Extensions.

OSGi usage is kept completely under the hood. Code written against Dynamic Extensions has no OSGi dependencies. If you see  `import org.osgi.*` somewhere you're not doing it right.
