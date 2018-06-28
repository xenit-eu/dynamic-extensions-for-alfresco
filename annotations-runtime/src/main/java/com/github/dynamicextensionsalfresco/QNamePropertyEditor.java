package com.github.dynamicextensionsalfresco;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.springframework.util.StringUtils;

/**
 * JavaBeans {@link PropertyEditor} for configuring {@link QName}s using either the prefix or fully-qualified format.
 * 
 * @author Laurens Fridael
 * 
 */
class QNamePropertyEditor extends PropertyEditorSupport {

	private final String PREFIX_FORMAT_PATTERN = (".+?:.+?");

	private final String FULLY_QUALIFIED_FORMAT_PATTERN = "\\{.+?\\}.+?";

	/* Dependencies */

	private NamespacePrefixResolver namespacePrefixResolver;

	/* Construction */

	public QNamePropertyEditor(final NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	public QNamePropertyEditor() {
	}

	/* Operations */

	@Override
	public void setAsText(final String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text) == false) {
			setValue(null);
		} else if (text.matches(PREFIX_FORMAT_PATTERN)) {
			setValue(QName.createQName(text, getNamespacePrefixResolver()));
		} else if (text.matches(FULLY_QUALIFIED_FORMAT_PATTERN)) {
			setValue(QName.createQName(text));
		} else {
			throw new IllegalArgumentException("Invalid value: " + text);
		}
	}

	/* Dependencies */

	public void setNamespacePrefixResolver(final NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	protected NamespacePrefixResolver getNamespacePrefixResolver() {
		return namespacePrefixResolver;
	}
}
