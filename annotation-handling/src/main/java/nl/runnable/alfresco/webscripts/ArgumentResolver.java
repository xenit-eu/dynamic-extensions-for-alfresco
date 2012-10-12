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

package nl.runnable.alfresco.webscripts;

import java.lang.annotation.Annotation;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Strategy for resolving method arguments for handler methods managed by an {@link AnnotationBasedWebScript}.
 * 
 * @author Laurens Fridael
 * 
 * @param <ArgumentType>
 * @param <AnnotationType>
 */
public interface ArgumentResolver<ArgumentType extends Object, AnnotationType extends Annotation> {

	/**
	 * Indicates whether this implementation support parameters of the given type in combination with an optional
	 * {@link Annotation} type.
	 * 
	 * @param parameterType
	 * @return True if this implementation support the given type, false if not.
	 */
	boolean supports(Class<?> parameterType, Class<? extends Annotation> annotationType);

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
	 *            The argument name. May be null if the calling cannot determine the argument name.
	 * @param request
	 * @param response
	 * @return The parameter value, may be null if the result of the evaluation is indeed null.
	 */
	ArgumentType resolveArgument(Class<?> argumentType, AnnotationType parameterAnnotation, String name,
			WebScriptRequest request, WebScriptResponse response);

}
