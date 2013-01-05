Dynamic Extensions for Alfresco
===============================

Rapid development of Alfresco repository extensions in Java. Deploy your code in seconds, not minutes. Life is too short for endless server restarts.

Getting started
---------------

Prerequisites: make sure you have a local Alfresco development server installed.

* Download the <a href="https://github.com/downloads/lfridael/dynamic-extensions-for-alfresco/nl.runnable.alfresco.dynamicextensions-1.0.0.M3-SNAPSHOT.amp">Dynamic Extensions milestone 3 AMP</a>.
* Install the AMP in Alfresco using the <a href="https://wiki.alfresco.com/wiki/Module_Management_Tool">Module Management Tool</a>.
* Start your local Alfresco development server.
* Open <a href="http://localhost:8080/alfresco/service/dynamic-extensions">http://localhost:8080/alfresco/service/dynamic-extensions</a>. This shows the Dynamic Extensions console with some information.

Supported Alfresco versions
---------------------------

Dynamic Extensions is developed and tested against

* Alfresco Community 3.4
* Alfresco Community 4.0

Dynamic Extensions can also be used with Alfresco Enterprise 4.0 and 4.1. Recent user feedback indicates it works on Community 4.2 as well.

Intended usage
--------------

Dynamic Extensions is intended for developing extensions to the Alfresco repository. The repository is Java-based and uses Spring as a core framework. 

Extending Alfresco Share is outside the scope of this project, as Share is built on an entirely different framework.

However: we are building an all-new Alfresco user interface, code-named <a href="https://github.com/lfridael/vault">Vault</a>. You can expect to a see a lot of synergy between the two projects.

OSGi
----

Dynamic Extensions adds an OSGi container to Alfresco, enabling live deployment of Java code packaged as OSGi bundles. This is where the magic happens. Alfresco itself is not "OSGi"-fied in any way: the OSGi container runs completely separate from the core Alfresco platform.

Basically the OSGi container is responsible for:

* Loading and managing the lifecycle of OSGi bundles.
* Injecting dependencies on Alfresco repository services. I.e. NodeService, CategoryService, etc.
* Providing support for Java annotations that reduce boilerplate code for Web Scripts, Behaviours and Actions. These annotations are unique to Dynamic Extensions.

OSGi usage is kept completely under the hood. Alfresco extensions have no OSGi API dependencies.
