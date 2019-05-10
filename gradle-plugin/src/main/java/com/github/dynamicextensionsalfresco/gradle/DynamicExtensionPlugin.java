package com.github.dynamicextensionsalfresco.gradle;

import aQute.bnd.gradle.BndBuilderPlugin;
import aQute.bnd.gradle.BundleTaskConvention;
import aQute.bnd.osgi.Constants;
import com.github.dynamicextensionsalfresco.gradle.configuration.BaseConfig;
import com.github.dynamicextensionsalfresco.gradle.internal.BndHandler;
import com.github.dynamicextensionsalfresco.gradle.tasks.InstallBundle;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.jvm.tasks.Jar;
import org.gradle.util.GradleVersion;

public class DynamicExtensionPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        if(GradleVersion.current().compareTo(GradleVersion.version("4.10")) < 0) {
            throw new GradleException("The Dynamic Extension plugin requires at least Gradle 4.10. Your current gradle version is: "+GradleVersion.current().getVersion());
        }
        // Apply BND plugin to handle OSGi manifest
        project.getPlugins().apply(BndBuilderPlugin.class);

        // Register alfrescoDynamicExtensions configuration block
        BaseConfig config = project.getObjects().newInstance(BaseConfig.class);
        project.getExtensions().add("alfrescoDynamicExtensions", config);


        // Reconfigure BND to add DE header and import package
        project.getTasks().withType(Jar.class).named("jar").configure(jar -> {
            BundleTaskConvention bndConvention = jar.getConvention().getPlugin(BundleTaskConvention.class);
            BndHandler bndHandler = new BndHandler(jar, bndConvention);

            bndHandler.setHeader("Alfresco-Dynamic-Extension", "true");
            if(!bndHandler.hasHeader(Constants.IMPORT_PACKAGE)) {
                bndHandler.setHeader(Constants.IMPORT_PACKAGE, "*");
                bndHandler.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*");
            }
        });


        // Add installBundle task that uploads the jar by default
        project.getTasks().register("installBundle", InstallBundle.class, task -> {
            task.dependsOn(project.getTasks().named("jar"));
            task.setFiles(project.files(project.getTasks().named("jar")));
        });

        // Configure all InstallBundle type tasks to point to the default repository if none is explicitly set
        project.getTasks().withType(InstallBundle.class, task -> {
            if(!task.getRepository().isPresent()) {
                task.getRepository().set(config.getRepository());
            }
        });

    }
}
