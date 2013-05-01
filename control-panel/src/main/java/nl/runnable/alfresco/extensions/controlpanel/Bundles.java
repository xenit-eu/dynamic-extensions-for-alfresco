package nl.runnable.alfresco.extensions.controlpanel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import nl.runnable.alfresco.extensions.controlpanel.template.TemplateBundle;
import nl.runnable.alfresco.extensions.controlpanel.template.Variables;
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
import org.springframework.extensions.webscripts.servlet.FormData.FormField;

@ManagedBean
@WebScript(baseUri = "/dynamic-extensions/bundles")
@Authentication(AuthenticationType.ADMIN)
@Cache(neverCache = true)
public class Bundles extends AbstractControlPanelHandler {

	/* Dependencies */

	@Inject
	private BundleHelper bundleHelper;

	/* Main operations */

	@Uri(method = HttpMethod.GET, defaultFormat = "html")
	public Map<String, Object> index(@Attribute final ResponseHelper responseHelper) {
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(Variables.EXTENSION_BUNDLES, toTemplateBundles(bundleHelper.getExtensionBundles()));
		model.put(Variables.FRAMEWORK_BUNDLES, toTemplateBundles(bundleHelper.getFrameworkBundles()));
		return model;
	}

	@Uri(method = HttpMethod.GET, value = "/{id}", defaultFormat = "html")
	public Map<String, Object> show(@UriVariable final long id, @Attribute final ResponseHelper responseHelper)
			throws IOException {
		final Map<String, Object> model = new HashMap<String, Object>();
		final Bundle bundle = bundleHelper.getBundle(id);
		if (bundle != null) {
			if (id == 0) {
				model.put(Variables.SYSTEM_PACKAGE_COUNT, getSystemPackages().size());
			}
			model.put(Variables.BUNDLE, new TemplateBundle(bundle));
		} else {
			model.put(Variables.ID, id);
			responseHelper.status(HttpServletResponse.SC_NOT_FOUND);
		}
		return model;
	}

	@Uri(method = HttpMethod.POST, value = "/install", defaultFormat = "html", multipartProcessing = true)
	public void install(@FileField final FormField file, @Attribute final ResponseHelper responseHelper) {
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
		responseHelper.redirectToBundles();
	}

	@Uri(method = HttpMethod.POST, value = "/delete")
	public void delete(@RequestParam final long id, @Attribute final ResponseHelper responseHelper) {
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
		responseHelper.redirectToBundles();
	}

	@Uri(method = HttpMethod.POST, value = "/start")
	public void start(@RequestParam final long id, @Attribute final ResponseHelper responseHelper) {
		final Bundle bundle = bundleHelper.getBundle(id);
		if (bundle != null) {
			try {
				bundle.start();
				final String message = String.format("Started bundle %s %s",
						bundle.getHeaders().get(Constants.BUNDLE_NAME), bundle.getVersion());
				responseHelper.flashSuccessMessage(message);
				responseHelper.redirectToBundles();
			} catch (final BundleException e) {
				responseHelper.flashErrorMessage(String.format("Error starting Bundle: %s", e.getMessage()));
				responseHelper.redirectToBundle(id);
			}
		} else {
			responseHelper.flashErrorMessage(String.format("Cannot start bundle. Bundle with ID %d not found.", id));
			responseHelper.redirectToBundle(id);
		}
	}

	/* Utility operations */

	protected Collection<TemplateBundle> toTemplateBundles(final Collection<Bundle> bundles) {
		final List<TemplateBundle> templateBundles = new ArrayList<TemplateBundle>();
		for (final Bundle bundle : bundles) {
			templateBundles.add(new TemplateBundle(bundle));
		}
		Collections.sort(templateBundles);
		return templateBundles;
	}

	@Attribute(Variables.REPOSITORY_STORE_LOCATION)
	protected String getRepositoryStoreLocation() {
		return bundleHelper.getBundleRepositoryLocation();
	}

}
