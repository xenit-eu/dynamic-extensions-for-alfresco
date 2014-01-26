package com.github.dynamicextensionsalfresco.actions;

import org.alfresco.repo.action.ActionDefinitionImpl;
import org.alfresco.service.namespace.QName;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contains utility methods for dealing with API compatibility issues across different versions of the Alfresco API.
 * 
 * @author Laurens Fridael
 * 
 */
class ApiCompatibilityUtil {

	/**
	 * Provides a workaround breaking API change in {@link ActionDefinitionImpl#setApplicableTypes(List)}. The
	 * {@link List} argument was changed into a {@link Set} in version 4.2 of the Alfresco Community API.
	 * <p>
	 * See: <a href="https://github.com/laurentvdl/dynamic-extensions-for-alfresco/issues/10">Issue on Github</a>.
	 * 
	 * @param actionDefinition
	 * @throws IllegalStateException
	 *             If the 'setApplicableTypes' method could not be invoked.
	 */
	public static void setApplicableTypes(final ActionDefinitionImpl actionDefinition, final List<QName> applicableTypes) {
		Assert.notNull(actionDefinition, "ActionDefinition cannot be null.");
		Assert.notNull(applicableTypes, "Applicable types cannot ben null.");

		final Class<? extends ActionDefinitionImpl> clazz = actionDefinition.getClass();
		// Alfresco 4.0 API, with List argument.
		try {
			final Method method = clazz.getMethod("setApplicableTypes", List.class);
			ReflectionUtils.invokeMethod(method, actionDefinition, applicableTypes);
			return;
		} catch (final NoSuchMethodException e) {
			// Not handled
		} catch (final SecurityException e) {
			throw new RuntimeException(e);
		}
		// Alfresco 4.2 API, with Set argument.
		try {
			final Method method = clazz.getMethod("setApplicableTypes", Set.class);
			ReflectionUtils.invokeMethod(method, actionDefinition, new HashSet<QName>(applicableTypes));
			return;
		} catch (final NoSuchMethodException e) {
			// Not handled
		} catch (final SecurityException e) {
			throw new RuntimeException(e);
		}
		throw new IllegalStateException("Could not invoke method 'setApplicableTypes' on '" + clazz + "' instance.");
	}

	private ApiCompatibilityUtil() {
	}

}
