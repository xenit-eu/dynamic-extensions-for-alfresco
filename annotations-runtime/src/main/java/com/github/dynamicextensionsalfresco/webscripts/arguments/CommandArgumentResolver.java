package com.github.dynamicextensionsalfresco.webscripts.arguments;

import java.lang.annotation.Annotation;

import com.github.dynamicextensionsalfresco.webscripts.WebScriptWebRequest;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Command;

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
