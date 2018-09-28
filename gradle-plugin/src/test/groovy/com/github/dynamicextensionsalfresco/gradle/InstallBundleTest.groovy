package com.github.dynamicextensionsalfresco.gradle

import com.github.dynamicextensionsalfresco.gradle.tasks.InstallBundle
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class InstallBundleTest {
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