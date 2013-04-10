package nl.runnable.alfresco.osgi;

import nl.runnable.alfresco.osgi.spring.LibraryVersionDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Provides operations for discovering Java packages.
 * 
 * @author Laurens Fridael
 * 
 */
public class JavaPackageScanner implements ServletContextAware {

	private static final Pattern CLASS_FILENAME_PATTERN = Pattern.compile("!/(.+)\\.class");

  private static final String OSGI_PACKAGE = "org.osgi";

  private static final String FELIX_PACKAGE = "org.apache.felix";

  private static final Collection<String> frameworkPackages = asList(OSGI_PACKAGE, FELIX_PACKAGE);

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private ServletContextResourcePatternResolver resourcePatternResolver;

  private List<LibraryVersionDetector> libraryVersionDetectors;

	/* Main operations */

	/**
	 * 
	 * Obtains the Java packages by scanning JARs in /WEB-INF/lib.
	 * <p>
	 * This implementation uses a {@link MetadataReader} to obtain class information, without actually loading the
	 * classses into the VM.
	 */
	public List<SystemPackage> scanWebApplicationPackages() {
		if (resourcePatternResolver == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("ResourcePatternResolver was not configured. This is normal during a unit test.");
			}
			return Collections.emptyList();
		}

    logger.debug("Scanning for Java packages.");

    final long before = System.currentTimeMillis();
		final List<SystemPackage> packages = new ArrayList<SystemPackage>();
    try {
      for (final Resource resource : getClassResources()) {
        if (resource.isReadable()) {
          final Matcher matcher = CLASS_FILENAME_PATTERN.matcher(resource.getURL().toString());
          if (matcher.find()) {
            final String className = matcher.group(1);

            if (className.lastIndexOf('/') > -1) {
              final String packageName = className.substring(0, className.lastIndexOf('/')).replace('/', '.');
              if (!isFrameworkPackage(packageName)) {
                final String version = getVersion(packageName, resource);
                final SystemPackage systemPackage = new SystemPackage(packageName, version);
                if (packages.contains(systemPackage) == false) {
                  packages.add(systemPackage);
                }
              }
            }
          }
        }
      }
      final long after = System.currentTimeMillis();
      if (logger.isInfoEnabled()) {
        logger.info("Found {} Java packages. Time taken: {}ms.", packages.size(), (after - before));
      }
    } catch (final IOException e) {
      if (logger.isWarnEnabled()) {
        logger.warn("Error scanning Java packages. This will cause Dynamic Extensions framework not to work!",
            e.getMessage(), e);
      }
    }
    return packages;
	}
  protected String getVersion(final String packageName, final Resource resource) {
    String version = null;
    for (final LibraryVersionDetector libraryVersionDetector : getLibraryVersionDetectors()) {
      version = libraryVersionDetector.detectLibraryVersion(packageName, resource);
      if (version != null) {
        break;
      }
    }
    return version;
  }

  protected boolean isFrameworkPackage(final String packageName) {
    for (final String osgiPackage : frameworkPackages) {
      if (packageName.startsWith(osgiPackage)) {
        return true;
      }
    }
    return false;
  }

  private List<Resource> getClassResources() throws IOException {
    final String location = "/WEB-INF/lib/*.jar!/**/*.class";
    return Arrays.asList(resourcePatternResolver.getResources(location));
	}

	/* Dependencies */

	@Override
	public void setServletContext(final ServletContext servletContext) {
		resourcePatternResolver = new ServletContextResourcePatternResolver(servletContext);
	}

  public List<LibraryVersionDetector> getLibraryVersionDetectors() {
    return libraryVersionDetectors;
  }

  public void setLibraryVersionDetectors(List<LibraryVersionDetector> libraryVersionDetectors) {
    this.libraryVersionDetectors = libraryVersionDetectors;
  }
}
