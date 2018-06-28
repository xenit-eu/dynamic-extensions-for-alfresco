package com.github.dynamicextensionsalfresco.actions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.alfresco.service.cmr.dictionary.DataTypeDefinition;

/**
 * Indicates an {@link ActionMethod} method parameter.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionParam {
	/**
	 * The parameter name.
	 * <p>
	 * As with {@link ActionMethod}s, it is recommended to specify the name explicitly, preferably using a static String
	 * constant visible to code that invokes the action.
	 * <p>
	 * If the parameter name is not specified, the parameter's Java variable name will be used instead. (The variable
	 * name is only available if classes are compiled with debug info.)
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * The qualified name of the parameter type. If not specified, the Java parameter type is used to determine a
	 * matching {@link DataTypeDefinition}. For ambiguous cases, you need to specify the type explicitly.
	 * 
	 * @return
	 */
	String type() default "";

	boolean mandatory() default true;

	String displayLabel() default "";

	String constraintName() default "";
}
