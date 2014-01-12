package com.github.dynamicextensionsalfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as providing an attribute and parameters for {@link Uri}-annotated methods as using this attribute.
 * 
 * Similar to Spring MVC's <code>@ModelAttribute</code>.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {
	/**
	 * The attribute name. If not supplied, the name is inferred from the method name or the parameter name.
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * Indicates if the attribute is required. This only has effect when applied to a method parameter.
	 * 
	 * @return
	 */
	boolean required() default true;
}
