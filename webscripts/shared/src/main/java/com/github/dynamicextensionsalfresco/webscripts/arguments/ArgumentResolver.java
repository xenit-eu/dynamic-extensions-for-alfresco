package com.github.dynamicextensionsalfresco.webscripts.arguments;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.lang.annotation.Annotation;

/**
 * Strategy for resolving method arguments for handler methods managed by an annotated WebScript.
 * 
 * @author Laurens Fridael
 * 
 * @param <ArgumentType>
 * @param <AnnotationType>
 */
public interface ArgumentResolver<ArgumentType, AnnotationType extends Annotation> {

	/**
	 * Indicates whether this implementation support parameters of the given type in combination with an optional
	 * {@link Annotation} type.
	 * 
	 * @param argumentType
	 * @return True if this implementation support the given type, false if not.
	 */
	boolean supports(Class<?> argumentType, Class<? extends Annotation> annotationType);

	/**
	 * Resolves the argument value for the given type and optional annotation, using the {@link WebScriptRequest} and
	 * {@link WebScriptResponse} to obtain the relevant information.
	 * 
	 * @param argumentType
	 *            The parameter type.
	 * @param parameterAnnotation
	 *            The parameter annotation. Will be null if no annotation is present. Implementations may choose to
	 *            disregard this.
	 * @param name
	 *            The argument name. May be null if the calling code cannot determine the argument name.
	 * @param request
	 * @param response
	 * @return The parameter value, may be null if the result of the evaluation is indeed null.
	 */
	ArgumentType resolveArgument(Class<?> argumentType, AnnotationType parameterAnnotation, String name,
			WebScriptRequest request, WebScriptResponse response);

}
