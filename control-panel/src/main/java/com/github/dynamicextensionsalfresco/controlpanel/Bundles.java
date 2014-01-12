package com.github.dynamicextensionsalfresco.controlpanel;

import com.github.dynamicextensionsalfresco.controlpanel.template.TemplateBundle;
import com.github.dynamicextensionsalfresco.controlpanel.template.Variables;
import com.github.dynamicextensionsalfresco.webscripts.annotations.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
@WebScript(baseUri = "/dynamic-extensions/bundles", defaultFormat = "html")
@Authentication(AuthenticationType.ADMIN)
@Cache(neverCache = true)
public class Bundles extends AbstractControlPanelHandler {

	/* Dependencies */

	@Autowired
	private BundleHelper bundleHelper;

	/* Main operations */

	@Uri(method = HttpMethod.GET)
	public Map<String, Object> index(@Attribute final ResponseHelper responseHelper) {
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(Variables.EXTENSION_BUNDLES, toTemplateBundles(bundleHelper.getExtensionBundles()));
		model.put(Variables.FRAMEWORK_BUNDLES, toTemplateBundles(bundleHelper.getFrameworkBundles()));
		return model;
	}

	@Uri(method = HttpMethod.GET, value = "/{id}")
	public Map<String, Object> show(final @Attribute Bundle bundle, @Attribute final ResponseHelper responseHelper,
			final Map<String, Object> model) throws IOException {
		if (bundle != null) {
			if (bundle.getBundleId() == 0) {
				model.put(Variables.SYSTEM_PACKAGE_COUNT, getSystemPackages().size());
			}
			model.put(Variables.BUNDLE, new TemplateBundle(bundle));
		} else {
			responseHelper.status(HttpServletResponse.SC_NOT_FOUND);
		}
		return model;
	}

	@Uri(method = HttpMethod.POST, value = "/install", multipartProcessing = true)
	public void install(@FileField final FormField file, @Attribute final ResponseHelper responseHelper) {
		responseHelper.redirectToBundles();
		responseHelper.checkBundleInstallConfiguration();
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
	}

	@Uri(method = HttpMethod.POST, value = "/delete")
	public void delete(final @Attribute Bundle bundle, @Attribute final String id,
			@Attribute final ResponseHelper responseHelper) {
		responseHelper.checkBundleInstallConfiguration();
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
	public void start(final @Attribute Bundle bundle, @Attribute final String id,
			@Attribute final ResponseHelper responseHelper) {
		responseHelper.checkBundleInstallConfiguration();
		if (bundle != null) {
			try {
				bundle.start();
				final String message = String.format("Started bundle %s %s",
						bundle.getHeaders().get(Constants.BUNDLE_NAME), bundle.getVersion());
				responseHelper.flashSuccessMessage(message);
				responseHelper.redirectToBundles();
			} catch (final BundleException e) {
				responseHelper.flashErrorMessage(String.format("Error starting Bundle: %s", e.getMessage()));
				responseHelper.redirectToBundle(bundle.getBundleId());
			}
		} else {
			responseHelper.flashErrorMessage(String.format("Cannot start bundle. Bundle with ID %d not found.", id));
			responseHelper.redirectToBundles();
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

	@Attribute(Variables.ID)
	public Long getId(@UriVariable(required = false) final Long pathId) {
		return pathId;
	}

	@Attribute(Variables.BUNDLE)
	public Bundle getBundle(@UriVariable(value = "id", required = false) final Long pathId,
			@RequestParam(value = "id", required = false) final Long paramId) {
		final Long id = pathId != null ? pathId : paramId;
		if (id != null) {
			return bundleHelper.getBundle(id);
		} else {
			return null;
		}
	}

}
