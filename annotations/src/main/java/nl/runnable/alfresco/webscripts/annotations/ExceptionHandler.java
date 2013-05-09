package nl.runnable.alfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a method for handling exceptions that occur during the invocation of {@link Before}, {@link Attribute} or
 * {@link Uri} handler methods. It is an error to combine an {@link ExceptionHandler} with any of the handler method
 * annotations.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandler {

	Class<? extends Throwable>[] value() default {};

}
