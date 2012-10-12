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

package nl.runnable.alfresco.osgi.container.webscripts;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Front controller for handling POST requests containing JSON commands and dispatching them to the appropriate
 * {@link JsonBundleCommand}.
 * 
 * @author Laurens Fridael
 * 
 */
public class JsonBundleCommandWebScript extends AbstractBundleWebScript {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Configuration */

	private Map<String, JsonBundleCommand> commandsByName;

	/* Request handling */

	@Override
	public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		if ("application/json".equalsIgnoreCase(request.getContent().getMimetype())) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Executing request:\n{}", request.getContent().getContent());
				}
				final JSONObject requestObject = new JSONObject(request.getContent().getContent());
				final String command = requestObject.optString("command");
				if (StringUtils.hasText(command)) {
					if (commandsByName.containsKey(command)) {
						final JSONObject responseObject = commandsByName.get(command).execute(
								getBundleWebScriptService(), requestObject);
						response.setStatus(HttpServletResponse.SC_OK);
						if (responseObject != null) {
							response.setContentType("application/json");
							response.getWriter().write(responseObject.toString());
						}
					} else {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						writeResponseMessage(response, String.format("Unknown command: %s", command));
					}
				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					writeResponseMessage(response, "Command not specified.");
				}
			} catch (final JSONException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writeResponseMessage(response, String.format("Error parsing request: %s", e.getMessage()));
			} catch (final BundleException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writeResponseMessage(response, String.format("Error handling command: %s", e.getMessage()));
				logger.warn("Error handling command.", e);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			writeResponseMessage(response, String.format(
					"Request content of type %s is not supported. This service only supports content of type %s.",
					request.getContent().getMimetype(), "application/json"));
			return;
		}
	}

	/* Configuration */

	public void setCommandsByName(final Map<String, JsonBundleCommand> commandsByName) {
		Assert.notNull(commandsByName);
		this.commandsByName = commandsByName;
	}

}
