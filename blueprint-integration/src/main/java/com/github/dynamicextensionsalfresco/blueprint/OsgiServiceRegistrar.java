package com.github.dynamicextensionsalfresco.blueprint;

import com.github.dynamicextensionsalfresco.osgi.OsgiService;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

public class OsgiServiceRegistrar implements ApplicationContextAware, BundleContextAware, InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ApplicationContext applicationContext;

	private BundleContext bundleContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        final Map<String,Object> exportables = applicationContext.getBeansWithAnnotation(OsgiService.class);
		for (final Map.Entry<String, Object> entry : exportables.entrySet()) {
            final OsgiService osgiService = applicationContext.findAnnotationOnBean(entry.getKey(), OsgiService.class);
            registerOsgiService(entry.getKey(), osgiService);
        }
	}

	private void registerOsgiService(final String beanName, final OsgiService service) {
		final Class<?> type = applicationContext.getType(beanName);
		final Class<?>[] interfaces = getInterfaces(type, service);
        try {
            logger.debug("Registering bean {} as OSGi service using interfaces {}.", beanName, Arrays.asList(interfaces));
            final OsgiServiceFactoryBean factoryBean = new OsgiServiceFactoryBean();
            factoryBean.setServiceProperties(getServiceProperties(service));
            factoryBean.setInterfaces(interfaces);
            factoryBean.setBeanFactory(applicationContext);
            factoryBean.setTargetBeanName(beanName);
            factoryBean.setBundleContext(bundleContext);
            factoryBean.afterPropertiesSet();
        } catch (final Exception e) {
            logger.warn("Error registering bean '{}' as OSGi service.", beanName, e);
        }
    }

    private Map getServiceProperties(final OsgiService osgiService) {
        final OsgiService.ExportHeader[] headers = osgiService.headers();
        final Map<String,String> properties = new Hashtable<String,String>(headers.length);
        for (OsgiService.ExportHeader header : headers) {
            properties.put(header.key(), header.value());
        }
        return properties;
    }

    protected Class<?>[] getInterfaces(final Class<?> type, final OsgiService service) {
		if (type.isInterface()) {
			return new Class<?>[] { type };
		} else {
			final Class<?>[] interfaces = service.interfaces().length > 0 ? service.interfaces() : ClassUtils.getAllInterfacesForClass(type);
			if (interfaces.length == 0) {
				return new Class[] {type};
			}
			return interfaces;
		}
	}

	/* Dependencies */

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setBundleContext(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
}
