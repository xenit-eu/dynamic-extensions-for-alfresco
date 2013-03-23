package nl.runnable.alfresco.webscripts;

import java.util.Map;

import nl.runnable.alfresco.webscripts.annotations.Attribute;

import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Description.FormatStyle;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.util.Assert;

/**
 * {@link WebScriptRequest} wrapper for supporting {@link Attribute} annotation infrastructure.
 * <p>
 * This class adds a {@link #getAttributes()} method for obtaining the attributes for the current request.
 * 
 * @author Laurens Fridael
 * 
 */
class AttributesWebScriptRequest implements WebScriptRequest {

	/* State */

	private final WebScriptRequest webScriptRequest;

	private final Map<String, Object> attributes;

	AttributesWebScriptRequest(final WebScriptRequest webScriptRequest, final Map<String, Object> attributes) {
		Assert.notNull(webScriptRequest);
		Assert.notNull(attributes);
		this.webScriptRequest = webScriptRequest;
		this.attributes = attributes;
	}

	/* State */

	Map<String, Object> getAttributes() {
		return attributes;
	}

	/* Delegate methods */

	@Override
	public Match getServiceMatch() {
		return webScriptRequest.getServiceMatch();
	}

	@Override
	public String getServerPath() {
		return webScriptRequest.getServerPath();
	}

	@Override
	public String getContextPath() {
		return webScriptRequest.getContextPath();
	}

	@Override
	public String getServiceContextPath() {
		return webScriptRequest.getServiceContextPath();
	}

	@Override
	public String getServicePath() {
		return webScriptRequest.getServicePath();
	}

	@Override
	public String getURL() {
		return webScriptRequest.getURL();
	}

	@Override
	public String getPathInfo() {
		return webScriptRequest.getPathInfo();
	}

	@Override
	public String getQueryString() {
		return webScriptRequest.getQueryString();
	}

	@Override
	public String[] getParameterNames() {
		return webScriptRequest.getParameterNames();
	}

	@Override
	public String getParameter(final String name) {
		return webScriptRequest.getParameter(name);
	}

	@Override
	public String[] getParameterValues(final String name) {
		return webScriptRequest.getParameterValues(name);
	}

	@Override
	public String[] getHeaderNames() {
		return webScriptRequest.getHeaderNames();
	}

	@Override
	public String getHeader(final String name) {
		return webScriptRequest.getHeader(name);
	}

	@Override
	public String[] getHeaderValues(final String name) {
		return webScriptRequest.getHeaderValues(name);
	}

	@Override
	public String getExtensionPath() {
		return webScriptRequest.getExtensionPath();
	}

	@Override
	public String getContentType() {
		return webScriptRequest.getContentType();
	}

	@Override
	public Content getContent() {
		return webScriptRequest.getContent();
	}

	@Override
	public Object parseContent() {
		return webScriptRequest.parseContent();
	}

	@Override
	public boolean isGuest() {
		return webScriptRequest.isGuest();
	}

	@Override
	public String getFormat() {
		return webScriptRequest.getFormat();
	}

	@Override
	public FormatStyle getFormatStyle() {
		return webScriptRequest.getFormatStyle();
	}

	@Override
	public String getAgent() {
		return webScriptRequest.getAgent();
	}

	@Override
	public String getJSONCallback() {
		return webScriptRequest.getJSONCallback();
	}

	@Override
	public boolean forceSuccessStatus() {
		return webScriptRequest.forceSuccessStatus();
	}

	@Override
	public Runtime getRuntime() {
		return webScriptRequest.getRuntime();
	}
}
