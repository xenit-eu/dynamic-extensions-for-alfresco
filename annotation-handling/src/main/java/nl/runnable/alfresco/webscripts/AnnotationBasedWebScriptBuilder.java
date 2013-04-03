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

package nl.runnable.alfresco.webscripts;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nl.runnable.alfresco.webscripts.annotations.Attribute;
import nl.runnable.alfresco.webscripts.annotations.Authentication;
import nl.runnable.alfresco.webscripts.annotations.Cache;
import nl.runnable.alfresco.webscripts.annotations.Transaction;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;
import org.springframework.extensions.webscripts.Description.RequiredTransaction;
import org.springframework.extensions.webscripts.Description.TransactionCapability;
import org.springframework.extensions.webscripts.DescriptionImpl;
import org.springframework.extensions.webscripts.TransactionParameters;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Creates {@link AnnotationBasedWebScript} instances from beans defined in a {@link BeanFactory}.
 * 
 * @author Laurens Fridael
 * 
 */
public class AnnotationBasedWebScriptBuilder implements BeanFactoryAware {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private ConfigurableListableBeanFactory beanFactory;

	private AnnotationBasedWebScriptHandler annotationBasedWebScriptHandler = new AnnotationBasedWebScriptHandler();

	/* Main operations */

	/**
	 * Creates {@link AnnotationBasedWebScript}s from a given named bean by scanning methods annotated with {@link Uri}.
	 * 
	 * @param beanName
	 * @return The {@link AnnotationBasedWebScript} or null if the implementation does not consider the bean to be an
	 *         {@link AnnotationBasedWebScript}.
	 */
	public List<AnnotationBasedWebScript> createAnnotationBasedWebScripts(final String beanName) {
		Assert.hasText(beanName, "Bean name cannot be empty.");

		final ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		final Class<?> beanType = beanFactory.getType(beanName);
		if (beanType == null) {
			return null;
		}
		final WebScript webScriptAnnotation = beanFactory.findAnnotationOnBean(beanName, WebScript.class);
		if (webScriptAnnotation == null) {
			return null;
		}

		final List<AnnotationBasedWebScript> webScripts = new ArrayList<AnnotationBasedWebScript>();
		final List<Method> referenceDataMethods = new ArrayList<Method>();
		ReflectionUtils.doWithMethods(beanType, new ReflectionUtils.MethodCallback() {

			@Override
			public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
				final Attribute referenceDataAnnotation = AnnotationUtils.findAnnotation(method, Attribute.class);
				if (referenceDataAnnotation != null) {
					if (AnnotationUtils.findAnnotation(method, Uri.class) != null) {
						throw new RuntimeException("Cannot mark a method with both @Attribute and @Uri.");
					}
					if (method.getReturnType().equals(Void.TYPE)) {
						throw new RuntimeException("@Attribute methods cannot have a void return type.");
					}
					referenceDataMethods.add(method);
				}
			}
		});
		ReflectionUtils.doWithMethods(beanType, new ReflectionUtils.MethodCallback() {

			@Override
			public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
				final Uri uriAnnotation = AnnotationUtils.findAnnotation(method, Uri.class);
				if (uriAnnotation != null) {
					final AnnotationBasedWebScript webScript = createWebScript(beanName, uriAnnotation, method,
							referenceDataMethods);
					if (webScript != null) {
						webScripts.add(webScript);
					}
				}
			}

		});
		final Set<String> ids = new HashSet<String>();
		for (final AnnotationBasedWebScript webScript : webScripts) {
			if (ids.contains(webScript.getDescription().getId())) {
				throw new IllegalStateException("Duplicate Web Script ID \"" + webScript.getDescription().getId()
						+ "\" Make sure handler methods of annotation-based Web Scripts have unique names.");
			}
		}

		return webScripts;
	}

	/* Utility operations */

	protected AnnotationBasedWebScript createWebScript(final String beanName, final Uri uri,
			final Method handlerMethod, final List<Method> helperMethods) {
		Assert.hasText(beanName, "Bean name cannot be empty.");
		try {
			final DescriptionImpl description = new DescriptionImpl();
			handleHandlerMethodAnnotation(uri, handlerMethod, description);
			handleTypeAnnotations(beanName, description);
			final String id = String.format("%s.%s.%s", generateId(beanName), handlerMethod.getName(), description
					.getMethod().toLowerCase());
			description.setId(id);
			final Object handler = getBeanFactory().getBean(beanName);
			description.setStore(new AnnotationWebScriptStore());
			return new AnnotationBasedWebScript(description, handler, handlerMethod, helperMethods,
					getAnnotationBasedWebScriptHandler());
		} catch (final Exception e) {
			logger.warn("Error creating annotation-based Web Script for bean '{}'.", beanName, e);
			return null;
		}
	}

	protected void handleHandlerMethodAnnotation(final Uri uri, final Method method, final DescriptionImpl description) {
		Assert.notNull(uri, "Uri cannot be null.");
		Assert.notNull(method, "HttpMethod cannot be null.");
		Assert.notNull(description, "Description cannot be null.");

		description.setUris(uri.value());
		/*
		 * For the sake of consistency we translate the HTTP method from the HttpMethod enum. This also shields us from
		 * changes in the HttpMethod enum names.
		 */
		final String httpMethod;
		switch (uri.method()) {
		default:
			// Fall through
		case GET:
			httpMethod = "GET";
			break;
		case POST:
			httpMethod = "POST";
			break;
		case PUT:
			httpMethod = "PUT";
			break;
		case DELETE:
			httpMethod = "DELETE";
			break;
		case OPTIONS:
			httpMethod = "OPTIONS";
			break;
		}
		description.setMethod(httpMethod);
		/*
		 * Idem dito for FormatStyle.
		 */
		final Description.FormatStyle formatStyle;
		switch (uri.formatStyle()) {
		default:
			// Fall through
		case ANY:
			formatStyle = Description.FormatStyle.any;
			break;
		case ARGUMENT:
			formatStyle = Description.FormatStyle.argument;
			break;
		case EXTENSION:
			formatStyle = Description.FormatStyle.extension;
			break;
		}
		description.setFormatStyle(formatStyle);
		if (StringUtils.hasText(uri.defaultFormat())) {
			description.setDefaultFormat(uri.defaultFormat());
		}
	}

	protected void handleTypeAnnotations(final String beanName, final DescriptionImpl description) {
		final ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		final WebScript webScript = beanFactory.findAnnotationOnBean(beanName, WebScript.class);
		assert (webScript != null);
		handleWebScriptAnnotation(webScript, beanName, description);
		Authentication authentication = beanFactory.findAnnotationOnBean(beanName, Authentication.class);
		if (authentication == null) {
			authentication = getDefaultAuthenticationAnnotation();
		}
		handleAuthenticationAnnotation(authentication, beanName, description);
		Transaction transaction = beanFactory.findAnnotationOnBean(beanName, Transaction.class);
		if (transaction == null) {
			transaction = getDefaultTransactionAnnotation();
		}
		handleTransactionAnnotation(transaction, beanName, description);
		Cache cache = beanFactory.findAnnotationOnBean(beanName, Cache.class);
		if (cache == null) {
			cache = getDefaultCacheAnnotation();
		}
		handleCacheAnnotation(cache, beanName, description);

		description.setDescPath("");
	}

	protected void handleWebScriptAnnotation(final WebScript webScript, final String beanName,
			final DescriptionImpl description) {
		Assert.notNull(webScript, "Annotation cannot be null.");
		Assert.hasText(beanName, "Bean name cannot be empty.");
		Assert.notNull(description, "Description cannot be null.");
		Assert.hasText(description.getMethod(), "Description method is not specified.");

		if (StringUtils.hasText(webScript.value())) {
			description.setShortName(webScript.value());
		} else {
			description.setShortName(generateShortName(beanName));
		}
		if (StringUtils.hasText(webScript.description())) {
			description.setDescription(webScript.description());
		} else {
			description.setDescription(String.format("Annotation-based WebScript for class %s", getBeanFactory()
					.getType(beanName).getName()));
		}
		if (webScript.families() != null && webScript.families().length > 0) {
			description.setFamilys(new LinkedHashSet<String>(Arrays.asList(webScript.families())));
		}
		final Description.Lifecycle lifecycle;
		switch (webScript.lifecycle()) {
		default:
			// Fall through
		case NONE:
			lifecycle = Description.Lifecycle.none;
			break;
		case DRAFT:
			lifecycle = Description.Lifecycle.draft;
			break;
		case DRAFT_PUBLIC_API:
			lifecycle = Description.Lifecycle.draft_public_api;
			break;
		case DEPRECATED:
			lifecycle = Description.Lifecycle.deprecated;
			break;
		case INTERNAL:
			lifecycle = Description.Lifecycle.internal;
			break;
		case PUBLIC_API:
			lifecycle = Description.Lifecycle.public_api;
			break;
		case SAMPLE:
			lifecycle = Description.Lifecycle.sample;
			break;
		}
		description.setLifecycle(lifecycle);
	}

	protected void handleAuthenticationAnnotation(final Authentication authentication, final String beanName,
			final DescriptionImpl description) {
		Assert.notNull(authentication, "Annotation cannot be null.");
		Assert.hasText(beanName, "Bean name cannot be empty.");
		Assert.notNull(description, "Description cannot be null.");
		if (StringUtils.hasText(authentication.runAs())) {
			description.setRunAs(authentication.runAs());
		}
		final RequiredAuthentication requiredAuthentication;
		switch (authentication.value()) {
		case NONE:
			requiredAuthentication = RequiredAuthentication.none;
			break;
		case GUEST:
			requiredAuthentication = RequiredAuthentication.guest;
			break;
		default:
			// Fall through
		case USER:
			requiredAuthentication = RequiredAuthentication.user;
			break;
		case ADMIN:
			requiredAuthentication = RequiredAuthentication.admin;
			break;

		}
		description.setRequiredAuthentication(requiredAuthentication);
	}

	protected void handleTransactionAnnotation(final Transaction transaction, final String beanName,
			final DescriptionImpl description) {
		Assert.notNull(transaction, "Annotation cannot be null.");
		Assert.hasText(beanName, "Bean name cannot be empty.");
		Assert.notNull(description, "Description cannot be null.");

		final TransactionParameters transactionParameters = new TransactionParameters();
		final RequiredTransaction requiredTransaction;
		switch (transaction.value()) {
		case NONE:
			requiredTransaction = RequiredTransaction.none;
			break;
		default:
			// Fall through
		case REQUIRED:
			requiredTransaction = RequiredTransaction.required;
			break;
		case REQUIRES_NEW:
			requiredTransaction = RequiredTransaction.requiresnew;
			break;
		}
		transactionParameters.setRequired(requiredTransaction);
		if (transaction.readOnly()) {
			transactionParameters.setCapability(TransactionCapability.readonly);
		} else {
			transactionParameters.setCapability(TransactionCapability.readwrite);
		}
		transactionParameters.setBufferSize(transaction.bufferSize());
		description.setRequiredTransactionParameters(transactionParameters);
	}

	protected void handleCacheAnnotation(final Cache cache, final String beanName, final DescriptionImpl description) {
		Assert.notNull(cache, "Annotation cannot be null.");
		Assert.hasText(beanName, "Bean name cannot be empty.");
		Assert.notNull(description, "Description cannot be null.");

		final org.springframework.extensions.webscripts.Cache requiredCache = new org.springframework.extensions.webscripts.Cache();
		requiredCache.setNeverCache(cache.neverCache());
		requiredCache.setIsPublic(cache.isPublic());
		requiredCache.setMustRevalidate(cache.mustRevalidate());
		description.setRequiredCache(requiredCache);

	}

	protected String generateId(final String beanName) {
		Assert.hasText(beanName, "Bean name cannot be empty");
		final Class<?> clazz = getBeanFactory().getType(beanName);
		return clazz.getName();
	}

	protected String generateShortName(final String beanName) {
		Assert.hasText(beanName, "Bean name cannot be empty");
		final Class<?> clazz = getBeanFactory().getType(beanName);
		return ClassUtils.getShortName(clazz);
	}

	/*
	 * These methods use local classes to obtain annotations with default settings.
	 */
	private static Authentication getDefaultAuthenticationAnnotation() {
		@Authentication
		class Default {
		}
		return Default.class.getAnnotation(Authentication.class);
	}

	private static Transaction getDefaultTransactionAnnotation() {
		@Transaction
		class Default {
		}
		return Default.class.getAnnotation(Transaction.class);
	}

	private static Cache getDefaultCacheAnnotation() {
		@Cache
		class Default {
		}
		return Default.class.getAnnotation(Cache.class);
	}

	/* Dependencies */

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory,
				"BeanFactory is not of type ConfigurableListableBeanFactory.");
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	protected ConfigurableListableBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setAnnotationBasedWebScriptHandler(final AnnotationBasedWebScriptHandler annotationMethodHandler) {
		Assert.notNull(annotationMethodHandler);
		this.annotationBasedWebScriptHandler = annotationMethodHandler;
	}

	protected AnnotationBasedWebScriptHandler getAnnotationBasedWebScriptHandler() {
		return annotationBasedWebScriptHandler;
	}

}
