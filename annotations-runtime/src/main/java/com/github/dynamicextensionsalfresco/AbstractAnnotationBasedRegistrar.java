package com.github.dynamicextensionsalfresco;

import java.lang.annotation.Annotation;

import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for registration services that use a {@link ConfigurableListableBeanFactory}. This class defines
 * utility methods for parsing {@link QName}s.
 * 
 * @author Laurens Fridael
 * 
 */
public abstract class AbstractAnnotationBasedRegistrar implements BeanFactoryAware {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private ConfigurableListableBeanFactory beanFactory;

	private NamespacePrefixResolver namespacePrefixResolver;

	/* Utility operations */

	protected QName[] parseQNames(final String[] values, final Annotation annotation) {
		final QName[] qNames = new QName[values.length];
		for (int i = 0; i < values.length; i++) {
			qNames[i] = parseQName(values[i], annotation);
		}
		return qNames;
	}

	protected QName parseQName(final String value, final Annotation annotation) {
		QName qName = null;
		try {
			final QNamePropertyEditor editor = new QNamePropertyEditor(getNamespacePrefixResolver());
			editor.setAsText(value);
			qName = (QName) editor.getValue();
		} catch (final RuntimeException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Invalid QName specified on annotation {}: {}", annotation, e.getMessage());
			}

		}
		return qName;
	}

	protected static String nullForEmptyString(final String value) {
		if (StringUtils.hasText(value)) {
			return value;
		} else {
			return null;
		}
	}

	/* Dependencies */

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) {
		Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory,
				"BeanFactory is not of type ConfigurableListableBeanFactory.");
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	protected ConfigurableListableBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setNamespacePrefixResolver(final NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	public NamespacePrefixResolver getNamespacePrefixResolver() {
		return namespacePrefixResolver;
	}

}
