package nl.runnable.alfresco.management;

import java.io.IOException;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class AbstractJsonWebScript {

	private static final String PRETTY_PARAMETER = "pretty";

	/* Dependencies */

	private final JsonFactory jsonFactory = new JsonFactory();

	/* Utility operations */

	/**
	 * Creates a {@link JsonGenerator}. This implementation also configures the response's content type and encoding.
	 * 
	 * @param response
	 * @param serializationSettings
	 * @return
	 * @throws IOException
	 */
	protected JsonGenerator createJsonGenerator(final WebScriptRequest request, final WebScriptResponse response)
			throws IOException {
		Assert.notNull(response, "Response cannot be null.");

		response.setContentType("application/json");
		response.setContentEncoding("utf-8");
		final JsonGenerator generator = getJsonFactory().createJsonGenerator(response.getOutputStream(),
				JsonEncoding.UTF8);
		if (Boolean.valueOf(request.getParameter(PRETTY_PARAMETER))) {
			generator.setPrettyPrinter(new DefaultPrettyPrinter());
		}
		return generator;
	}

	/* Dependencies */

	protected JsonFactory getJsonFactory() {
		return jsonFactory;
	}

}
