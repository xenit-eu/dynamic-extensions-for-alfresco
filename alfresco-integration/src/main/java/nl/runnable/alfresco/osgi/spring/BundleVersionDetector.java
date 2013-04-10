package nl.runnable.alfresco.osgi.spring;

import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.ExportedPackage;
import com.springsource.util.osgi.manifest.internal.StandardBundleManifest;
import com.springsource.util.osgi.manifest.parse.DummyParserLogger;
import org.apache.commons.io.IOUtils;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detect the package version using the Bundle MANIFEST if there is one.
 *
 * @author Laurent Van der Linden
 */
public class BundleVersionDetector implements LibraryVersionDetector, ServletContextAware {
  private final static Logger logger = LoggerFactory.getLogger(BundleVersionDetector.class);

  private static final Pattern JAR_FILENAME_PATTERN = Pattern.compile("^(jar:file:.+)!/.+\\.class$");

  private static final Pattern IMPLEMENTATION_VERSION_PATTERN = Pattern.compile("^Implementation-Version: (.+)$");

  private final Map<String, String> packageVersionCache = new HashMap<String, String>();

 /* dependencies */

  private ServletContext servletContent;

  @Override
  public String detectLibraryVersion(final String packageName, final Resource classResource) {
    // can't resolve without resource
    if (classResource == null) return null;

    String version = packageVersionCache.get(packageName);
    if (version == null) {
      try {
        final ResourcePatternResolver resourcePatternResolver = new ServletContextResourcePatternResolver(servletContent);
        final Matcher matcher = JAR_FILENAME_PATTERN.matcher(classResource.getURL().toString());
        if (matcher.matches()) {
          final String jarPath = matcher.group(1);
          final Resource manifest = resourcePatternResolver.getResource(String.format("%s!/META-INF/MANIFEST.MF", jarPath));
          if (manifest.isReadable()) {
            try {
              BundleManifest bundleManifest = new StandardBundleManifest(
                  new DummyParserLogger(), new InputStreamReader(manifest.getInputStream())
              );
              if (bundleManifest.getBundleName() != null) {
                final List<ExportedPackage> exportedPackages = bundleManifest.getExportPackage().getExportedPackages();
                for (ExportedPackage exportedPackage : exportedPackages) {
                  packageVersionCache.put(exportedPackage.getPackageName(), exportedPackage.getVersion().toString());
                  if (exportedPackage.getPackageName().equals(packageName)) {
                    version = exportedPackage.getVersion().toString();
                    if (logger.isDebugEnabled()) {
                      logger.debug(String.format("found version using BE: %s = %s", packageName, version));
                    }
                  }
                }
              } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(manifest.getInputStream()));
                try {
                  String line;
                  while ((line = reader.readLine()) != null) {
                    final Matcher ivm = IMPLEMENTATION_VERSION_PATTERN.matcher(line);
                    if (ivm.matches()) {
                      String possibleVersion = ivm.group(1);
                      try {
                        // try to parse to check if it is a valid Bundle Version
                        Version.parseVersion(possibleVersion);
                        version = possibleVersion;
                        packageVersionCache.put(packageName, version);
                        if (logger.isDebugEnabled()) {
                          logger.debug(String.format("found version using IV: %s = %s", packageName, version));
                        }
                      } catch (Exception ignore) {
                        if (logger.isDebugEnabled()) {
                          logger.debug(String.format("version %s is an invalid bundle version for package %s", version, packageName));
                        }
                      }
                      break;
                    }
                  }
                } finally {
                  IOUtils.closeQuietly(reader);
                }
              }
            } catch (Exception e) {
              if (logger.isDebugEnabled()) {
                logger.debug(String.format("failed to read manifest for file %s: %s", jarPath, e.getMessage()));
              }
            }
          }
        }
      } catch (IOException e) {
        logger.warn("failed to access package resource", e);
      }
    }
    if ("".equals(version)) return null;
    if (version == null) packageVersionCache.put(packageName, "");
    return version;
  }

  @Override
  public void setServletContext(final ServletContext servletContext) {
    this.servletContent = servletContext;
  }
}
