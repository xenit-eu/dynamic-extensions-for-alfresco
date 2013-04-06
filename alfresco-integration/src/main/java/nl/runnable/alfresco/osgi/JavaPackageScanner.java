package nl.runnable.alfresco.osgi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

/**
 * Provides operations for discovering Java packages.
 * 
 * @author Laurens Fridael
 * 
 */
public class JavaPackageScanner implements ServletContextAware {

	private static final Pattern CLASS_FILENAME_PATTERN = Pattern.compile("!/(.+)\\.class");

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private ServletContextResourcePatternResolver resourcePatternResolver;

	/* Main operations */

	/**
	 * 
	 * Obtains the Java packages by scanning JARs in /WEB-INF/lib.
	 * <p>
	 * This implementation uses a {@link MetadataReader} to obtain class information, without actually loading the
	 * classses into the VM.
	 * 
	 * @param basePackageNames
	 *            The names of the base packages or null to scan every package.
	 */
	public List<String> scanWebApplicationPackages(final Collection<String> basePackageNames) {
		if (resourcePatternResolver == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("ResourcePatternResolver was not configured. This is normal during a unit test.");
			}
			return Collections.emptyList();
		}

		if (logger.isDebugEnabled()) {
			if (CollectionUtils.isEmpty(basePackageNames)) {
				logger.debug("Scanning for Java packages.");
			} else {
				logger.debug("Scanning for Java packages in the following base packages: {}", basePackageNames);
			}
		}
		final long before = System.currentTimeMillis();
		final List<String> packageNames = new ArrayList<String>();
		try {
			for (final Resource resource : getClassResources(basePackageNames)) {
				if (resource.isReadable()) {
					final Matcher matcher = CLASS_FILENAME_PATTERN.matcher(resource.getURL().toString());
					if (matcher.find()) {
						final String className = matcher.group(1);
						if (className.lastIndexOf('/') > -1) {
							final String packageName = className.substring(0, className.lastIndexOf('/')).replace('/',
									'.');
							if (packageNames.contains(packageName) == false) {
								packageNames.add(packageName);
							}
						}
					}
				}
			}
			final long after = System.currentTimeMillis();
			if (logger.isInfoEnabled()) {
				logger.info("Found {} Java packages. Time taken: {}ms.", packageNames.size(), (after - before));
			}
		} catch (final IOException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error scanning Java packages. This will cause Dynamic Extensions framework not to work!",
						e.getMessage(), e);
			}
		}
		return packageNames;
	}

	private List<Resource> getClassResources(final Collection<String> packagesToScan) throws IOException {
		final List<Resource> resources;
		if (CollectionUtils.isEmpty(packagesToScan)) {
			final String location = "/WEB-INF/lib/*.jar!/**/*.class";
			resources = Arrays.asList(resourcePatternResolver.getResources(location));
		} else {
			resources = new ArrayList<Resource>();
			for (final String packageName : packagesToScan) {
				final String location = String.format("/WEB-INF/lib/*.jar!/%s/**/*.class",
						packageName.replace('.', '/'));
				resources.addAll(Arrays.asList(resourcePatternResolver.getResources(location)));
			}
		}
		return resources;
	}

	/* Dependencies */

	@Override
	public void setServletContext(final ServletContext servletContext) {
		resourcePatternResolver = new ServletContextResourcePatternResolver(servletContext);
	}

}
