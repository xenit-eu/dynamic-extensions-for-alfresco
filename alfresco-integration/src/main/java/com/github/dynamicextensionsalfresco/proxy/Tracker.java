package com.github.dynamicextensionsalfresco.proxy;

import com.github.dynamicextensionsalfresco.osgi.FrameworkManager;
import com.github.dynamicextensionsalfresco.osgi.FrameworkService;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks services within the embedded OSGi context using interface matching or a custom {@link Filter}.
 * When {@link #invokeUsing(ServiceInvoker)} is called, the {@link ServiceInvoker} receives the sole matching instance.
 * This pattern is used so that we can avoid keeping a reference to the service. (dynamic nature of service discovery)
 *
 * @author Laurent Van der Linden
 */
public class Tracker {
	private final static Logger logger = LoggerFactory.getLogger(Tracker.class);

	private final FilterModel filterModel;
	private final FrameworkService frameworkService;

	private volatile ServiceTracker<Object,Object> serviceTracker;
	private volatile FrameworkManager frameworkManager;

	public Tracker(FilterModel filterModel, FrameworkService frameworkService) {
		this.filterModel = filterModel;
		this.frameworkService = frameworkService;
	}

	/**
	 * Perform an action on a service.
	 * @param serviceInvoker action to receive a reference to the service
	 * @param <T> the target service type
	 * @return result from the service invocation
	 * @throws IllegalStateException if an exact service match is not possible
	 */
	@SuppressWarnings("unchecked")
	public <T> Object invokeUsing(ServiceInvoker<T> serviceInvoker) throws Throwable {
		try {
			if (frameworkManager != null && frameworkManager != frameworkService.getFrameworkManager()) {
				// the OSGi context was restarted, discard any references
				closeServiceTracker();
				frameworkManager = null;
			}
			if (serviceTracker == null) {
				synchronized (this) {
					if (serviceTracker == null) {
						if (frameworkManager == null) {
							frameworkManager = frameworkService.getFrameworkManager();
						}
						serviceTracker = new ServiceTracker<Object, Object>(
							frameworkManager.getFramework().getBundleContext(),
							filterModel.getServiceFilter(), null)
						;
						serviceTracker.open(true);
						logger.debug("Opened servicetracker using filter {}.", filterModel.getServiceFilter());
					}
				}
			}
		} catch (IllegalStateException e) {
			throw new IllegalStateException("OSGi Framework is not ready yet.", e);
		}

		final ServiceReference<Object>[] serviceReferences = serviceTracker.getServiceReferences();
		if (serviceReferences == null) {
			throw new IllegalStateException(
				String.format("No Dynamic service found using filter %s.", filterModel.getServiceFilter())
			);
		}
		if (serviceReferences.length != 1) {
			throw new IllegalStateException(
				String.format("More then 1 service found using filter %s.", filterModel.getServiceFilter())
			);
		}
		final ServiceReference<Object> soleReference = serviceReferences[0];
		final Object service = serviceTracker.getService(soleReference);
		try {
			return serviceInvoker.invokeService((T) service);
		} finally {
			serviceTracker.removedService(soleReference, service);
		}
	}

	private synchronized void closeServiceTracker() {
		if (serviceTracker != null) {
			serviceTracker.close();
			serviceTracker = null;
			logger.debug("Closed servicetracker using filter {}.", filterModel.getServiceFilter());
		}
	}

	public FilterModel getFilterModel() {
		// add a hook to reset the ServiceTracker
		return new FilterModel() {
			@Override
			public Filter getServiceFilter() {
				return filterModel.getServiceFilter();
			}

			@Override
			public void setServiceFilter(Filter serviceFilter) {
				filterModel.setServiceFilter(serviceFilter);
				closeServiceTracker();
			}

			@Override
			public void setServiceFilterString(String ldapFilter) {
				filterModel.setServiceFilterString(ldapFilter);
				closeServiceTracker();
			}
		};
	}
}
