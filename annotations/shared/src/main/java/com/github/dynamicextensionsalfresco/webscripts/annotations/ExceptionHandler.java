package com.github.dynamicextensionsalfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a method for handling exceptions that occur during the invocation of {@link Before}, {@link Attribute} or
 * {@link Uri} handler methods.
 * <p>
 * Handlers can be limited to specific exceptions. The implementation invokes every matching handler, in an order that
 * is undefined.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandler {

	/**
	 * Specifies the type of exceptions that the method should handle. If no types are specified the method will handle
	 * every type of exception.
	 * 
	 * @return
	 */
	Class<? extends Throwable>[] value() default {};

}
