package com.github.dynamicextensionsalfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UriVariable {
	/**
	 * The URI variable name. If not specified, the parameter's Java variable name will be used instead. (The variable
	 * name is only available if classes are compiled with debug info.)
	 */
	String value() default "";

	boolean required() default true;
}
