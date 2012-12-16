package nl.runnable.alfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as providing reference data and a parameter as using this reference data.
 * 
 * Similar to Spring MVC's <code>@ModelAttribute</code> when used with methods.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferenceData {
	String value() default "";

	/**
	 * Only has effect when the annotation is used with a method parameter.
	 * 
	 * @return
	 */
	boolean required() default true;
}
