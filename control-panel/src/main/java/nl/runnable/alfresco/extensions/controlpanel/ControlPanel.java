package nl.runnable.alfresco.extensions.controlpanel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import nl.runnable.alfresco.extensions.controlpanel.template.TemplateBundle;
import nl.runnable.alfresco.extensions.controlpanel.template.Variables;
import nl.runnable.alfresco.osgi.Configuration;
import nl.runnable.alfresco.osgi.RepositoryStoreService;
import nl.runnable.alfresco.osgi.SystemPackage;
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

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
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
	private FrameworkHelper frameworkHelper;

	@Inject
	private FileFolderService fileFolderService;

	@Inject
	private RepositoryStoreService repositoryStoreService;

	@Inject
	@Named("osgi.container.SystemPackages")
	private List<SystemPackage> systemPackages;

	/* Main operations */

	/**
	 * Generates reference data for the index page.
	 * 
	 * @return The model.
	 */
	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/", defaultFormat = "html")
	public Map<String, Object> index(@Attribute final ResponseHelper responseHelper) {
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(Variables.EXTENSION_BUNDLES, toTemplateBundles(bundleHelper.getExtensionBundles()));
		model.put(Variables.INSTALLED_BUNDLE, responseHelper.getFlashVariable(Variables.INSTALLED_BUNDLE));
		populateFlashMessages(responseHelper, model);

		return model;
	}

	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/framework", defaultFormat = "html")
	public Map<String, Object> framework() {
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(Variables.FRAMEWORK_BUNDLES, toTemplateBundles(bundleHelper.getFrameworkBundles()));
		return model;
	}

	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/bundles/{id}", defaultFormat = "html")
	public Map<String, Object> bundle(@UriVariable final long id, @Attribute final ResponseHelper responseHelper)
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
		model.put(Variables.ERROR_MESSAGE, responseHelper.getFlashVariable(Variables.ERROR_MESSAGE));
		return model;
	}

	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/configuration", defaultFormat = "html")
	public Map<String, Object> configuration(@Attribute final ResponseHelper responseHelper) {
		final Map<String, Object> model = new HashMap<String, Object>();
		populateFlashMessages(responseHelper, model);
		return model;
	}

	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/system-packages", defaultFormat = "html")
	public Map<String, Object> systemPackages() {
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(Variables.SYSTEM_PACKAGES, getSystemPackages());
		return model;
	}

	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/services", defaultFormat = "html")
	public Map<String, Object> services() {
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(Variables.SERVICES_BY_BUNDLE, getServicesByBundle());
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

	@Uri(method = HttpMethod.POST, value = "/dynamic-extensions/start-bundle")
	public void startBundle(@RequestParam final long id, @Attribute final ResponseHelper responseHelper) {
		final Bundle bundle = bundleHelper.getBundle(id);
		if (bundle != null) {
			try {
				bundle.start();
				final String message = String.format("Started bundle %s %s",
						bundle.getHeaders().get(Constants.BUNDLE_NAME), bundle.getVersion());
				responseHelper.flashSuccessMessage(message);
				responseHelper.redirectToIndex();
			} catch (final BundleException e) {
				e.printStackTrace();
				responseHelper.flashErrorMessage(String.format("Error starting Bundle: %s", e.getMessage()));
				responseHelper.redirectToBundle(id);
			}
		} else {
			responseHelper.flashErrorMessage(String.format("Cannot start bundle. Bundle with ID %d not found.", id));
			responseHelper.redirectToBundle(id);
		}
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
		if (configuration.getMode().isFrameworkRestartEnabled() == false) {
			response.status(HttpServletResponse.SC_FORBIDDEN, "Framework restart is currently not allowed.");
			return;
		}
		response.status(HttpServletResponse.SC_OK, "Restarting framework.");
		restartFrameworkAsynchronously();
	}

	@Uri(method = HttpMethod.POST, value = "/dynamic-extensions/delete-system-package-cache")
	public void deleteSystemPackageCache(@Attribute final ResponseHelper responseHelper) {
		final FileInfo systemPackageCache = repositoryStoreService.getSystemPackageCache();
		if (systemPackageCache != null) {
			fileFolderService.delete(systemPackageCache.getNodeRef());
			responseHelper.flashSuccessMessage("Deleted System Package cache.");
		} else {
			responseHelper.flashErrorMessage("System Package cache was not found, It may have been deleted already.");
		}
		responseHelper.redirectToConfiguration();
	}

	/* Utility operations */

	protected void populateFlashMessages(final ResponseHelper responseHelper, final Map<String, Object> model) {
		model.put(Variables.SUCCESS_MESSAGE, responseHelper.getFlashVariable(Variables.SUCCESS_MESSAGE));
	}

	protected Collection<TemplateBundle> toTemplateBundles(final Collection<Bundle> bundles) {
		final List<TemplateBundle> templateBundles = new ArrayList<TemplateBundle>();
		for (final Bundle bundle : bundles) {
			templateBundles.add(new TemplateBundle(bundle));
		}
		Collections.sort(templateBundles);
		return templateBundles;
	}

	/**
	 * Restarts the framework in a separate thread, running as the 'system' user and using a read-only transaction. This
	 * boilerplate is necessary for accessing the repository during framework restart
	 */
	protected void restartFrameworkAsynchronously() {
		Executors.newSingleThreadExecutor().execute(new Runnable() {

			@Override
			public void run() {
				frameworkHelper.restartFramework();
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected Collection<SystemPackage> getSystemPackages() {
		return (Collection<SystemPackage>) systemPackages.get(0);
	}

	@SuppressWarnings("rawtypes")
	protected List<TemplateBundle> getServicesByBundle() {
		final Map<Long, List<ServiceReference>> servicesByBundleId = new LinkedHashMap<Long, List<ServiceReference>>();
		final List<ServiceReference> allServices = bundleHelper.getAllServices();
		for (final ServiceReference serviceReference : allServices) {
			final long bundleId = serviceReference.getBundle().getBundleId();
			if (servicesByBundleId.containsKey(bundleId) == false) {
				servicesByBundleId.put(bundleId, new ArrayList<ServiceReference>());
			}
			servicesByBundleId.get(bundleId).add(serviceReference);
		}
		final List<TemplateBundle> templateBundles = new ArrayList<TemplateBundle>(servicesByBundleId.keySet().size());
		for (final Entry<Long, List<ServiceReference>> entry : servicesByBundleId.entrySet()) {
			final Bundle bundle = bundleHelper.getBundle(entry.getKey());
			final List<ServiceReference> services = servicesByBundleId.get(entry.getKey());
			templateBundles.add(new TemplateBundle(bundle, services));
		}
		return templateBundles;
	}

	/* Attributes */

	@Attribute
	protected ResponseHelper getResponseHelper(final WebScriptRequest request, final WebScriptResponse response) {
		return new ResponseHelper(request, response);
	}

	/* Reference data */

	@Attribute(Variables.CONFIGURATION)
	protected Configuration getConfiguration() {
		return configuration;
	}

	@Attribute(Variables.CAN_RESTART_FRAMEWORK)
	protected boolean canRestartFramework() {
		return configuration.getMode().isFrameworkRestartEnabled() && systemPackageCacheExists();
	}

	@Attribute(Variables.SYSTEM_PACKAGE_CACHE_EXISTS)
	protected boolean systemPackageCacheExists() {
		return (repositoryStoreService.getSystemPackageCache() != null);
	}

	@Attribute(Variables.SYSTEM_PACKAGE_CACHE_NODEREF)
	protected String getSystemPackageCacheNodeRef() {
		final FileInfo systemPackageCache = repositoryStoreService.getSystemPackageCache();
		if (systemPackageCache != null) {
			return systemPackageCache.getNodeRef().toString();
		} else {
			return null;
		}
	}

	@Attribute(Variables.REPOSITORY_STORE_LOCATION)
	protected String getRepositoryStoreLocation() {
		return bundleHelper.getBundleRepositoryLocation();
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
