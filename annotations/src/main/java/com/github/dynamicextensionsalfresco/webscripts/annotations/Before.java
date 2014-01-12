package com.github.dynamicextensionsalfresco.webscripts.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a 'Before' handler for Web Script requests.
 * <p>
 * Before handlers accept the same types of parameters as {@link Attribute} and {@link Uri} handlers. They can return a
 * boolean to indicate control flow in the overall request handling cycle.
 * 
 * <pre>
 * &#064;Before
 * public boolean checkIfNodeExists(@RequestParam NodeRef nodeRef, WebScriptResponse response) {
 * 	if (nodeService.exists(nodeRef)) {
 * 		return true; // Proceed.
 * 	} else {
 * 		response.setStatus(404);
 * 		return false; // End request handling.
 * 	}
 * }
 * </pre>
 * 
 * Like {@link Attribute} handlers, the order in which Before handlers are invoked is undefined. You should avoid
 * logical dependencies between Before handlers.
 * 
 * 
 * @author Laurens Fridael
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {

}
