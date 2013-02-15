Dynamic Extensions for Alfresco
===============================

Rapid development of Alfresco repository extensions in Java. Deploy your code in seconds, not minutes. Life is too short for endless server restarts.

Status
------

All unused and unsupported old code has been removed; we can now move forward with a lean code base.

Most of the recent development effort has focused on the <a href="https://github.com/lfridael/vault">Vault UI platform</a>, but this project has not been forgotten.

Coming Soon
-----------

* Async Servlet 3.0 support for annotation-based Web Scripts.
* Mapping Web Script requests to annotation-based Spring MVC controllers.
* More documentation.

Coming Later
------------

While Dynamic Extensions has billed itself as "OSGi for Alfresco" from the start, in the long term it will transform into a more general solution for rapid development of Alfresco repository extensions. 

The current line of thinking is to introduce a new server-side, repository JavaScript API. While Alfresco already offers server-side scripting, this new framework will be more like a Domain Specific Language.

Java, Spring and OSGi will continue to be supported.


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
