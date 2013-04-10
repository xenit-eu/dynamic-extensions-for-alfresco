package nl.runnable.alfresco.osgi.spring;

import nl.runnable.alfresco.osgi.SystemPackage;
import org.springframework.core.io.Resource;

import java.util.Set;

/**
 * Derive version from preconfigured set of base packages.
 * @author Laurent Van der Linden
 */
public class BasePackageVersionDetector implements LibraryVersionDetector {
  private Set<SystemPackage> basePackages;

  @Override
  public String detectLibraryVersion(String packageName, Resource classResource) {
    for (final SystemPackage basePackage : getBasePackages()) {
      if (packageName.startsWith(basePackage.getName()) && basePackage.getVersion() != null) {
        return basePackage.getVersion();
      }
    }
    return null;
  }

  public Set<SystemPackage> getBasePackages() {
    return basePackages;
  }

  public void setBasePackages(Set<SystemPackage> basePackages) {
    this.basePackages = basePackages;
  }
}
