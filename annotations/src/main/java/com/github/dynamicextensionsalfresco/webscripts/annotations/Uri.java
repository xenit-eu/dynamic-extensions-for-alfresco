package com.github.dynamicextensionsalfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Uri {

	String[] value() default {};

	HttpMethod method() default HttpMethod.GET;

	FormatStyle formatStyle() default FormatStyle.ANY;

	String defaultFormat() default "";

	boolean multipartProcessing() default false;

}
