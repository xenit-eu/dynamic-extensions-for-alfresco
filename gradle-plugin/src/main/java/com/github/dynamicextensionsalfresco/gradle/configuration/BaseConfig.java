package com.github.dynamicextensionsalfresco.gradle.configuration;

import com.github.dynamicextensionsalfresco.gradle.tasks.DeBundleTaskConvention;
import com.github.dynamicextensionsalfresco.gradle.tasks.InstallBundle;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.util.GUtil;

/**
 * @author Laurent Van der Linden
 */
public class BaseConfig {

    private final Project project;
    private final Repository repository;
    private final Versions versions;

    @Inject
    public BaseConfig(Project project, ObjectFactory objectFactory) {
        this.project = project;
        repository = objectFactory.newInstance(Repository.class);
        versions = objectFactory.newInstance(Versions.class);
    }

    public Repository getRepository() {
        return repository;
    }

    public Versions getVersions() {
        return versions;
    }

    public void repository(Action<? super Repository> action) {
        action.execute(repository);
    }

    public void versions(Action<? super Versions> action) {
        action.execute(versions);
    }

    private static String createInstallNameFromJarName(String jarName) {
        return "install"+ GUtil.toCamelCase(jarName.replaceAll("[Jj]ar$", ""))+"Bundle";
    }

    public void configureBundle(Jar jar) {
        DeBundleTaskConvention.apply(jar);
        project.getTasks().create(createInstallNameFromJarName(jar.getName()), InstallBundle.class,
                installBundle -> installBundle.getFiles().from(jar));
    }

    public void configureBundle(TaskProvider<Jar> jarProvider) {
        jarProvider.configure(DeBundleTaskConvention::apply);
        project.getTasks().create(createInstallNameFromJarName(jarProvider.getName()), InstallBundle.class,
                installBundle -> installBundle.getFiles().from(jarProvider));
    }
}
