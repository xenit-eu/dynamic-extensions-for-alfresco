package com.github.dynamicextensionsalfresco.gradle.tasks

import com.github.dynamicextensionsalfresco.gradle.BundleService
import com.github.dynamicextensionsalfresco.gradle.RestClientException
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.BuildException

/**
 * @author Laurent Van der Linden
 */
class InstallBundle extends DefaultTask {
    def FileCollection configuration

    @TaskAction
    def installBundle() {
        BundleService bundleService = new BundleService()
        bundleService.client.with {
            endpoint = project.alfrescoDynamicExtensions.repository.endpoint
            authentication = project.alfrescoDynamicExtensions.repository.authentication
        }
        try {
            if (configuration) {
                configuration.each { bundle ->
                    install(bundle, bundleService)
                }
            } else {
                install(project.jar.archivePath, bundleService)
            }
        } catch (RestClientException e) {
            if (e.status.code == 401) {
                throw new BuildException("User not authorized to install bundles in repository. " +
                        "Make sure you specify the correct username and password for an admin-level account.", e)
            } else if (e.status.code == 500) {
                throw new BuildException("Error installing bundle in repository: ${e.message}", e)
            }
        }
    }

    def install(bundle, bundleService) {
        def response = bundleService.installBundle(bundle)
        project.logger.debug response.message
        project.logger.info "${bundle.name} deployed to ${bundleService.client.endpoint.host}: Bundle ID ${response.bundleId}"
    }
}
