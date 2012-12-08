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

import nl.runnable.alfresco.webscripts.annotations.Command;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebRequestDataBinder;

public class CommandArgumentResolver implements ArgumentResolver<Object, Command> {

	@Override
	public boolean supports(final Class<?> parameterType, final Class<? extends Annotation> annotationType) {
		return Command.class.equals(annotationType);
	}

	@Override
	public Object resolveArgument(final Class<?> parameterType, final Command command, final String name,
			final WebScriptRequest request, final WebScriptResponse response) {
		try {
			final Object commandObject = parameterType.newInstance();
			final BindingResult result = performDataBinding(request, command, commandObject);
			if (result.hasErrors()) {
				throw new IllegalArgumentException("Errors binding @Command method parameter '" + name + "':"
						+ result.getAllErrors().toString());
			}
			return commandObject;
		} catch (final InstantiationException e) {
			throw new RuntimeException("Cannot create instance of class '" + parameterType.getName()
					+ "'. Method parameters annotated with @Command must have a no-argument constructor.");
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	protected BindingResult performDataBinding(final WebScriptRequest request, final Command command,
			final Object commandObject) {
		final WebRequestDataBinder dataBinder = new WebRequestDataBinder(commandObject);
		dataBinder.setIgnoreInvalidFields(command.ignoreInvalidFields());
		dataBinder.setIgnoreUnknownFields(command.ignoreUnknownFields());
		if (command.allowedFields().length > 0) {
			dataBinder.setAllowedFields(command.allowedFields());
		}
		dataBinder.bind(new WebScriptWebRequest(request));
		return dataBinder.getBindingResult();
	}
}
