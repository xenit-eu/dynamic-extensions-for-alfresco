package nl.runnable.alfresco.osgi.spring;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

public class SpringLibraryVersionDetector extends AbstractLibraryVersionDetector implements ServletContextAware {

	/* Dependencies */

	private ServletContext servletContext;

	/* Configuration */

	private String defaultVersion = "3.0.0.RELEASE";

	/* Utility operations */

	@Override
	protected String getBasePackageName() {
		return "org.springframework";
	}

	@Override
	protected String doDetectLibraryVersion(final String packageName) {
		final ResourcePatternResolver resourcePatternResolver = new ServletContextResourcePatternResolver(
				getServletContext());
		String version = getDefaultVersion();
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

	/* Dependencies */

	@Override
	public void setServletContext(final ServletContext servletContext) {
		Assert.notNull(servletContext);
		this.servletContext = servletContext;
	}

	protected ServletContext getServletContext() {
		return servletContext;
	}

	/* Configuration */

	public void setDefaultVersion(final String defaultVersion) {
		Assert.hasText(defaultVersion);
		this.defaultVersion = defaultVersion;
	}

	public String getDefaultVersion() {
		return defaultVersion;
	}
}
