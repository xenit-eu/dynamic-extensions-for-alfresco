package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.RequestParam;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.springframework.stereotype.Component;

@Component
@Spied
public class RequestParamHandler {

	@Uri("/handleNaming")
	public void handleNaming(@RequestParam("explicitlyNamed") final String param,
			@RequestParam final String implicitlyNamed) {
	}

	@Uri("/handleDefaultValues")
	public void handleDefaultValues(@RequestParam(required = false, defaultValue = "default") final String param) {
	}

	@Uri("/handleArray")
	public void handleArray(@RequestParam final String[] params) {
	}

	@Uri("/handleDelimitedValues")
	public void handleDelimitedValues(@RequestParam(delimiter = ",") final String[] params) {
	}

	@Uri("/handleString")
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
