package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.UriVariable;

import org.springframework.stereotype.Component;

@Component
@Spied
public class UriVariableHandler {

	String variable;

	@Uri("/handleUriVariable/{variable}")
	public void handleUriVariable(@UriVariable final String variable) {
		this.variable = variable;

	}

}
