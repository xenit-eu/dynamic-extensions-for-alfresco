package com.github.dynamicextensionsalfresco.controlpanel;

import com.github.dynamicextensionsalfresco.controlpanel.template.TemplateBundle;
import com.github.dynamicextensionsalfresco.controlpanel.template.TemplateServiceReference;
import com.github.dynamicextensionsalfresco.controlpanel.template.Variables;
import com.github.dynamicextensionsalfresco.osgi.RepositoryStoreService;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Authentication;
import com.github.dynamicextensionsalfresco.webscripts.annotations.AuthenticationType;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Cache;
import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.UriVariable;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Handles requests for the configuration page.
 * 
 * @author Laurens Fridael
 * 
 */
@Component
@WebScript(baseUri = "/dynamic-extensions/container", defaultFormat = "html", families = "control panel")
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
	public Map<String, Object> services() throws InvalidSyntaxException {
		return model(Variables.SERVICES_BY_BUNDLE, getTemplateServicesByBundle());
	}

	@Uri(method = HttpMethod.GET, value = "/services/bundle/{bundleid}/{serviceIndex}")
	public Map<String, Object> service(@UriVariable final long bundleid, @UriVariable int serviceIndex)
			throws InvalidSyntaxException {
        final List<TemplateBundle> servicesByBundle = getTemplateServicesByBundle();
        TemplateServiceReference serviceReferences = null;
        for (TemplateBundle templateBundle : servicesByBundle) {
            if (templateBundle.getBundleId() == bundleid) {
                serviceReferences = templateBundle.getServices().get(serviceIndex);
                break;
            }
        }
        final Object context;
        if (serviceReferences != null) {
            @SuppressWarnings("unchecked")
            final ServiceReference<Object> serviceReference = serviceReferences.getServiceReference();
            context = bundleHelper.getBundle(bundleid).getBundleContext().getService(serviceReference);
            if (context instanceof ApplicationContext) {
                ApplicationContext applicationContext = (ApplicationContext)context;
                final String[] definitionNames = applicationContext.getBeanDefinitionNames();
                final Map<String,String> beans = new HashMap<String, String>(definitionNames.length);
                for (String name : definitionNames) {
	                try {
		                final Object instance = applicationContext.getBean(name);
		                final String className;
		                if (AopUtils.isAopProxy(instance)) {
		                    // remark: getTargetClass does not resolve for non Spring Proxies
		                    className = "[Spring Proxy] " + AopUtils.getTargetClass(instance).getName();
		                } else if (Proxy.isProxyClass(instance.getClass())) {
		                    className = "[Java Proxy] " + Proxy.getInvocationHandler(instance).getClass().getName() + " -> " + Arrays.toString(ClassUtils.getAllInterfaces(instance));
		                } else {
		                    className = instance.getClass().getCanonicalName();
		                }
		                beans.put(name, className);
	                } catch (BeanIsAbstractException ignore) {}
                }
                return model("contextBeans", beans);
            }
            throw new IllegalArgumentException("Service is not a Spring ApplicationContext, but a " + context.getClass().getName());
        } else {
            throw new IllegalArgumentException(String.format("No service found for bundle %s at index %s.",bundleid, serviceIndex));
        }
	}

	/* Utility operations */

	@SuppressWarnings("rawtypes")
	protected List<TemplateBundle> getTemplateServicesByBundle() throws InvalidSyntaxException {
        final Map<Long, List<ServiceReference<Object>>> servicesByBundleId = getServicesByBundleId();
		final List<TemplateBundle> templateBundles = new ArrayList<TemplateBundle>(servicesByBundleId.keySet().size());
		for (final Entry<Long, List<ServiceReference<Object>>> entry : servicesByBundleId.entrySet()) {
			final Bundle bundle = bundleHelper.getBundle(entry.getKey());
			final List<ServiceReference<Object>> services = servicesByBundleId.get(entry.getKey());
			templateBundles.add(new TemplateBundle(bundle, services));
		}
		return templateBundles;
	}

    @SuppressWarnings("unchecked")
	protected Map<Long, List<ServiceReference<Object>>> getServicesByBundleId() throws InvalidSyntaxException {
        final Map<Long, List<ServiceReference<Object>>> servicesByBundleId = new LinkedHashMap<Long, List<ServiceReference<Object>>>();
        final ServiceReference<?>[] allServices = bundleHelper.getAllServices();
		if (allServices != null) {
			for (final ServiceReference serviceReference : allServices) {
                final long bundleId = serviceReference.getBundle().getBundleId();
                if (servicesByBundleId.containsKey(bundleId) == false) {
                    servicesByBundleId.put(bundleId, new ArrayList<ServiceReference<Object>>());
                }
                servicesByBundleId.get(bundleId).add(serviceReference);
            }
		}
		return servicesByBundleId;
	}

	/* Reference data */

	@Attribute(Variables.CAN_RESTART_FRAMEWORK)
	protected boolean canRestartFramework() {
		return osgiConfiguration.getFrameworkRestartEnabled();
	}

	@Attribute(Variables.SYSTEM_PACKAGE_CACHE_EXISTS)
	protected boolean systemPackageCacheExists() {
		return (getConfiguration().getSystemPackageCache().isFile());
	}

	@Attribute(Variables.REPOSITORY_STORE_LOCATION)
	protected String getRepositoryStoreLocation() {
		return bundleHelper.getBundleRepositoryLocation();
	}

	/* Utility operations */

	protected void restartFramework(final Framework framework) {
		try {
			framework.stop();
			framework.waitForStop(0);
			framework.start();
		} catch (final BundleException ignore) {
		} catch (final InterruptedException ignore) {
		}
	}

}
