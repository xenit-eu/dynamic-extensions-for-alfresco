package com.github.dynamicextensionsalfresco.gradle

import com.github.dynamicextensionsalfresco.gradle.internal.BuildConfig
import eu.xenit.gradle.alfrescosdk.AlfrescoPlugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencySet
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class DependencyVersionsTest {

    @Test
    void addsDefaultDependencies() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'alfresco-dynamic-extension'
        project.apply plugin: 'java'

        project.configurations.compileOnly.runDependencyActions() // Force running defaultDependencies and friends

        DependencySet compileOnlyDependencies = project.configurations.compileOnly.allDependencies

        def annotationsDependency = compileOnlyDependencies.matching({ it.group == "eu.xenit.de" && it.name == "annotations" }).stream().findFirst().orElse(null)
        def webscriptsDependency = compileOnlyDependencies.matching({ it.group == "eu.xenit.de" && it.name == "webscripts" }).stream().findFirst().orElse(null)

        Assert.assertNotNull("A dependency on eu.xenit.de:annotations should be present", annotationsDependency)
        Assert.assertNotNull("A dependency on eu.xenit.de:webscripts should be present", webscriptsDependency)

        Assert.assertEquals("eu.xenit.de:annotations should default to the same version as the plugin", BuildConfig.VERSION, annotationsDependency.version)
        Assert.assertEquals("eu.xenit.de:webscripts should default to the same version as the plugin", BuildConfig.VERSION, webscriptsDependency.version)
    }

    @Test
    public void addsDefaultDependenciesWithAlfrescoPlugin() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'alfresco-dynamic-extension'
        project.apply plugin: AlfrescoPlugin.class
        project.apply plugin: 'java'

        project.configurations.alfrescoProvided.runDependencyActions() // Force running defaultDependencies and friends

        DependencySet alfrescoProvidedDependencies = project.configurations.alfrescoProvided.allDependencies

        def annotationsDependency = alfrescoProvidedDependencies.matching({ it.group == "eu.xenit.de" && it.name == "annotations" }).stream().findFirst().orElse(null)
        def webscriptsDependency = alfrescoProvidedDependencies.matching({ it.group == "eu.xenit.de" && it.name == "webscripts" }).stream().findFirst().orElse(null)

        Assert.assertNotNull("A dependency on eu.xenit.de:annotations should be present", annotationsDependency)
        Assert.assertNotNull("A dependency on eu.xenit.de:webscripts should be present", webscriptsDependency)

        Assert.assertEquals("eu.xenit.de:annotations should default to the same version as the plugin", BuildConfig.VERSION, annotationsDependency.version)
        Assert.assertEquals("eu.xenit.de:webscripts should default to the same version as the plugin", BuildConfig.VERSION, webscriptsDependency.version)
    }

    @Test
    public void addsDependenciesWithVersionSet() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'alfresco-dynamic-extension'
        project.apply plugin: 'java'

        project.alfrescoDynamicExtensions.versions.dynamicExtensions = "99.99.99"

        project.configurations.compileOnly.runDependencyActions() // Force running defaultDependencies and friends

        DependencySet compileOnlyDependencies = project.configurations.compileOnly.allDependencies

        def annotationsDependency = compileOnlyDependencies.matching({ it.group == "eu.xenit.de" && it.name == "annotations" }).stream().findFirst().orElse(null)
        def webscriptsDependency = compileOnlyDependencies.matching({ it.group == "eu.xenit.de" && it.name == "webscripts" }).stream().findFirst().orElse(null)

        Assert.assertNotNull("A dependency on eu.xenit.de:annotations should be present", annotationsDependency)
        Assert.assertNotNull("A dependency on eu.xenit.de:webscripts should be present", webscriptsDependency)

        Assert.assertEquals("eu.xenit.de:annotations should be the requested version", "99.99.99", annotationsDependency.version)
        Assert.assertEquals("eu.xenit.de:webscripts should default to the same version as the plugin", "99.99.99", webscriptsDependency.version)
    }

    @Test
    public void doesNotAddDependenciesWithVersionCleared() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'alfresco-dynamic-extension'
        project.apply plugin: 'java'

        project.alfrescoDynamicExtensions.versions.dynamicExtensions = null

        project.configurations.compileOnly.runDependencyActions() // Force running defaultDependencies and friends

        DependencySet compileOnlyDependencies = project.configurations.compileOnly.allDependencies

        def annotationsDependency = compileOnlyDependencies.matching({ it.group == "eu.xenit.de" && it.name == "annotations" }).stream().findFirst().orElse(null)
        def webscriptsDependency = compileOnlyDependencies.matching({ it.group == "eu.xenit.de" && it.name == "webscripts" }).stream().findFirst().orElse(null)

        Assert.assertNull("A dependency on eu.xenit.de:annotations should not be present", annotationsDependency)
        Assert.assertNull("A dependency on eu.xenit.de:webscripts should not be present", webscriptsDependency)
    }
}
