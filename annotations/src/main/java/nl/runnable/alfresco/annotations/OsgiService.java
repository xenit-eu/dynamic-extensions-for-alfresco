package nl.runnable.alfresco.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that instances of this type should be exported as an OSGi service.
 * <p>
 * In an OSGi container, these
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OsgiService {

	Class<?>[] interfaces() default {};
}
