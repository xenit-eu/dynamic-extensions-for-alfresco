package nl.runnable.alfresco.osgi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

public class ReflectionService implements ServletContextAware {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private ServletContextResourcePatternResolver resourcePatternResolver;

	/* Main operations */

	/**
	 * 
	 * Obtains the package names of all classes in /WEB-INF/lib.
	 * <p>
	 * This implementation uses a {@link MetadataReader} to obtain class information, without actually loading the
	 * classses into the VM.
	 * 
	 */
	public List<String> scanPackageNames() {
		if (resourcePatternResolver == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("ResourcePatternResolver was not configured. This is normal duromg a unit test.");
			}
			return Collections.emptyList();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Scanning for Java packages.");
		}
		final long before = System.currentTimeMillis();
		final List<String> packageNames = new ArrayList<String>();
		try {
			final String location = "/WEB-INF/lib/*.jar!/**/*.class";
			final Pattern pattern = Pattern.compile("!/(.+)\\.class");
			final Resource[] resources = resourcePatternResolver.getResources(location);
			for (final Resource resource : resources) {
				if (resource.isReadable()) {
					final Matcher matcher = pattern.matcher(resource.getURL().toString());
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

	/* Dependencies */

	@Override
	public void setServletContext(final ServletContext servletContext) {
		resourcePatternResolver = new ServletContextResourcePatternResolver(servletContext);
	}

}
