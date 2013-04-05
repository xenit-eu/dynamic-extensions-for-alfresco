package nl.runnable.alfresco.osgi.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import nl.runnable.alfresco.osgi.ReflectionService;
import nl.runnable.alfresco.osgi.SystemPackage;

import org.alfresco.service.descriptor.DescriptorService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

/**
 * Provides {@link SystemPackage}s by scanning the web application for Java packages.
 * 
 * @author Laurens Fridael
 * 
 */
public class WebApplicationSystemPackageFactoryBean implements FactoryBean<List<SystemPackage>>, ServletContextAware {

	private static final String FELIX_PACKAGE = "org.apache.felix";

	private static final String OSGI_PACKAGE = "org.osgi";

	private static final String DEFAULT_SPRING_VERSION = "3.0.0.RELEASE";

	private static final String ALFRESCO_PACKAGE = "org.alfresco";

	private static final String SPRING_PACKAGE = "org.springframework";

	/* Dependencies */

	private ReflectionService reflectionService;

	private DescriptorService descriptorService;

	private ServletContext servletContext;

	/* Configuration */

	private final String defaultSpringVersion = DEFAULT_SPRING_VERSION;

	private final Collection<String> frameworkPackages = Arrays.asList(OSGI_PACKAGE, FELIX_PACKAGE);

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
		final List<String> packageNames = reflectionService.scanPackageNames();
		final List<SystemPackage> systemPackages = new ArrayList<SystemPackage>(packageNames.size());
		for (final String packageName : packageNames) {
			if (isFrameworkPackage(packageName) == false) {
				final String version = getVersion(packageName);
				systemPackages.add(new SystemPackage(packageName, version));
			}
		}
		return systemPackages;
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
		String version = SystemPackage.DEFAULT_VERSION;
		if (packageName.startsWith(SPRING_PACKAGE)) {
			version = getSpringVersion();
		} else if (packageName.startsWith(ALFRESCO_PACKAGE)) {
			version = getAlfrescoVersion();
		}
		return version;
	}

	private String getSpringVersion() {
		final ResourcePatternResolver resourcePatternResolver = new ServletContextResourcePatternResolver(
				servletContext);
		String version = defaultSpringVersion;
		try {
			final Resource[] resources = resourcePatternResolver
					.getResources("/WEB-INF/lib/org.springframework.core-*.jar");
			if (resources.length == 1) {
				final String filename = resources[0].getFilename();
				final Matcher matcher = Pattern.compile("core-(.+?)\\.jar$").matcher(filename);
				if (matcher.find()) {
					version = matcher.group(1);
				}
			}
		} catch (final IOException e) {
		}
		return version;
	}

	private String getAlfrescoVersion() {
		return descriptorService.getServerDescriptor().getVersionNumber().toString();
	}

	/* Dependencies */

	public void setReflectionService(final ReflectionService reflectionService) {
		Assert.notNull(reflectionService);
		this.reflectionService = reflectionService;
	}

	public void setDescriptorService(final DescriptorService descriptorService) {
		this.descriptorService = descriptorService;
	}

	@Override
	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
