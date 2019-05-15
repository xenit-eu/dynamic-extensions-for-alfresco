package com.github.dynamicextensionsalfresco.gradle.tasks;

import com.github.dynamicextensionsalfresco.gradle.configuration.Repository;
import com.github.dynamicextensionsalfresco.gradle.internal.BundleService;
import com.github.dynamicextensionsalfresco.gradle.internal.rest.RestClient;
import com.github.dynamicextensionsalfresco.gradle.internal.rest.RestClientException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;

/**
 * Installs a (set of) bundles into a running Alfresco server with Dynamic Extensions.
 */
public class InstallBundle extends DefaultTask {
    private static final Logger LOGGER = Logging.getLogger(InstallBundle.class);
    private ConfigurableFileCollection files = getProject().files();
    private Property<Repository> repository;
    private Property<BundleService> bundleService;

    @Inject
    public InstallBundle(ObjectFactory objectFactory) {
        repository = objectFactory.property(Repository.class);
        bundleService = objectFactory.property(BundleService.class);
        bundleService.set(repository.map(repo ->
                new BundleService(new RestClient(repo.getEndpoint(), repo.getAuthentication()))
        ));
    }

    @Internal
    public Property<BundleService> getBundleService() {
        return bundleService;
    }

    /**
     * @return the repository coordinates to install bundles into
     */
    @Input
    public Property<Repository> getRepository() {
        return repository;
    }


    /**
     * Create new repository coordinates and configures them
     * @param repositoryAction Action that configures repository coordinates
     */
    public void repository(Action<? super Repository> repositoryAction) {
        Repository repo = getProject().getObjects().newInstance(Repository.class);
        repositoryAction.execute(repo);
        repository.set(repo);
    }

    /**
     * @return Set of files that will be installed into the repository
     */
    @InputFiles
    @SkipWhenEmpty
    public ConfigurableFileCollection getFiles() {
        return files;
    }

    /**
     * Sets the files that will be installed into the repository
     * @param paths The paths to bundles. The given paths are evaluated as per {@link org.gradle.api.Project#files(Object...)}.
     */
    public void setFiles(Object... paths) {
        this.files.setFrom(paths);
    }

    @TaskAction
    public void installBundles() {
        try {
            files.forEach(this::install);
        } catch (RestClientException e) {
            if (e.getStatus().getCode() == 401) {
                throw new GradleException("User not authorized to install bundles in repository. " +
                        "Make sure you specify the correct username and password for an admin-level account.", e);
            } else {
                throw new GradleException("Error installing bundles in repository "+repository.get().getEndpoint().getUrl().get()+": "+e.getMessage(), e);
            }
        }
    }

    private void install(File bundle) {
        try {
            Map<String, Object> response = (Map<String, Object>) bundleService.get().installBundle(bundle);
            LOGGER.debug((String)response.get("message"));
            LOGGER.info("{} deployed to {}: Bundle ID {}", bundle.getName(), repository.get().getEndpoint().getUrl().get(), response.get("bundleId"));
        } catch (IOException e) {
            throw new GradleException("Error installing bundle "+bundle.getName()+" in repository "+repository.get().getEndpoint().getUrl().get()+": "+e.getMessage(), e);
        }
    }
}
