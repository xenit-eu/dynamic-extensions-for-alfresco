package com.github.dynamicextensionsalfresco.osgi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that components of this type should be exported as an OSGi service.
 *
 * @author Laurent Van der Linden
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OsgiService {

	Class<?>[] interfaces() default {};

    ExportHeader[] headers() default {};

    static @interface ExportHeader {
        String key();
        String value();
    }
}
