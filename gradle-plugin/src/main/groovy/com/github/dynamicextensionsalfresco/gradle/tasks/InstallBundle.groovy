package com.github.dynamicextensionsalfresco.gradle.tasks

import com.github.dynamicextensionsalfresco.gradle.BundleService
import com.github.dynamicextensionsalfresco.gradle.RestClientException
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.BuildException

/**
 * @author Laurent Van der Linden
 */
class InstallBundle extends DefaultTask {
    def jarFile

    @Input def getBundleJar() {
        if (jarFile) {
            return Project.file(jarFile)
        } else {
            return project.jar.archivePath
        }
    }

    @TaskAction
    def installBundle() {
        BundleService bundleService = new BundleService()
        bundleService.client.with {
            endpoint = project.alfrescoDynamicExtensions.repository.endpoint
            authentication = project.alfrescoDynamicExtensions.repository.authentication
        }
        try {
            def response = bundleService.installBundle(getBundleJar())
            project.logger.info response.message
            project.logger.info "Bundle ID: ${response.bundleId}"
        } catch (RestClientException e) {
            if (e.status.code == 401) {
                throw new BuildException("User not authorized to install bundles in repository. " +
                        "Make sure you specify the correct username and password for an admin-level account.", e)
            } else if (e.status.code == 500) {
                throw new BuildException("Error installing bundle in repository: ${e.message}", e)
            }
        }
    }
}
