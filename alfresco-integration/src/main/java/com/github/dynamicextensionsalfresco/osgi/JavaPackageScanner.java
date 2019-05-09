package com.github.dynamicextensionsalfresco.osgi;

import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.ExportedPackage;
import com.springsource.util.osgi.manifest.internal.StandardBundleManifest;
import com.springsource.util.osgi.manifest.parse.BundleManifestParseException;
import com.springsource.util.osgi.manifest.parse.DummyParserLogger;
import org.alfresco.service.descriptor.DescriptorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import static com.github.dynamicextensionsalfresco.osgi.ManifestUtils.getImplementationVersion;
import static java.util.Arrays.asList;

/**
 * Provides operations for discovering Java packages.
 * 
 * @author Laurens Fridael
 * 
 */
public class JavaPackageScanner implements ServletContextAware {

	private static final String OSGI_PACKAGE = "org.osgi";

	private static final String FELIX_PACKAGE = "org.apache.felix";

	private static final Collection<String> frameworkPackages = asList(OSGI_PACKAGE, FELIX_PACKAGE);

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private ServletContextResourcePatternResolver resourcePatternResolver;

	private ServletContext servletContext;

	private DescriptorService descriptorService;

	/* Main operations */

	/**
	 * 
	 * Obtains the Java packages by scanning JARs in /WEB-INF/lib.
	 * <p>
	 * This implementation uses a {@link MetadataReader} to obtain class information, without actually loading the
	 * classses into the VM.
	 * <p>
	 * Note: calling this for a second time during Framework restart will currently fail.
	 */
	public Set<SystemPackage> scanWebApplicationPackages() {
		if (resourcePatternResolver == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("ResourcePatternResolver was not configured. This is normal during a unit test.");
			}
			return Collections.emptySet();
		}

		logger.debug("Scanning for Java packages.");

		final long before = System.currentTimeMillis();
		final Set<SystemPackage> systemPackages = new LinkedHashSet<SystemPackage>(4500, 0.1f);
		try {

			for (final Resource jarResource : resourcePatternResolver.getResources("/WEB-INF/lib/*.jar")) {
				final String jarPath = servletContext.getRealPath(String.format("/WEB-INF/lib/%s",
						jarResource.getFilename()));
				final JarFile jarFile = new JarFile(jarPath);
				final Set<SystemPackage> exportPackages = scanBundleExportPackages(jarFile);
				if (exportPackages != null) {
					systemPackages.addAll(exportPackages);
				} else {
					final Set<SystemPackage> javaPackages = scanJavaPackages(jarFile);
					systemPackages.addAll(javaPackages);
				}
			}

			final long after = System.currentTimeMillis();
			if (logger.isInfoEnabled()) {
				logger.info("Found {} Java packages. Time taken: {}ms.", systemPackages.size(), (after - before));
			}

		} catch (final IOException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error scanning Java packages. This will cause Dynamic Extensions framework not to work!",
						e.getMessage(), e);
			}
		}
		return systemPackages;
	}

	protected boolean isFrameworkPackage(final String packageName) {
		for (final String frameworkPackage : frameworkPackages) {
			if (packageName.startsWith(frameworkPackage)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Scans the given {@link JarFile} for an OSGi-compliant manifest and uses the 'Export-Package' header to determine
	 * the {@link SystemPackage}s.
	 * 
	 * @param jarFile
	 * @return
	 * @throws IOException
	 */
	protected Set<SystemPackage> scanBundleExportPackages(final JarFile jarFile) throws IOException {
		Set<SystemPackage> exportPackages = null;
		final Manifest manifest = jarFile.getManifest();
		if (manifest != null) {
			final Map<String, String> contents = convertAttributesToMap(manifest.getMainAttributes());
			try {
				final BundleManifest bundleManifest = new StandardBundleManifest(new DummyParserLogger(), contents);
				if (bundleManifest.getBundleName() != null) {
					final List<ExportedPackage> exportedPackages = bundleManifest.getExportPackage().getExportedPackages();
					exportPackages = new LinkedHashSet<SystemPackage>(exportedPackages.size());
					for (final ExportedPackage exportedPackage : exportedPackages) {
						final SystemPackage exportPackage = new SystemPackage(exportedPackage.getPackageName(),
								exportedPackage.getVersion().toString());
						exportPackages.add(exportPackage);
					}
				}
			} catch (BundleManifestParseException e) {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Failed to parse manifest for %s, reverting to content scanning.", jarFile.getName()), e);
				}
			}
		}
		return exportPackages;
	}

	protected Map<String, String> convertAttributesToMap(final Attributes attributes) {
		final Map<String, String> map = new HashMap<String, String>();
		for (final Entry<Object, Object> entry : attributes.entrySet()) {
			final String key = entry.getKey().toString();
			map.put(key, entry.getValue() != null ? entry.getValue().toString() : null);
		}
		return map;
	}

	/**
	 * Scans the given {@link JarFile} for Java packages, using the 'Implementation-Version' header from the JAR
	 * manifest to determine the {@link SystemPackage} version.
	 * <p>
	 * For 'org.alfresco' packages the implementation uses the {@link DescriptorService} to determine the platform
	 * version.
	 * 
	 * @param jarFile
	 * @return
	 * @throws IOException
	 */
	protected Set<SystemPackage> scanJavaPackages(final JarFile jarFile) throws IOException {
		final String implementationVersion = getImplementationVersion(jarFile);
		final Set<SystemPackage> systemPackages = new LinkedHashSet<SystemPackage>(30);
		final Set<String> foundPackages = new HashSet<String>(30, 0.1f);
		for (final Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
			final JarEntry jarEntry = entries.nextElement();
			if (isJavaClassInPackage(jarEntry) == false) {
				continue;
			}
			final String name = jarEntry.getName();
			final String packageName = name.substring(0, name.lastIndexOf('/')).replace('/', '.');
			if (isFrameworkPackage(packageName)) {
				continue;
			}
			if (foundPackages.contains(packageName)) {
				continue;
			}
			if (isAlfrescoPackage(packageName)) {
				final String alfrescoVersion = descriptorService.getServerDescriptor().getVersionNumber().toString();
				systemPackages.add(new SystemPackage(packageName, alfrescoVersion));
			} else {
				systemPackages.add(new SystemPackage(packageName, implementationVersion));
			}
			foundPackages.add(packageName);
		}
		return systemPackages;
	}

	/**
	 * Tests if the given {@link ZipEntry} represents a Java class in a package. (I.e. one that is not in the default
	 * package.)
	 * 
	 * @param jarEntry
	 * @return
	 */
	protected boolean isJavaClassInPackage(final JarEntry jarEntry) {
		final String name = jarEntry.getName();
		return name.lastIndexOf('/') > -1 && name.endsWith(".class");
	}

	protected boolean isAlfrescoPackage(final String packageName) {
		return packageName.startsWith("org.alfresco");
	}

    /**
	 * Check if the cache node exists and postdates the WEB-INF/lib directory
	 * @param systemPackageCache file storing the package list
	 * @return true if node exists and postdates the lib directory
	 */
	public boolean isCacheValid(File systemPackageCache) {
		if (!systemPackageCache.isFile()) {
			return false;
		}
		final Resource libDir = resourcePatternResolver.getResource("/WEB-INF/lib");
		try {
			final boolean stale = libDir.lastModified() > systemPackageCache.lastModified();
			if (stale) {
				logger.info("system package cache is older then WEB-INF/lib, rescan packages");
			}
			return !stale;
		} catch (IOException e) {
			logger.warn("unable to read WEB-INF/lib directory, keeping package cache");
			return true;
		}
	}

	/* Dependencies */

	@Override
	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
		resourcePatternResolver = new ServletContextResourcePatternResolver(servletContext);
	}

	public void setDescriptorService(final DescriptorService descriptorService) {
		Assert.notNull(descriptorService);
		this.descriptorService = descriptorService;
	}
}
