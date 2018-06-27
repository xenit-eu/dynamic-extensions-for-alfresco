package com.github.dynamicextensionsalfresco.osgi;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * {@link PropertyEditor} for configuring {@link ServiceDefinition}s in Spring {@link ApplicationContext}s.
 * 
 * @author Laurens Fridael
 * 
 */
public class ServiceDefinitionEditor extends PropertyEditorSupport {

	private static final Pattern BEAN_NAME_AND_SERVICE_NAMES_PATTERN = Pattern
			.compile("(.*?):(.*?)(:(.*?))?(:(\\d+\\.\\d+))?");

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		Assert.hasText(text, "Text cannot be empty");
		text = text.trim();
		final Matcher matcher = BEAN_NAME_AND_SERVICE_NAMES_PATTERN.matcher(text);
		if (matcher.matches()) {
			final String[] beanNames = matcher.group(1).split(",");
			final String[] serviceNames = matcher.group(2).split(",");
			final String serviceType = matcher.group(4);
			final String platformVersion = matcher.group(6);
			setValue(new ServiceDefinition(beanNames, serviceNames, serviceType, platformVersion));
		}
	}

}
