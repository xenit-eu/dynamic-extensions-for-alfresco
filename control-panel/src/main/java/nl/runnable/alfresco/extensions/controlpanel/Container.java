package nl.runnable.alfresco.extensions.controlpanel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import nl.runnable.alfresco.extensions.controlpanel.template.TemplateBundle;
import nl.runnable.alfresco.extensions.controlpanel.template.Variables;
import nl.runnable.alfresco.osgi.RepositoryStoreService;
import nl.runnable.alfresco.webscripts.annotations.Attribute;
import nl.runnable.alfresco.webscripts.annotations.Authentication;
import nl.runnable.alfresco.webscripts.annotations.AuthenticationType;
import nl.runnable.alfresco.webscripts.annotations.Cache;
import nl.runnable.alfresco.webscripts.annotations.HttpMethod;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;

/**
 * Handles requests for the configuration page.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
@WebScript(baseUri = "/dynamic-extensions/container")
@Authentication(AuthenticationType.ADMIN)
@Cache(neverCache = true)
public class Container extends AbstractControlPanelHandler {

	/* Dependencies */

	@Inject
	private BundleHelper bundleHelper;

	@Inject
	private FrameworkHelper frameworkHelper;

	@Inject
	private FileFolderService fileFolderService;

	@Inject
	private NodeService nodeService;

	@Inject
	private RepositoryStoreService repositoryStoreService;

	/* Main operations */

	@Uri(method = HttpMethod.GET, defaultFormat = "html")
	public Map<String, Object> index() {
		return Collections.emptyMap();
	}

	@Uri(method = HttpMethod.GET, value = "/system-packages", defaultFormat = "html")
	public Map<String, Object> systemPackages() {
		return model(Variables.SYSTEM_PACKAGES, getSystemPackages());
	}

	@Uri(method = HttpMethod.GET, value = "/services", defaultFormat = "html")
	public Map<String, Object> services() {
		return model(Variables.SERVICES_BY_BUNDLE, getServicesByBundle());
	}

	@Uri(method = HttpMethod.POST, value = "/system-package-cache/delete")
	public void deleteSystemPackageCache(@Attribute final ResponseHelper responseHelper) {
		final FileInfo systemPackageCache = repositoryStoreService.getSystemPackageCache();
		if (systemPackageCache != null) {
			nodeService.addAspect(systemPackageCache.getNodeRef(), ContentModel.ASPECT_TEMPORARY,
					Collections.<QName, Serializable> emptyMap());
			fileFolderService.delete(systemPackageCache.getNodeRef());
			responseHelper.flashSuccessMessage("Deleted System Package cache.");
		} else {
			responseHelper.flashErrorMessage("System Package cache was not found, It may have been deleted already.");
		}
		responseHelper.redirectToContainer();
	}

	@Uri(method = HttpMethod.POST, value = "/restart")
	public void restartFramework(@Attribute final ResponseHelper response) throws IOException, BundleException {
		if (osgiConfiguration.getMode().isFrameworkRestartEnabled() == false) {
			response.status(HttpServletResponse.SC_FORBIDDEN, "Framework restart is currently not allowed.");
			return;
		}
		response.status(HttpServletResponse.SC_OK, "Restarting framework.");
		restartFrameworkAsynchronously();
	}

	/* Utility operations */

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

	/* Reference data */

	@Attribute(Variables.CAN_RESTART_FRAMEWORK)
	protected boolean canRestartFramework() {
		return osgiConfiguration.getMode().isFrameworkRestartEnabled() && systemPackageCacheExists();
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
