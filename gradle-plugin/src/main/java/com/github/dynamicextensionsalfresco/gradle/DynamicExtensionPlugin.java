package com.github.dynamicextensionsalfresco.gradle;

import com.github.dynamicextensionsalfresco.gradle.configuration.BaseConfig;
import com.github.dynamicextensionsalfresco.gradle.configuration.Repository;
import com.github.dynamicextensionsalfresco.gradle.configuration.Versions;
import com.github.dynamicextensionsalfresco.gradle.tasks.DeBundleTaskConvention;
import com.github.dynamicextensionsalfresco.gradle.tasks.InstallBundle;
import java.util.stream.Collectors;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.util.GradleVersion;

public class DynamicExtensionPlugin implements Plugin<Project> {
    private BaseConfig baseConfig;

    @Override
    public void apply(Project project) {
        if(GradleVersion.current().compareTo(GradleVersion.version("4.10")) < 0) {
            throw new GradleException("The Dynamic Extension plugin requires at least Gradle 4.10. Your current gradle version is: "+GradleVersion.current().getVersion());
        }
        // Register alfrescoDynamicExtensions configuration block
        baseConfig = project.getObjects().newInstance(BaseConfig.class);
        project.getExtensions().add("alfrescoDynamicExtensions", baseConfig);

        configureDependencies(project);
        configureDefaultTasks(project);

        // Configure all InstallBundle type tasks to point to the default repository if none is explicitly set
        project.getTasks().withType(InstallBundle.class, task -> {
            if(!task.getRepository().isPresent()) {
                task.getRepository().set(getRepositoryConfiguration());
            }
        });
    }

    /**
     * @return The repository configuration for this plugin
     */
    public Repository getRepositoryConfiguration() {
        return baseConfig.getRepository();
    }

    /**
     * Configures tasks for building and installing dynamic extensions
     *
     * Adds the {@link DeBundleTaskConvention} convention to the default {@link Jar} task
     * and creates an {@code installBundle} task that uploads the built jar.
     */
    private void configureDefaultTasks(Project project) {
        project.getPlugins().withType(JavaPlugin.class, p -> {
            // Apply DeBundleTaskConvention to the default Jar task
            project.getTasks().withType(Jar.class).named(JavaPlugin.JAR_TASK_NAME).configure(jar -> {
                DeBundleTaskConvention deBundleTaskConvention = new DeBundleTaskConvention(jar);
                jar.getConvention().getPlugins().put("bundle", deBundleTaskConvention);
            });

            // Add installBundle task that uploads the jar by default
            project.getTasks().register("installBundle", InstallBundle.class, task -> {
                task.dependsOn(project.getTasks().named(JavaPlugin.JAR_TASK_NAME));
                task.setFiles(project.files(project.getTasks().named(JavaPlugin.JAR_TASK_NAME)));
            });
        });
    }

    /**
     * Configures dynamic extensions dependencies
     *
     * When {@link Versions#getDynamicExtensions()} is present, dependencies on the DE annotations and webscripts are added.
     *
     * When the {@code eu.xenit.alfresco} gradle plugin is applied, these dependencies are added to {@code alfrescoProvided}.
     * If that plugin is not applied, the dependencies are added to {@code compileOnly}
     */
    private void configureDependencies(Project project) {
        // Detached configuration, so we can attach it to multiple different configurations if we want to
        // FIXME: replaced with a named configuration to work around https://github.com/gradle/gradle/issues/9398
        Configuration dynamicExtensions = project.getConfigurations().create("__dynamicExtensionsInternalDetachedConfiguration");
        dynamicExtensions.defaultDependencies(dependencies -> {
            if(baseConfig.getVersions().getDynamicExtensions().isPresent()) {
                String dynamicExtensionsVersion = baseConfig.getVersions().getDynamicExtensions().get();
                dependencies.add(project.getDependencies().create("eu.xenit.de:annotations:"+dynamicExtensionsVersion));
                dependencies.add(project.getDependencies().create("eu.xenit.de:webscripts:"+dynamicExtensionsVersion));
            }
        });

        project.getPlugins().withType(JavaPlugin.class, p -> {
            project.getConfigurations().named(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).configure(compileOnly -> {
                compileOnly.extendsFrom(dynamicExtensions);
            });
        });


        project.getPluginManager().withPlugin("eu.xenit.alfresco", appliedPlugin -> {
            project.getConfigurations().named("alfrescoProvided").configure(alfrescoProvided -> {
                alfrescoProvided.extendsFrom(dynamicExtensions);
            });

            project.getPlugins().withType(JavaPlugin.class, p -> {
                // If the eu.xenit.alfresco plugin is applied, remove the configuration from compileOnly
                project.getConfigurations().named(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).configure(compileOnly -> {
                    compileOnly.setExtendsFrom(
                            compileOnly.getExtendsFrom()
                                    .stream()
                                    .filter(c -> !c.equals(dynamicExtensions))
                                    .collect(Collectors.toSet())
                    );
                });
            });
        });
    }
}
