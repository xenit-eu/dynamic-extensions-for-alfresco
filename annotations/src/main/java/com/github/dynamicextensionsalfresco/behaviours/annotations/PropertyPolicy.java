package com.github.dynamicextensionsalfresco.behaviours.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a method is a Class-level Policy.
 * <p>
 * This annotation should only be applied to {@link org.alfresco.repo.policy.ClassPolicy} interface methods.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyPolicy {

	/**
	 * The QNames of the types or aspects to apply the Policy to. Can be specified in prefix format (
	 * <code>cm:content</code>) or fully-qualified format (
	 * <code>{http://www.alfresco.org/model/content/1.0}content</code>).
	 * <p>
	 * If no value is specified, the Policy is applied to all types and aspects.
	 * 
	 * @return The QNames of the types or aspects that apply to the Policy
	 */
	String[] value() default {};

	/**
	 * The QName of the property to apply the Policy to. Can be specified in prefix format ( or fully-qualified format.
	 * <p>
	 * If no value is specified, the Policy is applied to all properties. If a property has been specified, the class
	 * name must also be specified. It is invalid to specify only a property.
	 * 
	 * @return The QNames of the property that apply to the Policy
	 */
	String property() default "";

	/**
	 * Indicates when to trigger the Behaviour.
	 * 
	 * @return The event that triggers the behaviour.
	 */
	Event event() default Event.INHERITED_OR_ALL;

}
