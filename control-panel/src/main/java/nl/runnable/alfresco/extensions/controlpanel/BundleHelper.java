package nl.runnable.alfresco.extensions.controlpanel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.osgi.RepositoryStoreService;

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
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

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
	private RepositoryStoreService repositoryStoreService;

	@Inject
	private FileFolderService fileFolderService;

	@Inject
	private ContentService contentService;

	@Inject
	private NodeService nodeService;

	/* Container */

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
		final File tempFile = saveToTempFile(file.getInputStream());
		return doInstallBundleInRepository(tempFile, file.getFilename());
	}

	/**
	 * Installs a bundle using the given {@link Content} and filename.
	 * 
	 * @param content
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws BundleException
	 */
	public Bundle installBundleInRepository(final Content content) throws IOException, BundleException {
		final File tempFile = saveToTempFile(content.getInputStream());
		return doInstallBundleInRepository(tempFile, null);

	}

	public void uninstallAndDeleteBundle(final Bundle bundle) throws BundleException {
		final Matcher matcher = Pattern.compile("/Company Home(/.+)+/(.+\\.jar)$").matcher(bundle.getLocation());
		if (matcher.matches()) {
			final String filename = matcher.group(2);
			final NodeRef bundleFolder = repositoryStoreService.getBundleFolder(false);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<ServiceReference> getAllServices() {
		try {
			return (List) Arrays.asList(bundleContext.getAllServiceReferences(null, null));
		} catch (final InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/* Utility operations */

	protected Bundle doInstallBundleInRepository(final File tempFile, String filename) throws FileNotFoundException,
			BundleException, IOException {
		try {
			final BundleIdentifier identifier = getBundleIdentifier(tempFile);
			if (identifier == null) {
				throw new BundleException(
						"Could not generate Bundle filename. Make sure the content is an OSGi bundle.");
			}
			final Bundle installedBundle = findInstalledBundle(identifier);
			if (installedBundle != null) {
				installedBundle.uninstall();
			}
			if (filename == null) {
				filename = identifier.toJarFilename();
			}
			final String location = generateRepositoryLocation(filename);
			Bundle bundle = bundleContext.getBundle(location);
			final FileInputStream in = new FileInputStream(tempFile);
			if (bundle != null) {
				bundle.update(in);
			} else {
				bundle = bundleContext.installBundle(location, in);
			}
			bundle.start();
			final BundleManifest manifest = BundleManifestFactory.createBundleManifest(bundle.getHeaders());
			saveBundleInRepository(tempFile, filename, manifest);
			return bundle;
		} finally {
			tempFile.delete();
		}
	}

	protected File saveToTempFile(final InputStream data) throws IOException {
		final File tempFile = File.createTempFile("dynamic-extensions-bundle", null);
		tempFile.deleteOnExit();
		FileCopyUtils.copy(data, new FileOutputStream(tempFile));
		return tempFile;
	}

	protected BundleIdentifier getBundleIdentifier(final File tempFile) throws IOException {
		BundleIdentifier identifier = null;
		final JarFile jarFile = new JarFile(tempFile);
		try {
			final Manifest manifest = jarFile.getManifest();
			final Attributes attributes = manifest.getMainAttributes();
			final String symbolicName = attributes.getValue(Constants.BUNDLE_SYMBOLICNAME);
			final String version = attributes.getValue(Constants.BUNDLE_VERSION);
			if (StringUtils.hasText(symbolicName) && StringUtils.hasText(version)) {
				identifier = BundleIdentifier.fromSymbolicNameAndVersion(symbolicName, version);
			}
			return identifier;
		} finally {
			jarFile.close();
		}
	}

	protected Bundle findInstalledBundle(final BundleIdentifier bundleIdentifier) {
		for (final Bundle bundle : bundleContext.getBundles()) {
			if (bundle.getSymbolicName().equals(bundleIdentifier.getSymbolicName())
					&& bundle.getVersion().equals(bundleIdentifier.getVersion())) {
				return bundle;
			}
		}
		return null;
	}

	protected void saveBundleInRepository(final File file, final String filename, final BundleManifest manifest)
			throws IOException {
		final NodeRef bundleFolder = repositoryStoreService.getBundleFolder(true);
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

	protected String generateRepositoryLocation(final String filename) {
		return String.format("%s/%s", getBundleRepositoryLocation(), filename);
	}

	/* Container */

	public String getBundleRepositoryLocation() {
		return repositoryStoreService.getBundleRepositoryLocation();
	}

}
