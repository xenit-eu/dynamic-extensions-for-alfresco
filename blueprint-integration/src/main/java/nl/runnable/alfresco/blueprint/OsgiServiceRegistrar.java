package nl.runnable.alfresco.blueprint;

import java.util.Arrays;

import nl.runnable.alfresco.annotations.OsgiService;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class OsgiServiceRegistrar implements ApplicationContextAware, BundleContextAware {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private ApplicationContext applicationContext;

	private BundleContext bundleContext;

	/* Main operations */

	public void registerOsgiServiceBeans() {
		for (final String beanName : applicationContext.getBeanDefinitionNames()) {
			final OsgiService service = applicationContext.findAnnotationOnBean(beanName, OsgiService.class);
			if (service != null) {
				registerOsgiService(beanName, service);
			}
		}
	}

	/* Utility operations */

	private void registerOsgiService(final String beanName, final OsgiService service) {
		final Class<?> type = applicationContext.getType(beanName);
		final Class<?>[] interfaces = getInterfaces(type, service);
		if (interfaces.length > 0) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Registering bean '{}' as OSGi service {}", Arrays.asList(interfaces));
				}
				final OsgiServiceFactoryBean factoryBean = new OsgiServiceFactoryBean();
				factoryBean.setInterfaces(interfaces);
				factoryBean.setBeanFactory(applicationContext);
				factoryBean.setTargetBeanName(beanName);
				factoryBean.setBundleContext(bundleContext);
				factoryBean.afterPropertiesSet();
			} catch (final Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Error registering bean '{}' as OSGi service.", beanName, e);
				}
			}
		}

	}

	protected Class<?>[] getInterfaces(final Class<?> type, final OsgiService service) {
		if (type.isInterface()) {
			return new Class<?>[] { type };
		} else {
			final Class<?>[] interfaces = service.interfaces().length > 0 ? service.interfaces() : type.getInterfaces();
			if (interfaces.length == 0) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"No interfaces specified on @Service annotation or the type '{}' does not implement an interface",
							type.getName());
				}
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
