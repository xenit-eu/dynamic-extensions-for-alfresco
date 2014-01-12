package com.github.dynamicextensionsalfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking a a method parameter as a command object.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	boolean ignoreUnknownFields() default true;

	boolean ignoreInvalidFields() default false;

	String[] allowedFields() default {};

}
