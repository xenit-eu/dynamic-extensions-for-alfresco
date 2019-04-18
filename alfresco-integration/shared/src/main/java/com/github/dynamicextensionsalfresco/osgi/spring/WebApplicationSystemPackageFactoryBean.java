package com.github.dynamicextensionsalfresco.osgi.spring;

import com.github.dynamicextensionsalfresco.osgi.Configuration;
import com.github.dynamicextensionsalfresco.osgi.JavaPackageScanner;
import com.github.dynamicextensionsalfresco.osgi.PackageCacheMode;
import com.github.dynamicextensionsalfresco.osgi.SystemPackage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides {@link SystemPackage}s by scanning the web application for Java packages.
 * 
 * @author Laurens Fridael
 * 
 */
public class WebApplicationSystemPackageFactoryBean implements FactoryBean<Set<SystemPackage>> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private ObjectFactory<JavaPackageScanner> javaPackageScanner;

	private Configuration configuration;

	/* Main operations */

	@Override
	public boolean isSingleton() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Set<SystemPackage>> getObjectType() {
		return (Class<? extends Set<SystemPackage>>) (Class<?>) Set.class;
	}

	@Override
	public Set<SystemPackage> getObject() throws Exception {
		return createSystemPackages();
	}

	/* Utility operations */

	protected Set<SystemPackage> createSystemPackages() {
		final JavaPackageScanner packageScanner = javaPackageScanner.getObject();
		final boolean validCache = packageScanner.isCacheValid(configuration.getSystemPackageCache());

		Set<SystemPackage> packages;
		if (validCache) {
			packages = getCachedPackages();
		} else {
			packages = Collections.emptySet();
		}
		final boolean cacheDoesNotExist = CollectionUtils.isEmpty(packages);

		final PackageCacheMode packageCacheMode = configuration.getSystemPackageCacheMode();
		if (!packageCacheMode.isReadFromCache() || cacheDoesNotExist || !validCache) {
			packages = packageScanner.scanWebApplicationPackages();
		}

		if (packageCacheMode.isForceWriteToCache() || (cacheDoesNotExist && packageCacheMode.isWriteToCache())) {
			writeCachedPackages(packages);
		}

		if (!packageCacheMode.isWriteToCache()) {
            final File cacheFile = configuration.getSystemPackageCache();
			if (cacheFile.isFile()) {
				cacheFile.delete();
			}
		}

		return packages;
	}

    private Set<SystemPackage> getCachedPackages() {
        File packageCache = configuration.getSystemPackageCache();
		if (packageCache.isFile()) {
            try {
                final LineNumberReader in = new LineNumberReader(new InputStreamReader(new FileInputStream(packageCache)));
                try {
                    final Set<SystemPackage> systemPackages = new LinkedHashSet<SystemPackage>(4500, 0.1f);
                    for (String line; (line = in.readLine()) != null;) {
                        line = line.trim();
                        if (!line.isEmpty()) {
                            systemPackages.add(SystemPackage.fromString(line));
                        }
                    }
                    return systemPackages;
                } catch (final IOException e) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Error reading cached system package configuration from repository: {}.",
                                e.getMessage());
                    }
                } finally {
                    IOUtils.closeQuietly(in);
                }
            } catch (IOException e) {
                logger.warn("Failed to open Java packages cache reader", e);
            }
        }
		return null;
	}

	private void writeCachedPackages(final Set<SystemPackage> packages) {
		final File packageCache = configuration.getSystemPackageCache();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(packageCache));
            for (final SystemPackage systemPackage : packages) {
                writer.println(systemPackage.toString());
            }
            logger.debug("Wrote system package list to {}.", packageCache.getAbsolutePath());
		} catch (FileNotFoundException e) {
            logger.warn("Failed t");
        } finally {
			IOUtils.closeQuietly(writer);
		}
	}

	/* Dependencies */

	public void setJavaPackageScanner(final ObjectFactory<JavaPackageScanner> javaPackageScanner) {
		Assert.notNull(javaPackageScanner);
		this.javaPackageScanner = javaPackageScanner;
	}

	/* Configuration */

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
