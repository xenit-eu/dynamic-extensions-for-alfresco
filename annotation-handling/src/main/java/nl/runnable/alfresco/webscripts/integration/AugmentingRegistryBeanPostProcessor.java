/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
