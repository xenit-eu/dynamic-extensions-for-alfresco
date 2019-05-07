package com.github.dynamicextensionsalfresco.osgi;

import aQute.bnd.osgi.Constants;
import com.github.dynamicextensionsalfresco.osgi.io.FileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Manages a {@link Framework}'s lifecycle. It takes care of initializing and destroying the Framework and
 * (un)registering services and {@link BundleListener}s.
 *
 * @author Laurens Fridael
 */

@Service
public class DefaultFrameworkManager implements ResourceLoaderAware, FrameworkManager {

    private final Logger logger = LoggerFactory.getLogger(DefaultFrameworkManager.class);

    private final Framework framework;
    private final List<BundleContextRegistrar> bundleContextRegistrars;
    private final RepositoryStoreService repositoryStoreService;
    private final ContentService contentService;
    private final Configuration configuration;
    private final String blueprintBundlesLocation;
    private final String standardBundlesLocation;
    private final String customBundlesLocation;

    private ResourcePatternResolver resourcePatternResolver;
    private ArrayList<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();

    public DefaultFrameworkManager(
            Framework framework,
            List<BundleContextRegistrar> bundleContextRegistrars,
            RepositoryStoreService repositoryStoreService,
            ContentService contentService,
            Configuration configuration,
            String blueprintBundlesLocation,
            String standardBundlesLocation,
            String customBundlesLocation) {

        this.framework = framework;
        this.bundleContextRegistrars = bundleContextRegistrars;
        this.repositoryStoreService = repositoryStoreService;
        this.contentService = contentService;
        this.configuration = configuration;
        this.blueprintBundlesLocation = blueprintBundlesLocation;
        this.standardBundlesLocation = standardBundlesLocation;
        this.customBundlesLocation = customBundlesLocation;
    }

    @Override
    public Framework getFramework() {
        return framework;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.isInstanceOf(ResourcePatternResolver.class, resourceLoader);
        this.resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
    }

    /**
     * Starts the {@link Framework} and registers services and {@link BundleListener}s.
     */
    public final void initialize() {
        this.startFramework();
        this.registerServices();
        this.startBundles(this.installCoreBundles());
        if (this.getRepositoryInstallEnabled()) {
            this.startBundles(this.installRepositoryBundles());
        }

    }

    private boolean getRepositoryInstallEnabled() {
        return this.configuration.getRepositoryBundlesEnabled();
    }

    protected void startFramework() {
        try {
            logger.debug("Starting Framework");
            framework.start();
        } catch (BundleException e) {
            logger.error("Could not start Framework.", e);
        }
    }

    protected void registerServices() {
        logger.debug("Registering services.");
        for (BundleContextRegistrar bundleContextRegistrar : bundleContextRegistrars) {
            final List<ServiceRegistration<?>> servicesRegistered = bundleContextRegistrar
                    .registerInBundleContext(framework.getBundleContext());
            serviceRegistrations.addAll(servicesRegistered);
        }
    }

    /**
     * Installs the Bundles that make up the core of the framework. These bundles are started before any extension
     * bundles.
     *
     * The core bundles consist of:
     *
     * - Gemini Blueprint
     * - File Install (optional, can be disabled)
     * - Any additional standard bundles configured through {@link #standardBundlesLocation}.
     *
     * @return installed bundles
     */
    protected List<Bundle> installCoreBundles() {
        List<Bundle> bundles = new ArrayList<>();
        try {
            List<String> locationPatterns = new ArrayList<>();
            locationPatterns.add(blueprintBundlesLocation);

            if (StringUtils.hasText(standardBundlesLocation)) {
                locationPatterns.add(standardBundlesLocation);
            }

            if (StringUtils.hasText(customBundlesLocation)) {
                locationPatterns.add(customBundlesLocation);
            }

            for (String locationPattern : locationPatterns) {
                try {
                    for (Resource bundleResource : resourcePatternResolver.getResources(locationPattern)) {
                        String location = bundleResource.getURI().toString();
                        logger.debug("Installing Bundle: {}", location);
                        try {
                            Bundle bundle = installBundle(bundleResource, location);
                            bundles.add(bundle);
                        } catch (BundleException e) {
                            logger.error("Error installing Bundle in {}: {}", location, e);
                        }
                    }
                } catch (FileNotFoundException e) {
                    logger.debug("Could not find Bundles at location '{}'.", locationPattern);
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error installing core Bundles: " + ioe.getMessage(), ioe);
        }

        return bundles;
    }

    /**
     * optimistic install: first install & if it turns out to be a regular jar, replace with wrapped jar
     */
    private Bundle installBundle(Resource bundleResource, String location) throws IOException, BundleException {
        Bundle bundle = framework.getBundleContext().installBundle(location, bundleResource.getInputStream());

        if (bundle.getSymbolicName() == null) {
            bundle.uninstall();
            File localCopy = FileUtil.toTempFile(bundleResource.getInputStream(), "wrapped", ".jar");
            File bundleFile = FileUtil.convertToBundle(localCopy, bundleResource.getFilename());
            bundle = framework.getBundleContext().installBundle(location, new FileInputStream(bundleFile));
            logger.info("Wrapped plain jar as a OSGi bundle: " + bundle.getSymbolicName());
        }

        return bundle;
    }

    protected void startBundles(List<Bundle> bundles) {
        FrameworkWiring frameworkWiring = framework.adapt(FrameworkWiring.class);
        if (!frameworkWiring.resolveBundles(bundles)) {
            logger.warn("Could not resolve all {} bundles.", bundles.size());
        }

        List<Bundle> sortedByDependency = BundleDependencies.sortByDependencies(bundles);

        for (Bundle bundle : sortedByDependency) {
            if (!isFragmentBundle(bundle)) {
                this.startBundle(bundle);
            }
        }
    }

    protected void startBundle(Bundle bundle) {
        try {
            logger.debug("Starting Bundle {}.", bundle.getSymbolicName());
            bundle.start();
        } catch (Exception e) {
            logger.error("Error starting bundle {}:", bundle.getSymbolicName(), e);

        }
    }


    protected boolean isFragmentBundle(Bundle bundle) {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }

    /**
     * Installs the {@link Bundle}s in the repository.
     *
     *
     * This implementation uses {@link RepositoryStoreService}.
     */
    protected List<Bundle> installRepositoryBundles() {
        List<Bundle> bundles = new ArrayList<>();
        for (FileInfo jarFile : repositoryStoreService.getBundleJarFiles()) {
            try {
                String location = String
                        .format("%s/%s", repositoryStoreService.getBundleRepositoryLocation(), jarFile.getName());
                logger.debug("Installing Bundle: {}", location);
                ContentReader reader = contentService.getReader(jarFile.getNodeRef(), ContentModel.PROP_CONTENT);
                if (reader != null) {
                    Bundle bundle = framework.getBundleContext()
                            .installBundle(location, reader.getContentInputStream());
                    bundles.add(bundle);
                } else {
                    logger.warn("unable to read extension content for {}", jarFile.getNodeRef());
                }
            } catch (Exception e) {
                logger.warn("Error installing Bundle: {}", jarFile.getNodeRef());
            }

        }
        return bundles;
    }

    /**
     * Unregisters services and {@link BundleListener}s and stops the {@link Framework}.
     */
    protected void destroy() {
        unregisterServices();
        stopFramework();
    }


    protected void unregisterServices() {
        Iterator<ServiceRegistration<?>> it = serviceRegistrations.iterator();
        while (it.hasNext()) {
            ServiceRegistration<?> serviceRegistration = it.next();
            try {
                logger.debug("Unregistering service {}", serviceRegistration.getReference());
                serviceRegistration.unregister();
            } catch (RuntimeException e) {
                logger.warn("Error unregistering service $serviceRegistration.", e);
            } finally {
                it.remove();
            }
        }
    }

    protected void stopFramework() {
        try {
            logger.debug("Stopping Framework.");
            framework.stop();
            framework.waitForStop(0);
        } catch (BundleException e) {
            logger.error("Could not stop Framework.", e);
        } catch (InterruptedException ignore) {
        }
    }
}