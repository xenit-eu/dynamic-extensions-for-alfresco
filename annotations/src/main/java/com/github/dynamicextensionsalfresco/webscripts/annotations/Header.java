package com.github.dynamicextensionsalfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps an HTTP header in annotation-based Web Scripts to a handler method argument.
 * <p>
 * This annotation currently only works with String arguments.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Header {

	/**
	 * The header name.
	 */
	String value();

}
