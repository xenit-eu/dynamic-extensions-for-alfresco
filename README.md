Dynamic Extensions for Alfresco
===============================

Rapid development of Alfresco repository extensions in Java. Deploy your code in seconds, not minutes. Life is too short for endless server restarts.

Status
------

All unused and unsupported old code has been removed:

* **Removed**: Dynamic Extensions console
* **Removed**: Loading extension JARs from the repository.
* **Removed**: Data Dictionary browser.

These parts of the codebase were considered to be too hard to maintain. But do not worry: the console and loading extensions from repository will be reimplemented soon, in a much more lightweight fashion. The Data Dictionary browser will find a new home in the <a href="../vault">Vault</a> user interface.

Having shed the technical debt accumulated over the past 2 years, the project can now move forward with a much leaner codebase. It's about time for Dynamic Extensions to have a proper milestone release.

Coming Soon: Milestone 3 release
--------------------------------

* Status page. Replaces the previous console.
* Loading OSGi extensions from the Alfresco repository.
* More documentation.

Coming Later
------------

While Dynamic Extensions has billed itself as "OSGi for Alfresco" from the start, in the long term it will transform into a more general solution for rapid development of Alfresco repository extensions.

The project will introduce a new server-side JavaScript API for accessing the Alfresco repository. This brings the overall development approach in line with <a href="../vault">Vault</a>, which is a modern JavaScript web application that runs completely in the browser. While Alfresco already supports server-side JavaScript, the new framework will be more like a Domain Specific Language, taking inspiration from the myriad of libraries in the ecosystem that has sprung up around <a href="http://nodejs.org/">node.js</a>.

Java and OSGi will continue to be supported, but should be considered foundational technologies.

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
