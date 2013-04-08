package nl.runnable.alfresco.osgi.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import nl.runnable.alfresco.osgi.JavaPackageScanner;
import nl.runnable.alfresco.osgi.SystemPackage;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

/**
 * Provides {@link SystemPackage}s by scanning the web application for Java packages.
 * 
 * @author Laurens Fridael
 * 
 */
public class WebApplicationSystemPackageFactoryBean implements FactoryBean<List<SystemPackage>> {

	private static final String OSGI_PACKAGE = "org.osgi";

	private static final String FELIX_PACKAGE = "org.apache.felix";

	private static final Collection<String> frameworkPackages = Arrays.asList(OSGI_PACKAGE, FELIX_PACKAGE);

	/* Dependencies */

	private JavaPackageScanner javaPackageScanner;

	private List<LibraryVersionDetector> libraryVersionDetectors = Collections.emptyList();

	/* Configuration */

	private List<SystemPackage> basePackages;

	/* Main operations */

	@Override
	public boolean isSingleton() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends List<SystemPackage>> getObjectType() {
		return (Class<? extends List<SystemPackage>>) (Class<?>) List.class;
	}

	@Override
	public List<SystemPackage> getObject() throws Exception {
		return createSystemPackages();
	}

	/* Utility operations */

	protected List<SystemPackage> createSystemPackages() {
		final List<String> basePackageNames = new ArrayList<String>();
		for (final SystemPackage basePackage : getBasePackages()) {
			basePackageNames.add(basePackage.getName());
		}
		final List<String> packageNames = javaPackageScanner.scanWebApplicationPackages(basePackageNames);
		final List<SystemPackage> systemPackages = new ArrayList<SystemPackage>(packageNames.size());
		for (final String packageName : packageNames) {
			if (isFrameworkPackage(packageName) == false) {
				final String version = getVersion(packageName);
				systemPackages.add(new SystemPackage(packageName, version));
			}
		}
		return new ArrayList<SystemPackage>(systemPackages);
	}

	protected boolean isFrameworkPackage(final String packageName) {
		for (final String osgiPackage : frameworkPackages) {
			if (packageName.startsWith(osgiPackage)) {
				return true;
			}
		}
		return false;
	}

	protected String getVersion(final String packageName) {
		String version = null;
		for (final SystemPackage basePackage : getBasePackages()) {
			if (packageName.startsWith(basePackage.getName()) && basePackage.getVersion() != null) {
				version = basePackage.getVersion();
				break;
			}
		}
		if (version == null) {
			for (final LibraryVersionDetector libraryVersionDetector : getLibraryVersionDetectors()) {
				version = libraryVersionDetector.detectLibraryVersion(packageName);
				if (version != null) {
					break;
				}
			}
		}
		return version;
	}

	/* Dependencies */

	public void setJavaPackageScanner(final JavaPackageScanner javaPackageScanner) {
		Assert.notNull(javaPackageScanner);
		this.javaPackageScanner = javaPackageScanner;
	}

	public void setLibraryVersionDetectors(final List<LibraryVersionDetector> libraryVersionDetectors) {
		Assert.notNull(libraryVersionDetectors);
		this.libraryVersionDetectors = libraryVersionDetectors;
	}

	protected List<LibraryVersionDetector> getLibraryVersionDetectors() {
		return libraryVersionDetectors;
	}

	/* Configuration */

	public void setBasePackages(List<SystemPackage> basePackages) {
		Assert.notNull(basePackages);
		basePackages = new ArrayList<SystemPackage>(basePackages);
		Collections.sort(basePackages, SystemPackage.MOST_SPECIFIC_FIRST);
		this.basePackages = basePackages;
	}

	protected List<SystemPackage> getBasePackages() {
		return basePackages;
	}

}
