package nl.runnable.alfresco.webscripts;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.HttpMethod;
import nl.runnable.alfresco.webscripts.annotations.RequestParam;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.alfresco.service.namespace.QName;

@ManagedBean
@WebScript
public class RequestParamHandler {

	@Uri(method = HttpMethod.GET, value = "/handleNaming")
	protected void handleNaming(@RequestParam("explicitlyNamed") final String param,
			@RequestParam final String implicitlyNamed) {
	}

	@Uri(method = HttpMethod.GET, value = "/handleDefaults")
	protected void handleDefaultValues(@RequestParam(required = false, defaultValue = "default") final String param) {
	}

	@Uri(method = HttpMethod.GET, value = "/handleArrays")
	protected void handleArray(@RequestParam final String[] params) {
	}

	@Uri(method = HttpMethod.GET, value = "/handleDelimited")
	protected void handleDelimitedValues(@RequestParam(delimiter = ",") final String[] params) {
	}

	@Uri(method = HttpMethod.GET, value = "/handleStrings")
	protected void handleString(@RequestParam final String param1, @RequestParam(required = false) final String param2) {
	}

	@Uri(method = HttpMethod.GET, value = "/handleInt")
	protected void handleInt(@RequestParam final int param1, @RequestParam final Integer param2) {
	}

	@Uri(method = HttpMethod.GET, value = "/handleLong")
	protected void handleLong(@RequestParam final long param1, @RequestParam final Long param2) {
	}

	@Uri(method = HttpMethod.GET, value = "/handleBoolean")
	protected void handleBoolean(@RequestParam final boolean param1,
			@RequestParam(required = false) final Boolean param2) {
	}

	@Uri(method = HttpMethod.GET, value = "/handleQName")
	protected void handleQName(@RequestParam final QName param1) {
	}
}
