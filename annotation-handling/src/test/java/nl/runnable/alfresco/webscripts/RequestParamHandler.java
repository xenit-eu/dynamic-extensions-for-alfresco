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

	@Uri("/handleNamedParameters")
	public void handleNaming(@RequestParam("explicitlyNamed") final String param,
			@RequestParam final String implicitlyNamed) {
	}

	@Uri("/handleDefaultValues")
	public void handleDefaultValues(@RequestParam(required = false, defaultValue = "default") final String param) {
	}

	@Uri("/handleArray")
	public void handleArray(@RequestParam final String[] params) {
	}

	@Uri("/handleDelimited")
	public void handleDelimitedValues(@RequestParam(delimiter = ",") final String[] params) {
	}

	@Uri("/handleStrings")
	public void handleString(@RequestParam final String param1, @RequestParam(required = false) final String param2) {
	}

	@Uri("/handleInt")
	public void handleInt(@RequestParam final int param1, @RequestParam final Integer param2) {
	}

	@Uri("/handleLong")
	public void handleLong(@RequestParam final long param1, @RequestParam final Long param2,
			@RequestParam(defaultValue = "3") final long param3) {
	}

	@Uri("/handleBoolean")
	public void handleBoolean(@RequestParam final boolean param1, @RequestParam(required = false) final Boolean param2) {
	}

	@Uri("/handleQName")
	public void handleQName(@RequestParam final QName qname) {
	}

	@Uri("/handleNodeRef")
	public void handleNodeRef(@RequestParam final NodeRef nodeRef) {
	}
}
