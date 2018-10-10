package com.github.dynamicextensionsalfresco.proxy;

import org.apache.felix.framework.FilterImpl;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Model for the service filter. Will create a default 'objectClass' based filter if no custom {@link Filter} is provided.
 *
 * @author Laurent Van der Linden
 */
public class DefaultFilterModel implements FilterModel {
	private final Class<?>[] targetInterfaces;

	private Filter filter;

	public DefaultFilterModel(Class<?>[] targetInterfaces, Filter filter) {
		this.targetInterfaces = targetInterfaces;
		this.filter = filter;
	}

	@Override
	public Filter getServiceFilter() {
		if (filter == null) {
			final StringBuilder builder = new StringBuilder("(&");
			for (Class<?> targetInterface : targetInterfaces) {
				builder.append("(")
					.append(Constants.OBJECTCLASS).append("=").append(targetInterface.getName())
					.append(")");
			}
			builder.append(")");
			try {
				filter = new FilterImpl(builder.toString());
			} catch (InvalidSyntaxException e) {
				throw new RuntimeException(e);
			}
		}
		return filter;
	}

	@Override
	public void setServiceFilter(Filter serviceFilter) {
		this.filter = serviceFilter;
	}

	@Override
	public void setServiceFilterString(String ldapFilter) {
		try {
			this.filter = new FilterImpl(ldapFilter);
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
