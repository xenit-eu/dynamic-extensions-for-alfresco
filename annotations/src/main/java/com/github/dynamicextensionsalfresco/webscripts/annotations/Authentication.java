package com.github.dynamicextensionsalfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Authentication {
	/**
	 * Determines the {@link AuthenticationType}.
	 * @return the authentication type
	 */
	AuthenticationType value() default AuthenticationType.USER;

	/**
	 * Determines the user the Web Script is run as.
	 * 
	 * @return The user the webscript will run as.
	 */
	String runAs() default "";

}
