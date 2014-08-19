package com.github.dynamicextensionsalfresco.proxy;

import com.github.dynamicextensionsalfresco.osgi.FrameworkService;
import org.osgi.framework.Filter;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * Spring {@link FactoryBean} to allow registration of service proxies at the global Alfresco level.
 * The emitted proxy will delegate to a service implemented by a Dynamic Extension, if any.
 * <br/>
 * These proxies are not guaranteed to be available at all times (startup or bundle uninstall),
 * so users of these proxies should handle {@link IllegalStateException} explicitly.
 * <br/>
 * The {@link Filter} will select the appropriate target instance for the proxy.
 * If you do not define it explicitly, the targetInterfaces will be used to compose one.
 * (find a extension service, implementing all specified interfaces.
 * <br/>
 * Note, that the filter should be specific enough to limit any matches to 1 service: if more then 1 match is found,
 * an {@link IllegalStateException} will be throw.
 *
 * @author Laurent Van der Linden
 */
public class BundleServiceProxyFactory implements FactoryBean<Object> {
	private FrameworkService frameworkService;

	private Filter filter;
	private Class<?>[] targetInterfaces;

	@Override
	public Object getObject() throws Exception {
		final Tracker tracker = new Tracker(new DefaultFilterModel(targetInterfaces, filter), frameworkService);

		Class<?>[] proxyClasses = new Class[targetInterfaces.length + 1];
		System.arraycopy(targetInterfaces, 0, proxyClasses, 0, targetInterfaces.length);
		proxyClasses[targetInterfaces.length] = FilterModel.class;

		return Proxy.newProxyInstance(getClass().getClassLoader(), proxyClasses, new ServiceInvocationHandler(tracker));
	}

	@Override
	public Class<?> getObjectType() {
        if (targetInterfaces == null || targetInterfaces.length == 0) {
            return null;
        } else {
            return targetInterfaces[0];
        }
    }

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setFrameworkService(FrameworkService frameworkService) {
		this.frameworkService = frameworkService;
	}

	public void setTargetInterfaces(Class<?>[] targetInterfaces) {
		this.targetInterfaces = targetInterfaces;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}
}
