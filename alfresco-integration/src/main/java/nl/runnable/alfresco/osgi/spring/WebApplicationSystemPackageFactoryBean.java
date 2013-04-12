package nl.runnable.alfresco.osgi.spring;

import nl.runnable.alfresco.osgi.JavaPackageScanner;
import nl.runnable.alfresco.osgi.RepositoryStoreService;
import nl.runnable.alfresco.osgi.SystemPackage;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.Assert;

import java.io.PrintWriter;
import java.util.*;

/**
 * Provides {@link SystemPackage}s by scanning the web application for Java packages.
 * 
 * @author Laurens Fridael
 * 
 */
public class WebApplicationSystemPackageFactoryBean implements FactoryBean<Set<SystemPackage>> {

  private static final String SYSTEM_PACKAGES = "system-packages.txt";

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
    if (packages == null) {
      packages = javaPackageScanner.getObject().scanWebApplicationPackages();
      final FileInfo systemPackagesCacheInfo = fileFolderService.create(
          getConfigurationFolder(), SYSTEM_PACKAGES, ContentModel.TYPE_CONTENT
      );
      writeCachedPackages(systemPackagesCacheInfo, packages);
    }
    return packages;
  }

  private void writeCachedPackages(FileInfo systemPackagesCacheInfo, Set<SystemPackage> packages) {
    if (systemPackagesCacheInfo != null) {
      final ContentWriter cw = fileFolderService.getWriter(systemPackagesCacheInfo.getNodeRef());
      PrintWriter writer = new PrintWriter(cw.getContentOutputStream());
      try {
        for (SystemPackage systemPackage : packages) {
          writer.println(systemPackage.toString());
        }
      } finally {
        IOUtils.closeQuietly(writer);
      }
    }
  }

  private Set<SystemPackage> getCachedPackages() {
    final NodeRef configurationFolder = getConfigurationFolder();
    final NodeRef systemPackagesCached = fileFolderService.searchSimple(configurationFolder, SYSTEM_PACKAGES);
    if (systemPackagesCached != null) {
      final String[] lines = fileFolderService.getReader(systemPackagesCached).getContentString().split("\n");
      final Set<SystemPackage> systemPackages = new LinkedHashSet<SystemPackage>(lines.length);
      for (String line : lines) {
        systemPackages.add(SystemPackage.from(line));
      }
      return systemPackages;
    }
    return null;
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

  public void setRepositoryStoreService(RepositoryStoreService repositoryStoreService) {
    this.repositoryStoreService = repositoryStoreService;
  }


  public void setFileFolderService(FileFolderService fileFolderService) {
    this.fileFolderService = fileFolderService;
  }
}
