package com.github.dynamicextensionsalfresco.webscripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.extensions.webscripts.UriIndex;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Registers {@link WebScript}s in an {@link UriIndex}.
 * <p>
 * This implementation wraps and replaces the {@link UriIndex} bean, allowing {@link WebScript}s to be registered
 * independently of the existing {@link Registry}.
 * 
 * @author Laurens Fridael
 * 
 */
public class WebScriptUriRegistry implements UriIndex, BeanPostProcessor, InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private UriIndex uriIndex;

	private WebScript unavailableWebScript = new UnavailableWebScript();

	/* Configuration */

	private String uriIndexBeanName;

	/* State */

	private final List<WebScript> webScripts = new ArrayList<WebScript>();

	private final Map<UriMethod, WebScriptProxy> webScriptProxiesByUriMethod = new HashMap<UriMethod, WebScriptProxy>();

	private final Lock lock = new ReentrantLock();

	/* Main operations */

	/**
	 * Registers a {@link WebScript} with the {@link UriIndex}.
	 * 
	 * @param webScript
	 */
	public void registerWebScript(final WebScript webScript) {
		lock.lock();
		try {
			if (uriIndex != null) {
				doRegisterWebScript(webScript);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Delaying registration of Web Script '{}' until UriIndex is available.", webScript
							.getDescription().getId());
				}
			}
			webScripts.add(webScript);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Unregisters a WebScript.
	 * <p>
	 * As the {@link UriIndex} has no unregistration methods, the actual URI binding will only be cleared when the
	 * {@link Registry} is reset.
	 * 
	 * @param webScript
	 */
	public void unregisterWebScript(final WebScript webScript) {
		lock.lock();
		try {
			doUnregisterWebScript(webScript);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Clears the {@link UriIndex} and registers pending {@link WebScript}s.
	 */
	@Override
	public void clear() {
		Assert.state(uriIndex != null);
		lock.lock();
		try {
			uriIndex.clear();
			webScriptProxiesByUriMethod.clear();
			registerPendingWebScripts();
		} finally {
			lock.unlock();
		}
	}

	/* Callback operations */

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.state(StringUtils.hasText(uriIndexBeanName), "Name of UriIndex bean not configured.");
	}

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		if (this.uriIndexBeanName.equals(beanName) && bean instanceof UriIndex) {
			setUriIndex((UriIndex) bean);
			return this;
		} else {
			return bean;
		}
	}

	public List<WebScript> getWebScripts() {
		return webScripts;
	}

	/* Remaining delegated operations */

	@Override
	public int getSize() {
		return uriIndex.getSize();
	}

	@Override
	public void registerUri(final WebScript script, final String uri) {
		uriIndex.registerUri(script, uri);
	}

	@Override
	public Match findWebScript(final String method, final String uri) {
		return uriIndex.findWebScript(method, uri);
	}

	/* Utility operations */

	private void doRegisterWebScript(final WebScript webScript) {
		Assert.state(uriIndex != null);
		for (final String uri : webScript.getDescription().getURIs()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Registering Web Script '{}' for URI '{}'", webScript.getDescription().getId(), uri);
			}
			final WebScriptProxy webScriptProxy;
			final UriMethod key = UriMethod.forUriAndMethod(uri, webScript.getDescription().getMethod());
			if (webScriptProxiesByUriMethod.containsKey(key) == false) {
				webScriptProxy = new WebScriptProxy(webScript);
				webScriptProxiesByUriMethod.put(key, webScriptProxy);
			} else {
				webScriptProxy = webScriptProxiesByUriMethod.get(key);
				webScriptProxy.setWebScript(webScript);
			}
			uriIndex.registerUri(webScriptProxy, uri);
		}
	}

	private void doUnregisterWebScript(final WebScript webScript) {
		webScripts.remove(webScript);
		for (final String uri : webScript.getDescription().getURIs()) {
			final UriMethod key = UriMethod.forUriAndMethod(uri, webScript.getDescription().getMethod());
			if (webScriptProxiesByUriMethod.containsKey(key)) {
				final WebScriptProxy webScriptProxy = webScriptProxiesByUriMethod.get(key);
				webScriptProxy.setWebScript(unavailableWebScript);
			}
		}
	}

	private void registerPendingWebScripts() {
		for (final WebScript webScript : webScripts) {
			doRegisterWebScript(webScript);
		}
	}

	/* Dependencies */

	public void setUriIndex(final UriIndex uriIndex) {
		Assert.notNull(uriIndex);
		this.uriIndex = uriIndex;
	}

	public void setUnavailableWebScript(final WebScript unavailableWebScript) {
		Assert.notNull(unavailableWebScript);
		this.unavailableWebScript = unavailableWebScript;
	}

	/* Configuration */

	public void setUriIndexBeanName(final String uriIndexBeanName) {
		Assert.hasText(uriIndexBeanName);
		this.uriIndexBeanName = uriIndexBeanName;
	}

}
