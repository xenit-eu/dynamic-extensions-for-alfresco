package nl.runnable.alfresco.webscripts.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.util.Assert;

/**
 * {@link BeanPostProcessor} that transforms a Web Script {@link Registry} to an {@link AugmentingRegistry}, allowing
 * OSGi bundles to register additional {@link Registry} instances.
 * <p>
 * This {@link BeanPostProcessor} can be used multiple times.
 * 
 * @author Laurens Fridael
 * @see AugmentingRegistry
 * @see SearchPathRegistry
 */
public class AugmentingRegistryBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private ApplicationContext applicationContext;

	/* Configuration */

	private String registryBeanName;

	/* Main operations */

	/**
	 * Replaces the Alfresco WebScript {@link Registry} bean with an {@link AugmentingRegistry}.
	 * <p>
	 * If the bean is already an {@link AugmentingRegistry}, this methods adds additional {@link Registry} instances.
	 * 
	 * @see #setAdditionalRegistries(List)
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, final String beanName) throws BeansException {
		if (beanName.equals(getRegistryBeanName()) && bean instanceof Registry && !(bean instanceof AugmentingRegistry)) {
			final Collection<Registry> additionalRegistries = getAdditionalRegistries();
			if (additionalRegistries.isEmpty() == false) {
				if (logger.isDebugEnabled()) {
					logger.debug("Augmenting Web Script Registry bean {} with {} additional Registries.", beanName,
							additionalRegistries.size());
				}
				bean = createAugmentingRegistry((Registry) bean);
			}
		}
		return bean;
	}

	/**
	 * Does nothing.
	 */
	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

	/* Utility operations */

	/**
	 * Creates an {@link AugmentingRegistry} from the given {@link Registry}.
	 * 
	 * @param registry
	 * @return
	 */
	protected AugmentingRegistry createAugmentingRegistry(final Registry registry) {
		Assert.notNull(registry, "Registry cannot be null.");

		final List<Registry> registries = new ArrayList<Registry>();
		registries.add(registry);
		registries.addAll(getAdditionalRegistries());
		return new AugmentingRegistry(registries);
	}

	/* Dependencies */

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/* State */

	protected Collection<Registry> getAdditionalRegistries() {
		final RegistryProvider bean = applicationContext.getBean(RegistryProvider.class);
		Assert.state(bean != null, "Cannot find Web Script RegistryProvider.");
		return bean.getRegistries();
	}

	/* Configuration */

	public void setRegistryBeanName(final String registryBeanName) {
		Assert.hasText(registryBeanName);
		this.registryBeanName = registryBeanName;
	}

	protected String getRegistryBeanName() {
		return registryBeanName;
	}

}
