package nl.runnable.alfresco.extensions.controlpanel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.osgi.RepositoryStorageService;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.util.FileCopyUtils;

import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.BundleManifestFactory;

/**
 * Helper for working with {@link Bundle}s.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
public class BundleHelper {

	private static final String ALFRESCO_DYNAMIC_EXTENSION_HEADER = "Alfresco-Dynamic-Extension";

	/**
	 * Tests if the given bundle contains a Dynamic Extension.
	 * <p>
	 * This implementation looks if the bundle header <code>Alfresco-Dynamic-Extension</code> equals the String "true".
	 * 
	 * @param bundle
	 * @return
	 */
	public static boolean isDynamicExtension(final Bundle bundle) {
		return "true".equals(bundle.getHeaders().get(ALFRESCO_DYNAMIC_EXTENSION_HEADER));
	}

	/* Dependencies */

	@Inject
	private BundleContext bundleContext;

	@Inject
	private RepositoryStorageService repositoryService;

	@Inject
	private FileFolderService fileFolderService;

	@Inject
	private ContentService contentService;

	@Inject
	private NodeService nodeService;

	/* Main operations */

	/**
	 * Obtains the {@link Bundle}s that comprise the core framework.
	 * 
	 * @return
	 */
	public List<Bundle> getFrameworkBundles() {
		final List<Bundle> bundles = new ArrayList<Bundle>();
		for (final Bundle bundle : bundleContext.getBundles()) {
			if (isDynamicExtension(bundle) == false) {
				bundles.add(bundle);
			}
		}
		return bundles;
	}

	/**
	 * Obtains the {@link Bundle}s that comprise the core framework.
	 * 
	 * @return
	 */
	public List<Bundle> getExtensionBundles() {
		final List<Bundle> bundles = new ArrayList<Bundle>();
		for (final Bundle bundle : bundleContext.getBundles()) {
			if (isDynamicExtension(bundle)) {
				bundles.add(bundle);
			}
		}
		return bundles;
	}

	/**
	 * Obtains the {@link Bundle} for the given id.
	 * 
	 * @param id
	 * @return The matching {@link Bundle} or null if no match could be found.
	 */
	public Bundle getBundle(final long id) {
		return bundleContext.getBundle(id);
	}

	/**
	 * Obtains the {@link Framework} bundle.
	 * 
	 * @return
	 */
	public Framework getFramework() {
		return (Framework) bundleContext.getBundle(0);
	}

	/**
	 * Installs an uploaded file as a bundle in the repository.
	 * <p>
	 * This implementation first saves the upload to a temporary file. It then attempts to install the file as a bundle.
	 * If this succeeds, it saves the bundle in the repository.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws BundleException
	 */
	public Bundle installBundleInRepository(final FormField file) throws IOException, BundleException {
		final File tempFile = saveToTempFile(file);
		try {
			final String filename = file.getFilename();
			final Bundle bundle = bundleContext.installBundle(generateRepositoryLocation(filename),
					new FileInputStream(tempFile));
			bundle.start();
			final BundleManifest manifest = BundleManifestFactory.createBundleManifest(bundle.getHeaders());
			saveBundleInRepository(tempFile, filename, manifest);
			return bundle;
		} finally {
			tempFile.delete();
		}
	}

	public void uninstallAndDeleteBundle(final Bundle bundle) throws BundleException {
		final Matcher matcher = Pattern.compile("/Repository/(.+\\.jar)$").matcher(bundle.getLocation());
		if (matcher.matches()) {
			final String filename = matcher.group(1);
			final NodeRef bundleFolder = repositoryService.getBundleFolder(false);
			if (bundleFolder != null) {
				final NodeRef file = fileFolderService.searchSimple(bundleFolder, filename);
				if (file != null) {
					final Map<QName, Serializable> properties = Collections.<QName, Serializable> emptyMap();
					nodeService.addAspect(file, ContentModel.ASPECT_TEMPORARY, properties);
					nodeService.deleteNode(file);
				}
			}
		}
		bundle.uninstall();
	}

	/* Utility operations */

	/**
	 * Saves an uploaded file represented by a given {@link FormField} to a temporary file.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	protected File saveToTempFile(final FormField file) throws IOException {
		final File tempFile = File.createTempFile("dynamic-extensions-bundle", null);
		tempFile.deleteOnExit();
		FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(tempFile));
		return tempFile;
	}

	/**
	 * Saves a bundle in the repository.
	 * 
	 * @param file
	 * @param filename
	 * @param manifest
	 * @throws IOException
	 */
	protected void saveBundleInRepository(final File file, final String filename, final BundleManifest manifest)
			throws IOException {
		final NodeRef bundleFolder = repositoryService.getBundleFolder(true);
		NodeRef nodeRef = fileFolderService.searchSimple(bundleFolder, filename);
		if (nodeRef == null) {
			nodeRef = fileFolderService.create(bundleFolder, filename, ContentModel.TYPE_CONTENT).getNodeRef();
		}
		final String title = String.format("%s %s", manifest.getBundleName(), manifest.getBundleVersion());
		nodeService.setProperty(nodeRef, ContentModel.PROP_TITLE, title);
		nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, manifest.getBundleDescription());
		final ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
		writer.setMimetype(MimetypeMap.MIMETYPE_ZIP);
		writer.putContent(new FileInputStream(file));
	}

	/**
	 * Generates a repository location for the given filename. This location is used to identify the bundle.
	 * 
	 * @param filename
	 * @return
	 */
	protected String generateRepositoryLocation(final String filename) {
		return String.format("/Repository/%s", filename);
	}

}
