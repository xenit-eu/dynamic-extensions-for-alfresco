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
	 */
	String value() default "";

	/**
	 * The Web Script description.
	 * 
	 * @return
	 */
	String description() default "";

	/**
	 * Web Script families
	 * 
	 * @return
	 */
	String[] families() default {};

	/**
	 * API lifecycle status
	 */
	Lifecycle lifecycle() default Lifecycle.NONE;

	String baseUri() default "";

	/**
	 * Indicates the default format to use for {@link Uri} handlers. A Web Script typically uses the same default
	 * format.
	 * 
	 * @return
	 * @see Uri#defaultFormat()
	 */
	String defaultFormat() default "";
}
