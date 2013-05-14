package nl.runnable.alfresco.osgi.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.runnable.alfresco.osgi.JavaPackageScanner;
import nl.runnable.alfresco.osgi.PackageCacheMode;
import nl.runnable.alfresco.osgi.RepositoryStoreService;
import nl.runnable.alfresco.osgi.SystemPackage;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

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

	private RepositoryStoreService repositoryStoreService;

	private FileFolderService fileFolderService;

	private PackageCacheMode packageCacheMode;

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
		Set<SystemPackage> packages = getCachedPackages();
		final boolean cacheDoesNotExist = CollectionUtils.isEmpty(packages);

		if (packageCacheMode.isReadFromCache() == false || cacheDoesNotExist) {
			packages = javaPackageScanner.getObject().scanWebApplicationPackages();
		}

		if (packageCacheMode.isForceWriteToCache() || (cacheDoesNotExist && packageCacheMode.isWriteToCache())) {
			writeCachedPackages(packages);
		}

		if (packageCacheMode.isWriteToCache() == false) {
			final FileInfo currentCache = repositoryStoreService.getSystemPackageCache();
			if (currentCache != null) {
				fileFolderService.delete(currentCache.getNodeRef());
			}
		}

		return packages;
	}

	private Set<SystemPackage> getCachedPackages() {
		final FileInfo systemPackagesCached = repositoryStoreService.getSystemPackageCache();
		if (systemPackagesCached != null) {
			final ContentReader contentReader = fileFolderService.getReader(systemPackagesCached.getNodeRef());
			final LineNumberReader in = new LineNumberReader(new InputStreamReader(
					contentReader.getContentInputStream()));
			try {
				final Set<SystemPackage> systemPackages = new LinkedHashSet<SystemPackage>(4500, 0.1f);
				for (String line; (line = in.readLine()) != null;) {
					line = line.trim();
					if (line.isEmpty() == false) {
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
		}
		return null;
	}

	private void writeCachedPackages(final Set<SystemPackage> packages) {
		final FileInfo systemPackagesCached = repositoryStoreService.createSystemPackageCache();
		final ContentWriter cw = fileFolderService.getWriter(systemPackagesCached.getNodeRef());
		final PrintWriter writer = new PrintWriter(cw.getContentOutputStream());
		try {
			for (final SystemPackage systemPackage : packages) {
				writer.println(systemPackage.toString());
			}
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

	public void setRepositoryStoreService(final RepositoryStoreService repositoryStoreService) {
		this.repositoryStoreService = repositoryStoreService;
	}

	public void setFileFolderService(final FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setPackageCacheMode(final PackageCacheMode packageCacheMode) {
		this.packageCacheMode = packageCacheMode;
	}
}
