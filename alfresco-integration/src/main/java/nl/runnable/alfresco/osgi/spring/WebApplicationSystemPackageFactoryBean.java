package nl.runnable.alfresco.osgi.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import nl.runnable.alfresco.osgi.JavaPackageScanner;
import nl.runnable.alfresco.osgi.RepositoryStoreService;
import nl.runnable.alfresco.osgi.SystemPackage;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.Assert;

/**
 * Provides {@link SystemPackage}s by scanning the web application for Java packages.
 * 
 * @author Laurens Fridael
 * 
 */
public class WebApplicationSystemPackageFactoryBean implements FactoryBean<Set<SystemPackage>> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String SYSTEM_PACKAGES_FILE_NAME = "system-packages.txt";

	/* Dependencies */

	private ObjectFactory<JavaPackageScanner> javaPackageScanner;

	private RepositoryStoreService repositoryStoreService;

	private FileFolderService fileFolderService;

	/* Configuration */

	private Set<SystemPackage> basePackages;

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
		final List<String> basePackageNames = new ArrayList<String>();
		for (final SystemPackage basePackage : getBasePackages()) {
			basePackageNames.add(basePackage.getName());
		}
		Set<SystemPackage> packages = getCachedPackages();
		if (packages == null || packages.isEmpty()) {
			packages = javaPackageScanner.getObject().scanWebApplicationPackages();
			writeCachedPackages(packages);
		}
		return packages;
	}

	private Set<SystemPackage> getCachedPackages() {
		final NodeRef configurationFolder = getConfigurationFolder();
		final NodeRef systemPackagesCached = fileFolderService.searchSimple(configurationFolder,
				SYSTEM_PACKAGES_FILE_NAME);
		if (systemPackagesCached != null) {
			final ContentReader contentReader = fileFolderService.getReader(systemPackagesCached);
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
		NodeRef systemPackagesCached = fileFolderService.searchSimple(getConfigurationFolder(),
				SYSTEM_PACKAGES_FILE_NAME);
		if (systemPackagesCached == null) {
			systemPackagesCached = fileFolderService.create(getConfigurationFolder(), SYSTEM_PACKAGES_FILE_NAME,
					ContentModel.TYPE_CONTENT).getNodeRef();
		}
		final ContentWriter cw = fileFolderService.getWriter(systemPackagesCached);
		final PrintWriter writer = new PrintWriter(cw.getContentOutputStream());
		try {
			for (final SystemPackage systemPackage : packages) {
				writer.println(systemPackage.toString());
			}
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	private NodeRef getConfigurationFolder() {
		return repositoryStoreService.getConfigurationFolder(true);
	}

	/* Dependencies */

	public void setJavaPackageScanner(final ObjectFactory<JavaPackageScanner> javaPackageScanner) {
		Assert.notNull(javaPackageScanner);
		this.javaPackageScanner = javaPackageScanner;
	}

	/* Configuration */

	public void setBasePackages(Set<SystemPackage> basePackages) {
		Assert.notNull(basePackages);
		basePackages = new TreeSet<SystemPackage>(SystemPackage.MOST_SPECIFIC_FIRST);
		basePackages.addAll(basePackages);
		this.basePackages = basePackages;
	}

	protected Set<SystemPackage> getBasePackages() {
		return basePackages;
	}

	public void setRepositoryStoreService(final RepositoryStoreService repositoryStoreService) {
		this.repositoryStoreService = repositoryStoreService;
	}

	public void setFileFolderService(final FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}
}
