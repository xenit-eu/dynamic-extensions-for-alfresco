package com.github.dynamicextensionsalfresco.webscripts;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;

/**
 * 
 * Model representing 'url' object for use. The code is taken from the Spring Web Scripts 1.0.0 release. Later versions
 * of the Web Scripts API change the URLModel class into an interface.
 * <p>
 * Since Dynamic Extensions strives to support older Alfresco releases, we simply use take ownership of this problem
 * using this class.
 * <p>
 * {@link com.github.dynamicextensionsalfresco.webscripts.resolutions.TemplateResolution} uses this class for the 'url' helper in the template model.
 * <p>
 * This class has public visibility in order to satisfy JavaBeans conventions. (Freemarker requires models to follow
 * JavaBeans conventions.)
 * 
 * @author Laurens Fridael
 */
public class UrlModel {

	private final WebScriptRequest request;

	public UrlModel(final WebScriptRequest request) {
		Assert.notNull(request);
		this.request = request;
	}

	public String getServer() {
		return request.getServerPath();
	}

	public String getContext() {
		return request.getContextPath();
	}

	public String getServiceContext() {
		return request.getServiceContextPath();
	}

	public String getService() {
		return request.getServicePath();
	}

	public String getFull() {
		return request.getURL();
	}

	public String getArgs() {
		final String args = request.getQueryString();
		return (args == null) ? "" : args;
	}

	public String getMatch() {
		return request.getServiceMatch().getPath();
	}

	public String getExtension() {
		return request.getExtensionPath();
	}

	public String getTemplate() {
		return request.getServiceMatch().getTemplate();
	}

	public Map<String, String> getTemplateArgs() {
		final Map<String, String> args = request.getServiceMatch().getTemplateVars();
		return (args == null) ? Collections.<String, String> emptyMap() : args;
	}

}
