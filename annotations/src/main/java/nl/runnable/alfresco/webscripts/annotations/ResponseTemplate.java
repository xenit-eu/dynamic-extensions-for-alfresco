package nl.runnable.alfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Indicates that {@link Uri}-annotated handler methods use a response template.
 * <p>
 * This annotation provides an alternative to having the handler method return a {@link Map}. Furthermore, it allows for
 * specifying a custom template path. By default, the implementation determines the template path using Web Script
 * conventions.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseTemplate {

	/**
	 * The base template path. The implementation interprets the template path as relative to the handler class.
	 * 
	 * @return
	 */
	String value() default "";

	String nameSuffix() default "";

}
