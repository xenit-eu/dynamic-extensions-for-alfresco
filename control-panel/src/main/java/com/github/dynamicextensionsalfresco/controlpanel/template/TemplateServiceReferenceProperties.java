package com.github.dynamicextensionsalfresco.controlpanel.template;

import org.osgi.framework.ServiceReference;
import org.springframework.util.Assert;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

class TemplateServiceReferenceProperties implements TemplateHashModel {

	@SuppressWarnings("rawtypes")
	private final ServiceReference serviceReference;

	@SuppressWarnings("rawtypes")
	TemplateServiceReferenceProperties(final ServiceReference serviceReference) {
		Assert.notNull(serviceReference);
		this.serviceReference = serviceReference;
	}

	@Override
	public boolean isEmpty() throws TemplateModelException {
		return (serviceReference.getPropertyKeys().length == 0);
	}

	@Override
	public TemplateModel get(final String key) {
		final Object property = serviceReference.getProperty(key);
		if (property != null) {
			return new SimpleScalar(property.toString());
		} else {
			return null;
		}
	}

}
