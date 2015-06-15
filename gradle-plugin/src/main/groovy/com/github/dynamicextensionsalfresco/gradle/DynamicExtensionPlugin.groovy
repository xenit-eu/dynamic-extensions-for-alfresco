package com.github.dynamicextensionsalfresco.gradle

import com.github.dynamicextensionsalfresco.gradle.configuration.BaseConfig
import com.github.dynamicextensionsalfresco.gradle.tasks.InstallBundle
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin that configures build settings for an Alfresco Dynamic Extension.
 * 
 * @author Laurens Fridael
 * @author Laurent Van der Linden
 */
class DynamicExtensionPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		configurePlugins(project)
		configureExtensions(project)
		configureTasks(project)
		project.afterEvaluate {
            configureDependencies(project)
			configureRepositories(project)
			configureJarManifest(project)
		}
	}

    static void configurePlugins(Project project) {
		project.apply plugin: "java"
		project.apply plugin: "osgi"
	}

    static void configureExtensions(Project project) {
		project.extensions.create("alfrescoDynamicExtensions", BaseConfig.class, project)
	}

    static void configureTasks(Project project) {
		project.tasks.create("installBundle", InstallBundle.class).dependsOn('jar')
	}

	void configureDependencies(Project project) {
		def surf = [
			version: project.alfrescoDynamicExtensions.versions.surf
		]
		def dynamicExtensions = [
			version: project.alfrescoDynamicExtensions.versions.dynamicExtensions
		]
		def spring = [
			version: project.alfrescoDynamicExtensions.versions.spring
		]
		project.dependencies {
			compile ("org.springframework.extensions.surf:spring-webscripts:${surf.version}") { transitive = false }
			compile ("org.springframework.extensions.surf:spring-surf-core:${surf.version}") { transitive = false }
			compile ("com.github.dynamicextensionsalfresco:annotations:${dynamicExtensions.version}") { transitive = false }
			compile ("com.github.dynamicextensionsalfresco:annotations-runtime:${dynamicExtensions.version}") { transitive = false }
            compile ("com.github.dynamicextensionsalfresco:webscripts:${dynamicExtensions.version}") { transitive = false }
			// Since Spring is so fundamental, this is the one dependency we leave as transitive.
			compile ("org.springframework:spring-context:${spring.version}")
			// JSR-250 API contains the @Resource annotation for referencing dependencies by name.
			compile ('javax.annotation:jsr250-api:1.0') { transitive = false }
		}
	}

	void configureJarManifest(Project project) {
        project.jar {
            manifest {
                instruction "Alfresco-Dynamic-Extension", "true"
            }
        }

        /*
         * If the task has already set the "Import-Package", we leave it as is.
         */
        if (!project.jar.manifest.instructionValue("Import-Package")) {
            project.jar {
                manifest {
					instruction 'Import-Package', '*'
					instruction 'DynamicImport-Package', '*'
                }
            }
        }
    }

	void configureRepositories(Project project) {
		project.repositories {
			jcenter()
			maven { url "https://artifacts.alfresco.com/nexus/content/groups/public" }
			maven { url "http://repo.springsource.org/release" }
		}
	}
}


