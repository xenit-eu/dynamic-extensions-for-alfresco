package com.github.dynamicextensionsalfresco.proxy;

import com.github.dynamicextensionsalfresco.osgi.FrameworkService;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.felix.framework.FilterImpl;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.springframework.beans.factory.FactoryBean;

/**
 * Spring {@link FactoryBean} to allow registration of service proxies at the global Alfresco level.
 * The emitted proxy will delegate to a service implemented by a Dynamic Extension, if any.
 *
 * These proxies are not guaranteed to be available at all times (startup or bundle uninstall),
 * so users of these proxies should handle {@link IllegalStateException} explicitly.
 *
 * The {@link Filter} will select the appropriate target instance for the proxy.
 * If you do not define it explicitly, the targetInterfaces will be used to compose one.
 * (find a extension service, implementing all specified interfaces.
 *
 * Note, that the filter should be specific enough to limit any matches to 1 service: if more then 1 match is found,
 * an {@link IllegalStateException} will be throw.
 */
public class BundleServiceProxyFactory<T> implements FactoryBean<T> {

    private List<Class<?>> targetInterfaces = new ArrayList<Class<?>>();
    public void setTargetInterfaces(List<Class<?>> targetInterfaces) {
        this.targetInterfaces = targetInterfaces;
    }

    private FrameworkService frameworkService = null;
    public void setFrameworkService(FrameworkService frameworkService) {
        this.frameworkService = frameworkService;
    }

    private Filter filter = null;
    public void setFilterString(String filterString) throws InvalidSyntaxException {
        this.filter = new FilterImpl(filterString);
    }

    @Override
    public boolean isSingleton() { return true; }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        Tracker tracker = new Tracker(new DefaultFilterModel(targetInterfaces, filter), frameworkService);
        Class<?>[] proxyClasses = targetInterfaces.toArray(new Class<?>[targetInterfaces.size() + 1]);
        proxyClasses[targetInterfaces.size()] = FilterModel.class;

        return (T)Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                proxyClasses,
                new ServiceInvocationHandler(tracker));
    }

    @Override
    public Class<?> getObjectType() {
        if (targetInterfaces == null || targetInterfaces.size() == 0) {
            return null;
        } else {
            return targetInterfaces.get(0);
        }
    }
}
