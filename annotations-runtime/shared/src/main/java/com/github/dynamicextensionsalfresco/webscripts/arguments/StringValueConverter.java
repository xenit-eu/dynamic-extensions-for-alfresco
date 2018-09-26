package com.github.dynamicextensionsalfresco.webscripts.arguments;

import java.util.HashSet;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;

/**
 * Used by {@link RequestParamArgumentResolver} and {@link UriVariableArgumentResolver} to convert String parameter
 * values.
 * 
 * @author Laurens Fridael
 * 
 */
public class StringValueConverter {

	private static final Set<Class<?>> SUPPORTED_TYPES = new HashSet<Class<?>>();
	static {
		SUPPORTED_TYPES.add(String.class);
		SUPPORTED_TYPES.add(String[].class);
		SUPPORTED_TYPES.add(Integer.TYPE);
		SUPPORTED_TYPES.add(int[].class);
		SUPPORTED_TYPES.add(Integer.class);
		SUPPORTED_TYPES.add(Integer[].class);
		SUPPORTED_TYPES.add(Long.TYPE);
		SUPPORTED_TYPES.add(long[].class);
		SUPPORTED_TYPES.add(Long.class);
		SUPPORTED_TYPES.add(Long[].class);
		SUPPORTED_TYPES.add(Boolean.TYPE);
		SUPPORTED_TYPES.add(boolean[].class);
		SUPPORTED_TYPES.add(Boolean.class);
		SUPPORTED_TYPES.add(Boolean[].class);
		SUPPORTED_TYPES.add(QName.class);
		SUPPORTED_TYPES.add(NodeRef.class);
	}

	/* Dependencies */

	private NamespacePrefixResolver namespacePrefixResolver;

	/* Main operations */

	public boolean isSupportedType(final Class<?> type) {
		return SUPPORTED_TYPES.contains(type);
	}

	@SuppressWarnings("unchecked")
	public <T> T convertStringValue(final Class<T> type, final String stringValue) {
		Object value;
		if (String.class.equals(type)) {
			value = stringValue;
		} else if (Integer.TYPE.equals(type)) {
			value = Integer.parseInt(stringValue);
		} else if (Integer.class.equals(type)) {
			value = Integer.valueOf(stringValue);
		} else if (Long.TYPE.equals(type)) {
			value = Long.parseLong(stringValue);
		} else if (Long.class.equals(type)) {
			value = Long.valueOf(stringValue);
		} else if (Boolean.TYPE.equals(type)) {
			value = Boolean.parseBoolean(stringValue);
		} else if (Boolean.class.equals(type)) {
			value = Boolean.valueOf(stringValue);
		} else if (QName.class.equals(type)) {
			if (stringValue.matches("\\{.+?\\}.+?")) {
				value = QName.createQName(stringValue);
			} else if (stringValue.matches("\\w+?:\\w+?")) {
				value = QName.createQName(stringValue, getNamespacePrefixResolver());
			} else {
				throw new IllegalArgumentException("Invalid QName format: " + stringValue);
			}
		} else if (NodeRef.class.equals(type)) {
			value = new NodeRef(stringValue);
		} else {
			throw new IllegalArgumentException(String.format("Unhandled parameter type %s", type));
		}
		return (T) value;
	}

	/* Dependencies */

	public void setNamespacePrefixResolver(final NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	protected NamespacePrefixResolver getNamespacePrefixResolver() {
		return namespacePrefixResolver;
	}

}
