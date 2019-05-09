package com.github.dynamicextensionsalfresco.blueprint;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * {@link ApplicationContext} wrapper that contains no-op implementation of {@link #publishEvent(ApplicationEvent)}.
 * This, in turn, prevents the propagation of {@link ApplicationEvent}s to the main Alfresco {@link ApplicationContext}.
 * <p>
 * Publishing {@link ApplicationEvent}s, such as {@link ContextRefreshedEvent}, has unwanted side-effects on
 * {@link ApplicationContext}s that are expected to remain static during the lifetime of an application.
 * <p>
 * We extend {@link AbstractApplicationContext} in preference to the {@link ApplicationContext}
 * interface to avoid referencing the newly introduced Spring Environment type and this remain compatible with different
 * Alfresco versions
 *
 * @author Laurens Fridael
 * @author Laurent Van der Linden
 */
class Spring3HostApplicationContext extends AbstractApplicationContext {

	private final ApplicationContext applicationContext;

	/**
	 * No-op.
	 *
	 * @param event
	 */
	@Override
	public void publishEvent(final ApplicationEvent event) {
	}

	/* Delegated operations */

	@Override
	public BeanFactory getParentBeanFactory() {
		return applicationContext.getParentBeanFactory();
	}

	@Override
	public boolean containsLocalBean(final String name) {
		return applicationContext.containsLocalBean(name);
	}

	@Override
	public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
		return applicationContext.getMessage(code, args, defaultMessage, locale);
	}

	@Override
	public Resource getResource(final String location) {
		return applicationContext.getResource(location);
	}

	@Override
	public String getMessage(final String code, final Object[] args, final Locale locale) throws NoSuchMessageException {
		return applicationContext.getMessage(code, args, locale);
	}

	@Override
	public boolean containsBeanDefinition(final String beanName) {
		return applicationContext.containsBeanDefinition(beanName);
	}

	@Override
	public String getId() {
		return applicationContext.getId();
	}

	@Override
	public ClassLoader getClassLoader() {
		return applicationContext.getClassLoader();
	}

	@Override
	public Resource[] getResources(final String locationPattern) throws IOException {
		return applicationContext.getResources(locationPattern);
	}

	@Override
	public String getDisplayName() {
		return applicationContext.getDisplayName();
	}

	@Override
	public String getMessage(final MessageSourceResolvable resolvable, final Locale locale)
			throws NoSuchMessageException {
		return applicationContext.getMessage(resolvable, locale);
	}

	@Override
	public long getStartupDate() {
		return applicationContext.getStartupDate();
	}

	@Override
	public int getBeanDefinitionCount() {
		return applicationContext.getBeanDefinitionCount();
	}

	@Override
	public ApplicationContext getParent() {
		return applicationContext.getParent();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return applicationContext.getBeanDefinitionNames();
	}

	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		return applicationContext.getAutowireCapableBeanFactory();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String[] getBeanNamesForType(final Class type) {
		return applicationContext.getBeanNamesForType(type);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String[] getBeanNamesForType(final Class type, final boolean includeNonSingletons,
			final boolean allowEagerInit) {
		return applicationContext.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public Object getBean(final String name) throws BeansException {
		return applicationContext.getBean(name);
	}

	@Override
	public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
		return applicationContext.getBean(name, requiredType);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(final Class<T> type) throws BeansException {
		return applicationContext.getBeansOfType(type);
	}

	@Override
	public <T> T getBean(final Class<T> requiredType) throws BeansException {
		return applicationContext.getBean(requiredType);
	}

	@Override
	public Object getBean(final String name, final Object... args) throws BeansException {
		return applicationContext.getBean(name, args);
	}

	@Override
	public boolean containsBean(final String name) {
		return applicationContext.containsBean(name);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(final Class<T> type, final boolean includeNonSingletons,
			final boolean allowEagerInit) throws BeansException {
		return applicationContext.getBeansOfType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
		return applicationContext.isSingleton(name);
	}

	@Override
	public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
		return applicationContext.isPrototype(name);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isTypeMatch(final String name, final Class targetType) throws NoSuchBeanDefinitionException {
		return applicationContext.isTypeMatch(name, targetType);
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType)
			throws BeansException {
		return applicationContext.getBeansWithAnnotation(annotationType);
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType) {
		return applicationContext.findAnnotationOnBean(beanName, annotationType);
	}

	@Override
	public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
		return applicationContext.getType(name);
	}

	@Override
	public String[] getAliases(final String name) {
		return applicationContext.getAliases(name);
	}

	Spring3HostApplicationContext(final ApplicationContext applicationContext) {
		Assert.notNull(applicationContext);
		this.applicationContext = applicationContext;
	}

	@Override
	protected void refreshBeanFactory() throws BeansException, IllegalStateException {}

	@Override
	protected void closeBeanFactory() {}

	@Override
	public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
		return ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
	}
}
