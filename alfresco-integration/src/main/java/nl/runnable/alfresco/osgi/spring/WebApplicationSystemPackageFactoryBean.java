package nl.runnable.alfresco.osgi.spring;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static java.util.Arrays.asList;

/**
 * Provides {@link SystemPackage}s by scanning the web application for Java packages.
 * 
 * @author Laurens Fridael
 * 
 */
public class WebApplicationSystemPackageFactoryBean implements FactoryBean<List<SystemPackage>> {

  private static final String SYSTEM_PACKAGES = "system-packages.txt";

	/* Dependencies */

	private JavaPackageScanner javaPackageScanner;

	private List<LibraryVersionDetector> libraryVersionDetectors = Collections.emptyList();

  private RepositoryStoreService repositoryStoreService;

  private FileFolderService fileFolderService;

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
    List<SystemPackage> packages = getCachedPackages();
    if (packages == null) {
      packages = javaPackageScanner.scanWebApplicationPackages();
      final FileInfo systemPackagesCacheInfo = fileFolderService.create(
          getConfigurationFolder(), SYSTEM_PACKAGES, ContentModel.TYPE_CONTENT
      );
      writeCachedPackages(systemPackagesCacheInfo, packages);
    }
    return packages;
  }

  private void writeCachedPackages(FileInfo systemPackagesCacheInfo, List<SystemPackage> packages) {
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

  private List<SystemPackage> getCachedPackages() {
    final NodeRef configurationFolder = getConfigurationFolder();
    final NodeRef systemPackagesCached = fileFolderService.searchSimple(configurationFolder, SYSTEM_PACKAGES);
    if (systemPackagesCached != null) {
      final String[] lines = fileFolderService.getReader(systemPackagesCached).getContentString().split("\n");
      List<SystemPackage> systemPackages = new ArrayList<SystemPackage>(lines.length);
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

    public void setRepositoryStoreService(RepositoryStoreService repositoryStoreService) {
        this.repositoryStoreService = repositoryStoreService;
    }


    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }
}
