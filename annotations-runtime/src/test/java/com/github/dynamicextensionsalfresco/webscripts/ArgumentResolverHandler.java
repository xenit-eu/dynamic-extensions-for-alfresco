package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WebScriptSession;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
@Spied
public class ArgumentResolverHandler {

	@Uri("/handleWebScriptRequest")
	public void handleWebScriptRequest(final WebScriptRequest request) {
	}

	@Uri("/handleWebScriptResponse")
	public void handleWebScriptResponse(final WebScriptResponse response) {
	}

	@Uri("/handleWebScriptSession")
	public void handleWebScriptSession(final WebScriptSession session) {
	}

	@Uri("/handleMap")
	public void handleMap(final Map<String, Object> model) {
	}

	@Uri("/handleContent")
	public void handleContent(final Content content) {
	}

	@Uri("/handleHttpServletRequest")
	public void handleHttpServletRequest(final HttpServletRequest request) {
	}

	@Uri("/handleHttpServletResponse")
	public void handleHttpServletResponse(final HttpServletResponse response) {
	}
}
