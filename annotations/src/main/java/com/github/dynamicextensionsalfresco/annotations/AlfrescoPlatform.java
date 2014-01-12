package com.github.dynamicextensionsalfresco.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a bean should be instantiated only for specific Alfresco platforms.
 * <p>
 * This annotation is mainly intended for extensions that must support backwards-compatibility with earlier Alfresco
 * versions. Effectively it allows you to avoid {@link ClassNotFoundException}s for types that do not exist in earlier
 * versions of the Alfresco API.
 * <p>
 * This example illustrates a class that uses an Alfresco 4-specific service:
 * 
 * <pre>
 * &#064;ManagedBean
 * &#064;AlfrescoPlatform(minVersion = "4.0")
 * public Alfresco4SpecificClass {
 * 
 *   &#064;Inject
 *   private BulkFilesystemImporter importer;
 *  
 * }
 * </pre>
 * <p>
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AlfrescoPlatform {

	/**
	 * The minimum version number in "&lt;major&gt;.&lt;minor&gt;.&lt;micro&gt;" format. (The micro version part is
	 * optional.)
	 * 
	 */
	String minVersion() default "";

	/**
	 * The maximum version number in "&lt;major&gt;.&lt;minor&gt;.&lt;micro&gt;" format. (The micro version part is
	 * optional.)
	 * 
	 */
	String maxVersion() default "";

}
