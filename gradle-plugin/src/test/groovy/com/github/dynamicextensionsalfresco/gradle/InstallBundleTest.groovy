package com.github.dynamicextensionsalfresco.gradle


import com.github.dynamicextensionsalfresco.gradle.configuration.Repository
import com.github.dynamicextensionsalfresco.gradle.internal.BundleService
import com.github.dynamicextensionsalfresco.gradle.internal.rest.RestClient
import com.github.dynamicextensionsalfresco.gradle.internal.rest.RestClientPostFileOptions
import com.github.dynamicextensionsalfresco.gradle.tasks.InstallBundle
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito

class InstallBundleTest {

    @Test
    public void installsBundles() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'alfresco-dynamic-extension'

        RestClient restClientMock = Mockito.mock(RestClient.class)
        BundleService bundleService = new BundleService(restClientMock)
        project.tasks.installBundle { task ->
            task.bundleService = bundleService
        }
        project.tasks.jar.destinationDir.mkdirs()
        project.tasks.jar.archivePath.write("test jar")

        def postFileCaptor = ArgumentCaptor.forClass(RestClientPostFileOptions)

        Mockito.when(restClientMock.postFile(postFileCaptor.capture())).thenReturn(["message": "OK", "bundleId": "test"])

        // Run all the actions of the task
        project.tasks.installBundle.getActions().forEach({ a -> a.execute(project.tasks.installBundle) })

        def postFileConfig = postFileCaptor.value

        Assert.assertEquals(project.tasks.jar.archivePath, postFileConfig.file)
    }

    @Test
    public void installBundleTasksConfiguredAutomatically() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'alfresco-dynamic-extension'
        project.tasks.jar.destinationDir.mkdirs()
        project.tasks.jar.archivePath.write("test jar")

        project.tasks.create("installTestBundle", InstallBundle) {
            files = project.tasks.jar.archivePath
        }

        Assert.assertNotNull(project.tasks.installTestBundle.bundleService)
        Assert.assertTrue(project.tasks.installTestBundle.bundleService.isPresent())
        Assert.assertEquals(project.alfrescoDynamicExtensions.repository.endpoint, project.tasks.installTestBundle.repository.get().endpoint)
        Assert.assertEquals(project.alfrescoDynamicExtensions.repository.authentication, project.tasks.installTestBundle.repository.get().authentication)
    }

    @Test
    public void installBundleTasksConfiguredManually() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'alfresco-dynamic-extension'

        project.ext.otherAlfresco = project.objects.newInstance(Repository)
        project.ext.otherAlfresco.endpoint {
            port = 9999
        }

        project.tasks.jar.destinationDir.mkdirs()
        project.tasks.jar.archivePath.write("test jar")
        project.tasks.create("installTestBundle", InstallBundle) {
            repository = project.ext.otherAlfresco
            files = project.tasks.jar.archivePath
        }

        Assert.assertNotNull(project.tasks.installTestBundle.bundleService)
        Assert.assertTrue(project.tasks.installTestBundle.bundleService.isPresent())
        Assert.assertNotEquals(project.alfrescoDynamicExtensions.repository.endpoint, project.tasks.installTestBundle.repository.get().endpoint)
        Assert.assertNotEquals(project.alfrescoDynamicExtensions.repository.authentication, project.tasks.installTestBundle.repository.get().authentication)
        Assert.assertEquals(project.ext.otherAlfresco.endpoint, project.tasks.installTestBundle.repository.get().endpoint)
        Assert.assertEquals(project.ext.otherAlfresco.authentication, project.tasks.installTestBundle.repository.get().authentication)
    }

    @Test
    public void installBundleTaskIsCreated() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'alfresco-dynamic-extension'

        project.configure(project.alfrescoDynamicExtensions, {
            repository {
                endpoint {
                    host = 'localhost'
                    port = '9090'
                    serviceUrl = '/alfresco/service'
                }
                authentication {
                    username = 'admin'
                    password = 'password'
                }
            }
            versions {
                dynamicExtensions = '1.1.0'
            }
        })

        Assert.assertTrue(project.tasks.installBundle instanceof InstallBundle)
    }
}
