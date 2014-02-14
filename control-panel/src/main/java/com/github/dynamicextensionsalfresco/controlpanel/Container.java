package com.github.dynamicextensionsalfresco.controlpanel;

import com.github.dynamicextensionsalfresco.controlpanel.template.TemplateBundle;
import com.github.dynamicextensionsalfresco.controlpanel.template.Variables;
import com.github.dynamicextensionsalfresco.osgi.RepositoryStoreService;
import com.github.dynamicextensionsalfresco.webscripts.annotations.*;
import com.github.dynamicextensionsalfresco.webscripts.resolutions.RedirectResolution;
import com.github.dynamicextensionsalfresco.webscripts.resolutions.Resolution;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * Handles requests for the configuration page.
 * 
 * @author Laurens Fridael
 * 
 */
@Component
@WebScript(baseUri = "/dynamic-extensions/container", defaultFormat = "html")
@Authentication(AuthenticationType.ADMIN)
@Cache(neverCache = true)
public class Container extends AbstractControlPanelHandler {

	/* Dependencies */

	@Autowired
	private BundleHelper bundleHelper;

	@Autowired
	private FileFolderService fileFolderService;

	@Autowired
	private NodeService nodeService;

	@Autowired
	private RepositoryStoreService repositoryStoreService;

	/* Main operations */

	@Uri(method = HttpMethod.GET)
	public Map<String, Object> index() {
		return Collections.emptyMap();
	}

	@Uri(method = HttpMethod.GET, value = "/system-packages")
	public Map<String, Object> systemPackages() {
		return model(Variables.SYSTEM_PACKAGES, getSystemPackages());
	}

	@Uri(method = HttpMethod.GET, value = "/services")
	public Map<String, Object> services() {
		return model(Variables.SERVICES_BY_BUNDLE, getServicesByBundle());
	}

	@Uri(method = HttpMethod.POST, value = "/system-package-cache/delete")
	public Resolution deleteSystemPackageCache(@Attribute final ResponseHelper responseHelper) {
		final FileInfo systemPackageCache = repositoryStoreService.getSystemPackageCache();
		if (systemPackageCache != null) {
			nodeService.addAspect(systemPackageCache.getNodeRef(), ContentModel.ASPECT_TEMPORARY,
					Collections.<QName, Serializable> emptyMap());
			fileFolderService.delete(systemPackageCache.getNodeRef());
			responseHelper.flashSuccessMessage("Deleted System Package cache.");
		} else {
			responseHelper.flashErrorMessage("System Package cache was not found, It may have been deleted already.");
		}
		return new RedirectResolution(Urls.CONTAINER);
	}

	/* Utility operations */

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
		return osgiConfiguration.isFrameworkRestartEnabled();
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
		} catch (final BundleException ignore) {
		} catch (final InterruptedException ignore) {
		}
	}

}
