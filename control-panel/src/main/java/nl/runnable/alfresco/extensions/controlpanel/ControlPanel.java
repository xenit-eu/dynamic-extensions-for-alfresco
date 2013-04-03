package nl.runnable.alfresco.extensions.controlpanel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import nl.runnable.alfresco.extensions.controlpanel.template.TemplateBundle;
import nl.runnable.alfresco.extensions.controlpanel.template.Variables;
import nl.runnable.alfresco.osgi.Configuration;
import nl.runnable.alfresco.osgi.RepositoryFolderService;
import nl.runnable.alfresco.webscripts.annotations.Attribute;
import nl.runnable.alfresco.webscripts.annotations.Authentication;
import nl.runnable.alfresco.webscripts.annotations.AuthenticationType;
import nl.runnable.alfresco.webscripts.annotations.Cache;
import nl.runnable.alfresco.webscripts.annotations.FileField;
import nl.runnable.alfresco.webscripts.annotations.HttpMethod;
import nl.runnable.alfresco.webscripts.annotations.RequestParam;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.UriVariable;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;

/**
 * Web Script for the Dynamic Extensions console.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
@WebScript
@Authentication(AuthenticationType.ADMIN)
@Cache(neverCache = true)
public class ControlPanel {

	/* Dependencies */

	@Inject
	private Configuration configuration;

	@Inject
	private BundleHelper bundleHelper;

	@Inject
	private RepositoryFolderService repositoryFolderService;

	/* Main operations */

	/**
	 * Generates reference data for the index page.
	 * 
	 * @return The model.
	 */
	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/", defaultFormat = "html")
	public Map<String, Object> index(@Attribute final ResponseHelper responseHelper) {
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(Variables.EXTENSION_BUNDLES, bundleHelper.getExtensionBundles());
		model.put(Variables.FILE_INSTALL_PATHS, configuration.getFileInstallPaths());
		model.put(Variables.INSTALLED_BUNDLE, responseHelper.getFlashVariable(Variables.INSTALLED_BUNDLE));
		model.put(Variables.ERROR_MESSAGE, responseHelper.getFlashVariable(Variables.ERROR_MESSAGE));
		model.put(Variables.SUCCESS_MESSAGE, responseHelper.getFlashVariable(Variables.SUCCESS_MESSAGE));
		return model;
	}

	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/framework", defaultFormat = "html")
	public Map<String, Object> framework() {
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(Variables.FRAMEWORK_BUNDLES, bundleHelper.getFrameworkBundles());
		return model;
	}

	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/configuration", defaultFormat = "html")
	public Map<String, Object> configuration() {
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(Variables.FILE_INSTALL_PATHS, configuration.getFileInstallPaths());
		model.put("jarFiles", repositoryFolderService.getJarFilesInBundleFolder());
		return model;
	}

	@Uri(method = HttpMethod.POST, value = "/dynamic-extensions/install-bundle", defaultFormat = "html", multipartProcessing = true)
	public void installBundle(@FileField final FormField file, @Attribute final ResponseHelper responseHelper) {
		if (file != null) {
			if (file.getFilename().endsWith(".jar")) {
				try {
					final Bundle installedBundle = bundleHelper.installBundleInRepository(file);
					responseHelper.setFlashVariable(Variables.INSTALLED_BUNDLE, new TemplateBundle(installedBundle));
				} catch (final Exception e) {
					responseHelper.flashErrorMessage(String.format("Error installing Bundle: %s", e.getMessage()));
				}
			} else {
				responseHelper.flashErrorMessage(String.format("Not a JAR file: %s", file.getFilename()));
			}
		} else {
			responseHelper.flashErrorMessage("No file uploaded.");
		}
		responseHelper.redirectToIndex();
	}

	@Uri(method = HttpMethod.POST, value = "/dynamic-extensions/delete-bundle")
	public void deleteBundle(@RequestParam final long id, @Attribute final ResponseHelper responseHelper) {
		final Bundle bundle = bundleHelper.getBundle(id);
		if (bundle != null) {
			try {
				bundleHelper.uninstallAndDeleteBundle(bundle);
				final String message = String.format("Deleted bundle %s %s",
						bundle.getHeaders().get(Constants.BUNDLE_NAME), bundle.getVersion());
				responseHelper.flashSuccessMessage(message);
			} catch (final BundleException e) {
				responseHelper.flashErrorMessage("Error uninstalling Bundle");
			}
		} else {
			responseHelper.flashErrorMessage(String.format("Cannot delete bundle. Bundle with ID %d not found.", id));
		}
		responseHelper.redirectToIndex();
	}

	/**
	 * Restarts the {@link Bundle} with the given ID.
	 * 
	 * @param wait
	 * @param response
	 * @throws IOException
	 * @throws BundleException
	 */
	@Uri(method = HttpMethod.POST, value = "/dynamic-extensions/framework/restart")
	public void restartFramework(@RequestParam(defaultValue = "0") final long wait,
			@Attribute final ResponseHelper response) throws IOException, BundleException {
		restartFramework(bundleHelper.getFramework());
		response.status(HttpServletResponse.SC_OK, "Restarted framework.");
	}

	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/bundles/{id}", defaultFormat = "html")
	public Map<String, Object> bundle(@UriVariable final long id, @Attribute final ResponseHelper responseHelper)
			throws IOException {
		final Map<String, Object> model = new HashMap<String, Object>();
		final Bundle bundle = bundleHelper.getBundle(id);
		if (bundle != null) {
			model.put(Variables.BUNDLE, new TemplateBundle(bundle));
		} else {
			model.put(Variables.ID, id);
			responseHelper.status(HttpServletResponse.SC_NOT_FOUND);
		}
		return model;
	}

	/* Attributes */

	@Attribute
	protected ResponseHelper getResponseHelper(final WebScriptRequest request, final WebScriptResponse response) {
		return new ResponseHelper(request, response);
	}

	/* Utility operations */

	protected void restartFramework(final Framework framework) throws IOException {
		try {
			framework.stop();
			framework.waitForStop(0);
			framework.start();
		} catch (final BundleException e) {
		} catch (final InterruptedException e) {
		}
	}

}
