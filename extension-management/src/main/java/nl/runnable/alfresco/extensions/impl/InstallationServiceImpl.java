package nl.runnable.alfresco.extensions.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import nl.runnable.alfresco.extensions.Extension;
import nl.runnable.alfresco.extensions.Installation;
import nl.runnable.alfresco.extensions.InstallationService;
import nl.runnable.alfresco.extensions.RepositoryFolderService;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link InstallationService} implementation that stores its entries as <code>.properties</code> files in XML format in
 * the repository.
 * 
 * @author Laurens Fridael
 * 
 */
public class InstallationServiceImpl implements InstallationService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String FIRST_INSTALLED = "firstInstalled";

	private static final String FIRST_VERSION = "firstVersion";

	private static final String LAST_INSTALLED = "lastInstalled";

	private static final String LAST_VERSION = "lastVersion";

	private static final String INSTALLATION_COUNT = "installationCount";

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	/* Dependencies */

	private RepositoryFolderService repositoryFolderService;

	private FileFolderService fileFolderService;

	private NodeService nodeService;

	private ContentService contentService;

	/* Main operations */

	@Override
	public void registerInstallation(final Extension extension) {
		final NodeRef folder = getConfigurationFolder();
		final String filename = getFilename(extension.getName());
		NodeRef file = getFileFolderService().searchSimple(folder, filename);
		final Properties properties;
		final Installation installation = new Installation();
		final Date now = new Date();
		if (file == null) {
			file = getFileFolderService().create(folder, filename, ContentModel.TYPE_CONTENT).getNodeRef();
			installation.setFirstInstalled(now);
			installation.setFirstVersion(extension.getVersion());
			properties = new Properties();
		} else {
			properties = loadProperties(file);
			if (properties != null) {
				populateInstallation(installation, properties);
			}
		}
		installation.setInstallationCount(installation.getInstallationCount() + 1);
		installation.setLastInstalled(now);
		installation.setLastVersion(extension.getVersion());
		saveInstallation(file, installation, properties);
	}

	@Override
	public Installation getInstallation(final String extensionName) {
		final NodeRef folder = getConfigurationFolder();
		final String filename = getFilename(extensionName);
		final NodeRef file = getFileFolderService().searchSimple(folder, filename);
		Installation installation = null;
		if (file != null) {
			installation = loadInstallation(file);
		}
		return installation;
	}

	@Override
	public void clearInstallation(final String extensionName) {
		final NodeRef file = getFileFolderService().searchSimple(getConfigurationFolder(), getFilename(extensionName));
		if (file != null) {
			deleteWithoutArchiving(file);
		}
	}

	/* Conversion operations */

	protected void populateProperties(final Properties properties, final Installation installation) {
		if (installation.getFirstInstalled() != null) {
			properties.setProperty(FIRST_INSTALLED,
					new SimpleDateFormat(DATE_FORMAT).format(installation.getFirstInstalled()));
		}
		if (installation.getFirstVersion() != null) {
			properties.setProperty(FIRST_VERSION, installation.getFirstVersion());
		}
		if (installation.getLastInstalled() != null) {
			properties.setProperty(LAST_INSTALLED,
					new SimpleDateFormat(DATE_FORMAT).format(installation.getLastInstalled()));
		}
		if (installation.getLastVersion() != null) {
			properties.setProperty(LAST_VERSION, installation.getLastVersion());
		}
		properties.setProperty(INSTALLATION_COUNT, Integer.toString(installation.getInstallationCount()));
	}

	protected void populateInstallation(final Installation installation, final Properties properties) {
		try {
			installation.setFirstInstalled(new SimpleDateFormat(DATE_FORMAT).parse(properties
					.getProperty(FIRST_INSTALLED)));
		} catch (final ParseException e) {
			// Ignore, too bad, not essential.
		}
		try {
			installation.setLastInstalled(new SimpleDateFormat(DATE_FORMAT).parse(properties
					.getProperty(LAST_INSTALLED)));
		} catch (final ParseException e) {
			// Idem
		}
		try {
			installation.setInstallationCount(Integer.parseInt(properties.getProperty(INSTALLATION_COUNT)));
		} catch (final NumberFormatException e) {
			// Idem
		}
		installation.setFirstVersion(properties.getProperty(FIRST_VERSION));
		installation.setLastVersion(properties.getProperty(LAST_VERSION));
	}

	protected Properties loadProperties(final NodeRef file) {
		try {
			final Properties properties = new Properties();
			final ContentReader reader = getContentService().getReader(file, ContentModel.PROP_CONTENT);
			properties.loadFromXML(reader.getContentInputStream());
			return properties;
		} catch (final ContentIOException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error reading repository file: {}", e.getMessage(), e);
			}
		} catch (final InvalidPropertiesFormatException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error reading repository file: {}", e.getMessage(), e);
			}
		} catch (final IOException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error reading repository file: {}", e.getMessage(), e);
			}
		}
		return null;
	}

	protected Installation loadInstallation(final NodeRef file) {
		Installation installation = null;
		final Properties properties = loadProperties(file);
		if (properties != null) {
			installation = new Installation();
			populateInstallation(installation, properties);
		}
		return installation;
	}

	protected void saveInstallation(final NodeRef file, final Installation installation, final Properties properties) {
		OutputStream out = null;
		try {
			populateProperties(properties, installation);
			final ContentWriter writer = getContentService().getWriter(file, ContentModel.PROP_CONTENT, true);
			writer.setEncoding("utf-8");
			writer.setMimetype("text/xml");
			out = writer.getContentOutputStream();
			properties.storeToXML(out, null);
		} catch (final ContentIOException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error writing repository file: {}", e.getMessage(), e);
			}
		} catch (final IOException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error writing repository file: {}", e.getMessage(), e);
			}
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (final IOException e) {
				}
			}
		}
	}

	/* Utility operations */

	protected void deleteWithoutArchiving(final NodeRef nodeRef) {
		nodeService.addAspect(nodeRef, ContentModel.ASPECT_TEMPORARY, Collections.<QName, Serializable> emptyMap());
		nodeService.deleteNode(nodeRef);
	}

	protected NodeRef getConfigurationFolder() {
		return getRepositoryFolderService().getInstallationHistoryFolder();
	}

	protected String getFilename(final String extensionName) {
		return String.format("%s.properties", extensionName);
	};

	/* Dependencies */

	public void setRepositoryFolderService(final RepositoryFolderService repositoryFolderService) {
		this.repositoryFolderService = repositoryFolderService;
	}

	protected RepositoryFolderService getRepositoryFolderService() {
		return repositoryFolderService;
	}

	public void setFileFolderService(final FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	protected FileFolderService getFileFolderService() {
		return fileFolderService;
	}

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	protected NodeService getNodeService() {
		return nodeService;
	}

	public void setContentService(final ContentService contentService) {
		this.contentService = contentService;
	}

	protected ContentService getContentService() {
		return contentService;
	}

}
