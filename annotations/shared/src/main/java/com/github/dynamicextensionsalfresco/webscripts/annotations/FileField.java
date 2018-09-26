package com.github.dynamicextensionsalfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps a <code>FormField</code> parameter of a Web Script handler method. These fields represent uploaded files in
 * multipart.
 * <p>
 * This annotation must be combined with {@link Uri#multipartProcessing()} set to <code>true</code>, otherwise the Web
 * Script implementation will fail with a NullPointerException.
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface FileField {

	/**
	 * The form field name.
	 */
	String value() default "";

}
