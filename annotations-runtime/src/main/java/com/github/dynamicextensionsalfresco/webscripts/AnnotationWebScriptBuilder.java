package com.github.dynamicextensionsalfresco.webscripts;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Authentication;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Before;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Cache;
import com.github.dynamicextensionsalfresco.webscripts.annotations.ExceptionHandler;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Transaction;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import com.github.dynamicextensionsalfresco.webscripts.arguments.HandlerMethodArgumentsResolver;

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
 * Creates {@link AnnotationWebScript} instances from beans defined in a {@link BeanFactory}.
 * 
 * @author Laurens Fridael
 * 
 */
public class AnnotationWebScriptBuilder implements BeanFactoryAware {

	/* Dependencies */

	private ConfigurableListableBeanFactory beanFactory;

	private HandlerMethodArgumentsResolver handlerMethodArgumentsResolver;

	/* Main operations */

	/**
	 * Creates {@link AnnotationWebScript}s from a given named bean by scanning methods annotated with {@link Uri}.
	 * 
	 * @param beanName
	 * @return The {@link AnnotationWebScript} or null if the implementation does not consider the bean to be a handler
	 *         for an {@link AnnotationWebScript}.
	 */
	public List<org.springframework.extensions.webscripts.WebScript> createWebScripts(final String beanName) {
		Assert.hasText(beanName, "Bean name cannot be empty.");

		final ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		final Class<?> beanType = beanFactory.getType(beanName);
		if (beanType == null) {
			return Collections.emptyList();
		}
		final WebScript webScriptAnnotation = beanFactory.findAnnotationOnBean(beanName, WebScript.class) != null ? beanFactory
				.findAnnotationOnBean(beanName, WebScript.class) : getDefaultWebScriptAnnotation();
		final String baseUri = webScriptAnnotation.baseUri();
		if (StringUtils.hasText(baseUri) && baseUri.startsWith("/") == false) {
			throw new RuntimeException(String.format(
					"@WebScript baseUri for class '%s' does not start with a slash: '%s'", beanType, baseUri));
		}

		final HandlerMethods handlerMethods = new HandlerMethods();
		ReflectionUtils.doWithMethods(beanType, new ReflectionUtils.MethodCallback() {

			@Override
			public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
				final Before before = AnnotationUtils.findAnnotation(method, Before.class);
				if (before != null) {
					if (AnnotationUtils.findAnnotation(method, Attribute.class) != null
							|| AnnotationUtils.findAnnotation(method, Uri.class) != null) {
						throw new RuntimeException(String.format(
								"Cannot combine @Before, @Attribute and @Uri on a single method. Method: %s",
								ClassUtils.getQualifiedMethodName(method)));
					}
					handlerMethods.getBeforeMethods().add(method);
				}
			}
		});
		ReflectionUtils.doWithMethods(beanType, new ReflectionUtils.MethodCallback() {

			@Override
			public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
				final Attribute attribute = AnnotationUtils.findAnnotation(method, Attribute.class);
				if (attribute != null) {
					if (AnnotationUtils.findAnnotation(method, Before.class) != null
							|| AnnotationUtils.findAnnotation(method, Uri.class) != null) {
						throw new RuntimeException(String.format(
								"Cannot combine @Before, @Attribute and @Uri on a single method. Method: %s",
								ClassUtils.getQualifiedMethodName(method)));
					}
					if (method.getReturnType().equals(Void.TYPE)) {
						throw new RuntimeException("@Attribute methods cannot have a void return type.");
					}
					handlerMethods.getAttributeMethods().add(method);
				}
			}
		});
		ReflectionUtils.doWithMethods(beanType, new ReflectionUtils.MethodCallback() {

			@Override
			public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
				final ExceptionHandler exceptionHandler = AnnotationUtils
						.findAnnotation(method, ExceptionHandler.class);
				if (exceptionHandler != null) {
					if (AnnotationUtils.findAnnotation(method, Attribute.class) != null
							|| AnnotationUtils.findAnnotation(method, Before.class) != null
							|| AnnotationUtils.findAnnotation(method, Uri.class) != null) {
						throw new RuntimeException(
								String.format(
										"Cannot combine @Before, @Attribute @ExceptionHandler or @Uri on a single method. Method: %s",
										ClassUtils.getQualifiedMethodName(method)));
					}
					handlerMethods.getExceptionHandlerMethods().add(
							new ExceptionHandlerMethod(exceptionHandler.value(), method));
				}
			}
		});
		final List<org.springframework.extensions.webscripts.WebScript> webScripts = new ArrayList<org.springframework.extensions.webscripts.WebScript>();
		ReflectionUtils.doWithMethods(beanType, new ReflectionUtils.MethodCallback() {

			@Override
			public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
				final Uri uri = AnnotationUtils.findAnnotation(method, Uri.class);
				if (uri != null) {
					final AnnotationWebScript webScript = createWebScript(beanName, webScriptAnnotation, uri,
							handlerMethods.createForUriMethod(method));
					webScripts.add(webScript);
				}
			}

		});
		final Set<String> ids = new HashSet<String>();
		for (final org.springframework.extensions.webscripts.WebScript webScript : webScripts) {
			final String webscriptId = webScript.getDescription().getId();
			final boolean notContained = ids.add(webscriptId);
			if (!notContained) {
				throw new IllegalStateException("Duplicate Web Script ID \"" + webscriptId
						+ "\" Make sure handler methods of annotation-based Web Scripts have unique names.");
			}
		}

		return webScripts;
	}

	/* Utility operations */

	protected AnnotationWebScript createWebScript(final String beanName, final WebScript webScript, final Uri uri,
			final HandlerMethods handlerMethods) {
		final DescriptionImpl description = new DescriptionImpl();
		if (StringUtils.hasText(webScript.defaultFormat())) {
			description.setDefaultFormat(webScript.defaultFormat());
		}
		final String baseUri = webScript.baseUri();
		handleHandlerMethodAnnotation(uri, handlerMethods.getUriMethod(), description, baseUri);
		handleTypeAnnotations(beanName, webScript, description);
		final String id = String.format("%s.%s.%s", generateId(beanName), handlerMethods.getUriMethod().getName(),
				description.getMethod().toLowerCase());
		description.setId(id);
		final Object handler = getBeanFactory().getBean(beanName);
		description.setStore(new AnnotationWebScriptStore());
		return createWebScript(description, handler, handlerMethods);
	}

	protected AnnotationWebScript createWebScript(final Description description, final Object handler,
			final HandlerMethods handlerMethods) {
		return new AnnotationWebScript(description, handler, handlerMethods, getHandlerMethodArgumentsResolver());
	}

	protected void handleHandlerMethodAnnotation(final Uri uri, final Method method, final DescriptionImpl description,
			final String baseUri) {
		Assert.notNull(uri, "Uri cannot be null.");
		Assert.notNull(method, "HttpMethod cannot be null.");
		Assert.notNull(description, "Description cannot be null.");

		final String[] uris;
		if (uri.value().length > 0) {
			uris = new String[uri.value().length];
			for (int i = 0; i < uris.length; i++) {
				uris[i] = String.format("%s/%s", baseUri.replaceAll("/$", ""), uri.value()[i].replaceAll("^/", ""));
			}
		} else if (StringUtils.hasText(baseUri)) {
			uris = new String[] { baseUri.replaceAll("/$", "") };
		} else {
			throw new RuntimeException(String.format(
					"No value specified for @Uri on method '%s' and no base URI found for @WebScript on class.",
					ClassUtils.getQualifiedMethodName(method)));
		}
		description.setUris(uris);
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
		description.setMultipartProcessing(uri.multipartProcessing());
	}

	protected void handleTypeAnnotations(final String beanName, final WebScript webScript,
			final DescriptionImpl description) {
		final ConfigurableListableBeanFactory beanFactory = getBeanFactory();
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

	private static WebScript getDefaultWebScriptAnnotation() {
		@WebScript
		class Default {
		}
		return Default.class.getAnnotation(WebScript.class);
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

	public void setHandlerMethodArgumentsResolver(final HandlerMethodArgumentsResolver handlerMethodArgumentsResolver) {
		Assert.notNull(handlerMethodArgumentsResolver);
		this.handlerMethodArgumentsResolver = handlerMethodArgumentsResolver;
	}

	protected HandlerMethodArgumentsResolver getHandlerMethodArgumentsResolver() {
		return handlerMethodArgumentsResolver;
	}

}
