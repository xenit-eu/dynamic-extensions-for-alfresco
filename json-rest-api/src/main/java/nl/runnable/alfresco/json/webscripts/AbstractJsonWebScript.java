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

package nl.runnable.alfresco.json.webscripts;

import java.io.IOException;

import javax.annotation.PostConstruct;

import nl.runnable.alfresco.json.SerializationSettings;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

/**
 * Abstract base class for annotated Web Scripts that serialize repository entities to JSON format.
 * 
 * @author Laurens Fridael
 * 
 */
public class AbstractJsonWebScript {

	private static final String PRETTY_PARAMETER = "pretty";

	private static final String CHILD_DEPTH_PARAMETER = "childDepth";

	/* Dependencies */

	private JsonFactory jsonFactory;

	/* Utility Operations */

	protected void writeJsonMessage(final int statusCode, final String message, final WebScriptResponse response)
			throws IOException {
		Assert.notNull(response, "Response cannot be null.");
		response.setStatus(statusCode);
		final JsonGenerator generator = createJsonGenerator(response);
		generator.writeStartObject();
		generator.writeStringField("message", message);
		generator.writeEndObject();
		generator.close();
	}

	/**
	 * Creates a serialization format using a given {@link WebScriptRequest}. This implementation simply returns a new
	 * {@link SerializationSettings} instance. Later revisions should configure its settings according to request
	 * parameters.
	 * 
	 * @param request
	 * @return
	 */
	protected SerializationSettings createSerializationSettings(final WebScriptRequest request) {
		final SerializationSettings serializationSettings = new SerializationSettings();
		serializationSettings.setBasePath(request.getServiceContextPath());
		final String childDepth = request.getParameter(CHILD_DEPTH_PARAMETER);
		if (StringUtils.hasText(childDepth)) {
			serializationSettings.setChildDepth(Integer.parseInt(childDepth));
		}
		serializationSettings.setPrettyPrint(Boolean.valueOf(request.getParameter(PRETTY_PARAMETER)));
		return serializationSettings;
	}

	/**
	 * Creates a {@link JsonGenerator}. This implementation also configures the response's content type and encoding.
	 * 
	 * @param response
	 * @param serializationSettings
	 * @return
	 * @throws IOException
	 */
	protected JsonGenerator createJsonGenerator(final WebScriptResponse response,
			final SerializationSettings serializationSettings) throws IOException {
		Assert.notNull(response, "Response cannot be null.");

		response.setContentType("application/json");
		response.setContentEncoding("utf-8");
		final JsonGenerator generator = getJsonFactory().createJsonGenerator(response.getOutputStream(),
				JsonEncoding.UTF8);
		if (serializationSettings != null && serializationSettings.isPrettyPrint()) {
			generator.setPrettyPrinter(new DefaultPrettyPrinter());
		}
		return generator;
	}

	/**
	 * @deprecated use {@link #createJsonGenerator(WebScriptResponse, SerializationSettings)} instead.
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	protected JsonGenerator createJsonGenerator(final WebScriptResponse response) throws IOException {
		Assert.notNull(response, "Response cannot be null.");
		return createJsonGenerator(response, null);
	}

	/* Dependencies */

	public void setJsonFactory(final JsonFactory jsonFactory) {
		Assert.notNull(jsonFactory);
		this.jsonFactory = jsonFactory;
	}

	protected JsonFactory getJsonFactory() {
		return jsonFactory;
	}

	@PostConstruct
	protected void initializeJsonFactory() {
		if (getJsonFactory() == null) {
			setJsonFactory(new JsonFactory());
		}
	}

}
