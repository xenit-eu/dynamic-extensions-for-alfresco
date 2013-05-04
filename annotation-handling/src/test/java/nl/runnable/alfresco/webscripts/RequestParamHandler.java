package nl.runnable.alfresco.webscripts;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.RequestParam;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

@ManagedBean
@WebScript
public class RequestParamHandler {

	@Uri("/handleNaming")
	protected void handleNaming(@RequestParam("explicitlyNamed") final String param,
			@RequestParam final String implicitlyNamed) {
	}

	@Uri("/handleDefaults")
	protected void handleDefaultValues(@RequestParam(required = false, defaultValue = "default") final String param) {
	}

	@Uri("/handleArrays")
	protected void handleArray(@RequestParam final String[] params) {
	}

	@Uri("/handleDelimited")
	protected void handleDelimitedValues(@RequestParam(delimiter = ",") final String[] params) {
	}

	@Uri("/handleStrings")
	protected void handleString(@RequestParam final String param1, @RequestParam(required = false) final String param2) {
	}

	@Uri("/handleInt")
	protected void handleInt(@RequestParam final int param1, @RequestParam final Integer param2) {
	}

	@Uri("/handleLong")
	protected void handleLong(@RequestParam final long param1, @RequestParam final Long param2) {
	}

	@Uri("/handleBoolean")
	protected void handleBoolean(@RequestParam final boolean param1,
			@RequestParam(required = false) final Boolean param2) {
	}

	@Uri("/handleQName")
	protected void handleQName(@RequestParam final QName qname) {
	}

	@Uri("/handleNodeRef")
	public void handleNodeRef(@RequestParam final NodeRef nodeRef) {
	}
}
