package com.github.dynamicextensionsalfresco.actions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.alfresco.service.cmr.action.Action;

/**
 * Indicates a method that maps to an annotation-based {@link Action}.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionMethod {

	/**
	 * The action name.
	 * <p>
	 * As with {@link ActionParam}s, it is recommended to specify the name explicitly, preferably using a static String
	 * constant visible to code that invokes the action.
	 * 
	 * If the name is not specified, the implementation uses the short class name combined with the method name. For
	 * example: <code>MyClass.myAction</code>
	 * 
	 * @return
	 */
	String value() default "";

	String titleKey() default "";

	String descriptionKey() default "";

	String[] applicableTypes() default {};

	String queueName() default "";

	boolean adhocPropertiesAllowed() default false;

	String ruleActionExecutor() default "";

}
