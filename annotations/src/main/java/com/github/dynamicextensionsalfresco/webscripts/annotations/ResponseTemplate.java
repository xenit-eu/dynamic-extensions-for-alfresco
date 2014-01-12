package com.github.dynamicextensionsalfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Indicates that {@link Uri}-annotated handler methods use a response template. This annotation provides an alternative
 * to having the handler method return a {@link Map}. It also allows for specifying a custom template path.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseTemplate {

	/**
	 * The path to the template. If not specified, the implementation will generate a template path based on Web Script
	 * conventions.
	 * 
	 * @return
	 */
	String value() default "";

}
