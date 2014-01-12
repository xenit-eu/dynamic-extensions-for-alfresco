package com.github.dynamicextensionsalfresco.behaviours.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a class that acts as a Behaviour.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Behaviour {

	/**
	 * The QNames of the types or aspects to apply the Behaviour to. Can be specified in prefix format (
	 * <code>cm:content</code>) or fully-qualified format (
	 * <code>{http://www.alfresco.org/model/content/1.0}content</code>).
	 * <p>
	 * The class names can be overridden for each Policy method by {@link ClassPolicy}, {@link AssociationPolicy} and
	 * {@link PropertyPolicy}.
	 * 
	 * @return
	 */
	String[] value() default {};

	/**
	 * Indicates when to trigger the Behaviour. This can be overridden for each Policy method.
	 * 
	 * @return
	 */
	Event event() default Event.ALL;

}
