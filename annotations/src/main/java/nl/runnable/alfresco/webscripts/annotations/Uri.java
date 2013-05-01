package nl.runnable.alfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Uri {

	String[] value();

	HttpMethod method() default HttpMethod.GET;

	FormatStyle formatStyle() default FormatStyle.ANY;

	String defaultFormat() default "";

	boolean multipartProcessing() default false;

}
