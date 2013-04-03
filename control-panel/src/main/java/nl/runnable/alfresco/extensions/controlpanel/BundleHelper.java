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

import nl.runnable.alfresco.extensions.controlpanel.template.TemplateBundle;
import nl.runnable.alfresco.osgi.RepositoryFolderService;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.util.FileCopyUtils;

import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.BundleManifestFactory;

@ManagedBean
public class BundleHelper implements BundleContextAware {

	private static final String ALFRESCO_DYNAMIC_EXTENSION_HEADER = "Alfresco-Dynamic-Extension";

	/* Dependencies */

	private BundleContext bundleContext;

	@Inject
	private RepositoryFolderService repositoryFolderService;

	@Inject
	private FileFolderService fileFolderService;

	@Inject
	private ContentService contentService;

	@Inject
	private NodeService nodeService;

	/* Main operations */

	public List<TemplateBundle> getFrameworkBundles() {
		final List<TemplateBundle> templateBundles = new ArrayList<TemplateBundle>();
		for (final Bundle bundle : bundleContext.getBundles()) {
			if (isDynamicExtension(bundle) == false) {
				templateBundles.add(new TemplateBundle(bundle));
			}
		}
		Collections.sort(templateBundles);
		return templateBundles;
	}

	public List<TemplateBundle> getExtensionBundles() {
		final List<TemplateBundle> templateBundles = new ArrayList<TemplateBundle>();
		for (final Bundle bundle : bundleContext.getBundles()) {
			if (isDynamicExtension(bundle)) {
				templateBundles.add(new TemplateBundle(bundle));
			}
		}
		Collections.sort(templateBundles);
		return templateBundles;
	}

	public Bundle getBundle(final long id) {
		return bundleContext.getBundle(id);
	}

	public Framework getFramework() {
		return (Framework) bundleContext.getBundle(0);
	}

	public Bundle installBundleInRepository(final FormField file) throws IOException, BundleException {
		final File tempFile = saveToTempFile(file);
		try {
			final String filename = file.getFilename();
			final Bundle bundle = bundleContext
					.installBundle(generateLocation(filename), new FileInputStream(tempFile));
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
			final NodeRef file = fileFolderService.searchSimple(repositoryFolderService.getBundleFolder(), filename);
			if (file != null) {
				final Map<QName, Serializable> properties = Collections.<QName, Serializable> emptyMap();
				nodeService.addAspect(file, ContentModel.ASPECT_TEMPORARY, properties);
				nodeService.deleteNode(file);
			}
		}
		bundle.uninstall();
	}

	/* Utility operations */

	/**
	 * Tests if the given bundle contains a Dynamic Extension.
	 * <p>
	 * This implementation looks if the bundle header <code>Alfresco-Dynamic-Extension</code> equals the String "true".
	 * 
	 * @param bundle
	 * @return
	 */
	private boolean isDynamicExtension(final Bundle bundle) {
		return "true".equals(bundle.getHeaders().get(ALFRESCO_DYNAMIC_EXTENSION_HEADER));
	}

	protected File saveToTempFile(final FormField file) throws IOException {
		final File tempFile = File.createTempFile("dynamic-extensions-bundle", null);
		tempFile.deleteOnExit();
		FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(tempFile));
		return tempFile;
	}

	private void saveBundleInRepository(final File tempFile, final String filename, final BundleManifest manifest)
			throws IOException {
		final NodeRef bundleFolder = repositoryFolderService.getBundleFolder();
		NodeRef nodeRef = fileFolderService.searchSimple(bundleFolder, filename);
		if (nodeRef == null) {
			nodeRef = fileFolderService.create(bundleFolder, filename, ContentModel.TYPE_CONTENT).getNodeRef();
		}
		final String title = String.format("%s %s", manifest.getBundleName(), manifest.getBundleVersion());
		nodeService.setProperty(nodeRef, ContentModel.PROP_TITLE, title);
		nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, manifest.getBundleDescription());
		final ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
		writer.setMimetype(MimetypeMap.MIMETYPE_ZIP);
		writer.putContent(new FileInputStream(tempFile));
	}

	protected String generateLocation(final String filename) {
		return String.format("/Repository/%s", filename);
	}

	/* Dependencies */

	@Override
	public void setBundleContext(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;

	}

}
