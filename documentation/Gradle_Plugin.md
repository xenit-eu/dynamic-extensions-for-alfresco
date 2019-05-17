# The Dynamic Extensions Gradle plugin

The Dynamic Extensions Gradle plugin, [`eu.xenit.de`](https://plugins.gradle.org/plugin/eu.xenit.de), helps you to develop a Dynamic Extensions Bundle by:

1. Automatically adding a dependency on the Dynamic Extensions `annotations` and `webscripts` jars.
    By default, the plugin adds a dependency on the same version as the plugin. This can be changed or disabled in the plugin configuration.
2. Configuring the `jar` task to add OSGi headers with bnd.
3. Creating an `installBundle` task that you can use to install your Dynamic Extensions bundle inside a running Alfresco repository.

```groovy
plugins {
    id "eu.xenit.de" version "2.0.0" // For the latest version, see: https://plugins.gradle.org/plugin/eu.xenit.de
}
```

## Configuration

The Gradle plugin exposes its configuration in `alfrescoDynamicExtensions`.

This is the built-in default configuration. You can use this configuration as a template to make changes to.

```groovy
alfrescoDynamicExtensions {

    versions {
        // Configure the version of eu.xenit.de:annotations and eu.xenit.de:webscripts dependencies.
        // By default, it is equal to the plugin version
        // If set to null, no dependencies are added.
        dynamicExtensions = '2.0.0' 
    }
    
    // Configuring the repository is only used by `InstallBundle`, you can leave it out when you are not automatically installing bundles.
    repository {
    
        // Configure the URL to the Alfresco services endpoint
        endpoint {
            protocol = "http"
            host = "localhost"
            port = 8080
            serviceUrl = "/alfresco/service"
        }
        
        // Configure admin credentials to install DE bundles
        authentication {
            username = "admin"
            password = "admin"
        }
    }
}
``` 


## Tasks

### `DeBundle`: create a Dynamic Extensions bundle jar

The Gradle plugin extends the `jar` task with a `bnd` function that can be used to modify OSGi headers.

Next to the [standard OSGi headers](https://bnd.bndtools.org/chapters/800-headers.html),
Dynamic Extensions recognizes some [custom headers](./Building_Bundles.md#building-dynamic-extensions-compatible-bundles) that can be set as well.

[BND instructions](https://bnd.bndtools.org/chapters/825-instructions-ref.html) can be passed to the `bnd` function as well.

```groovy
jar {
    bnd(
        'Alfresco-Spring-Configuration': 'eu.xenit.de.example',
        'Export-Package': 'eu.xenit.de.example,eu.xenit.de.example.*'
    )
}
```

The `Alfresco-Dynamic-Extension` header is always set automatically.
When no `Import-Package` header is defined, `Import-Package` is automatically set to `*` (all packages) and `DynamicImport-Package` is set to `*` as well.

This function is provided by the [bnd Gradle plugin](https://github.com/bndtools/bnd/tree/master/biz.aQute.bnd.gradle#create-a-task-of-the-bundle-type).
However, usage of the `bndfile` parameter or applying the `biz.aQute.bnd.builder` on the project is not supported.

> You can create additional bundle tasks by using the `DeBundle` task type.
> This type extends the Gradle `Jar` type and has the same configuration options as the `jar` task.

### `InstallBundle`: install a Dynamic Extensions bundle in a running Alfresco

The Gradle plugin adds an `installBundle` task that uploads the output of `jar` to the repository configured in `alfrescoDynamicExtensions.repository`.

The `InstallBundle` task type installs a jar file or set of jar files into a running Dynamic Extensions installation.

| Property | Description |
| -------- | ----------- |
| `files`  | `ConfigurableFileCollection` that contains all jar files to upload when the task is executed. |
| `repository` | `Repository` the Alfresco repository to upload the files to. This property refers to `alfrescoDynamicExtensions.repository` when it is not explicitly configured. |

| Function | Description |
| -------- | ----------- |
| `repository(Action<? super Repository>)` | Configures the repository to upload the jar files to. Fully replaces the existing configuration with a new configuration. |

```groovy
configurations {
    install
}

dependencies {
    install("[...]")
    install("[...]")
}

// Install some files from a configuration
task installDependencies(type: InstallBundle) {
    files = configurations.install
}

// Install the outputs of some task
task installFile(type: InstallBundle) {
    files += tasks.jar.archivePath
}
```

## Building bundles for Dynamic Extensions 1.x

You can use the Gradle plugin 2.x to build extensions for Dynamic Extensions 1.x.

You should take into account that the `eu.xenit.de:annotations` and `eu.xenit.de:webscripts` dependencies for Dynamic Extensions 1.x pull in a lot of Alfresco dependencies.
It is recommended to disable the automatic addition of these dependencies by setting `alfrescoDynamicExtensions.versions.dynamicExtensions = null` and then
manually add non-transitive `compileOnly` dependencies on both dependencies.


```groovy
alfrescoDynamicExtensions {
    versions {
        dynamicExtensions = null
    }
}

dependencies {
    compileOnly('eu.xenit.de:annotations:${dynamicExtensionsVersion}') { transitive = false }
    compileOnly('eu.xenit.de:webscripts:${dynamicExtensionsVersion}')  { transitive = false }
}
```
