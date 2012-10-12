/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.annotations;

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
