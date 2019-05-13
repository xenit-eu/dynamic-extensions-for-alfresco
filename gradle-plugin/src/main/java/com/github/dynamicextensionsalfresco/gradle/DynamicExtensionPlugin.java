package com.github.dynamicextensionsalfresco.gradle;

import com.github.dynamicextensionsalfresco.gradle.configuration.BaseConfig;
import com.github.dynamicextensionsalfresco.gradle.tasks.DeBundleTaskConvention;
import com.github.dynamicextensionsalfresco.gradle.tasks.InstallBundle;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.util.GradleVersion;

public class DynamicExtensionPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        if(GradleVersion.current().compareTo(GradleVersion.version("4.10")) < 0) {
            throw new GradleException("The Dynamic Extension plugin requires at least Gradle 4.10. Your current gradle version is: "+GradleVersion.current().getVersion());
        }
        // Register alfrescoDynamicExtensions configuration block
        BaseConfig config = project.getObjects().newInstance(BaseConfig.class);
        project.getExtensions().add("alfrescoDynamicExtensions", config);


        project.getPlugins().withType(JavaPlugin.class, p -> {
            // Apply DeBundleTaskConvention to the default Jar task
            project.getTasks().withType(Jar.class).named(JavaPlugin.JAR_TASK_NAME).configure(jar -> {
                DeBundleTaskConvention deBundleTaskConvention = new DeBundleTaskConvention(jar);
                jar.getConvention().getPlugins().put("bundle", deBundleTaskConvention);
                jar.doLast(t -> {
                    deBundleTaskConvention.buildDeBundle();
                });
            });

            // Add installBundle task that uploads the jar by default
            project.getTasks().register("installBundle", InstallBundle.class, task -> {
                task.dependsOn(project.getTasks().named(JavaPlugin.JAR_TASK_NAME));
                task.setFiles(project.files(project.getTasks().named(JavaPlugin.JAR_TASK_NAME)));
            });
        });


        // Configure all InstallBundle type tasks to point to the default repository if none is explicitly set
        project.getTasks().withType(InstallBundle.class, task -> {
            if(!task.getRepository().isPresent()) {
                task.getRepository().set(config.getRepository());
            }
        });

    }
}
