package com.github.dynamicextensionsalfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a class acts as a Web Script.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface WebScript {
	/**
	 * The short name. If not provided, the framework will use the handler's fully-qualified class name.
	 * @return the short name
	 */
	String value() default "";

	/**
	 * The Web Script description.
	 * 
	 * @return the description
	 */
	String description() default "";

	/**
	 * Web Script families
	 * 
	 * @return the family
	 */
	String[] families() default {};

	/**
	 * API lifecycle status
	 *
	 * @return the {@link Lifecycle} of the webscript
	 */
	Lifecycle lifecycle() default Lifecycle.NONE;

	String baseUri() default "";

	/**
	 * Indicates the default format to use for {@link Uri} handlers. A Web Script typically uses the same default
	 * format.
	 * 
	 * @return the default format
	 * @see Uri#defaultFormat()
	 */
	String defaultFormat() default "";
}
